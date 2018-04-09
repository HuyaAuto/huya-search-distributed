package com.huya.search.index.meta;

import org.apache.lucene.search.SortField;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/9.
 */
public enum IndexFieldType {
    Integer,
    Long,
    Float,
    Double,
    String,
    Text,
    Date;

    public static IndexFieldType getType(String string) {
        switch (string) {
            case "integer" : return Integer;
            case "long" : return Long;
            case "float" : return Float;
            case "double" : return Double;
            case "string" : return String;
            case "text" : return Text;
            case "date" : return Date;
            default: return String;
        }
    }

    private static final Map<IndexFieldType, SortField.Type> typeMap = new HashMap<>();

    static {
        typeMap.put(IndexFieldType.Integer, SortField.Type.INT);
        typeMap.put(IndexFieldType.Long, SortField.Type.LONG);
        typeMap.put(IndexFieldType.Float, SortField.Type.FLOAT);
        typeMap.put(IndexFieldType.Double, SortField.Type.DOUBLE);
        typeMap.put(IndexFieldType.Date, SortField.Type.LONG);
        typeMap.put(IndexFieldType.String, SortField.Type.STRING);
        typeMap.put(IndexFieldType.Text, SortField.Type.STRING);
    }

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

    public SortField.Type getSortType() {
        return typeMap.get(this);
    }

    public Object matchOrConvertType(Object object) {
        try {
            switch (this) {
                case Integer:
                    if (object instanceof Integer) {
                        return object;
                    } else if (object instanceof String) {
                        return java.lang.Integer.parseInt((String) object);
                    }
                    break;
                case Long:
                case Date:
                    if (object instanceof Long) {
                        return object;
                    } else if (object instanceof Integer) {
                        return (long) (int) object;
                    } else if (object instanceof String) {
                        return java.lang.Long.parseLong((String) object);
                    }
                    break;
                case Float:
                    if (object instanceof Float) {
                        return object;
                    } else if (object instanceof String) {
                        return java.lang.Float.parseFloat((String) object);
                    }
                    break;
                case Double:
                    if (object instanceof Double) {
                        return object;
                    } else if (object instanceof Float) {
                        return (double) (float) object;
                    } else if (object instanceof String) {
                        return java.lang.Double.parseDouble((String) object);
                    }
                    break;
                case String:
                    if (object instanceof String) {
                        return object;
                    } else {
                        return java.lang.String.valueOf(object);
                    }
                case Text:
                    if (object instanceof String) {
                        return object;
                    } else {
                        return java.lang.String.valueOf(object);
                    }
            }
        } catch (Exception e) {
            throw new TypeNoMatchException(this.toString() + " is not match value : " + object.toString(), e);
        }
        throw new TypeNoMatchException(this.toString() + " is not match value : " + object.toString());
    }

    public static class TypeNoMatchException extends RuntimeException {

        private TypeNoMatchException(java.lang.String message) {
            super(message);
        }

        private TypeNoMatchException(java.lang.String message, Throwable cause) {
            super(message, cause);
        }
    }

}
