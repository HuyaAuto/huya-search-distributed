package com.huya.search.index.dsl.where.expression;

import com.huya.search.IndexSettings;
import com.huya.search.index.analyzer.DynamicAnalyzer;
import com.huya.search.index.dsl.where.expression.builder.BuildLuceneQueryException;
import com.huya.search.index.dsl.where.expression.builder.TypeBuilderFactory;
import com.huya.search.index.meta.IndexFeatureType;
import com.huya.search.index.meta.IntactMetaDefine;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/28.
 */
public abstract class BooleanExpression implements WhereExpression {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private String field;

    private ExpressionOperator operator;

    private String value;

    private Query query;

    public BooleanExpression(String field, ExpressionOperator operator, String value) {
        this.field = field;
        this.operator = operator;
        this.value = value;
    }

    @Override
    public String getField() {
        return field;
    }

    @Override
    public ExpressionOperator getOperator() {
        return operator;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void explain() {}

    @Override
    public boolean aboutTimestamp() {
        return false;
    }

    @Override
    public Query getQuery(IntactMetaDefine intactMetaDefine, String logSvrApp, String logSvrModule, String logSvrType) throws BuildLuceneQueryException {
        if (query == null) {
            logger.info("Query查询=========================================================");
            StringBuilder sb = new StringBuilder();
            sb.append("logSvrApp:" + logSvrApp + ", logSvrModule:" + logSvrModule + ", logSvrType:" + logSvrType);
            IndexFeatureType type = intactMetaDefine.getIndexFieldFeatureType(getField());
            if (StringUtils.isNotEmpty(logSvrApp) && StringUtils.isNotEmpty(logSvrModule) && StringUtils.isNotEmpty(logSvrType)) {
                DynamicAnalyzer dynamicAnalyzer = IndexSettings.getDynamicAnalyzer(logSvrApp, logSvrModule, logSvrType);
                if (dynamicAnalyzer != null) {
                    sb.append(", dynamicAnalyzer:" + dynamicAnalyzer.toString());
                    type.setAnalyzer(dynamicAnalyzer);
                }
            }
            logger.info(sb.toString());
            query = TypeBuilderFactory.getBuilder(type.getType(), intactMetaDefine).build(field, operator, value, type);
        }
        return query;
    }

}
