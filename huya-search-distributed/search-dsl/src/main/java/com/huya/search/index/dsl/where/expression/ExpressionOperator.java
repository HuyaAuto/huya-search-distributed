package com.huya.search.index.dsl.where.expression;

import com.huya.search.index.dsl.parse.ParseExpection;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/24.
 */
public enum ExpressionOperator {

    EQUAL("="),
    NOT("!="),
    MT(">"),
    MTOE(">="),
    LT("<"),
    LTOE("<="),
    LIKE("like");

    private String operator;

    ExpressionOperator(String operator) {
        this.operator = operator;
    }

    public static ExpressionOperator operator(String str) {
        switch (str) {
            case "=" : return EQUAL;
            case "!=" : return NOT;
            case ">" : return MT;
            case ">=" : return MTOE;
            case "<" : return LT;
            case "<=" : return LTOE;
            case "like" : return LIKE;
            default: throw new ParseExpection("no support operator");
        }
    }

    @Override
    public String toString() {
        return operator;
    }
}
