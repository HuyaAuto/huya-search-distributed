package com.huya.search.partition;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/24.
 */
public class PartitionRange {

    public static final PartitionRange ALL = new PartitionRange(Range.all());

    List<Range<Long>> ranges;

    public static PartitionRange getByRange(Range<Long> range) {
        return new PartitionRange(range);
    }

    public static PartitionRange getByRanges(List<Range<Long>> ranges) {
        return new PartitionRange(ranges);
    }

    public static PartitionRange intersection(PartitionRange rangeA, PartitionRange rangeB) {
        return rangeA.intersection(rangeB);
    }

    public static PartitionRange union(PartitionRange rangeA, PartitionRange rangeB) {
        return rangeA.union(rangeB);
    }

    private PartitionRange(Range<Long> range) {
        this.ranges = new ArrayList<>();
        this.ranges.add(range);
    }

    public PartitionRange(List<Range<Long>> ranges) {
        this.ranges = ranges;
    }

    public PartitionRange() {
        this.ranges = new ArrayList<>();
    }

    public boolean isEmpty() {
        return ranges.isEmpty();
    }

    public boolean isAll() {
        if (ranges.size() == 1) {
            Range<Long> range = ranges.get(0);
            if (!range.hasUpperBound() && !range.hasLowerBound()) {
                return true;
            }
        }
        return false;
    }

    private PartitionRange intersection(PartitionRange otherPartitionRange) {
        if (this.isAll()) return otherPartitionRange;
        if (otherPartitionRange.isAll()) return this;

        List<Range<Long>> temp = new ArrayList<>();
        for (Range<Long> range : this.ranges) {
            boolean intersectionSuccess = false;
            for (Range<Long> otherRange : otherPartitionRange.ranges) {
                Range<Long> oneRange = intersection(range, otherRange);
                if (oneRange != null) {
                    temp.add(oneRange);
                    intersectionSuccess = true;
                }
                else if (intersectionSuccess) {
                    break;
                }
            }
        }
        return new PartitionRange(temp);
    }

    private int compareLow(Range<Long> rangeA, Range<Long> rangeB) {
        boolean aHasLowerBound = rangeA.hasLowerBound(), bHasLowerBound = rangeB.hasLowerBound();
        if (!(aHasLowerBound || bHasLowerBound)) return 0;
        if (aHasLowerBound && bHasLowerBound) {
            long aCut = rangeA.lowerEndpoint(), bCut = rangeB.lowerEndpoint();
            int compare =  Long.compare(aCut, bCut);
            if (compare == 0) {
                BoundType aBoundType = rangeA.lowerBoundType(), bBoundType = rangeB.lowerBoundType();
                if (aBoundType == bBoundType) return 0;
                else return aBoundType == BoundType.CLOSED ? 1 : -1;
            }
            else {
                return -compare;
            }
        }
        else {
            return aHasLowerBound ? -1 : 1;
        }
    }

    public PartitionRange add(Range<Long> oneRange) {
        if (this.isAll()) {
            return this;
        }
        else if (this.isEmpty()) {
            ranges.add(oneRange);
            return this;
        }
        else {
            int size = ranges.size();
            for (int i = 0; i < size; i++) {
                Range<Long> range = ranges.get(i);
                int compare = compareLow(range, oneRange);
                if (compare == -1) {
                    ranges.add(i, oneRange);
                    break;
                }
            }

            if (ranges.size() == size) {
                Range<Long> lastRange = ranges.get(size - 1);
                if (lastRange.isConnected(oneRange)) {
                    ranges.set(size - 1, lastRange.span(oneRange));
                }
                else {
                    ranges.add(oneRange);
                }
            }
            else {
                boolean flag;
                int start = 0;
                do {
                    flag = false;
                    for (int i = start; i < ranges.size() - 1; i++) {
                        Range<Long> aRange = ranges.get(i);
                        Range<Long> bRange = ranges.get(i + 1);
                        if (aRange.isConnected(bRange)) {
                            ranges.set(i, aRange.span(bRange));
                            ranges.remove(i + 1);
                            start = i;
                            flag = true;
                            break;
                        }
                    }
                } while (flag);
            }
            return this;
        }
    }

    private PartitionRange union(PartitionRange otherPartitionRange) {
        if (otherPartitionRange.isEmpty()) return this;
        if (this.isAll()) return this;
        PartitionRange temp = this;
        for (Range<Long> range : otherPartitionRange.ranges) {
            temp = temp.add(range);
        }
        return temp;
    }

    private Range<Long> intersection(Range<Long> a, Range<Long> b) {
        try {
            return a.intersection(b);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public List<Range<Long>> getHasIntersectionRangeList(Range<Long> range) {
        List<Range<Long>> temp = new ArrayList<>();
        for (Range<Long> thisRange : ranges) {
            if (intersection(thisRange, range) != null) {
                temp.add(thisRange);
            }
        }
        return temp;
    }

    public List<Range<Long>> getRanges() {
        return ranges;
    }

    public long getLow() {
        return ranges.get(0).lowerEndpoint();
    }

    public long getUp() {
        return ranges.get(ranges.size() - 1).upperEndpoint();
    }

    public Set<Long> getPartitionCycleList(PartitionGrain grain, PartitionCycle firstCycle) {
        Set<Long> temp = new HashSet<>();
        for (Range<Long> range : ranges) {
            temp.addAll(getPartitionCycleList(range, grain, firstCycle));
        }
        return temp;
    }

    private List<Long> getPartitionCycleList(Range<Long> range, PartitionGrain grain, PartitionCycle firstCycle) {
        assert range.hasLowerBound() || range.hasUpperBound();
        long floorCycle, ceilCycle;
        if (range.hasLowerBound()) {
            long floor = range.lowerBoundType() == BoundType.OPEN ? range.lowerEndpoint() + 1 : range.lowerEndpoint();
            floorCycle = grain.getFloor(floor);
        }
        else {
            floorCycle = firstCycle.getFloor();
        }

        if (range.hasUpperBound()) {
            long ceil = range.upperBoundType() == BoundType.OPEN ? range.upperEndpoint() - 1 : range.upperEndpoint();
            ceilCycle = grain.getFloor(ceil);
        }
        else {
            ceilCycle = grain.getFloor(System.currentTimeMillis());
        }

        List<Long> cycleList = new ArrayList<>();

        long grainUnixTime = PartitionGrain.getMillis(grain.getGrain());

        for (long i = floorCycle; i <= ceilCycle; i += grainUnixTime) {
            cycleList.add(i);
        }

        return cycleList;
    }

}
