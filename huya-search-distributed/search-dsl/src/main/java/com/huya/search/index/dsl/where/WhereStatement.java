package com.huya.search.index.dsl.where;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.huya.search.index.dsl.where.expression.WhereExpression;
import com.huya.search.index.dsl.where.expression.builder.BuildLuceneQueryException;
import com.huya.search.index.meta.IntactMetaDefine;
import com.huya.search.partition.PartitionRange;
import org.apache.lucene.search.BooleanQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/23.
 */
public interface WhereStatement {

    WhereStatementType type();

    PartitionRange getPartitionRange();

    /**
     * 所属应用，比如taf
     * TODO: 重构至元数据管理
     *
     * @return
     */
    String getLogSvrApp();

    /**
     * 模块
     * TODO: 重构至元数据管理
     *
     * @return
     */
    String getLogSvrModule();

    /**
     * 类型
     * TODO: 重构至元数据管理
     *
     * @return
     */
    String getLogSvrType();

    BooleanQuery getQuery(IntactMetaDefine intactMetaDefine) throws BuildLuceneQueryException;

    /**
     * TODO: 重构至元数据管理
     *
     * @param intactMetaDefine
     * @param logSvrApp
     * @param logSvrModule
     * @param logSvrType
     * @return
     * @throws BuildLuceneQueryException
     */
    BooleanQuery getQuery(
            IntactMetaDefine intactMetaDefine,
            String logSvrApp,
            String logSvrModule,
            String logSvrType
    ) throws BuildLuceneQueryException;

    class WhereStatementSerializer extends Serializer<WhereStatement> {

        @Override
        public void write(Kryo kryo, Output output, WhereStatement object) {
            kryo.writeObject(output, object.type());

            if (object instanceof SingleWhereStatement) {
                kryo.writeObject(output, ((SingleWhereStatement)object).getExpression());
            }
            else {
                List<WhereStatement> statements = null;
                if (object instanceof AndWhereStatement) {
                    statements = ((AndWhereStatement) object).getStatements();

                } else if (object instanceof OrWhereStatement) {
                    statements = ((OrWhereStatement) object).getStatements();
                }
                assert statements != null;
                kryo.writeObject(output, statements.size());
                statements.forEach(whereStatement -> kryo.writeObject(output, whereStatement));
            }
        }

        @Override
        public WhereStatement read(Kryo kryo, Input input, Class<WhereStatement> type) {
            WhereStatementType whereStatementType = kryo.readObject(input, WhereStatementType.class);
            if (whereStatementType == WhereStatementType.SINGLE) {
                WhereExpression whereExpression = kryo.readObject(input, WhereExpression.class);
                return SingleWhereStatement.create(whereExpression);
            }
            else {
                int size = kryo.readObject(input, Integer.class);
                List<WhereStatement> statements = new ArrayList<>();
                for (int i = 0; i < size; i++) {
                    statements.add(kryo.readObject(input, WhereStatement.class));
                }
                if (whereStatementType == WhereStatementType.AND) {
                    return AndWhereStatement.create(statements);
                }
                else {
                    return OrWhereStatement.create(statements);
                }
            }
        }

    }

}
