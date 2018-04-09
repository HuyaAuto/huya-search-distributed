package com.huya.search.index.dsl.where;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.huya.search.index.dsl.where.expression.builder.BuildLuceneQueryException;
import com.huya.search.index.meta.IntactMetaDefine;
import com.huya.search.partition.PartitionRange;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/23.
 */
public class OrWhereStatement implements WhereStatement {

    private List<WhereStatement> statements;

    public static WhereStatement create(List<WhereStatement> statements) {
        return new OrWhereStatement(statements);
    }

    private OrWhereStatement(List<WhereStatement> statements) {
        this.statements = statements;
    }

    @Override
    public WhereStatementType type() {
        return WhereStatementType.OR;
    }

    public List<WhereStatement> getStatements() {
        return statements;
    }

    @Override
    public PartitionRange getPartitionRange() {
        assert statements.size() >= 1;
        if (statements.size() == 1) {
            return statements.get(0).getPartitionRange();
        }
        else {
            PartitionRange range = statements.get(0).getPartitionRange();
            for (int i=1; i<statements.size(); i++) {
                range = PartitionRange.union(range, statements.get(i).getPartitionRange());
            }
            return range;
        }
    }

    @Override
    public String getLogSvrApp() {
        for (WhereStatement statement : statements) {
            if (statement.getLogSvrApp() != null) {
                return statement.getLogSvrApp();
            }
        }
        return null;
    }

    @Override
    public String getLogSvrModule() {
        for (WhereStatement statement : statements) {
            if (statement.getLogSvrModule() != null) {
                return statement.getLogSvrModule();
            }
        }
        return null;
    }

    @Override
    public String getLogSvrType() {
        for (WhereStatement statement : statements) {
            if (statement.getLogSvrType() != null) {
                return statement.getLogSvrType();
            }
        }
        return null;
    }

    @Override
    public BooleanQuery getQuery(IntactMetaDefine intactMetaDefine) throws BuildLuceneQueryException {
        return getQuery(intactMetaDefine, getLogSvrApp(), getLogSvrModule(), getLogSvrType());
    }

    @Override
    public BooleanQuery getQuery(IntactMetaDefine intactMetaDefine, String logSvrApp, String logSvrModule, String logSvrType) throws BuildLuceneQueryException {
        assert statements.size() >= 1;
        if (statements.size() == 1) {
            return statements.get(0).getQuery(intactMetaDefine);
        }
        else {
            BooleanQuery.Builder bqb = new BooleanQuery.Builder();
            for (WhereStatement statement : statements) {
                bqb.add(statement.getQuery(intactMetaDefine, logSvrApp, logSvrModule, logSvrType), BooleanClause.Occur.SHOULD);
            }
            return bqb.build();
        }
    }

    public static class OrWhereStatementSerializer extends Serializer<OrWhereStatement> {

        @Override
        public void write(Kryo kryo, Output output, OrWhereStatement object) {
            int size = object.statements.size();
            kryo.writeObject(output, size);
            object.statements.forEach(whereStatement -> kryo.writeObject(output, whereStatement));
        }

        @Override
        public OrWhereStatement read(Kryo kryo, Input input, Class<OrWhereStatement> type) {
            int size = kryo.readObject(input, Integer.class);
            List<WhereStatement> statements = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                statements.add(kryo.readObject(input, WhereStatement.class));
            }
            return new OrWhereStatement(statements);
        }

    }

}
