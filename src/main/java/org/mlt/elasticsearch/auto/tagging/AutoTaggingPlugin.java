package org.mlt.elasticsearch.auto.tagging;

import static org.elasticsearch.common.collect.Lists.newArrayList;

import java.util.Collection;

import org.elasticsearch.common.inject.Module;
import org.elasticsearch.plugins.AbstractPlugin;
import org.mlt.elasticsearch.auto.tagging.module.AutoTaggingIndexModule;

public class AutoTaggingPlugin extends AbstractPlugin {

    @Override
    public String name() {
        return "AutoTaggerPlugin";
    }

    @Override
    public String description() {
        return "This is a elasticsearch-auto-tagging plugin.";
    }

    @Override
    public Collection<Class<? extends Module>> indexModules() {
        Collection<Class<? extends Module>> modules = newArrayList();
        modules.add(AutoTaggingIndexModule.class);
        return modules;
    }
}
