package com.huya.search.index.lucene;

import com.google.common.collect.Range;
import com.huya.search.SearchException;
import com.huya.search.index.data.merger.AggrFunUnitSet;
import com.huya.search.index.meta.MetaEnum;
import com.huya.search.util.ThreadPoolSingleton;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

import static com.huya.search.index.data.SingleQueryResult.createNumberIndexableField;
import static com.huya.search.index.meta.impl.FieldFactory.OFFSET;

public class ReadyIndexReaderContainer implements IndexReaderContainer {

    private static final double PROCESSOR_NUM = Runtime.getRuntime().availableProcessors();

    private static final Logger LOG = LoggerFactory.getLogger(ReadyIndexReaderContainer.class);

    private static final Query MAX_OFFSET_QUERY = new FieldValueQuery(OFFSET);

    private static final Sort MAX_OFFSET_SORT = new Sort(new SortField(OFFSET, SortField.Type.LONG, true));

    private DirectoryReader reader;

    private CustomSearcherManager<CustomIndexSearcher> searcherManager;

    private volatile boolean open;

    public static ReadyIndexReaderContainer newInstance() {
        return new ReadyIndexReaderContainer();
    }

    private ReadyIndexReaderContainer() {}

    @Override
    public void open(IndexWriter writer) throws IOException {
        reader = DirectoryReader.open(writer);
        searcherManager = new CustomSearcherManager<>(reader, HuyaCustomSearcherFactory.getInstance());
        open = true;
    }

    @Override
    public void open(Directory directory) throws IOException {
        reader = DirectoryReader.open(directory);
        searcherManager = new CustomSearcherManager<>(reader, HuyaCustomSearcherFactory.getInstance());
        open = true;
    }

    public void refresh() throws IOException {
        searcherManager.maybeRefresh();
    }

    @Override
    public String tag() {
        return reader.directory().toString();
    }


    @Override
    public boolean isOpen() {
        return open;
    }

    @Override
    public void close() throws IOException {
        if (open) {
            try {
                LOG.info("will close searcher manager");
                searcherManager.close();
                LOG.info("will close dir index reader");
                reader.close();
            } catch (IOException e) {
                LOG.error("IndexReaderContainer close error", e);
                throw e;
            } finally {
                open = false;
            }
        }
    }

    @Override
    public boolean exist(long offset) throws IOException {
        if (open) {
            CustomIndexSearcher searcher = null;
            try {
                if (DirectoryReader.indexExists(reader.directory())) {
                    searcher = searcherManager.acquire();
                    Query query = LongPoint.newExactQuery(MetaEnum.OFFSET, offset);
                    TopDocs topDocs = searcher.search(query, 1);
                    return topDocs.totalHits >= 1;
                }
                return false;
            } finally {
                release(searcher);
            }
        }
        throw new SearchException("index reader is not open");
    }

    @Override
    public long maxOffset() throws IOException {
        if (open) {
            CustomIndexSearcher searcher = null;
            try {
                searcher = searcherManager.acquire();
                TopFieldDocs topFieldDocs = searcher.search(MAX_OFFSET_QUERY, 1, MAX_OFFSET_SORT);

                if (topFieldDocs.totalHits <= 0) {
                    return -1;
                } else {
                    int doc = topFieldDocs.scoreDocs[0].doc;
                    return searcher.doc(doc).getField(OFFSET).numericValue().longValue();
                }
            } finally {
                release(searcher);
            }
        }
        throw new SearchException("index reader is not open");
    }

    private List<? extends Iterable<IndexableField>> innerQueryMethod(List<Integer> list, IndexSearcher searcher, Set<String> fields, int from, String poolName) {
        int size = list.size();
        List<Integer> docIdList = size > from ? list.subList(from, size) : Collections.emptyList();

        size = docIdList.size();
        if (size == 0) {
            return Collections.emptyList();
        }
        else {
            int minThreadQueryLen = (int) Math.ceil(size / PROCESSOR_NUM);
            return ThreadPoolSingleton.getInstance().getForkJoinPool(poolName)
                    .invoke(new GetAndMergerTask(searcher, fields, docIdList, 0, size - 1, minThreadQueryLen));
        }
    }

    @Override
    public List<? extends Iterable<IndexableField>> query(Query query, Set<String> fields, int from, int to) throws IOException {
        if (open) {
            CustomIndexSearcher searcher = null;
            try {
                searcher = searcherManager.acquire();
                List<Integer> list = searcher.search(query, MakeUpCollectorManager.newInstance(to + 1));
                return innerQueryMethod(list, searcher, fields, from, ThreadPoolSingleton.Names.GENERIC);
            } finally {
                release(searcher);
            }
        }
        throw new SearchException("index reader is not open");
    }

    @Override
    public List<? extends Iterable<IndexableField>> queryNew(Query query, Set<String> fields, int from, int to) throws IOException {
        if (open) {
            CustomIndexSearcher searcher = null;
            try {
                searcher = searcherManager.acquire();
                List<Integer> list = searcher.search(query, NewCollectorManager.newInstance(to + 1));
                return innerQueryMethod(list, searcher, fields, from, ThreadPoolSingleton.Names.GENERIC);
            } finally {
                release(searcher);
            }
        }
        throw new SearchException("index reader is not open");
    }

    @Override
    public List<? extends Iterable<IndexableField>> querySort(Query query, Sort sort, Set<String> fields, int from, int to) throws IOException {
        if (open) {
            CustomIndexSearcher searcher = null;
            try {
                searcher = searcherManager.acquire();
                List<Integer> list = Arrays.stream(searcher.search(query, to + 1, sort, false, false).scoreDocs)
                        .map(scoreDoc -> scoreDoc.doc).collect(Collectors.toList());
                return innerQueryMethod(list, searcher, fields, from, ThreadPoolSingleton.Names.GENERIC);
            } finally {
                release(searcher);
            }
        }
        throw new SearchException("index reader is not open");
    }

    @Override
    public List<? extends Iterable<IndexableField>> queryAggr(Query query, AggrFunUnitSet aggrFunUnitSet) throws IOException {
        if (open) {

        }
        throw new SearchException("index reader is not open");
    }

    @Override
    public List<? extends Iterable<IndexableField>> queryCount(Query query) throws IOException {
        return Collections.singletonList(Collections.singletonList(createNumberIndexableField("*", count(query))));
    }

    @Override
    public List<? extends Iterable<IndexableField>> queryCount(Query query, List<Range<Long>> rangeList) throws IOException {
        if (open) {
            CustomIndexSearcher searcher = null;
            try {
                searcher = searcherManager.acquire();
                return searcher.groupByTimestampSearch(query, rangeList);
            } finally {
                release(searcher);
            }
        }
        throw new SearchException("index reader is not open");
    }

    @Override
    public long count(Query query) throws IOException {
        if (open) {
            CustomIndexSearcher searcher = null;
            try {
                searcher = searcherManager.acquire();
                TotalHitCountCollector collector = new TotalHitCountCollector();
                searcher.search(query, collector);
                return collector.getTotalHits();
            } finally {
                release(searcher);
            }
        }
        throw new SearchException("index reader is not open");
    }

    @Override
    public void search(Query query, Collector collector) {
        if (open) {
            CustomIndexSearcher searcher = null;
            try {
                searcher = searcherManager.acquire();
                searcher.search(query, collector);
            } catch (IOException e) {
                LOG.error("search collector error", e);
            } finally {
                release(searcher);
            }

        }
    }

    @Override
    public TopFieldDocs search(Query query, int n, Sort sort) {
        if (open) {
            CustomIndexSearcher searcher = null;
            try {
                searcher = searcherManager.acquire();
                return searcher.search(query, n, sort, false, false);
            } catch (IOException e) {
                LOG.error("search error", e);
                return new TopFieldDocs(0, new ScoreDoc[0], new SortField[0], 0);
            } finally {
                release(searcher);
            }
        }
        return new TopFieldDocs(0, new ScoreDoc[0], new SortField[0], 0);
    }

    private void release(CustomIndexSearcher searcher) {
        if (searcher != null) {
            try {
                searcherManager.release(searcher);
            } catch (IOException e) {
                LOG.error("searcherManager release error", e);
            }
        }
    }

    private static class GetAndMergerTask extends  RecursiveTask<List<Document>> {

        private IndexSearcher searcher;

        private Set<String> selectItems;

        private List<Integer> list;

        private int from;

        private int to;

        private int minLen;

        private GetAndMergerTask(IndexSearcher searcher, Set<String> selectItems, List<Integer> list, int from, int to, int minLen) {
            this.searcher = searcher;
            this.selectItems = selectItems;
            this.list = list;
            this.from = from;
            this.to = to;
            this.minLen = minLen;
        }

        @Override
        protected List<Document> compute() {
            if (to - from + 1 <= 12) {
                List<Document> documents = new ArrayList<>(to - from + 1);
                for (int i = from; i <= to ; i++) {
                    try {
                        documents.add(selectItems == null ? searcher.doc(list.get(i)) : searcher.doc(list.get(i), selectItems));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return documents;
            }
            else {
                int middle = (from + to) / 2;
                GetAndMergerTask taskLeft  = new GetAndMergerTask(searcher, selectItems, list, from, middle, minLen);
                GetAndMergerTask taskRight = new GetAndMergerTask(searcher, selectItems, list, middle + 1, to, minLen);
                taskLeft.fork();
                taskRight.fork();

                List<Document> temp = taskLeft.join();
                temp.addAll(taskRight.join());
                return temp;
            }

        }
    }

}
