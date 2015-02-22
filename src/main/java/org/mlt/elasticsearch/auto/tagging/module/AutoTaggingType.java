package org.mlt.elasticsearch.auto.tagging.module;

import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.AbstractIndexComponent;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.mapper.MapperService;
import org.elasticsearch.index.settings.IndexSettings;

public class AutoTaggingType extends AbstractIndexComponent {

    @Inject
    public AutoTaggingType(Index index, @IndexSettings Settings indexSettings,
            MapperService mapperService) {
        super(index, indexSettings);
        mapperService.documentMapperParser().putTypeParser("autotagging",
                new AutoTaggingTypeParser());
    }
}
