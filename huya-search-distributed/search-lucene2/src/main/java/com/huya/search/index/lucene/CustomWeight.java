package com.huya.search.index.lucene;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;

import java.io.IOException;
import java.util.Set;

/**
 * Created by zhangyiqun1@yy.com on 2017/12/6.
 */
public abstract class CustomWeight extends Weight {

    private final Weight weight;

    protected CustomWeight(Weight weight) {
        super(weight.getQuery());
        this.weight = weight;
    }

    @Override
    public void extractTerms(Set<Term> terms) {
        weight.extractTerms(terms);
    }

    @Override
    public Explanation explain(LeafReaderContext context, int doc) throws IOException {
        return weight.explain(context, doc);
    }

    @Override
    public float getValueForNormalization() throws IOException {
        return weight.getValueForNormalization();
    }

    @Override
    public void normalize(float norm, float boost) {
        weight.normalize(norm, boost);
    }

    @Override
    public Scorer scorer(LeafReaderContext context) throws IOException {
        return weight.scorer(context);
    }

    @Override
    public BulkScorer bulkScorer(LeafReaderContext context) throws IOException {
        Scorer scorer = scorer(context);
        if (scorer == null) {
            // No docs match
            return null;
        }
        return customBulkScorer(scorer);
    }

    protected abstract BulkScorer customBulkScorer(Scorer scorer);

}
