package com.huya.search.util;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


/**
 * Created by zhangyiqun1@yy.com on 2017/8/7.
 */
public class JodaUtil {

    public static final String YYYY_MM_DD_HH_MM_SS_F_FORMATTER_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String YYYY_MM_DD_HH_MM_SS_FORMATTER_PATTERN   = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYYMMDDHHMMSS_FORMATTER_PATTERN        = "yyyyMMddHHmmss";
    public static final String YYYY_MM_DD_HH_MM_FORMATTER_PATTERN      = "yyyy-MM-dd HH:mm";
    public static final String YYYYMMDDHHMM_FORMATTER_PATTERN          = "yyyyMMddHHmm";
    public static final String YYYY_MM_DD_HH_FORMATTER_PATTERN         = "yyyy-MM-dd HH";
    public static final String YYYYMMDDHH_FORMATTER_PATTERN            = "yyyyMMddHH";
    public static final String YYYY_MM_DD_FORMATTER_PATTERN            = "yyyy-MM-dd";
    public static final String YYYYMMDD_FORMATTER_PATTERN              = "yyyyMMdd";
    public static final String YYYY_FORMATTER_PATTERN                  = "yyyy";


    public static final DateTimeFormatter YYYY_MM_DD_HH_MM_SS_F_FORMATTER = DateTimeFormat.forPattern(YYYY_MM_DD_HH_MM_SS_F_FORMATTER_PATTERN);
    public static final DateTimeFormatter YYYY_MM_DD_HH_MM_SS_FORMATTER = DateTimeFormat.forPattern(YYYY_MM_DD_HH_MM_SS_FORMATTER_PATTERN);
    public static final DateTimeFormatter YYYYMMDDHHMMSS_FORMATTER = DateTimeFormat.forPattern(YYYYMMDDHHMMSS_FORMATTER_PATTERN);
    public static final DateTimeFormatter YYYY_MM_DD_HH_MM_FORMATTER = DateTimeFormat.forPattern(YYYY_MM_DD_HH_MM_FORMATTER_PATTERN);
    public static final DateTimeFormatter YYYYMMDDHHMM_FORMATTER = DateTimeFormat.forPattern(YYYYMMDDHHMM_FORMATTER_PATTERN);
    public static final DateTimeFormatter YYYY_MM_DD_HH_FORMATTER = DateTimeFormat.forPattern(YYYY_MM_DD_HH_FORMATTER_PATTERN);
    public static final DateTimeFormatter YYYYMMDDHH_FORMATTER = DateTimeFormat.forPattern(YYYYMMDDHH_FORMATTER_PATTERN);
    public static final DateTimeFormatter YYYY_MM_DD_FORMATTER = DateTimeFormat.forPattern(YYYY_MM_DD_FORMATTER_PATTERN);
    public static final DateTimeFormatter YYYYMMDD_FORMATTER = DateTimeFormat.forPattern(YYYYMMDD_FORMATTER_PATTERN);
    public static final DateTimeFormatter YYYY_FORMATTER = DateTimeFormat.forPattern(YYYY_FORMATTER_PATTERN);

    public static long getUnixTime(String timeStamp) {
        return getFormatterByStr(timeStamp).parseDateTime(timeStamp).getMillis();
    }

    public static DateTimeFormatter getFormatterByStr(String timestamp) {
        int length = timestamp.length();
        switch (length) {
            case 19 : return YYYY_MM_DD_HH_MM_SS_FORMATTER;
            case 16 : return YYYY_MM_DD_HH_MM_FORMATTER;
            case 14 : return YYYYMMDDHHMMSS_FORMATTER;
            case 13 : return YYYY_MM_DD_HH_FORMATTER;
            case 12 : return YYYYMMDDHHMM_FORMATTER;
            case 10 : return timestamp.contains("-")
                    ? YYYY_MM_DD_FORMATTER
                    : YYYYMMDDHH_FORMATTER;
            case 8  : return YYYYMMDD_FORMATTER;
            default : return YYYY_MM_DD_HH_MM_SS_F_FORMATTER;
        }
    }

}
