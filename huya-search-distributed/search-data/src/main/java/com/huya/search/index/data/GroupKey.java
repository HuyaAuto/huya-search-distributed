package com.huya.search.index.data;

import org.apache.lucene.index.IndexableField;

import java.util.List;

public interface GroupKey {

    boolean equals(Object o);

    int hashCode();

    List<IndexableField> groupKeyFields();
}
