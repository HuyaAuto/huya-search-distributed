package com.huya.search.index.data.merger;

import com.huya.search.index.meta.MetaDefine;
import org.apache.lucene.search.Sort;

import java.util.List;

/**
 * Created by zhangyiqun1@yy.com on 2017/9/7.
 */
public class MergerFactory {

    public static Merger createMerger() {
        return new Merger();
    }

    public static Merger createMerger(int top) {
        return new Merger(top);
    }

    public static GroupMerger createGroupMerger(List<String> group, MetaDefine metaDefine) {
        return new GroupMerger(group, metaDefine);
    }

    public static GroupMerger createGroupMerger(List<String> group, MetaDefine metaDefine, int top) {
        return new GroupMerger(group, metaDefine, top);
    }

    public static GroupMerger createGroupMerger(List<String> group, MetaDefine metaDefine, int from, int to) {
        return new GroupMerger(group, metaDefine, from, to);
    }

    public static OriginSortMerger createOriginSortMerger(Sort sort) {
        return new OriginSortMerger(sort);
    }

    public static OriginSortMerger createOriginSortMerger(Sort sort, int top) {
        return new OriginSortMerger(sort, top);
    }

    public static OriginSortMerger createOriginSortMerger(Sort sort, int from, int to) {
        return new OriginSortMerger(sort, from, to);
    }

    public static SortMerger createSortMerger(Sort sort) {
        return new SortMerger(sort);
    }

    public static SortMerger createSortMerger(Sort sort, int top) {
        return new SortMerger(sort, top);
    }

    public static SortMerger createSortMerger(Sort sort, int from, int to) {
        return new SortMerger(sort, from, to);
    }

    public static SortGroupMerger createSortGroupMerger(Sort sort, List<String> group, MetaDefine metaDefine) {
        return new SortGroupMerger(sort, group, metaDefine);
    }

    public static SortGroupMerger createSortGroupMerger(Sort sort, List<String> group, MetaDefine metaDefine, int top) {
        return new SortGroupMerger(sort, group, metaDefine, top);
    }

    public static SortGroupMerger createSortGroupMerger(Sort sort, List<String> group, MetaDefine metaDefine, int from, int to) {
        return new SortGroupMerger(sort, group, metaDefine, from, to);
    }
}
