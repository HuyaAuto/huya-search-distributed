package com.huya.search.partition;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import com.huya.search.util.JodaUtil;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import java.util.Arrays;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/7.
 */
public class PartitionCycle implements Comparable<PartitionCycle> {

    private static final DateTimeFormatter PARTITION_CYCLE = JodaUtil.YYYY_MM_DD_FORMATTER;

    private static final String UNDERLINE = "_";

    private PartitionGrain grain;

    private String partitionName;

    private long[] boundary;

    private int ranking;

    public PartitionCycle(DateTime dateTime, PartitionGrain grain) {
        long now = dateTime.getMillis();
        this.grain = grain;
        boundary = this.grain.getBoundary(now);
        this.ranking = dateTime.getHourOfDay() / grain.getGrain();
    }

    public PartitionCycle(long unixTime, PartitionGrain grain) {
        DateTime dateTime = new DateTime(unixTime);
        this.grain = grain;
        boundary = this.grain.getBoundary(unixTime);
        this.ranking = dateTime.getHourOfDay() / grain.getGrain();
    }

    public PartitionCycle(String partitionName) {
        String[] temp = partitionName.split(UNDERLINE);
        assert temp.length == 3;
        this.ranking = Integer.parseInt(temp[1]);
        int grain = Integer.parseInt(temp[2]);
        int hour = this.ranking * grain;
        String strHour = hour < 10 ? "0" + hour : String.valueOf(hour);
        String timestamp = temp[0] + " " + strHour + ":00:00";
        DateTime dateTime = DateTime.parse(timestamp, JodaUtil.YYYY_MM_DD_HH_MM_SS_FORMATTER);
        this.grain = PartitionGrain.getGrain(Integer.parseInt(temp[2]));
        this.boundary = this.grain.getBoundary(dateTime.getMillis());
    }

    private PartitionCycle (long[] boundary, PartitionGrain grain, int ranking) {
        this.boundary = boundary;
        this.grain = grain;
        this.ranking = ranking;
    }

    public PartitionCycle next() {
        long[] temp = new long[2];
        temp[0] = this.boundary[1];
        temp[1] = this.boundary[1] + PartitionGrain.getMillis(grain.getGrain());
        return new PartitionCycle(temp, grain, (ranking + 1) % grain.getRanking());
    }

    public PartitionCycle prev() {
        long[] temp = new long[2];
        temp[1] = this.boundary[0];
        temp[0] = this.boundary[0] - PartitionGrain.getMillis(grain.getGrain());
        return new PartitionCycle(temp, grain, (ranking + grain.getRanking() - 1) % grain.getRanking());
    }

    public PartitionGrain getGrain() {
        return grain;
    }

    public String partitionName() {
        if (partitionName == null) {
            partitionName = new DateTime(boundary[0]).toString(PARTITION_CYCLE) + UNDERLINE + rankingStr()
                    + UNDERLINE + grainStr();
        }
        return partitionName;
    }

    private String rankingStr() {
        assert ranking >= 0 && ranking <= 23;
        return ranking < 10 ? "0" + ranking : String.valueOf(ranking);
    }

    private String grainStr() {
        int grainInt = grain.getGrain();
        assert grainInt >= 1 && grainInt <= 24;
        return grainInt < 10 ? "0" + grainInt : String.valueOf(grainInt);
    }

    public boolean adapt(long unixTime) {
        return boundary[0] <= unixTime && boundary[1] > unixTime;
    }

    public boolean current() {
        long currentUnixTime = System.currentTimeMillis();
        return currentUnixTime < boundary[1];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PartitionCycle that = (PartitionCycle) o;

        return Arrays.equals(boundary, that.boundary);
    }

    public long getFloor() {
        return boundary[0];
    }

    public long getCeil() {
        return boundary[1];
    }

    public Range<Long> getRange() {
        return Range.range(getFloor(), BoundType.CLOSED, getCeil(), BoundType.OPEN);
    }

    public long[] split(int num) {
        long[] times = new long[num];
        long range = (boundary[1] - boundary[0]) / num;
        times[0] = boundary[0];
        for (int i = 1; i < num; i++) {
            times[i] = times[i-1] + range;
        }
        return times;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(boundary);
    }

    @Override
    public int compareTo(PartitionCycle o) {
        return Long.compare(this.boundary[0], o.boundary[0]);
    }
}
