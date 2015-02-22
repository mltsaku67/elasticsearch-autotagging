package org.mlt.elasticsearch.auto.tagging.mapper;

import org.apache.lucene.document.FieldType;
import org.elasticsearch.index.mapper.FieldMapper.Names;
import org.elasticsearch.index.mapper.Mapper.BuilderContext;
import org.elasticsearch.index.mapper.MapperBuilders;
import org.elasticsearch.index.mapper.core.AbstractFieldMapper;
import org.elasticsearch.index.mapper.core.AbstractFieldMapper.Defaults;
import org.elasticsearch.index.mapper.core.StringFieldMapper;
import org.mlt.elasticsearch.auto.tagging.mapper.classifier.Classifier;

public class AutoTaggingMapperBuilder
        extends
        AbstractFieldMapper.Builder<AutoTaggingMapperBuilder, AutoTaggingMapper> {

    private final String fieldName;
    private Classifier classifier;
    private StringFieldMapper.Builder contentMapperBuilder;
    private StringFieldMapper.Builder tagMapperBuilder;

    public AutoTaggingMapperBuilder(String fieldName) {
        super(fieldName, new FieldType(Defaults.FIELD_TYPE));
        this.fieldName = fieldName;
        this.contentMapperBuilder = MapperBuilders.stringField(fieldName);
        this.tagMapperBuilder = MapperBuilders.stringField("tag");
    }

    public void setClassifier(Classifier classifier) {
        this.classifier = classifier;
    }

    public void setContentMapperBuilder(
            StringFieldMapper.Builder contentMapperBuilder) {
        this.contentMapperBuilder = contentMapperBuilder;
    }

    public void setTagMapperBuilder(StringFieldMapper.Builder tagMapperBuilder) {
        this.tagMapperBuilder = tagMapperBuilder;
    }

    @Override
    public AutoTaggingMapper build(BuilderContext context) {
        context.path().add(fieldName);
        StringFieldMapper contentMapper = contentMapperBuilder.build(context);
        StringFieldMapper tagMapper = tagMapperBuilder.build(context);
        context.path().remove();
        return new AutoTaggingMapper(new Names(fieldName), classifier,
                contentMapper, tagMapper);
    }
}
