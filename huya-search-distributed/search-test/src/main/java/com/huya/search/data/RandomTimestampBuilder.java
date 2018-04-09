package com.huya.search.data;


/**
 * Created by zhangyiqun1@yy.com on 2017/10/8.
 */
public class RandomTimestampBuilder {

    private static final RandomData randomData = new RandomData();

    private long floorTimestamp;

    private long ceilTimestamp;

    public RandomTimestampBuilder setFloorTimestamp(long floorTimestamp) {
        this.floorTimestamp = floorTimestamp;
        return this;
    }

    public RandomTimestampBuilder setCeilTimestamp(long ceilTimestamp) {
        this.ceilTimestamp = ceilTimestamp;
        return this;
    }

    public RandomTimestamp build() {
        return new HavingBoundRandomTimestamp(floorTimestamp, ceilTimestamp);
    }

    private static class HavingBoundRandomTimestamp implements RandomTimestamp {

        private long floorTimestamp;

        private long ceilTimestamp;

        private long subTimestamp;

        private HavingBoundRandomTimestamp(long floorTimestamp, long ceilTimestamp) {
            this.floorTimestamp = floorTimestamp;
            this.ceilTimestamp  = ceilTimestamp;
            this.subTimestamp   = ceilTimestamp - floorTimestamp;
            assert subTimestamp >= 0;
        }

        @Override
        public long getTimestamp() {
            if (subTimestamp == 0) {
                return floorTimestamp;
            }
            else {
                long ramSubTimestamp = Math.abs(randomData.getLong()) % (subTimestamp + 1);
                return floorTimestamp + ramSubTimestamp;
            }
        }

        public long getFloorTimestamp() {
            return floorTimestamp;
        }

        public long getCeilTimestamp() {
            return ceilTimestamp;
        }
    }

}
