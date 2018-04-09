package com.huya.search.util;

import java.util.concurrent.CountDownLatch;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/9.
 */
public class CountUpAndDownLatch {

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    private int count;

    public CountUpAndDownLatch(int count) {
        assert count > 0;
        this.count = count;
    }

    public synchronized void down() {
        count--;
        if (count == 0) countDownLatch.countDown();
    }

    public synchronized void up() {
        if (count == 0) {
            countDownLatch = new CountDownLatch(1);
        }
        count ++;
    }

    public void await() throws InterruptedException {
        countDownLatch.await();
    }

    public int unDonecount() {
        return count;
    }

}
