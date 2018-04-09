package com.huya.search.index.dsl.where.expression.timestamp;

public class TimestampFactory {

    public static TimestampExplain getExplain(String value) {
        int length = value.length();
        switch (length) {
            case 19 :
            case 14 : return new UnixTimePointExplain(value);
            case 16 : return new YYYY_MM_DD_HH_mmExplain(value);
            case 12 : return new YYYYMMDDHHmmExplain(value);
            case 13 : return new YYYY_MM_DD_HHExplain(value);
            case 10 : return value.contains("-") ? new YYYY_MM_DDExplain(value) : new YYYYMMDDHHExplain(value);
            case 8  : return new YYYYMMDDExplain(value);
            default: throw new RuntimeException("no support timestamp format");
        }
    }
}
