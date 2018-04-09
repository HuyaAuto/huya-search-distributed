package com.huya.search.index.data.impl;

import org.apache.lucene.index.IndexableField;

import java.util.List;

/**
 * Created by zhangyiqun1@yy.com on 2017/9/8.
 */
public class PartitionDocs {

    public static PartitionDocs newInstance(String tag, List<? extends Iterable<IndexableField>> documentList) {
        return new PartitionDocs(tag, documentList);
    }

    private final String tag;

    private final List<? extends Iterable<IndexableField>> documentList;

    public PartitionDocs(String tag, List<? extends Iterable<IndexableField>> documentList) {
        this.tag = tag;
        this.documentList = documentList;
    }

    public String tag() {
        return tag;
    }

    public List<? extends Iterable<IndexableField>> getDocumentList() {
        return documentList;
    }

    public int size() {
        return documentList.size();
    }

}
