package com.huya.search.index.opeation;

import com.google.common.collect.BoundType;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.huya.search.index.dsl.parse.ComponentFill;
import com.huya.search.index.dsl.parse.SearchSQLComponentFill;
import com.huya.search.index.lucene.ConcurrentCountCollector;
import com.huya.search.index.meta.MetaService;
import com.huya.search.inject.ModulesBuilder;
import com.huya.search.partition.PartitionRange;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ZhangXueJun
 * @date 2018年03月28日
 */
public class DisOperationFactoryTest {

    private static String sql = "" +
            "select count(*), grain2 from web_nginx where pr >= '2018-04-01 10:00:00' and pr <= '2018-04-01 16:00:00'  group by grain2";

    @Test
    public void getIndexContext() throws Exception {
    }

    @Test
    public void getShardIndexContext() throws Exception {
    }

    @Test
    public void getQueryContext() throws Exception {
        ModulesBuilder.getInstance().add(new AbstractModule() {
            @Override
            protected void configure() {
                bind(MetaService.class).to(MockRealMetaService.class).in(Singleton.class);
            }
        });
        DisQueryContext disQueryContext = DisOperationFactory.getQueryContext(sql);
        Query query = disQueryContext.getQuery();
        System.out.println(query.toString());
    }

    @Test
    public void testRange() {
        ModulesBuilder.getInstance().add(new AbstractModule() {
            @Override
            protected void configure() {
                bind(MetaService.class).to(MockRealMetaService.class).in(Singleton.class);
            }
        });
        ComponentFill componentFill = SearchSQLComponentFill.newInstance(sql);
        DisQueryFactory disQueryFactory = DisQueryFactory.newInstance();
        componentFill.fillComponent(disQueryFactory);
        PartitionRange times = getTimes(disQueryFactory, 1);

        Query query = new TermQuery(new Term("xx", "xx"));

        List<ConcurrentCountCollector> collectors = Lists.newArrayList();
        for (Range<Long> longRange : times.getRanges()) {
            ConcurrentCountCollector collector = new ConcurrentCountCollector(query, longRange);
            collectors.add(collector);
        }
        System.out.println(collectors);
    }

    private PartitionRange getTimes(DisQueryFactory disQueryFactory, int splitNum) {
        long low = disQueryFactory.getWhereCondition().getLowCycle();
        long up  = disQueryFactory.getWhereCondition().getUpCycle();
        long range = (up - low) / splitNum;
        List<Range<Long>> rangeList = new ArrayList<>();
        for (int i = 0; i < splitNum; i++) {
            long current = low + range;
            rangeList.add(Range.range(low, BoundType.CLOSED, current, BoundType.OPEN));
            low = current;
        }
        return new PartitionRange(rangeList);
    }
}
