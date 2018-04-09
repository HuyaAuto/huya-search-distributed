package com.huya.search.partition;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/7.
 */
public enum PartitionGrain {
    HOUR(1),
    _2HOUR(2),
    _3HOUR(3),
    _4HOUR(4),
    _6HOUR(6),
    _12HOUR(12),
    _24HOUR(24);

    private int grain;

    PartitionGrain(int grain) {
        this.grain = grain;
    }

    public int getGrain() {
        return grain;
    }

    public int getRanking() {
        return 24 / grain;
    }

    public static long getMillis(int grain) {
        switch (grain) {
            case 1 : return 3600000L;
            case 2 : return 7200000L;
            case 3 : return 10800000L;
            case 4 : return 14400000L;
            case 6 : return 21600000L;
            case 12 : return 43200000L;
            case 24 : return 86400000L;
            default : return 3600000L;
        }
    }

    public long[] getBoundary(long unixTime) {
        long grainUnixTime = getMillis(grain);
        long cycle = (unixTime + 28800000L) / grainUnixTime;   //北京时间 东八区 + 8 小时的毫秒数
        long floor = cycle * grainUnixTime - 28800000L;
        long ceil  = floor + grainUnixTime;
        return new long[]{floor, ceil};
    }

    public long getFloor(long unixTime) {
        long grainUnixTime = getMillis(grain);
        return ((unixTime + 28800000L) / grainUnixTime) * grainUnixTime - 28800000L;
    }

    public long getCeil(long unixTime) {
        long grainUnixTime = getMillis(grain);
        return getFloor(unixTime) + grainUnixTime;
    }

    public static PartitionGrain getGrain(int i) {
        switch (i) {
            case 1 : return HOUR;
            case 2 : return _2HOUR;
            case 3 : return _3HOUR;
            case 4 : return _4HOUR;
            case 6 : return _6HOUR;
            case 12 : return _12HOUR;
            case 24 : return _24HOUR;
            default : return HOUR;
        }
    }
}
