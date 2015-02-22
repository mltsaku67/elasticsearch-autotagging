package org.mlt.elasticsearch.auto.tagging;

import static org.codelibs.elasticsearch.runner.ElasticsearchClusterRunner.newConfigs;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;

import org.codelibs.elasticsearch.runner.ElasticsearchClusterRunner;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.ImmutableSettings.Builder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AutoTaggingPluginTest {

    private static final String pathToConfig = "org/mlt/elasticsearch/auto/tagging/AutoTaggingPluginTest.json";
    private ElasticsearchClusterRunner runner;

    @Before
    public void setUp() throws IOException {
        runner = new ElasticsearchClusterRunner();
        runner.onBuild(new ElasticsearchClusterRunner.Builder() {
            @Override
            public void build(int index, Builder settingsBuilder) {
                settingsBuilder.put("http.cors.enabled", true);
                settingsBuilder.put("index.number_of_replicas", 0);
            }
        }).build(newConfigs().ramIndexStore().numOfNode(1));
        runner.ensureYellow();
        String config = readConfig(pathToConfig);
        runner.createIndex("test_index", ImmutableSettings.EMPTY);
        runner.createMapping("test_index", "test_type", config);
    }

    @After
    public void cleanUp() {
        runner.close();
        runner.clean();
    }

    @Test
    public void test() throws Exception {
        // add documents
        index("1", "mew");
        index("2", "bow-wow");
        index("3", "mew");
        index("4", "foo bar");
        index("5", "clip-clop");
        index("6", "neigh");
        runner.refresh();

        {
            SearchResponse response = searchByTag("cat");
            assertThat(response.getHits().getTotalHits(), is((long) 2));
            assertThat(response.getHits().getAt(0).getId(), is("1"));
            assertThat(response.getHits().getAt(1).getId(), is("3"));
        }
        {
            SearchResponse response = searchByTag("dog");
            assertThat(response.getHits().getTotalHits(), is((long) 1));
            assertThat(response.getHits().getAt(0).getId(), is("2"));
        }
        {
            SearchResponse response = searchByTag("unknown");
            assertThat(response.getHits().getTotalHits(), is((long) 3));
            assertThat(response.getHits().getAt(0).getId(), is("4"));
            assertThat(response.getHits().getAt(1).getId(), is("5"));
            assertThat(response.getHits().getAt(2).getId(), is("6"));
        }
    }

    private final String format = "{ text: \"%s\" }";

    private void index(String id, String text) throws Exception {
        IndexRequestBuilder builder = runner.client().prepareIndex(
                "test_index", "test_type");
        builder.setId(id);
        builder.setSource(String.format(format, text));
        IndexResponse response = builder.execute().get();
        assertTrue(response.isCreated());
    }

    private SearchResponse searchByTag(String tagName)
            throws InterruptedException, ExecutionException {
        MatchQueryBuilder builder = matchQuery("text.tag", tagName);
        SearchResponse response = runner.client().prepareSearch("test_index")
                .setTypes("test_type").addSort("_id", SortOrder.ASC)
                .setQuery(builder).execute().get();
        return response;
    }

    private String readConfig(String path) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream(path), "UTF-8"))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append('\n');
            }
        }
        return sb.toString();
    }
}
