package com.huya.search.index.dsl.parse;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.huya.search.index.dsl.parse.SearchSQLParser.*;

/**
 * Created by zhangyiqun1@yy.com on 2017/9/7.
 */
public class SearchSQLComponentFill implements ComponentFill {

    public static SearchSQLComponentFill newInstance(String dsl) {
        return new SearchSQLComponentFill(dsl);
    }

    private String dsl;

    private ComponentFill componentFill;

    private SearchSQLParser parser;

    protected SearchSQLComponentFill(String dsl) {
        this.dsl = dsl;
        initParse();
    }

    @Override
    public void fillComponent(QueryComponent queryComponent) {
        parserSimpleSQL();
        componentFill.fillComponent(queryComponent);
    }

    private void initParse() {
        try {
            InputStream stream = new ByteArrayInputStream(dsl.getBytes(StandardCharsets.UTF_8.name()));
            CharStream input = CharStreams.fromStream(stream);
            SearchSQLLexer lexer = new SearchSQLLexer(input);
            TokenStream tokenStream = new CommonTokenStream(lexer);
            parser = new SearchSQLParser(tokenStream);
        } catch (IOException e) {
            throw new ParseExpection("init parse error", e);
        }
    }

    private void parserSimpleSQL() {
        List<Sql_stmt_listContext> sql_stmt_listContexts = parser.parse().sql_stmt_list();

        if (sql_stmt_listContexts.size() != 1) throw new ParseExpection("Currently only supported single sql");

        Sql_stmt_listContext sql_stmt_listContext = sql_stmt_listContexts.get(0);
        List<Sql_stmtContext> sql_stmtContexts = sql_stmt_listContext.sql_stmt();


        if (sql_stmt_listContexts.size() != 1) throw new ParseExpection("Currently only supported single sql");

        Sql_stmtContext sql_stmtContext = sql_stmtContexts.get(0);
        parserSqlStmtContext(sql_stmtContext);
    }

    private void parserSqlStmtContext(Sql_stmtContext sql_stmtContext) {
        Simple_select_stmtContext sssc = sql_stmtContext.simple_select_stmt();
        Cursor_select_stmtContext cssc = sql_stmtContext.cursor_select_stmt();
        Cursor_stmtContext        csc  = sql_stmtContext.cursor_stmt();

        if (sssc == null && cssc == null && csc == null) throw new ParseExpection("parse error, unknown stmt");

        if (sssc != null) parserSimpleSelect(sssc);
        else if (cssc != null)  parserCursorSelect(cssc);
        else parserCursor(csc);
    }

    private void parserSimpleSelect(Simple_select_stmtContext sssc) {
        this.componentFill = SimpleSelectComponentFill.newInstance(sssc);
    }

    private void parserCursorSelect(Cursor_select_stmtContext cssc) {
        throw new ParseExpection("no support cursor select");
    }

    private void parserCursor(Cursor_stmtContext csc) {
        throw new ParseExpection("np support cursor");
    }
}
