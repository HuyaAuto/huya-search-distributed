package com.huya.search.index.dsl.where.expression.builder;

import com.huya.search.index.meta.IndexFieldType;
import com.huya.search.index.meta.IntactMetaDefine;
import com.huya.search.index.meta.MetaDefine;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/6.
 */
public class TypeBuilderFactory {

    public static LuceneQueryBuilder getBuilder(IndexFieldType type, IntactMetaDefine intactMetaDefine) {
        switch (type) {
            case Integer: return IntegerBuilder.create();
            case Long:    return LongBuilder.create();
            case Float:   return FloatBuilder.create(intactMetaDefine);
            case Double:  return DoubleBuilder.create(intactMetaDefine);
            case Date:    return DateBuilder.create();
            case String:  return StringBuilder.create(intactMetaDefine);
            case Text:    return TextBuilder.create(intactMetaDefine);
            default: throw new BuildLuceneQueryException("no support type to get type builder");
        }
    }
}
