package com.imeee.elasticsearch.plugin;

import org.elasticsearch.index.IndexModule;
import org.elasticsearch.plugins.Plugin;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: zhangyi
 * Date: 2019-04-26
 * Time: 16:00
 */
public class Bm25WithouIdfSimilarityPlugin extends Plugin {
    public static final String NAME_SIMILARITY = "BM25_WITHOUT_IDF";
    @Override
    public final void onIndexModule(IndexModule indexModule) {

        indexModule.addSimilarity(
                NAME_SIMILARITY,

                (settings, version, scriptService) ->
                        new Bm25WithouIdfSimilarity(
                                settings.getAsFloat("k1", 1.2f),
                                settings.getAsFloat("b", 0.75f),
                                settings.getAsFloat("idfs", 1.0f)
                        )
        );
    }
}
