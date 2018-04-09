package com.huya.search.data;

import com.huya.search.index.data.SearchData;
import com.huya.search.index.data.SearchDataItem;
import com.huya.search.index.data.SearchDataRow;
import com.huya.search.index.meta.IndexFeatureType;
import com.huya.search.index.meta.TimelineMetaDefine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 产生随机的数据，用于测试
 *
 * Created by zhangyiqun1@yy.com on 2017/10/8.
 */
public class RandomSearchDataBuilder {

    private static final RandomData RANDOM_DATA = new RandomData();

    private TimelineMetaDefine metaDefine;

    private RandomTimestamp randomTimestamp;

    private RandomData randomData;

    private SearchData<SearchDataRow> buildSearchData;

    public RandomSearchDataBuilder(TimelineMetaDefine metaDefine, RandomTimestamp randomTimestamp, RandomData randomData) {
        this.metaDefine = metaDefine;
        this.randomTimestamp = randomTimestamp;
        this.randomData = randomData;
    }

    public RandomSearchDataBuilder(TimelineMetaDefine metaDefine, RandomTimestamp randomTimestamp) {
        this(metaDefine, randomTimestamp, RANDOM_DATA);
    }

    public SearchData<SearchDataRow> build(int shardNum, int size) {
        if (buildSearchData == null) {
            List<SearchDataRow> collection = new ArrayList<>();
            for (int j = 0; j < shardNum; j++) {
                for (int i = 0; i < size; i++) {
                    collection.add(buildRow(j, i, randomTimestamp.getTimestamp()));
                }
            }
            buildSearchData = SearchData.newInstance(collection);
        }
        return buildSearchData;
    }

    private SearchDataRow buildRow(int shardNum, int offset, long timestamp) {
        SearchDataRow searchDataRow = new SearchDataRow();
        searchDataRow.setId(shardNum);
        searchDataRow.setOffset(offset);
        searchDataRow.setUnixTime(timestamp);
        searchDataRow.setItems(buildItems());
        return searchDataRow;
    }

    private List<SearchDataItem> buildItems() {
        List<SearchDataItem> items = new ArrayList<>();
        Map<String, IndexFeatureType> indexTypeMap = metaDefine.getLast().getMap();
        indexTypeMap.forEach((key, value) -> items.add(SearchDataItem.newInstance(key, randomData.getObject(value.getType()))));
        return items;
    }

}
