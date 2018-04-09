package com.huya.search.data;

import com.huya.search.index.meta.IndexFieldType;
import org.apache.commons.lang3.StringUtils;

import java.util.Random;

public class RandomData {

    private static final Random random = new Random();

    private static final RandomStringFeature DEFAULT_FEATURE
            = RandomStringFeature.newInstance(10, 4, 6);

    private RandomStringFeature thisFeature;

    public RandomData(RandomStringFeature thisFeature) {
        this.thisFeature = thisFeature;
    }

    public RandomData() {
        this.thisFeature = DEFAULT_FEATURE;
    }

    public Object getObject(IndexFieldType type) {
        switch (type) {
            case Integer: return getInt();
            case Long:
            case Date:    return getLong();
            case Float:   return getFloat();
            case Double:  return getDouble();
            case String:  return getString();
            case Text:    return getText();
            default:      return getString();
        }
    }

    public int getInt() {
        return random.nextInt();
    }

    public long getLong() {
        return random.nextLong();
    }

    public float getFloat() {
        return random.nextFloat();
    }

    public double getDouble() {
        return random.nextDouble();
    }

    public String getString() {
        return getString(thisFeature.stringMaxStringLen);
    }

    private String getString(int maxStringLen) {
        return getString(0, maxStringLen);
    }

    private String getString(int blankNum, int maxStringLen) {
        String[] temp = new String[blankNum + 1];
        for (int i = 0; i < blankNum + 1; i++) {
            temp[i] = getKey(maxStringLen);
        }
        return StringUtils.join(temp, " ");
    }

    public String getText() {
        return getText(thisFeature.textBlankNum, thisFeature.textMaxStringLen);
    }

    private String getText(int blankNum, int maxStringLen) {
        return getString(blankNum, maxStringLen);
    }

    private String getKey(int maxStringLen) {
        int i = random.nextInt(maxStringLen) + 1;
        StringBuilder builder = new StringBuilder();
        for (int j = 0; j < i; j++) {
            char c = (char) ('a' + random.nextInt(24));
            builder.append(c);
        }
        return builder.toString();
    }

    public static class RandomStringFeature {
        private int stringMaxStringLen;

        private int textBlankNum;
        private int textMaxStringLen;

        private RandomStringFeature(int stringMaxStringLen, int textBlankNum, int textMaxStringLen) {
            this.stringMaxStringLen = stringMaxStringLen;
            this.textBlankNum = textBlankNum;
            this.textMaxStringLen = textMaxStringLen;
        }

        public static RandomStringFeature newInstance(int stringMaxStringLen, int textBlankNum, int textMaxStringLen) {
            return new RandomStringFeature(stringMaxStringLen, textBlankNum, textMaxStringLen);
        }

    }
}
