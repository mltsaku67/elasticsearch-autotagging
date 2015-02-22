package org.mlt.elasticsearch.auto.tagging.mapper.classifier;

import java.io.Closeable;
import java.io.IOException;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.ToXContent.Params;

public interface Classifier extends Closeable {

    String classify(String text);

    XContentBuilder toXContent(XContentBuilder builder, Params params)
            throws IOException;
}
