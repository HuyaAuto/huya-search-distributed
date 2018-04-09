package com.huya.search.index.dsl.parse;

import static com.huya.search.index.dsl.parse.SearchSQLParser.*;


/**
 * Created by zhangyiqun1@yy.com on 2017/9/11.
 */
public class LiteralValueParse {

    private static boolean isString(Literal_valueContext lvc) {
        return lvc.STRING_LITERAL() != null;
    }

    private static boolean isNumber(Literal_valueContext lvc) {
        return lvc.NUMERIC_LITERAL() != null;
    }

    private static boolean isNull(Literal_valueContext lvc) {
        return lvc.K_NULL() != null;
    }

    private static boolean isCurrentTime(Literal_valueContext lvc) {
        return lvc.K_CURRENT_TIME() != null;
    }

    private static boolean isCurrentDate(Literal_valueContext lvc) {
        return lvc.K_CURRENT_DATE() != null;
    }

    private static boolean isCurrentTimestamp(Literal_valueContext lvc) {
        return lvc.K_CURRENT_TIMESTAMP() != null;
    }

    private static String removeAPO(String value) {
        return value.substring(1, value.length()-1);
    }

    public static String getString(Literal_valueContext lvc) {
        if (isString(lvc)) {
            String value = lvc.STRING_LITERAL().getText();
            return removeAPO(value);
        }
        else if (isCurrentDate(lvc) || isCurrentTime(lvc) || isCurrentTimestamp(lvc)) {
            return String.valueOf(System.currentTimeMillis());
        }
        else if (isNumber(lvc)) {
            return lvc.NUMERIC_LITERAL().getText();
        }
        else if (isNull(lvc)) {
            return "NULL";
        }
        throw new ParseExpection("literal value no support to string error");
    }

    public static int getInt(Literal_valueContext lvc) {
        if (isNumber(lvc)) {
            return Integer.parseInt(lvc.NUMERIC_LITERAL().getText());
        }
        else if (isString(lvc)) {
            String value = lvc.STRING_LITERAL().getText();
            return Integer.parseInt(removeAPO(value));
        }
        else if (isNull(lvc)) {
            return 0;
        }
        throw new ParseExpection("literal value no support to int error");
    }

    public static long getLong(Literal_valueContext lvc) {
        if (isNumber(lvc)) {
            return Long.parseLong(lvc.NUMERIC_LITERAL().getText());
        }
        else if (isString(lvc)) {
            String value = lvc.STRING_LITERAL().getText();
            return Long.parseLong(removeAPO(value));
        }
        else if (isNull(lvc)) {
            return 0L;
        }
        else if (isCurrentDate(lvc) || isCurrentTime(lvc) || isCurrentTimestamp(lvc)) {
            return System.currentTimeMillis();
        }
        throw new ParseExpection("literal value no support to long error");
    }

    public static float getFloat(Literal_valueContext lvc) {
        if (isNumber(lvc)) {
            return Float.parseFloat(lvc.NUMERIC_LITERAL().getText());
        }
        else if (isString(lvc)) {
            String value = lvc.STRING_LITERAL().getText();
            return Float.parseFloat(removeAPO(value));
        }
        else if (isNull(lvc)) {
            return 0.0f;
        }
        else if (isCurrentDate(lvc) || isCurrentTime(lvc) || isCurrentTimestamp(lvc)) {
            return System.currentTimeMillis();
        }
        throw new ParseExpection("literal value no support to float error");
    }

    public static double getDouble(Literal_valueContext lvc) {
        if (isNumber(lvc)) {
            return Double.parseDouble(lvc.NUMERIC_LITERAL().getText());
        }
        else if (isString(lvc)) {
            String value = lvc.STRING_LITERAL().getText();
            return Double.parseDouble(removeAPO(value));
        }
        else if (isNull(lvc)) {
            return 0.0d;
        }
        else if (isCurrentDate(lvc) || isCurrentTime(lvc) || isCurrentTimestamp(lvc)) {
            return System.currentTimeMillis();
        }
        throw new ParseExpection("literal value no support to double error");
    }


}
