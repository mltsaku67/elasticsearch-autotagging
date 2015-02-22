package org.mlt.elasticsearch.auto.tagging.mapper.classifier;

import java.net.UnknownHostException;
import java.util.Map;

public class JubatusClassifierFactory implements ClassifierFactory {

    @Override
    public Classifier createClassifier(Map<String, Object> settings) {
        String host = (String) settings.get("host");
        int port = (int) settings.get("port");
        String classifierName = (String) settings.get("name");
        Integer timeoutSec = (Integer) settings.get("timeoutSec");
        if (timeoutSec == null) {
            timeoutSec = 5;
        }
        try {
            return new JubatusClassifier(host, port, classifierName, timeoutSec);
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException(
                    "Specified hostname is unknown: " + host, e);
        }
    }
}
