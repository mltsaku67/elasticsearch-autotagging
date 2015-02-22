package org.mlt.elasticsearch.auto.tagging;

import java.io.IOException;
import java.util.Map;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.ToXContent.Params;
import org.mlt.elasticsearch.auto.tagging.mapper.classifier.Classifier;
import org.mlt.elasticsearch.auto.tagging.mapper.classifier.ClassifierFactory;

public class TestClassifierFactory implements ClassifierFactory {

    @Override
    public Classifier createClassifier(Map<String, Object> settings) {
        return new Classifier() {

            @Override
            public void close() throws IOException {
                // NOP
            }

            @Override
            public XContentBuilder toXContent(XContentBuilder builder,
                    Params params) throws IOException {
                builder.field("classifier_factory",
                        TestClassifierFactory.class.getCanonicalName());
                return builder;
            }

            @Override
            public String classify(String text) {
                switch (text.toLowerCase()) {
                case "mew":
                    return "cat";
                case "bow-wow":
                    return "dog";
                default:
                    return "unknown";
                }
            }
        };
    }
}
