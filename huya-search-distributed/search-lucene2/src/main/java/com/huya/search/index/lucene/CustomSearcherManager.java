package com.huya.search.index.lucene;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.ReferenceManager;

import java.io.IOException;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/25.
 */
public class CustomSearcherManager<T extends CustomIndexSearcher> extends ReferenceManager<T> {

    private final CustomSearcherFactory<T> customSearcherFactory;

    public CustomSearcherManager(DirectoryReader reader, CustomSearcherFactory<T> customSearcherFactory) throws IOException {
        this.customSearcherFactory = customSearcherFactory;
        this.current = getSearcher(customSearcherFactory, reader, null);
    }

    @Override
    protected void decRef(T reference) throws IOException {
        reference.getIndexReader().decRef();
    }

    /**
     * 刷新directory if changed
     *
     * @param referenceToRefresh
     * @return
     * @throws IOException
     */
    @Override
    protected T refreshIfNeeded(T referenceToRefresh) throws IOException {
        final IndexReader r = referenceToRefresh.getIndexReader();
        assert r instanceof DirectoryReader : "searcher's IndexReader should be a DirectoryReader, but got " + r;
        final IndexReader newReader = DirectoryReader.openIfChanged((DirectoryReader) r);
        if (newReader == null) {
            return null;
        }
        return getSearcher(customSearcherFactory, newReader, r);
    }

    @Override
    protected boolean tryIncRef(T reference) throws IOException {
        return reference.getIndexReader().tryIncRef();
    }

    @Override
    protected int getRefCount(T reference) {
        return reference.getIndexReader().getRefCount();
    }

    private T getSearcher(CustomSearcherFactory<T> customSearcherFactory, IndexReader reader, IndexReader previousReader) throws IOException {
        boolean success = false;
        final T searcher;
        try {
            searcher = customSearcherFactory.newSearcher(reader, previousReader);
            if (searcher.getIndexReader() != reader) {
                throw new IllegalStateException("CustomSearcherFactory must wrap exactly the provided reader (got " + searcher.getIndexReader() + " but expected " + reader + ")");
            }
            success = true;
        } finally {
            if (!success) {
                reader.decRef();
            }
        }
        return searcher;
    }
}