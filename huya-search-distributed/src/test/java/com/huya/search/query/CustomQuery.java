package com.huya.search.query;

import com.huya.search.rest.HttpOperator;
import com.huya.search.util.JodaUtil;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.junit.Test;

import java.io.IOException;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangyiqun1@yy.com on 2017/12/27.
 */
public class CustomQuery {

    private static final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(4);

    private static final QueryTask QUERY_TASK = new QueryTask();

    private static final HttpOperator HTTP_OPERATOR = new HttpOperator("http://14.29.58.111:28888");

    @Test
    public void queryTest() throws InterruptedException {
        scheduledExecutorService.scheduleAtFixedRate(QUERY_TASK, 600, 600, TimeUnit.SECONDS);
        CountDownLatch latch = new CountDownLatch(1);
        latch.await();
    }

    static class QueryTask extends TimerTask {

        @Override
        public void run() {
            try {
                DateTime dateTime = new DateTime(System.currentTimeMillis());
                String low = dateTime.toString(JodaUtil.YYYY_MM_DD_HH_FORMATTER) + ":00:00";
                String up  = dateTime.plusHours(1).toString(JodaUtil.YYYY_MM_DD_HH_FORMATTER) + ":00:00";
                String result = HTTP_OPERATOR.sql("select * from video_bad_quality_ratio where pr >= '" + low + "' and pr < '" + up + "'  order by NEW_DOC limit 50");
                System.out.println(result);
                TimeUnit.SECONDS.sleep(2);

                result = HTTP_OPERATOR.sql("select * from video_bad_quality_ratio where pr >= '" + low + "' and pr < '" + up + "'  and line = 3 and _ip = '124.226.11.116'  order by NEW_DOC limit 50");
                System.out.println(result);
                TimeUnit.SECONDS.sleep(2);


                result = HTTP_OPERATOR.sql("select * from video_bad_quality_ratio where pr >= '" + low + "' and pr < '" + up + "'  and _area like '杭州'  order by NEW_DOC limit 50");
                System.out.println(result);
                TimeUnit.SECONDS.sleep(2);

                result = HTTP_OPERATOR.sql("select * from video_bad_quality_ratio where pr >= '" + low + "' and pr < '" + up + "'  limit 50");
                System.out.println(result);
                TimeUnit.SECONDS.sleep(2);

                result = HTTP_OPERATOR.sql("select * from video_bad_quality_ratio where pr >= '" + low + "' and pr < '" + up + "'  and line = 3 and _ip = '124.226.11.116'  limit 50");
                System.out.println(result);
                TimeUnit.SECONDS.sleep(2);

                result = HTTP_OPERATOR.sql("select * from video_bad_quality_ratio where pr >= '" + low + "' and pr < '" + up + "'  and _area like '杭州'  limit 50");
                System.out.println(result);

            } catch (IOException | InterruptedException e) {
                System.out.println(ExceptionUtils.getStackTrace(e));
            }
        }
    }

}
