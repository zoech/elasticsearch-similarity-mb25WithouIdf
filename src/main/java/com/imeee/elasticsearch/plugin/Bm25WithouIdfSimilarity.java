package com.imeee.elasticsearch.plugin;

import org.apache.lucene.search.CollectionStatistics;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.similarities.BM25Similarity;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: zhangyi
 * Date: 2019-04-26
 * Time: 15:58
 */
public class Bm25WithouIdfSimilarity extends BM25Similarity {
    float idfs = 1.0f;

    public Bm25WithouIdfSimilarity(float k1, float b, float idfs){
        super(k1, b);
        this.idfs = idfs;
    }

    @Override
    public Explanation idfExplain(CollectionStatistics collectionStats, TermStatistics termStats) {
        return Explanation.match(idfs, "idf, ignore:");
    }
}
