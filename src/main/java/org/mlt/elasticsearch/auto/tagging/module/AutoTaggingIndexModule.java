package org.mlt.elasticsearch.auto.tagging.module;

import org.elasticsearch.common.inject.Binder;
import org.elasticsearch.common.inject.Module;

public class AutoTaggingIndexModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(AutoTaggingType.class).asEagerSingleton();
    }
}