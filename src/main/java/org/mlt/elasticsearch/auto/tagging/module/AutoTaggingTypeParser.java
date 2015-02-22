package org.mlt.elasticsearch.auto.tagging.module;

import java.util.Map;

import org.elasticsearch.index.mapper.Mapper;
import org.elasticsearch.index.mapper.Mapper.Builder;
import org.elasticsearch.index.mapper.MapperParsingException;
import org.elasticsearch.index.mapper.core.StringFieldMapper;
import org.mlt.elasticsearch.auto.tagging.mapper.AutoTaggingMapperBuilder;
import org.mlt.elasticsearch.auto.tagging.mapper.classifier.Classifier;
import org.mlt.elasticsearch.auto.tagging.mapper.classifier.ClassifierFactory;

/**
 * @author mltsaku67
 */
public class AutoTaggingTypeParser implements Mapper.TypeParser {

    private static final String PROPKEY_CLASSIFIER = "classifier_factory";
    private static final String PROPKEY_SETTINGS = "settings";
    private static final String PROPKEY_FIELDS = "fields";
    private static final String PROPKEY_TAG = "tag";

    @Override
    public Builder<?, ?> parse(String name, Map<String, Object> node,
            ParserContext parserContext) throws MapperParsingException {
        AutoTaggingMapperBuilder builder = new AutoTaggingMapperBuilder(name);
        builder.setClassifier(createClassifier(node));
        if (node.containsKey(PROPKEY_FIELDS)) {
            Map<String, Object> fields = getValueAsMap(node, PROPKEY_FIELDS);
            if (fields.containsKey(name)) {
                Map<String, Object> prop = getValueAsMap(fields, name);
                builder.setContentMapperBuilder((StringFieldMapper.Builder) parserContext
                        .typeParser("string").parse(name, prop, parserContext));
            }
            if (fields.containsValue(PROPKEY_TAG)) {
                Map<String, Object> prop = getValueAsMap(fields, PROPKEY_TAG);
                builder.setContentMapperBuilder((StringFieldMapper.Builder) parserContext
                        .typeParser("string").parse(PROPKEY_TAG, prop,
                                parserContext));
            }
        }
        return builder;
    }

    private Classifier createClassifier(Map<String, Object> node) {
        try {
            String classifierFactory = (String) node.get(PROPKEY_CLASSIFIER);
            Map<String, Object> settings = getValueAsMap(node, PROPKEY_SETTINGS);
            ClassifierFactory factory = Class.forName(classifierFactory)
                    .asSubclass(ClassifierFactory.class).newInstance();
            return factory.createClassifier(settings);
        } catch (InstantiationException | IllegalAccessException
                | ClassNotFoundException e) {
            throw new IllegalArgumentException("Illegal autotagging settings.",
                    e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getValueAsMap(Map<String, Object> map,
            String key) {
        return (Map<String, Object>) map.get(key);
    }
}
