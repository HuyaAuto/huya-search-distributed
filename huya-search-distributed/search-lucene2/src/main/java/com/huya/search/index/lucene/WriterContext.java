package com.huya.search.index.lucene;

import com.huya.search.index.data.SearchDataRow;
import com.huya.search.index.meta.IndexField;
import com.huya.search.index.meta.IndexFieldType;
import com.huya.search.index.meta.MetaDefine;
import com.huya.search.index.opeation.InsertContext;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/9.
 */
public class WriterContext {

    public static final Logger LOG = LoggerFactory.getLogger(WriterContext.class);

    public static WriterContext newInstance(InsertContext insertContext, MetaDefine metaDefine) {
        return new WriterContext(insertContext, metaDefine);
    }

    private Iterator<IndexField> indexFieldIterable;

    private Document doc;

    private WriterContext(InsertContext insertContext, MetaDefine metaDefine) {
        SearchDataRow searchDataRow = insertContext.getSearchDataRow();
        indexFieldIterable = searchDataRow.iterator(metaDefine);
    }

    boolean hasData() {
        return indexFieldIterable.hasNext();
    }

    public Document getDocument() {
        if (doc == null) {
            doc = new Document();
            while (indexFieldIterable.hasNext()) {
                try {
                    IndexField indexField = indexFieldIterable.next();
                    for (IndexableField indexableField : indexField.getLuceneField()) {
                        doc.add(indexableField);
                    }
                } catch (IndexFieldType.TypeNoMatchException ignore) {}
            }
        }
        return doc;
    }
}
