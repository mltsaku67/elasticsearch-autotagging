package org.mlt.elasticsearch.auto.tagging.mapper.classifier;

import java.util.Map;

public interface ClassifierFactory {

    Classifier createClassifier(Map<String, Object> settings);
}
