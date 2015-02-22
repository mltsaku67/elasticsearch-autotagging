package org.mlt.elasticsearch.auto.tagging.mapper;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.index.fielddata.FieldDataType;
import org.elasticsearch.index.mapper.FieldMapperListener;
import org.elasticsearch.index.mapper.Mapper;
import org.elasticsearch.index.mapper.MergeContext;
import org.elasticsearch.index.mapper.MergeMappingException;
import org.elasticsearch.index.mapper.ObjectMapperListener;
import org.elasticsearch.index.mapper.ParseContext;
import org.elasticsearch.index.mapper.core.AbstractFieldMapper;
import org.elasticsearch.index.mapper.core.StringFieldMapper;
import org.mlt.elasticsearch.auto.tagging.mapper.classifier.Classifier;

public class AutoTaggingMapper extends AbstractFieldMapper<Object> {

    private static final String CONTENT_TYPE = "autotagging";

    private final ESLogger logger = ESLoggerFactory.getLogger(getClass()
            .getName());
    private final Classifier classifier;
    private StringFieldMapper contentMapper;
    private StringFieldMapper tagMapper;

    public AutoTaggingMapper(Names names, Classifier classifier,
            StringFieldMapper contentMapper, StringFieldMapper tagMapper) {
        super(names, 1.0f, Defaults.FIELD_TYPE, false, null, null, null, null,
                null, null, null, null, null, null);
        this.classifier = classifier;
        this.contentMapper = contentMapper;
        this.tagMapper = tagMapper;
    }

    @Override
    public void parse(ParseContext context) throws IOException {
        String fieldText = extractText(context);
        context = context.createExternalValueContext(fieldText);
        contentMapper.parse(context);
        String label = classifier.classify(fieldText);
        context = context.createExternalValueContext(label);
        tagMapper.parse(context);
    }

    @Override
    public Object value(Object value) {
        return null;
    }

    @Override
    public FieldType defaultFieldType() {
        return Defaults.FIELD_TYPE;
    }

    @Override
    public FieldDataType defaultFieldDataType() {
        return null;
    }

    @Override
    protected String contentType() {
        return CONTENT_TYPE;
    }

    @Override
    protected void parseCreateField(ParseContext context, List<Field> fields)
            throws IOException {
    }

    @Override
    public void merge(Mapper mergeWith, MergeContext mergeContext)
            throws MergeMappingException {
    }

    @Override
    public void traverse(FieldMapperListener fieldMapperListener) {
        contentMapper.traverse(fieldMapperListener);
        tagMapper.traverse(fieldMapperListener);
    }

    @Override
    public void traverse(ObjectMapperListener objectMapperListener) {
    }

    @Override
    public void close() {
        contentMapper.close();
        tagMapper.close();
        try {
            classifier.close();
        } catch (IOException e) {
            logger.warn("Failed jubatus client.");
        }
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params)
            throws IOException {
        builder.startObject(name());
        builder.field("type", CONTENT_TYPE);
        builder.startObject("fields");
        contentMapper.toXContent(builder, params);
        tagMapper.toXContent(builder, params);
        builder.endObject();
        classifier.toXContent(builder, params);
        builder.endObject();
        return builder;
    }

    private String extractText(ParseContext context) throws IOException {
        XContentParser parser = context.parser();
        XContentParser.Token token = parser.currentToken();
        if (token == XContentParser.Token.VALUE_STRING) {
            return parser.text();
        }
        return null;
    }
}
