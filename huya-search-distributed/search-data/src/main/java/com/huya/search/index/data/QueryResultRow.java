package com.huya.search.index.data;

import org.apache.lucene.index.IndexableField;


public interface QueryResultRow extends Iterable<IndexableField> {

    CharSequence getCharSequence(CharSequence name);
}
