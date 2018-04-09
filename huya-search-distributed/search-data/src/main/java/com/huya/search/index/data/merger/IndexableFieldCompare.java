package com.huya.search.index.data.merger;

import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.SortField;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/10.
 */
public class IndexableFieldCompare {

    public static int compare(SortField.Type type, IndexableField aField, IndexableField bField, int reverse) {
        if (aField == null && bField == null) return 0;
        else if (aField == null) return -reverse;
        else if (bField == null) return reverse;

        int c = 0;
        switch (type) {
            case INT:
                c = compareInt(aField, bField);
                break;
            case LONG:
                c = compareLong(aField, bField);
                break;
            case FLOAT:
                c = compareFloat(aField, bField);
                break;
            case DOUBLE:
                c = compareDouble(aField, bField);
                break;
            case STRING:
                c = compareString(aField, bField);
                break;
        }
        return c != 0 ? reverse * c : 0;
    }

    private static int compareInt(IndexableField a, IndexableField b) {
        Number aNumber = a.numericValue();
        Number bNumber = b.numericValue();
        if (aNumber == null && bNumber == null) {
            return compareString(a, b);
        }
        return Integer.compare(
                (int)aNumber,
                (int)bNumber
        );
    }

    private static int compareLong(IndexableField a, IndexableField b) {
        Number aNumber = a.numericValue();
        Number bNumber = b.numericValue();
        if (aNumber == null && bNumber == null) {
            return compareString(a, b);
        }
        return Long.compare(
                (long)a.numericValue(),
                (long)b.numericValue()
        );
    }

    private static int compareFloat(IndexableField a, IndexableField b) {
        Number aNumber = a.numericValue();
        Number bNumber = b.numericValue();
        if (aNumber == null && bNumber == null) {
            return compareString(a, b);
        }
        return Float.compare(
                (float)a.numericValue(),
                (float)b.numericValue()
        );
    }


    private static int compareDouble(IndexableField a, IndexableField b) {
        Number aNumber = a.numericValue();
        Number bNumber = b.numericValue();
        if (aNumber == null && bNumber == null) {
            return compareString(a, b);
        }
        return Double.compare(
                (double)a.numericValue(),
                (double)b.numericValue()
        );
    }

    private static int compareString(IndexableField a, IndexableField b) {
        return a.stringValue().compareTo(
                b.stringValue()
        );
    }
}
