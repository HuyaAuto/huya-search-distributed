package com.huya.search.index.lucene;

import org.apache.lucene.search.*;
import org.apache.lucene.util.Bits;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/26.
 */
public class MakeUpWeight extends CustomWeight {

    private final AtomicInteger count = new AtomicInteger(0);

    private final int makeUpCapped;

    protected MakeUpWeight(Weight weight, int makeUpCapped) {
        super(weight);
        this.makeUpCapped = makeUpCapped;
    }

    @Override
    protected BulkScorer customBulkScorer(Scorer scorer) {
        return new MakeUpBulkScorer(scorer, makeUpCapped, count);
    }

    protected static class MakeUpBulkScorer extends BulkScorer {

        private final AtomicInteger count;
        private final int makeUpCapped;

        private final Scorer scorer;
        private final DocIdSetIterator iterator;
        private final TwoPhaseIterator twoPhase;

        /**
         * Sole constructor.
         *
         * @param scorer
         */
        public MakeUpBulkScorer(Scorer scorer, int makeUpCapped, AtomicInteger count) {
            this.scorer = scorer;
            this.makeUpCapped = makeUpCapped;
            this.count = count;
            this.iterator = scorer.iterator();
            this.twoPhase = scorer.twoPhaseIterator();
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
                        if (count.addAndGet(1) >= makeUpCapped) break;
                    }
                    currentDoc = iterator.nextDoc();
                }
                return currentDoc;
            } else {
                final DocIdSetIterator approximation = twoPhase.approximation();
                while (currentDoc < end) {
                    if ((acceptDocs == null || acceptDocs.get(currentDoc)) && twoPhase.matches()) {
                        collector.collect(currentDoc);
                        if (count.addAndGet(1) >= makeUpCapped) break;
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
                        if (count.addAndGet(1) >= makeUpCapped) break;
                    }
                }
            } else {
                // The scorer has an approximation, so run the approximation first, then check acceptDocs, then confirm
                final DocIdSetIterator approximation = twoPhase.approximation();
                for (int doc = approximation.nextDoc(); doc != DocIdSetIterator.NO_MORE_DOCS; doc = approximation.nextDoc()) {
                    if ((acceptDocs == null || acceptDocs.get(doc)) && twoPhase.matches()) {
                        collector.collect(doc);
                        if (count.addAndGet(1) >= makeUpCapped) break;
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
