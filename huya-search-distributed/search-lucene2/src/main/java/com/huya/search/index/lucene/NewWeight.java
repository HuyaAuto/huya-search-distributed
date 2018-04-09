package com.huya.search.index.lucene;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.util.Bits;

import java.io.IOException;
import java.util.Set;

/**
 * Created by zhangyiqun1@yy.com on 2017/12/6.
 */
public class NewWeight extends CustomWeight {


    private final int newNum;

    public NewWeight(Weight weight, int newNum) {
        super(weight);
        this.newNum = newNum;
    }


    @Override
    protected BulkScorer customBulkScorer(Scorer scorer) {
        return new NewScorer(scorer, newNum);
    }


    protected static class NewScorer extends BulkScorer {

        private final int newNum;

        private final Scorer scorer;
        private final DocIdSetIterator iterator;
        private final TwoPhaseIterator twoPhase;

        public NewScorer(Scorer scorer, int newNum) {
            this.scorer = scorer;
            this.iterator = scorer.iterator();
            this.twoPhase = scorer.twoPhaseIterator();
            this.newNum = newNum;
        }

        @Override
        public int score(LeafCollector collector, Bits acceptDocs, int min, int max) throws IOException {
            collector.setScorer(scorer);
            if (scorer.docID() == -1 && min == 0 && max == DocIdSetIterator.NO_MORE_DOCS) {
                scoreAll(collector, iterator, twoPhase, acceptDocs);
                return DocIdSetIterator.NO_MORE_DOCS;
            } else {
                int doc = scorer.docID();
                if (doc < min) {
                    if (twoPhase == null) {
                        doc = iterator.advance(min);
                    } else {
                        doc = twoPhase.approximation().advance(min);
                    }
                }
                return scoreRange(collector, iterator, twoPhase, acceptDocs, doc, max);
            }
        }

        private int scoreRange(LeafCollector collector, DocIdSetIterator iterator, TwoPhaseIterator twoPhase, Bits acceptDocs, int currentDoc, int end) throws IOException {
            if (twoPhase == null) {
                while (currentDoc < end) {
                    if (acceptDocs == null || acceptDocs.get(currentDoc)) {
                        collector.collect(currentDoc);
                    }
                    currentDoc = iterator.nextDoc();
                }
                return currentDoc;
            } else {
                final DocIdSetIterator approximation = twoPhase.approximation();
                while (currentDoc < end) {
                    if ((acceptDocs == null || acceptDocs.get(currentDoc)) && twoPhase.matches()) {
                        collector.collect(currentDoc);
                    }
                    currentDoc = approximation.nextDoc();
                }
                return currentDoc;
            }
        }

        private void scoreAll(LeafCollector collector, DocIdSetIterator iterator, TwoPhaseIterator twoPhase, Bits acceptDocs) throws IOException {
            if (twoPhase == null) {
                for (int doc = iterator.nextDoc(); doc != DocIdSetIterator.NO_MORE_DOCS; doc = iterator.nextDoc()) {
                    if (acceptDocs == null || acceptDocs.get(doc)) {
                        collector.collect(doc);
                    }
                }
            } else {
                // The scorer has an approximation, so run the approximation first, then check acceptDocs, then confirm
                final DocIdSetIterator approximation = twoPhase.approximation();
                for (int doc = approximation.nextDoc(); doc != DocIdSetIterator.NO_MORE_DOCS; doc = approximation.nextDoc()) {
                    if ((acceptDocs == null || acceptDocs.get(doc)) && twoPhase.matches()) {
                        collector.collect(doc);
                    }
                }
            }
        }

        @Override
        public long cost() {
            return iterator.cost();
        }
    }

}
