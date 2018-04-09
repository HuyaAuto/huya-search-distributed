// Generated from /Users/geekcat/huya-search/core/src/main/resources/SearchSQL.g4 by ANTLR 4.7
package com.huya.search.index.dsl.parse;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SearchSQLParser}.
 */
public interface SearchSQLListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link SearchSQLParser#parse}.
	 * @param ctx the parse tree
	 */
	void enterParse(SearchSQLParser.ParseContext ctx);
	/**
	 * Exit a parse tree produced by {@link SearchSQLParser#parse}.
	 * @param ctx the parse tree
	 */
	void exitParse(SearchSQLParser.ParseContext ctx);
	/**
	 * Enter a parse tree produced by {@link SearchSQLParser#error}.
	 * @param ctx the parse tree
	 */
	void enterError(SearchSQLParser.ErrorContext ctx);
	/**
	 * Exit a parse tree produced by {@link SearchSQLParser#error}.
	 * @param ctx the parse tree
	 */
	void exitError(SearchSQLParser.ErrorContext ctx);
	/**
	 * Enter a parse tree produced by {@link SearchSQLParser#sql_stmt_list}.
	 * @param ctx the parse tree
	 */
	void enterSql_stmt_list(SearchSQLParser.Sql_stmt_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link SearchSQLParser#sql_stmt_list}.
	 * @param ctx the parse tree
	 */
	void exitSql_stmt_list(SearchSQLParser.Sql_stmt_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link SearchSQLParser#sql_stmt}.
	 * @param ctx the parse tree
	 */
	void enterSql_stmt(SearchSQLParser.Sql_stmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link SearchSQLParser#sql_stmt}.
	 * @param ctx the parse tree
	 */
	void exitSql_stmt(SearchSQLParser.Sql_stmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link SearchSQLParser#cursor_select_stmt}.
	 * @param ctx the parse tree
	 */
	void enterCursor_select_stmt(SearchSQLParser.Cursor_select_stmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link SearchSQLParser#cursor_select_stmt}.
	 * @param ctx the parse tree
	 */
	void exitCursor_select_stmt(SearchSQLParser.Cursor_select_stmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link SearchSQLParser#cursor_stmt}.
	 * @param ctx the parse tree
	 */
	void enterCursor_stmt(SearchSQLParser.Cursor_stmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link SearchSQLParser#cursor_stmt}.
	 * @param ctx the parse tree
	 */
	void exitCursor_stmt(SearchSQLParser.Cursor_stmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link SearchSQLParser#simple_select_stmt}.
	 * @param ctx the parse tree
	 */
	void enterSimple_select_stmt(SearchSQLParser.Simple_select_stmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link SearchSQLParser#simple_select_stmt}.
	 * @param ctx the parse tree
	 */
	void exitSimple_select_stmt(SearchSQLParser.Simple_select_stmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link SearchSQLParser#result_column}.
	 * @param ctx the parse tree
	 */
	void enterResult_column(SearchSQLParser.Result_columnContext ctx);
	/**
	 * Exit a parse tree produced by {@link SearchSQLParser#result_column}.
	 * @param ctx the parse tree
	 */
	void exitResult_column(SearchSQLParser.Result_columnContext ctx);
	/**
	 * Enter a parse tree produced by {@link SearchSQLParser#table_name}.
	 * @param ctx the parse tree
	 */
	void enterTable_name(SearchSQLParser.Table_nameContext ctx);
	/**
	 * Exit a parse tree produced by {@link SearchSQLParser#table_name}.
	 * @param ctx the parse tree
	 */
	void exitTable_name(SearchSQLParser.Table_nameContext ctx);
	/**
	 * Enter a parse tree produced by {@link SearchSQLParser#group_column}.
	 * @param ctx the parse tree
	 */
	void enterGroup_column(SearchSQLParser.Group_columnContext ctx);
	/**
	 * Exit a parse tree produced by {@link SearchSQLParser#group_column}.
	 * @param ctx the parse tree
	 */
	void exitGroup_column(SearchSQLParser.Group_columnContext ctx);
	/**
	 * Enter a parse tree produced by {@link SearchSQLParser#column_name}.
	 * @param ctx the parse tree
	 */
	void enterColumn_name(SearchSQLParser.Column_nameContext ctx);
	/**
	 * Exit a parse tree produced by {@link SearchSQLParser#column_name}.
	 * @param ctx the parse tree
	 */
	void exitColumn_name(SearchSQLParser.Column_nameContext ctx);
	/**
	 * Enter a parse tree produced by {@link SearchSQLParser#function_name}.
	 * @param ctx the parse tree
	 */
	void enterFunction_name(SearchSQLParser.Function_nameContext ctx);
	/**
	 * Exit a parse tree produced by {@link SearchSQLParser#function_name}.
	 * @param ctx the parse tree
	 */
	void exitFunction_name(SearchSQLParser.Function_nameContext ctx);
	/**
	 * Enter a parse tree produced by {@link SearchSQLParser#column_alias}.
	 * @param ctx the parse tree
	 */
	void enterColumn_alias(SearchSQLParser.Column_aliasContext ctx);
	/**
	 * Exit a parse tree produced by {@link SearchSQLParser#column_alias}.
	 * @param ctx the parse tree
	 */
	void exitColumn_alias(SearchSQLParser.Column_aliasContext ctx);
	/**
	 * Enter a parse tree produced by {@link SearchSQLParser#limit}.
	 * @param ctx the parse tree
	 */
	void enterLimit(SearchSQLParser.LimitContext ctx);
	/**
	 * Exit a parse tree produced by {@link SearchSQLParser#limit}.
	 * @param ctx the parse tree
	 */
	void exitLimit(SearchSQLParser.LimitContext ctx);
	/**
	 * Enter a parse tree produced by {@link SearchSQLParser#any_name}.
	 * @param ctx the parse tree
	 */
	void enterAny_name(SearchSQLParser.Any_nameContext ctx);
	/**
	 * Exit a parse tree produced by {@link SearchSQLParser#any_name}.
	 * @param ctx the parse tree
	 */
	void exitAny_name(SearchSQLParser.Any_nameContext ctx);
	/**
	 * Enter a parse tree produced by {@link SearchSQLParser#order_column}.
	 * @param ctx the parse tree
	 */
	void enterOrder_column(SearchSQLParser.Order_columnContext ctx);
	/**
	 * Exit a parse tree produced by {@link SearchSQLParser#order_column}.
	 * @param ctx the parse tree
	 */
	void exitOrder_column(SearchSQLParser.Order_columnContext ctx);
	/**
	 * Enter a parse tree produced by {@link SearchSQLParser#function_expr}.
	 * @param ctx the parse tree
	 */
	void enterFunction_expr(SearchSQLParser.Function_exprContext ctx);
	/**
	 * Exit a parse tree produced by {@link SearchSQLParser#function_expr}.
	 * @param ctx the parse tree
	 */
	void exitFunction_expr(SearchSQLParser.Function_exprContext ctx);
	/**
	 * Enter a parse tree produced by {@link SearchSQLParser#where_expr}.
	 * @param ctx the parse tree
	 */
	void enterWhere_expr(SearchSQLParser.Where_exprContext ctx);
	/**
	 * Exit a parse tree produced by {@link SearchSQLParser#where_expr}.
	 * @param ctx the parse tree
	 */
	void exitWhere_expr(SearchSQLParser.Where_exprContext ctx);
	/**
	 * Enter a parse tree produced by {@link SearchSQLParser#literal_value}.
	 * @param ctx the parse tree
	 */
	void enterLiteral_value(SearchSQLParser.Literal_valueContext ctx);
	/**
	 * Exit a parse tree produced by {@link SearchSQLParser#literal_value}.
	 * @param ctx the parse tree
	 */
	void exitLiteral_value(SearchSQLParser.Literal_valueContext ctx);
	/**
	 * Enter a parse tree produced by {@link SearchSQLParser#current_operator}.
	 * @param ctx the parse tree
	 */
	void enterCurrent_operator(SearchSQLParser.Current_operatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link SearchSQLParser#current_operator}.
	 * @param ctx the parse tree
	 */
	void exitCurrent_operator(SearchSQLParser.Current_operatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link SearchSQLParser#unary_operator}.
	 * @param ctx the parse tree
	 */
	void enterUnary_operator(SearchSQLParser.Unary_operatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link SearchSQLParser#unary_operator}.
	 * @param ctx the parse tree
	 */
	void exitUnary_operator(SearchSQLParser.Unary_operatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link SearchSQLParser#keyword}.
	 * @param ctx the parse tree
	 */
	void enterKeyword(SearchSQLParser.KeywordContext ctx);
	/**
	 * Exit a parse tree produced by {@link SearchSQLParser#keyword}.
	 * @param ctx the parse tree
	 */
	void exitKeyword(SearchSQLParser.KeywordContext ctx);
}