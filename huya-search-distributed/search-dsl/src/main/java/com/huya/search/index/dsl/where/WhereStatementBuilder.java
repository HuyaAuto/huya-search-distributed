package com.huya.search.index.dsl.where;

import com.huya.search.index.dsl.where.expression.WhereExpression;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/28.
 */
public class WhereStatementBuilder {


    public static class Single {
        public static WhereStatement build(WhereExpression expression) {
            return SingleWhereStatement.create(expression);
        }
    }

    public static class And {
        public static WhereStatement build(List<WhereStatement> statements) {
            return AndWhereStatement.create(statements);
        }
    }

    public static class Or {
        public static WhereStatement build(List<WhereStatement> statements) {
            return OrWhereStatement.create(statements);
        }
    }

    public static class AndBuilder {

        public static AndBuilder newInstance() {
            return new AndBuilder();
        }

        private List<WhereStatement> list = new ArrayList<>();

        public AndBuilder addWhereStatement(WhereStatement whereStatement) {
            list.add(whereStatement);
            return this;
        }

        public AndBuilder addWhereExpress(WhereExpression whereExpression) {
            list.add(Single.build(whereExpression));
            return this;
        }

        public WhereStatement build() {
            return And.build(this.list);
        }
    }

    public static class OrBuilder {

        public static OrBuilder newInstance() {
            return new OrBuilder();
        }

        private List<WhereStatement> list = new ArrayList<>();

        public OrBuilder addWhereStatement(WhereStatement whereStatement) {
            list.add(whereStatement);
            return this;
        }

        public OrBuilder addWhereExpress(WhereExpression whereExpression) {
            list.add(Single.build(whereExpression));
            return this;
        }

        public WhereStatement build() {
            return Or.build(this.list);
        }
    }

}
