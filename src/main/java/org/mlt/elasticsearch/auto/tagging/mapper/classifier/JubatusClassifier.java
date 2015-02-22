package org.mlt.elasticsearch.auto.tagging.mapper.classifier;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import org.elasticsearch.common.xcontent.ToXContent.Params;
import org.elasticsearch.common.xcontent.XContentBuilder;

import us.jubat.classifier.ClassifierClient;
import us.jubat.classifier.EstimateResult;
import us.jubat.common.Datum;

public class JubatusClassifier implements Classifier {

    private final String host;
    private final int port;
    private final String name;
    private final int timeoutSec;
    private final ClassifierClient client;

    public JubatusClassifier(String host, int port, String name, int timeoutSec)
            throws UnknownHostException {
        this.host = host;
        this.port = port;
        this.name = name;
        this.timeoutSec = timeoutSec;
        this.client = new ClassifierClient(host, port, name, timeoutSec);
    }

    public String classify(String content) {
        List<List<EstimateResult>> results = client.classify(Arrays
                .asList(makeDatum(content)));
        EstimateResult result = findBestResult(results.get(0));
        return result.label;
    }

    @Override
    public void close() {
        if (client.getClient() != null) {
            client.getClient().close();
        }
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params)
            throws IOException {
        builder.field("classifier_factory",
                JubatusClassifierFactory.class.getCanonicalName());
        builder.startObject("settings");
        builder.field("host", host);
        builder.field("port", port);
        builder.field("name", name);
        builder.field("timeoutSec", timeoutSec);
        builder.endObject();
        return builder;
    }

    private EstimateResult findBestResult(List<EstimateResult> results) {
        EstimateResult best = null;
        for (EstimateResult result : results) {
            if (best == null || result.score > best.score) {
                best = result;
            }
        }
        return best;
    }

    private static Datum makeDatum(String name) {
        return new Datum().addString("name", name);
    }
}
