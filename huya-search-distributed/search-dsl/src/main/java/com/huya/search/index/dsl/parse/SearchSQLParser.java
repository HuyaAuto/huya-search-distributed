// Generated from /Users/geekcat/huya-search/core/src/main/resources/SearchSQL.g4 by ANTLR 4.7
package com.huya.search.index.dsl.parse;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SearchSQLParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, SCOL=2, DOT=3, OPEN_PAR=4, CLOSE_PAR=5, COMMA=6, ASSIGN=7, STAR=8, 
		PLUS=9, MINUS=10, TILDE=11, PIPE2=12, DIV=13, MOD=14, LT2=15, GT2=16, 
		AMP=17, PIPE=18, LT=19, LT_EQ=20, GT=21, GT_EQ=22, EQ=23, NOT_EQ1=24, 
		NOT_EQ2=25, K_ABORT=26, K_ACTION=27, K_ADD=28, K_AFTER=29, K_ALL=30, K_ALTER=31, 
		K_ANALYZE=32, K_AND=33, K_AS=34, K_ASC=35, K_ATTACH=36, K_AUTOINCREMENT=37, 
		K_BEFORE=38, K_BEGIN=39, K_BETWEEN=40, K_BY=41, K_CASCADE=42, K_CASE=43, 
		K_CAST=44, K_CHECK=45, K_COLLATE=46, K_COLUMN=47, K_COMMIT=48, K_CONFLICT=49, 
		K_CONSTRAINT=50, K_CREATE=51, K_CROSS=52, K_CURRENT_DATE=53, K_CURRENT_TIME=54, 
		K_CURRENT_TIMESTAMP=55, K_CURSOR=56, K_DATABASE=57, K_DEFAULT=58, K_DEFERRABLE=59, 
		K_DEFERRED=60, K_DELETE=61, K_DESC=62, K_DETACH=63, K_DISTINCT=64, K_DROP=65, 
		K_EACH=66, K_ELSE=67, K_END=68, K_ENABLE=69, K_ESCAPE=70, K_EXCEPT=71, 
		K_EXCLUSIVE=72, K_EXISTS=73, K_EXPLAIN=74, K_FAIL=75, K_FOR=76, K_FOREIGN=77, 
		K_FROM=78, K_FULL=79, K_GLOB=80, K_GROUP=81, K_HAVING=82, K_IF=83, K_IGNORE=84, 
		K_IMMEDIATE=85, K_IN=86, K_INDEX=87, K_INDEXED=88, K_INITIALLY=89, K_INNER=90, 
		K_INSERT=91, K_INSTEAD=92, K_INTERSECT=93, K_INTO=94, K_IS=95, K_ISNULL=96, 
		K_JOIN=97, K_KEY=98, K_LEFT=99, K_LIKE=100, K_LIMIT=101, K_MATCH=102, 
		K_NATURAL=103, K_NEXTVAL=104, K_NO=105, K_NOT=106, K_NOTNULL=107, K_NULL=108, 
		K_OF=109, K_OFFSET=110, K_ON=111, K_ONLY=112, K_OR=113, K_ORDER=114, K_OUTER=115, 
		K_PLAN=116, K_PRAGMA=117, K_PRIMARY=118, K_QUERY=119, K_RAISE=120, K_RECURSIVE=121, 
		K_REFERENCES=122, K_REGEXP=123, K_REINDEX=124, K_RELEASE=125, K_RENAME=126, 
		K_REPLACE=127, K_RESTRICT=128, K_RIGHT=129, K_ROLLBACK=130, K_ROW=131, 
		K_SAVEPOINT=132, K_SELECT=133, K_SET=134, K_TABLE=135, K_TEMP=136, K_TEMPORARY=137, 
		K_THEN=138, K_TO=139, K_TRANSACTION=140, K_TRIGGER=141, K_UNION=142, K_UNIQUE=143, 
		K_UPDATE=144, K_USING=145, K_VACUUM=146, K_VALUES=147, K_VIEW=148, K_VIRTUAL=149, 
		K_WHEN=150, K_WHERE=151, K_WITH=152, K_WITHOUT=153, IDENTIFIER=154, NUMERIC_LITERAL=155, 
		BIND_PARAMETER=156, STRING_LITERAL=157, BLOB_LITERAL=158, SINGLE_LINE_COMMENT=159, 
		MULTILINE_COMMENT=160, SPACES=161, UNEXPECTED_CHAR=162;
	public static final int
		RULE_parse = 0, RULE_error = 1, RULE_sql_stmt_list = 2, RULE_sql_stmt = 3, 
		RULE_cursor_select_stmt = 4, RULE_cursor_stmt = 5, RULE_simple_select_stmt = 6, 
		RULE_result_column = 7, RULE_table_name = 8, RULE_group_column = 9, RULE_column_name = 10, 
		RULE_function_name = 11, RULE_column_alias = 12, RULE_limit = 13, RULE_any_name = 14, 
		RULE_order_column = 15, RULE_function_expr = 16, RULE_where_expr = 17, 
		RULE_literal_value = 18, RULE_current_operator = 19, RULE_unary_operator = 20, 
		RULE_keyword = 21;
	public static final String[] ruleNames = {
		"parse", "error", "sql_stmt_list", "sql_stmt", "cursor_select_stmt", "cursor_stmt", 
		"simple_select_stmt", "result_column", "table_name", "group_column", "column_name", 
		"function_name", "column_alias", "limit", "any_name", "order_column", 
		"function_expr", "where_expr", "literal_value", "current_operator", "unary_operator", 
		"keyword"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "':'", "';'", "'.'", "'('", "')'", "','", "'='", "'*'", "'+'", "'-'", 
		"'~'", "'||'", "'/'", "'%'", "'<<'", "'>>'", "'&'", "'|'", "'<'", "'<='", 
		"'>'", "'>='", "'=='", "'!='", "'<>'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, "SCOL", "DOT", "OPEN_PAR", "CLOSE_PAR", "COMMA", "ASSIGN", 
		"STAR", "PLUS", "MINUS", "TILDE", "PIPE2", "DIV", "MOD", "LT2", "GT2", 
		"AMP", "PIPE", "LT", "LT_EQ", "GT", "GT_EQ", "EQ", "NOT_EQ1", "NOT_EQ2", 
		"K_ABORT", "K_ACTION", "K_ADD", "K_AFTER", "K_ALL", "K_ALTER", "K_ANALYZE", 
		"K_AND", "K_AS", "K_ASC", "K_ATTACH", "K_AUTOINCREMENT", "K_BEFORE", "K_BEGIN", 
		"K_BETWEEN", "K_BY", "K_CASCADE", "K_CASE", "K_CAST", "K_CHECK", "K_COLLATE", 
		"K_COLUMN", "K_COMMIT", "K_CONFLICT", "K_CONSTRAINT", "K_CREATE", "K_CROSS", 
		"K_CURRENT_DATE", "K_CURRENT_TIME", "K_CURRENT_TIMESTAMP", "K_CURSOR", 
		"K_DATABASE", "K_DEFAULT", "K_DEFERRABLE", "K_DEFERRED", "K_DELETE", "K_DESC", 
		"K_DETACH", "K_DISTINCT", "K_DROP", "K_EACH", "K_ELSE", "K_END", "K_ENABLE", 
		"K_ESCAPE", "K_EXCEPT", "K_EXCLUSIVE", "K_EXISTS", "K_EXPLAIN", "K_FAIL", 
		"K_FOR", "K_FOREIGN", "K_FROM", "K_FULL", "K_GLOB", "K_GROUP", "K_HAVING", 
		"K_IF", "K_IGNORE", "K_IMMEDIATE", "K_IN", "K_INDEX", "K_INDEXED", "K_INITIALLY", 
		"K_INNER", "K_INSERT", "K_INSTEAD", "K_INTERSECT", "K_INTO", "K_IS", "K_ISNULL", 
		"K_JOIN", "K_KEY", "K_LEFT", "K_LIKE", "K_LIMIT", "K_MATCH", "K_NATURAL", 
		"K_NEXTVAL", "K_NO", "K_NOT", "K_NOTNULL", "K_NULL", "K_OF", "K_OFFSET", 
		"K_ON", "K_ONLY", "K_OR", "K_ORDER", "K_OUTER", "K_PLAN", "K_PRAGMA", 
		"K_PRIMARY", "K_QUERY", "K_RAISE", "K_RECURSIVE", "K_REFERENCES", "K_REGEXP", 
		"K_REINDEX", "K_RELEASE", "K_RENAME", "K_REPLACE", "K_RESTRICT", "K_RIGHT", 
		"K_ROLLBACK", "K_ROW", "K_SAVEPOINT", "K_SELECT", "K_SET", "K_TABLE", 
		"K_TEMP", "K_TEMPORARY", "K_THEN", "K_TO", "K_TRANSACTION", "K_TRIGGER", 
		"K_UNION", "K_UNIQUE", "K_UPDATE", "K_USING", "K_VACUUM", "K_VALUES", 
		"K_VIEW", "K_VIRTUAL", "K_WHEN", "K_WHERE", "K_WITH", "K_WITHOUT", "IDENTIFIER", 
		"NUMERIC_LITERAL", "BIND_PARAMETER", "STRING_LITERAL", "BLOB_LITERAL", 
		"SINGLE_LINE_COMMENT", "MULTILINE_COMMENT", "SPACES", "UNEXPECTED_CHAR"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "SearchSQL.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public SearchSQLParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class ParseContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(SearchSQLParser.EOF, 0); }
		public List<Sql_stmt_listContext> sql_stmt_list() {
			return getRuleContexts(Sql_stmt_listContext.class);
		}
		public Sql_stmt_listContext sql_stmt_list(int i) {
			return getRuleContext(Sql_stmt_listContext.class,i);
		}
		public List<ErrorContext> error() {
			return getRuleContexts(ErrorContext.class);
		}
		public ErrorContext error(int i) {
			return getRuleContext(ErrorContext.class,i);
		}
		public ParseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parse; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SearchSQLListener ) ((SearchSQLListener)listener).enterParse(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SearchSQLListener ) ((SearchSQLListener)listener).exitParse(this);
		}
	}

	public final ParseContext parse() throws RecognitionException {
		ParseContext _localctx = new ParseContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_parse);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(48);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==SCOL || _la==K_CURSOR || _la==K_EXPLAIN || _la==K_SELECT || _la==UNEXPECTED_CHAR) {
				{
				setState(46);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case SCOL:
				case K_CURSOR:
				case K_EXPLAIN:
				case K_SELECT:
					{
					setState(44);
					sql_stmt_list();
					}
					break;
				case UNEXPECTED_CHAR:
					{
					setState(45);
					error();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(50);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(51);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ErrorContext extends ParserRuleContext {
		public Token UNEXPECTED_CHAR;
		public TerminalNode UNEXPECTED_CHAR() { return getToken(SearchSQLParser.UNEXPECTED_CHAR, 0); }
		public ErrorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_error; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SearchSQLListener ) ((SearchSQLListener)listener).enterError(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SearchSQLListener ) ((SearchSQLListener)listener).exitError(this);
		}
	}

	public final ErrorContext error() throws RecognitionException {
		ErrorContext _localctx = new ErrorContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_error);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(53);
			((ErrorContext)_localctx).UNEXPECTED_CHAR = match(UNEXPECTED_CHAR);

			     throw new RuntimeException("UNEXPECTED_CHAR=" + (((ErrorContext)_localctx).UNEXPECTED_CHAR!=null?((ErrorContext)_localctx).UNEXPECTED_CHAR.getText():null));
			   
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Sql_stmt_listContext extends ParserRuleContext {
		public List<Sql_stmtContext> sql_stmt() {
			return getRuleContexts(Sql_stmtContext.class);
		}
		public Sql_stmtContext sql_stmt(int i) {
			return getRuleContext(Sql_stmtContext.class,i);
		}
		public Sql_stmt_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sql_stmt_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SearchSQLListener ) ((SearchSQLListener)listener).enterSql_stmt_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SearchSQLListener ) ((SearchSQLListener)listener).exitSql_stmt_list(this);
		}
	}

	public final Sql_stmt_listContext sql_stmt_list() throws RecognitionException {
		Sql_stmt_listContext _localctx = new Sql_stmt_listContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_sql_stmt_list);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(59);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==SCOL) {
				{
				{
				setState(56);
				match(SCOL);
				}
				}
				setState(61);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(62);
			sql_stmt();
			setState(71);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(64); 
					_errHandler.sync(this);
					_la = _input.LA(1);
					do {
						{
						{
						setState(63);
						match(SCOL);
						}
						}
						setState(66); 
						_errHandler.sync(this);
						_la = _input.LA(1);
					} while ( _la==SCOL );
					setState(68);
					sql_stmt();
					}
					} 
				}
				setState(73);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			}
			setState(77);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(74);
					match(SCOL);
					}
					} 
				}
				setState(79);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Sql_stmtContext extends ParserRuleContext {
		public Simple_select_stmtContext simple_select_stmt() {
			return getRuleContext(Simple_select_stmtContext.class,0);
		}
		public Cursor_select_stmtContext cursor_select_stmt() {
			return getRuleContext(Cursor_select_stmtContext.class,0);
		}
		public Cursor_stmtContext cursor_stmt() {
			return getRuleContext(Cursor_stmtContext.class,0);
		}
		public TerminalNode K_EXPLAIN() { return getToken(SearchSQLParser.K_EXPLAIN, 0); }
		public TerminalNode K_QUERY() { return getToken(SearchSQLParser.K_QUERY, 0); }
		public TerminalNode K_PLAN() { return getToken(SearchSQLParser.K_PLAN, 0); }
		public Sql_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sql_stmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SearchSQLListener ) ((SearchSQLListener)listener).enterSql_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SearchSQLListener ) ((SearchSQLListener)listener).exitSql_stmt(this);
		}
	}

	public final Sql_stmtContext sql_stmt() throws RecognitionException {
		Sql_stmtContext _localctx = new Sql_stmtContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_sql_stmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(85);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_EXPLAIN) {
				{
				setState(80);
				match(K_EXPLAIN);
				setState(83);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==K_QUERY) {
					{
					setState(81);
					match(K_QUERY);
					setState(82);
					match(K_PLAN);
					}
				}

				}
			}

			setState(90);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
			case 1:
				{
				setState(87);
				simple_select_stmt();
				}
				break;
			case 2:
				{
				setState(88);
				cursor_select_stmt();
				}
				break;
			case 3:
				{
				setState(89);
				cursor_stmt();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Cursor_select_stmtContext extends ParserRuleContext {
		public TerminalNode K_CURSOR() { return getToken(SearchSQLParser.K_CURSOR, 0); }
		public Simple_select_stmtContext simple_select_stmt() {
			return getRuleContext(Simple_select_stmtContext.class,0);
		}
		public Cursor_select_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cursor_select_stmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SearchSQLListener ) ((SearchSQLListener)listener).enterCursor_select_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SearchSQLListener ) ((SearchSQLListener)listener).exitCursor_select_stmt(this);
		}
	}

	public final Cursor_select_stmtContext cursor_select_stmt() throws RecognitionException {
		Cursor_select_stmtContext _localctx = new Cursor_select_stmtContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_cursor_select_stmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(92);
			match(K_CURSOR);
			setState(93);
			match(T__0);
			setState(94);
			simple_select_stmt();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Cursor_stmtContext extends ParserRuleContext {
		public TerminalNode K_CURSOR() { return getToken(SearchSQLParser.K_CURSOR, 0); }
		public TerminalNode STRING_LITERAL() { return getToken(SearchSQLParser.STRING_LITERAL, 0); }
		public Cursor_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cursor_stmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SearchSQLListener ) ((SearchSQLListener)listener).enterCursor_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SearchSQLListener ) ((SearchSQLListener)listener).exitCursor_stmt(this);
		}
	}

	public final Cursor_stmtContext cursor_stmt() throws RecognitionException {
		Cursor_stmtContext _localctx = new Cursor_stmtContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_cursor_stmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(96);
			match(K_CURSOR);
			setState(97);
			match(T__0);
			setState(98);
			match(STRING_LITERAL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Simple_select_stmtContext extends ParserRuleContext {
		public TerminalNode K_SELECT() { return getToken(SearchSQLParser.K_SELECT, 0); }
		public List<Result_columnContext> result_column() {
			return getRuleContexts(Result_columnContext.class);
		}
		public Result_columnContext result_column(int i) {
			return getRuleContext(Result_columnContext.class,i);
		}
		public TerminalNode K_FROM() { return getToken(SearchSQLParser.K_FROM, 0); }
		public TerminalNode K_WHERE() { return getToken(SearchSQLParser.K_WHERE, 0); }
		public Where_exprContext where_expr() {
			return getRuleContext(Where_exprContext.class,0);
		}
		public TerminalNode K_GROUP() { return getToken(SearchSQLParser.K_GROUP, 0); }
		public List<TerminalNode> K_BY() { return getTokens(SearchSQLParser.K_BY); }
		public TerminalNode K_BY(int i) {
			return getToken(SearchSQLParser.K_BY, i);
		}
		public List<Group_columnContext> group_column() {
			return getRuleContexts(Group_columnContext.class);
		}
		public Group_columnContext group_column(int i) {
			return getRuleContext(Group_columnContext.class,i);
		}
		public TerminalNode K_ORDER() { return getToken(SearchSQLParser.K_ORDER, 0); }
		public List<Order_columnContext> order_column() {
			return getRuleContexts(Order_columnContext.class);
		}
		public Order_columnContext order_column(int i) {
			return getRuleContext(Order_columnContext.class,i);
		}
		public TerminalNode K_LIMIT() { return getToken(SearchSQLParser.K_LIMIT, 0); }
		public LimitContext limit() {
			return getRuleContext(LimitContext.class,0);
		}
		public List<Table_nameContext> table_name() {
			return getRuleContexts(Table_nameContext.class);
		}
		public Table_nameContext table_name(int i) {
			return getRuleContext(Table_nameContext.class,i);
		}
		public Simple_select_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_simple_select_stmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SearchSQLListener ) ((SearchSQLListener)listener).enterSimple_select_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SearchSQLListener ) ((SearchSQLListener)listener).exitSimple_select_stmt(this);
		}
	}

	public final Simple_select_stmtContext simple_select_stmt() throws RecognitionException {
		Simple_select_stmtContext _localctx = new Simple_select_stmtContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_simple_select_stmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(100);
			match(K_SELECT);
			setState(101);
			result_column();
			setState(106);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(102);
				match(COMMA);
				setState(103);
				result_column();
				}
				}
				setState(108);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			{
			setState(109);
			match(K_FROM);
			{
			setState(110);
			table_name();
			setState(115);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(111);
				match(COMMA);
				setState(112);
				table_name();
				}
				}
				setState(117);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
			}
			setState(120);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_WHERE) {
				{
				setState(118);
				match(K_WHERE);
				setState(119);
				where_expr(0);
				}
			}

			setState(132);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_GROUP) {
				{
				setState(122);
				match(K_GROUP);
				setState(123);
				match(K_BY);
				setState(124);
				group_column();
				setState(129);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(125);
					match(COMMA);
					setState(126);
					group_column();
					}
					}
					setState(131);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(144);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_ORDER) {
				{
				setState(134);
				match(K_ORDER);
				setState(135);
				match(K_BY);
				setState(136);
				order_column();
				setState(141);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(137);
					match(COMMA);
					setState(138);
					order_column();
					}
					}
					setState(143);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(148);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_LIMIT) {
				{
				setState(146);
				match(K_LIMIT);
				setState(147);
				limit();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Result_columnContext extends ParserRuleContext {
		public Function_exprContext function_expr() {
			return getRuleContext(Function_exprContext.class,0);
		}
		public Column_aliasContext column_alias() {
			return getRuleContext(Column_aliasContext.class,0);
		}
		public TerminalNode K_AS() { return getToken(SearchSQLParser.K_AS, 0); }
		public Column_nameContext column_name() {
			return getRuleContext(Column_nameContext.class,0);
		}
		public Result_columnContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_result_column; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SearchSQLListener ) ((SearchSQLListener)listener).enterResult_column(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SearchSQLListener ) ((SearchSQLListener)listener).exitResult_column(this);
		}
	}

	public final Result_columnContext result_column() throws RecognitionException {
		Result_columnContext _localctx = new Result_columnContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_result_column);
		int _la;
		try {
			setState(165);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(150);
				match(STAR);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(151);
				function_expr();
				setState(156);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==K_AS || _la==IDENTIFIER || _la==STRING_LITERAL) {
					{
					setState(153);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==K_AS) {
						{
						setState(152);
						match(K_AS);
						}
					}

					setState(155);
					column_alias();
					}
				}

				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(158);
				column_name();
				setState(163);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==K_AS || _la==IDENTIFIER || _la==STRING_LITERAL) {
					{
					setState(160);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==K_AS) {
						{
						setState(159);
						match(K_AS);
						}
					}

					setState(162);
					column_alias();
					}
				}

				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Table_nameContext extends ParserRuleContext {
		public Any_nameContext any_name() {
			return getRuleContext(Any_nameContext.class,0);
		}
		public Table_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_table_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SearchSQLListener ) ((SearchSQLListener)listener).enterTable_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SearchSQLListener ) ((SearchSQLListener)listener).exitTable_name(this);
		}
	}

	public final Table_nameContext table_name() throws RecognitionException {
		Table_nameContext _localctx = new Table_nameContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_table_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(167);
			any_name();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Group_columnContext extends ParserRuleContext {
		public Any_nameContext any_name() {
			return getRuleContext(Any_nameContext.class,0);
		}
		public Group_columnContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_group_column; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SearchSQLListener ) ((SearchSQLListener)listener).enterGroup_column(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SearchSQLListener ) ((SearchSQLListener)listener).exitGroup_column(this);
		}
	}

	public final Group_columnContext group_column() throws RecognitionException {
		Group_columnContext _localctx = new Group_columnContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_group_column);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(169);
			any_name();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Column_nameContext extends ParserRuleContext {
		public Any_nameContext any_name() {
			return getRuleContext(Any_nameContext.class,0);
		}
		public Column_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_column_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SearchSQLListener ) ((SearchSQLListener)listener).enterColumn_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SearchSQLListener ) ((SearchSQLListener)listener).exitColumn_name(this);
		}
	}

	public final Column_nameContext column_name() throws RecognitionException {
		Column_nameContext _localctx = new Column_nameContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_column_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(171);
			any_name();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Function_nameContext extends ParserRuleContext {
		public Any_nameContext any_name() {
			return getRuleContext(Any_nameContext.class,0);
		}
		public Function_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_function_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SearchSQLListener ) ((SearchSQLListener)listener).enterFunction_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SearchSQLListener ) ((SearchSQLListener)listener).exitFunction_name(this);
		}
	}

	public final Function_nameContext function_name() throws RecognitionException {
		Function_nameContext _localctx = new Function_nameContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_function_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(173);
			any_name();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Column_aliasContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(SearchSQLParser.IDENTIFIER, 0); }
		public TerminalNode STRING_LITERAL() { return getToken(SearchSQLParser.STRING_LITERAL, 0); }
		public Column_aliasContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_column_alias; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SearchSQLListener ) ((SearchSQLListener)listener).enterColumn_alias(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SearchSQLListener ) ((SearchSQLListener)listener).exitColumn_alias(this);
		}
	}

	public final Column_aliasContext column_alias() throws RecognitionException {
		Column_aliasContext _localctx = new Column_aliasContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_column_alias);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(175);
			_la = _input.LA(1);
			if ( !(_la==IDENTIFIER || _la==STRING_LITERAL) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)== Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LimitContext extends ParserRuleContext {
		public List<TerminalNode> NUMERIC_LITERAL() { return getTokens(SearchSQLParser.NUMERIC_LITERAL); }
		public TerminalNode NUMERIC_LITERAL(int i) {
			return getToken(SearchSQLParser.NUMERIC_LITERAL, i);
		}
		public LimitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_limit; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SearchSQLListener ) ((SearchSQLListener)listener).enterLimit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SearchSQLListener ) ((SearchSQLListener)listener).exitLimit(this);
		}
	}

	public final LimitContext limit() throws RecognitionException {
		LimitContext _localctx = new LimitContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_limit);
		try {
			setState(181);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(177);
				match(NUMERIC_LITERAL);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(178);
				match(NUMERIC_LITERAL);
				setState(179);
				match(COMMA);
				setState(180);
				match(NUMERIC_LITERAL);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Any_nameContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(SearchSQLParser.IDENTIFIER, 0); }
		public KeywordContext keyword() {
			return getRuleContext(KeywordContext.class,0);
		}
		public TerminalNode STRING_LITERAL() { return getToken(SearchSQLParser.STRING_LITERAL, 0); }
		public Any_nameContext any_name() {
			return getRuleContext(Any_nameContext.class,0);
		}
		public Any_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_any_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SearchSQLListener ) ((SearchSQLListener)listener).enterAny_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SearchSQLListener ) ((SearchSQLListener)listener).exitAny_name(this);
		}
	}

	public final Any_nameContext any_name() throws RecognitionException {
		Any_nameContext _localctx = new Any_nameContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_any_name);
		try {
			setState(190);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IDENTIFIER:
				enterOuterAlt(_localctx, 1);
				{
				setState(183);
				match(IDENTIFIER);
				}
				break;
			case K_ABORT:
			case K_ACTION:
			case K_ADD:
			case K_AFTER:
			case K_ALL:
			case K_ALTER:
			case K_ANALYZE:
			case K_AND:
			case K_AS:
			case K_ASC:
			case K_ATTACH:
			case K_AUTOINCREMENT:
			case K_BEFORE:
			case K_BEGIN:
			case K_BETWEEN:
			case K_BY:
			case K_CASCADE:
			case K_CASE:
			case K_CAST:
			case K_CHECK:
			case K_COLLATE:
			case K_COLUMN:
			case K_COMMIT:
			case K_CONFLICT:
			case K_CONSTRAINT:
			case K_CREATE:
			case K_CROSS:
			case K_CURRENT_DATE:
			case K_CURRENT_TIME:
			case K_CURRENT_TIMESTAMP:
			case K_DATABASE:
			case K_DEFAULT:
			case K_DEFERRABLE:
			case K_DEFERRED:
			case K_DELETE:
			case K_DESC:
			case K_DETACH:
			case K_DISTINCT:
			case K_DROP:
			case K_EACH:
			case K_ELSE:
			case K_END:
			case K_ENABLE:
			case K_ESCAPE:
			case K_EXCEPT:
			case K_EXCLUSIVE:
			case K_EXISTS:
			case K_EXPLAIN:
			case K_FAIL:
			case K_FOR:
			case K_FOREIGN:
			case K_FROM:
			case K_FULL:
			case K_GLOB:
			case K_GROUP:
			case K_HAVING:
			case K_IF:
			case K_IGNORE:
			case K_IMMEDIATE:
			case K_IN:
			case K_INDEX:
			case K_INDEXED:
			case K_INITIALLY:
			case K_INNER:
			case K_INSERT:
			case K_INSTEAD:
			case K_INTERSECT:
			case K_INTO:
			case K_IS:
			case K_ISNULL:
			case K_JOIN:
			case K_KEY:
			case K_LEFT:
			case K_LIKE:
			case K_LIMIT:
			case K_MATCH:
			case K_NATURAL:
			case K_NEXTVAL:
			case K_NO:
			case K_NOT:
			case K_NOTNULL:
			case K_NULL:
			case K_OF:
			case K_OFFSET:
			case K_ON:
			case K_OR:
			case K_ORDER:
			case K_OUTER:
			case K_PLAN:
			case K_PRAGMA:
			case K_PRIMARY:
			case K_QUERY:
			case K_RAISE:
			case K_RECURSIVE:
			case K_REFERENCES:
			case K_REGEXP:
			case K_REINDEX:
			case K_RELEASE:
			case K_RENAME:
			case K_REPLACE:
			case K_RESTRICT:
			case K_RIGHT:
			case K_ROLLBACK:
			case K_ROW:
			case K_SAVEPOINT:
			case K_SELECT:
			case K_SET:
			case K_TABLE:
			case K_TEMP:
			case K_TEMPORARY:
			case K_THEN:
			case K_TO:
			case K_TRANSACTION:
			case K_TRIGGER:
			case K_UNION:
			case K_UNIQUE:
			case K_UPDATE:
			case K_USING:
			case K_VACUUM:
			case K_VALUES:
			case K_VIEW:
			case K_VIRTUAL:
			case K_WHEN:
			case K_WHERE:
			case K_WITH:
			case K_WITHOUT:
				enterOuterAlt(_localctx, 2);
				{
				setState(184);
				keyword();
				}
				break;
			case STRING_LITERAL:
				enterOuterAlt(_localctx, 3);
				{
				setState(185);
				match(STRING_LITERAL);
				}
				break;
			case OPEN_PAR:
				enterOuterAlt(_localctx, 4);
				{
				setState(186);
				match(OPEN_PAR);
				setState(187);
				any_name();
				setState(188);
				match(CLOSE_PAR);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Order_columnContext extends ParserRuleContext {
		public Column_nameContext column_name() {
			return getRuleContext(Column_nameContext.class,0);
		}
		public TerminalNode K_ASC() { return getToken(SearchSQLParser.K_ASC, 0); }
		public TerminalNode K_DESC() { return getToken(SearchSQLParser.K_DESC, 0); }
		public Order_columnContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_order_column; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SearchSQLListener ) ((SearchSQLListener)listener).enterOrder_column(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SearchSQLListener ) ((SearchSQLListener)listener).exitOrder_column(this);
		}
	}

	public final Order_columnContext order_column() throws RecognitionException {
		Order_columnContext _localctx = new Order_columnContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_order_column);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(192);
			column_name();
			setState(194);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_ASC || _la==K_DESC) {
				{
				setState(193);
				_la = _input.LA(1);
				if ( !(_la==K_ASC || _la==K_DESC) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)== Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Function_exprContext extends ParserRuleContext {
		public Function_nameContext function_name() {
			return getRuleContext(Function_nameContext.class,0);
		}
		public List<Column_nameContext> column_name() {
			return getRuleContexts(Column_nameContext.class);
		}
		public Column_nameContext column_name(int i) {
			return getRuleContext(Column_nameContext.class,i);
		}
		public Function_exprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_function_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SearchSQLListener ) ((SearchSQLListener)listener).enterFunction_expr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SearchSQLListener ) ((SearchSQLListener)listener).exitFunction_expr(this);
		}
	}

	public final Function_exprContext function_expr() throws RecognitionException {
		Function_exprContext _localctx = new Function_exprContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_function_expr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(196);
			function_name();
			setState(197);
			match(OPEN_PAR);
			setState(207);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STAR:
				{
				setState(198);
				match(STAR);
				}
				break;
			case OPEN_PAR:
			case K_ABORT:
			case K_ACTION:
			case K_ADD:
			case K_AFTER:
			case K_ALL:
			case K_ALTER:
			case K_ANALYZE:
			case K_AND:
			case K_AS:
			case K_ASC:
			case K_ATTACH:
			case K_AUTOINCREMENT:
			case K_BEFORE:
			case K_BEGIN:
			case K_BETWEEN:
			case K_BY:
			case K_CASCADE:
			case K_CASE:
			case K_CAST:
			case K_CHECK:
			case K_COLLATE:
			case K_COLUMN:
			case K_COMMIT:
			case K_CONFLICT:
			case K_CONSTRAINT:
			case K_CREATE:
			case K_CROSS:
			case K_CURRENT_DATE:
			case K_CURRENT_TIME:
			case K_CURRENT_TIMESTAMP:
			case K_DATABASE:
			case K_DEFAULT:
			case K_DEFERRABLE:
			case K_DEFERRED:
			case K_DELETE:
			case K_DESC:
			case K_DETACH:
			case K_DISTINCT:
			case K_DROP:
			case K_EACH:
			case K_ELSE:
			case K_END:
			case K_ENABLE:
			case K_ESCAPE:
			case K_EXCEPT:
			case K_EXCLUSIVE:
			case K_EXISTS:
			case K_EXPLAIN:
			case K_FAIL:
			case K_FOR:
			case K_FOREIGN:
			case K_FROM:
			case K_FULL:
			case K_GLOB:
			case K_GROUP:
			case K_HAVING:
			case K_IF:
			case K_IGNORE:
			case K_IMMEDIATE:
			case K_IN:
			case K_INDEX:
			case K_INDEXED:
			case K_INITIALLY:
			case K_INNER:
			case K_INSERT:
			case K_INSTEAD:
			case K_INTERSECT:
			case K_INTO:
			case K_IS:
			case K_ISNULL:
			case K_JOIN:
			case K_KEY:
			case K_LEFT:
			case K_LIKE:
			case K_LIMIT:
			case K_MATCH:
			case K_NATURAL:
			case K_NEXTVAL:
			case K_NO:
			case K_NOT:
			case K_NOTNULL:
			case K_NULL:
			case K_OF:
			case K_OFFSET:
			case K_ON:
			case K_OR:
			case K_ORDER:
			case K_OUTER:
			case K_PLAN:
			case K_PRAGMA:
			case K_PRIMARY:
			case K_QUERY:
			case K_RAISE:
			case K_RECURSIVE:
			case K_REFERENCES:
			case K_REGEXP:
			case K_REINDEX:
			case K_RELEASE:
			case K_RENAME:
			case K_REPLACE:
			case K_RESTRICT:
			case K_RIGHT:
			case K_ROLLBACK:
			case K_ROW:
			case K_SAVEPOINT:
			case K_SELECT:
			case K_SET:
			case K_TABLE:
			case K_TEMP:
			case K_TEMPORARY:
			case K_THEN:
			case K_TO:
			case K_TRANSACTION:
			case K_TRIGGER:
			case K_UNION:
			case K_UNIQUE:
			case K_UPDATE:
			case K_USING:
			case K_VACUUM:
			case K_VALUES:
			case K_VIEW:
			case K_VIRTUAL:
			case K_WHEN:
			case K_WHERE:
			case K_WITH:
			case K_WITHOUT:
			case IDENTIFIER:
			case STRING_LITERAL:
				{
				setState(199);
				column_name();
				setState(204);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(200);
					match(COMMA);
					setState(201);
					column_name();
					}
					}
					setState(206);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(209);
			match(CLOSE_PAR);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Where_exprContext extends ParserRuleContext {
		public List<Where_exprContext> where_expr() {
			return getRuleContexts(Where_exprContext.class);
		}
		public Where_exprContext where_expr(int i) {
			return getRuleContext(Where_exprContext.class,i);
		}
		public Column_nameContext column_name() {
			return getRuleContext(Column_nameContext.class,0);
		}
		public Current_operatorContext current_operator() {
			return getRuleContext(Current_operatorContext.class,0);
		}
		public Literal_valueContext literal_value() {
			return getRuleContext(Literal_valueContext.class,0);
		}
		public TerminalNode K_AND() { return getToken(SearchSQLParser.K_AND, 0); }
		public TerminalNode K_OR() { return getToken(SearchSQLParser.K_OR, 0); }
		public Where_exprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_where_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SearchSQLListener ) ((SearchSQLListener)listener).enterWhere_expr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SearchSQLListener ) ((SearchSQLListener)listener).exitWhere_expr(this);
		}
	}

	public final Where_exprContext where_expr() throws RecognitionException {
		return where_expr(0);
	}

	private Where_exprContext where_expr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		Where_exprContext _localctx = new Where_exprContext(_ctx, _parentState);
		Where_exprContext _prevctx = _localctx;
		int _startState = 34;
		enterRecursionRule(_localctx, 34, RULE_where_expr, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(220);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,27,_ctx) ) {
			case 1:
				{
				setState(212);
				match(OPEN_PAR);
				setState(213);
				where_expr(0);
				setState(214);
				match(CLOSE_PAR);
				}
				break;
			case 2:
				{
				setState(216);
				column_name();
				setState(217);
				current_operator();
				setState(218);
				literal_value();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(230);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,29,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(228);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,28,_ctx) ) {
					case 1:
						{
						_localctx = new Where_exprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_where_expr);
						setState(222);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(223);
						match(K_AND);
						setState(224);
						where_expr(4);
						}
						break;
					case 2:
						{
						_localctx = new Where_exprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_where_expr);
						setState(225);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(226);
						match(K_OR);
						setState(227);
						where_expr(3);
						}
						break;
					}
					} 
				}
				setState(232);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,29,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class Literal_valueContext extends ParserRuleContext {
		public TerminalNode NUMERIC_LITERAL() { return getToken(SearchSQLParser.NUMERIC_LITERAL, 0); }
		public TerminalNode STRING_LITERAL() { return getToken(SearchSQLParser.STRING_LITERAL, 0); }
		public TerminalNode BLOB_LITERAL() { return getToken(SearchSQLParser.BLOB_LITERAL, 0); }
		public TerminalNode K_NULL() { return getToken(SearchSQLParser.K_NULL, 0); }
		public TerminalNode K_CURRENT_TIME() { return getToken(SearchSQLParser.K_CURRENT_TIME, 0); }
		public TerminalNode K_CURRENT_DATE() { return getToken(SearchSQLParser.K_CURRENT_DATE, 0); }
		public TerminalNode K_CURRENT_TIMESTAMP() { return getToken(SearchSQLParser.K_CURRENT_TIMESTAMP, 0); }
		public Literal_valueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_literal_value; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SearchSQLListener ) ((SearchSQLListener)listener).enterLiteral_value(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SearchSQLListener ) ((SearchSQLListener)listener).exitLiteral_value(this);
		}
	}

	public final Literal_valueContext literal_value() throws RecognitionException {
		Literal_valueContext _localctx = new Literal_valueContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_literal_value);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(233);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << K_CURRENT_DATE) | (1L << K_CURRENT_TIME) | (1L << K_CURRENT_TIMESTAMP))) != 0) || ((((_la - 108)) & ~0x3f) == 0 && ((1L << (_la - 108)) & ((1L << (K_NULL - 108)) | (1L << (NUMERIC_LITERAL - 108)) | (1L << (STRING_LITERAL - 108)) | (1L << (BLOB_LITERAL - 108)))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)== Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Current_operatorContext extends ParserRuleContext {
		public TerminalNode ASSIGN() { return getToken(SearchSQLParser.ASSIGN, 0); }
		public TerminalNode LT() { return getToken(SearchSQLParser.LT, 0); }
		public TerminalNode LT_EQ() { return getToken(SearchSQLParser.LT_EQ, 0); }
		public TerminalNode GT() { return getToken(SearchSQLParser.GT, 0); }
		public TerminalNode GT_EQ() { return getToken(SearchSQLParser.GT_EQ, 0); }
		public TerminalNode NOT_EQ1() { return getToken(SearchSQLParser.NOT_EQ1, 0); }
		public TerminalNode NOT_EQ2() { return getToken(SearchSQLParser.NOT_EQ2, 0); }
		public TerminalNode K_LIKE() { return getToken(SearchSQLParser.K_LIKE, 0); }
		public Current_operatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_current_operator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SearchSQLListener ) ((SearchSQLListener)listener).enterCurrent_operator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SearchSQLListener ) ((SearchSQLListener)listener).exitCurrent_operator(this);
		}
	}

	public final Current_operatorContext current_operator() throws RecognitionException {
		Current_operatorContext _localctx = new Current_operatorContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_current_operator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(235);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ASSIGN) | (1L << LT) | (1L << LT_EQ) | (1L << GT) | (1L << GT_EQ) | (1L << NOT_EQ1) | (1L << NOT_EQ2))) != 0) || _la==K_LIKE) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)== Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Unary_operatorContext extends ParserRuleContext {
		public TerminalNode K_NOT() { return getToken(SearchSQLParser.K_NOT, 0); }
		public Unary_operatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unary_operator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SearchSQLListener ) ((SearchSQLListener)listener).enterUnary_operator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SearchSQLListener ) ((SearchSQLListener)listener).exitUnary_operator(this);
		}
	}

	public final Unary_operatorContext unary_operator() throws RecognitionException {
		Unary_operatorContext _localctx = new Unary_operatorContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_unary_operator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(237);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PLUS) | (1L << MINUS) | (1L << TILDE))) != 0) || _la==K_NOT) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)== Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class KeywordContext extends ParserRuleContext {
		public TerminalNode K_ABORT() { return getToken(SearchSQLParser.K_ABORT, 0); }
		public TerminalNode K_ACTION() { return getToken(SearchSQLParser.K_ACTION, 0); }
		public TerminalNode K_ADD() { return getToken(SearchSQLParser.K_ADD, 0); }
		public TerminalNode K_AFTER() { return getToken(SearchSQLParser.K_AFTER, 0); }
		public TerminalNode K_ALL() { return getToken(SearchSQLParser.K_ALL, 0); }
		public TerminalNode K_ALTER() { return getToken(SearchSQLParser.K_ALTER, 0); }
		public TerminalNode K_ANALYZE() { return getToken(SearchSQLParser.K_ANALYZE, 0); }
		public TerminalNode K_AND() { return getToken(SearchSQLParser.K_AND, 0); }
		public TerminalNode K_AS() { return getToken(SearchSQLParser.K_AS, 0); }
		public TerminalNode K_ASC() { return getToken(SearchSQLParser.K_ASC, 0); }
		public TerminalNode K_ATTACH() { return getToken(SearchSQLParser.K_ATTACH, 0); }
		public TerminalNode K_AUTOINCREMENT() { return getToken(SearchSQLParser.K_AUTOINCREMENT, 0); }
		public TerminalNode K_BEFORE() { return getToken(SearchSQLParser.K_BEFORE, 0); }
		public TerminalNode K_BEGIN() { return getToken(SearchSQLParser.K_BEGIN, 0); }
		public TerminalNode K_BETWEEN() { return getToken(SearchSQLParser.K_BETWEEN, 0); }
		public TerminalNode K_BY() { return getToken(SearchSQLParser.K_BY, 0); }
		public TerminalNode K_CASCADE() { return getToken(SearchSQLParser.K_CASCADE, 0); }
		public TerminalNode K_CASE() { return getToken(SearchSQLParser.K_CASE, 0); }
		public TerminalNode K_CAST() { return getToken(SearchSQLParser.K_CAST, 0); }
		public TerminalNode K_CHECK() { return getToken(SearchSQLParser.K_CHECK, 0); }
		public TerminalNode K_COLLATE() { return getToken(SearchSQLParser.K_COLLATE, 0); }
		public TerminalNode K_COLUMN() { return getToken(SearchSQLParser.K_COLUMN, 0); }
		public TerminalNode K_COMMIT() { return getToken(SearchSQLParser.K_COMMIT, 0); }
		public TerminalNode K_CONFLICT() { return getToken(SearchSQLParser.K_CONFLICT, 0); }
		public TerminalNode K_CONSTRAINT() { return getToken(SearchSQLParser.K_CONSTRAINT, 0); }
		public TerminalNode K_CREATE() { return getToken(SearchSQLParser.K_CREATE, 0); }
		public TerminalNode K_CROSS() { return getToken(SearchSQLParser.K_CROSS, 0); }
		public TerminalNode K_CURRENT_DATE() { return getToken(SearchSQLParser.K_CURRENT_DATE, 0); }
		public TerminalNode K_CURRENT_TIME() { return getToken(SearchSQLParser.K_CURRENT_TIME, 0); }
		public TerminalNode K_CURRENT_TIMESTAMP() { return getToken(SearchSQLParser.K_CURRENT_TIMESTAMP, 0); }
		public TerminalNode K_DATABASE() { return getToken(SearchSQLParser.K_DATABASE, 0); }
		public TerminalNode K_DEFAULT() { return getToken(SearchSQLParser.K_DEFAULT, 0); }
		public TerminalNode K_DEFERRABLE() { return getToken(SearchSQLParser.K_DEFERRABLE, 0); }
		public TerminalNode K_DEFERRED() { return getToken(SearchSQLParser.K_DEFERRED, 0); }
		public TerminalNode K_DELETE() { return getToken(SearchSQLParser.K_DELETE, 0); }
		public TerminalNode K_DESC() { return getToken(SearchSQLParser.K_DESC, 0); }
		public TerminalNode K_DETACH() { return getToken(SearchSQLParser.K_DETACH, 0); }
		public TerminalNode K_DISTINCT() { return getToken(SearchSQLParser.K_DISTINCT, 0); }
		public TerminalNode K_DROP() { return getToken(SearchSQLParser.K_DROP, 0); }
		public TerminalNode K_EACH() { return getToken(SearchSQLParser.K_EACH, 0); }
		public TerminalNode K_ELSE() { return getToken(SearchSQLParser.K_ELSE, 0); }
		public TerminalNode K_END() { return getToken(SearchSQLParser.K_END, 0); }
		public TerminalNode K_ENABLE() { return getToken(SearchSQLParser.K_ENABLE, 0); }
		public TerminalNode K_ESCAPE() { return getToken(SearchSQLParser.K_ESCAPE, 0); }
		public TerminalNode K_EXCEPT() { return getToken(SearchSQLParser.K_EXCEPT, 0); }
		public TerminalNode K_EXCLUSIVE() { return getToken(SearchSQLParser.K_EXCLUSIVE, 0); }
		public TerminalNode K_EXISTS() { return getToken(SearchSQLParser.K_EXISTS, 0); }
		public TerminalNode K_EXPLAIN() { return getToken(SearchSQLParser.K_EXPLAIN, 0); }
		public TerminalNode K_FAIL() { return getToken(SearchSQLParser.K_FAIL, 0); }
		public TerminalNode K_FOR() { return getToken(SearchSQLParser.K_FOR, 0); }
		public TerminalNode K_FOREIGN() { return getToken(SearchSQLParser.K_FOREIGN, 0); }
		public TerminalNode K_FROM() { return getToken(SearchSQLParser.K_FROM, 0); }
		public TerminalNode K_FULL() { return getToken(SearchSQLParser.K_FULL, 0); }
		public TerminalNode K_GLOB() { return getToken(SearchSQLParser.K_GLOB, 0); }
		public TerminalNode K_GROUP() { return getToken(SearchSQLParser.K_GROUP, 0); }
		public TerminalNode K_HAVING() { return getToken(SearchSQLParser.K_HAVING, 0); }
		public TerminalNode K_IF() { return getToken(SearchSQLParser.K_IF, 0); }
		public TerminalNode K_IGNORE() { return getToken(SearchSQLParser.K_IGNORE, 0); }
		public TerminalNode K_IMMEDIATE() { return getToken(SearchSQLParser.K_IMMEDIATE, 0); }
		public TerminalNode K_IN() { return getToken(SearchSQLParser.K_IN, 0); }
		public TerminalNode K_INDEX() { return getToken(SearchSQLParser.K_INDEX, 0); }
		public TerminalNode K_INDEXED() { return getToken(SearchSQLParser.K_INDEXED, 0); }
		public TerminalNode K_INITIALLY() { return getToken(SearchSQLParser.K_INITIALLY, 0); }
		public TerminalNode K_INNER() { return getToken(SearchSQLParser.K_INNER, 0); }
		public TerminalNode K_INSERT() { return getToken(SearchSQLParser.K_INSERT, 0); }
		public TerminalNode K_INSTEAD() { return getToken(SearchSQLParser.K_INSTEAD, 0); }
		public TerminalNode K_INTERSECT() { return getToken(SearchSQLParser.K_INTERSECT, 0); }
		public TerminalNode K_INTO() { return getToken(SearchSQLParser.K_INTO, 0); }
		public TerminalNode K_IS() { return getToken(SearchSQLParser.K_IS, 0); }
		public TerminalNode K_ISNULL() { return getToken(SearchSQLParser.K_ISNULL, 0); }
		public TerminalNode K_JOIN() { return getToken(SearchSQLParser.K_JOIN, 0); }
		public TerminalNode K_KEY() { return getToken(SearchSQLParser.K_KEY, 0); }
		public TerminalNode K_LEFT() { return getToken(SearchSQLParser.K_LEFT, 0); }
		public TerminalNode K_LIKE() { return getToken(SearchSQLParser.K_LIKE, 0); }
		public TerminalNode K_LIMIT() { return getToken(SearchSQLParser.K_LIMIT, 0); }
		public TerminalNode K_MATCH() { return getToken(SearchSQLParser.K_MATCH, 0); }
		public TerminalNode K_NATURAL() { return getToken(SearchSQLParser.K_NATURAL, 0); }
		public TerminalNode K_NO() { return getToken(SearchSQLParser.K_NO, 0); }
		public TerminalNode K_NOT() { return getToken(SearchSQLParser.K_NOT, 0); }
		public TerminalNode K_NOTNULL() { return getToken(SearchSQLParser.K_NOTNULL, 0); }
		public TerminalNode K_NULL() { return getToken(SearchSQLParser.K_NULL, 0); }
		public TerminalNode K_OF() { return getToken(SearchSQLParser.K_OF, 0); }
		public TerminalNode K_OFFSET() { return getToken(SearchSQLParser.K_OFFSET, 0); }
		public TerminalNode K_ON() { return getToken(SearchSQLParser.K_ON, 0); }
		public TerminalNode K_OR() { return getToken(SearchSQLParser.K_OR, 0); }
		public TerminalNode K_ORDER() { return getToken(SearchSQLParser.K_ORDER, 0); }
		public TerminalNode K_OUTER() { return getToken(SearchSQLParser.K_OUTER, 0); }
		public TerminalNode K_PLAN() { return getToken(SearchSQLParser.K_PLAN, 0); }
		public TerminalNode K_PRAGMA() { return getToken(SearchSQLParser.K_PRAGMA, 0); }
		public TerminalNode K_PRIMARY() { return getToken(SearchSQLParser.K_PRIMARY, 0); }
		public TerminalNode K_QUERY() { return getToken(SearchSQLParser.K_QUERY, 0); }
		public TerminalNode K_RAISE() { return getToken(SearchSQLParser.K_RAISE, 0); }
		public TerminalNode K_RECURSIVE() { return getToken(SearchSQLParser.K_RECURSIVE, 0); }
		public TerminalNode K_REFERENCES() { return getToken(SearchSQLParser.K_REFERENCES, 0); }
		public TerminalNode K_REGEXP() { return getToken(SearchSQLParser.K_REGEXP, 0); }
		public TerminalNode K_REINDEX() { return getToken(SearchSQLParser.K_REINDEX, 0); }
		public TerminalNode K_RELEASE() { return getToken(SearchSQLParser.K_RELEASE, 0); }
		public TerminalNode K_RENAME() { return getToken(SearchSQLParser.K_RENAME, 0); }
		public TerminalNode K_REPLACE() { return getToken(SearchSQLParser.K_REPLACE, 0); }
		public TerminalNode K_RESTRICT() { return getToken(SearchSQLParser.K_RESTRICT, 0); }
		public TerminalNode K_RIGHT() { return getToken(SearchSQLParser.K_RIGHT, 0); }
		public TerminalNode K_ROLLBACK() { return getToken(SearchSQLParser.K_ROLLBACK, 0); }
		public TerminalNode K_ROW() { return getToken(SearchSQLParser.K_ROW, 0); }
		public TerminalNode K_SAVEPOINT() { return getToken(SearchSQLParser.K_SAVEPOINT, 0); }
		public TerminalNode K_SELECT() { return getToken(SearchSQLParser.K_SELECT, 0); }
		public TerminalNode K_SET() { return getToken(SearchSQLParser.K_SET, 0); }
		public TerminalNode K_TABLE() { return getToken(SearchSQLParser.K_TABLE, 0); }
		public TerminalNode K_TEMP() { return getToken(SearchSQLParser.K_TEMP, 0); }
		public TerminalNode K_TEMPORARY() { return getToken(SearchSQLParser.K_TEMPORARY, 0); }
		public TerminalNode K_THEN() { return getToken(SearchSQLParser.K_THEN, 0); }
		public TerminalNode K_TO() { return getToken(SearchSQLParser.K_TO, 0); }
		public TerminalNode K_TRANSACTION() { return getToken(SearchSQLParser.K_TRANSACTION, 0); }
		public TerminalNode K_TRIGGER() { return getToken(SearchSQLParser.K_TRIGGER, 0); }
		public TerminalNode K_UNION() { return getToken(SearchSQLParser.K_UNION, 0); }
		public TerminalNode K_UNIQUE() { return getToken(SearchSQLParser.K_UNIQUE, 0); }
		public TerminalNode K_UPDATE() { return getToken(SearchSQLParser.K_UPDATE, 0); }
		public TerminalNode K_USING() { return getToken(SearchSQLParser.K_USING, 0); }
		public TerminalNode K_VACUUM() { return getToken(SearchSQLParser.K_VACUUM, 0); }
		public TerminalNode K_VALUES() { return getToken(SearchSQLParser.K_VALUES, 0); }
		public TerminalNode K_VIEW() { return getToken(SearchSQLParser.K_VIEW, 0); }
		public TerminalNode K_VIRTUAL() { return getToken(SearchSQLParser.K_VIRTUAL, 0); }
		public TerminalNode K_WHEN() { return getToken(SearchSQLParser.K_WHEN, 0); }
		public TerminalNode K_WHERE() { return getToken(SearchSQLParser.K_WHERE, 0); }
		public TerminalNode K_WITH() { return getToken(SearchSQLParser.K_WITH, 0); }
		public TerminalNode K_WITHOUT() { return getToken(SearchSQLParser.K_WITHOUT, 0); }
		public TerminalNode K_NEXTVAL() { return getToken(SearchSQLParser.K_NEXTVAL, 0); }
		public KeywordContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_keyword; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SearchSQLListener ) ((SearchSQLListener)listener).enterKeyword(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SearchSQLListener ) ((SearchSQLListener)listener).exitKeyword(this);
		}
	}

	public final KeywordContext keyword() throws RecognitionException {
		KeywordContext _localctx = new KeywordContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_keyword);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(239);
			_la = _input.LA(1);
			if ( !(((((_la - 26)) & ~0x3f) == 0 && ((1L << (_la - 26)) & ((1L << (K_ABORT - 26)) | (1L << (K_ACTION - 26)) | (1L << (K_ADD - 26)) | (1L << (K_AFTER - 26)) | (1L << (K_ALL - 26)) | (1L << (K_ALTER - 26)) | (1L << (K_ANALYZE - 26)) | (1L << (K_AND - 26)) | (1L << (K_AS - 26)) | (1L << (K_ASC - 26)) | (1L << (K_ATTACH - 26)) | (1L << (K_AUTOINCREMENT - 26)) | (1L << (K_BEFORE - 26)) | (1L << (K_BEGIN - 26)) | (1L << (K_BETWEEN - 26)) | (1L << (K_BY - 26)) | (1L << (K_CASCADE - 26)) | (1L << (K_CASE - 26)) | (1L << (K_CAST - 26)) | (1L << (K_CHECK - 26)) | (1L << (K_COLLATE - 26)) | (1L << (K_COLUMN - 26)) | (1L << (K_COMMIT - 26)) | (1L << (K_CONFLICT - 26)) | (1L << (K_CONSTRAINT - 26)) | (1L << (K_CREATE - 26)) | (1L << (K_CROSS - 26)) | (1L << (K_CURRENT_DATE - 26)) | (1L << (K_CURRENT_TIME - 26)) | (1L << (K_CURRENT_TIMESTAMP - 26)) | (1L << (K_DATABASE - 26)) | (1L << (K_DEFAULT - 26)) | (1L << (K_DEFERRABLE - 26)) | (1L << (K_DEFERRED - 26)) | (1L << (K_DELETE - 26)) | (1L << (K_DESC - 26)) | (1L << (K_DETACH - 26)) | (1L << (K_DISTINCT - 26)) | (1L << (K_DROP - 26)) | (1L << (K_EACH - 26)) | (1L << (K_ELSE - 26)) | (1L << (K_END - 26)) | (1L << (K_ENABLE - 26)) | (1L << (K_ESCAPE - 26)) | (1L << (K_EXCEPT - 26)) | (1L << (K_EXCLUSIVE - 26)) | (1L << (K_EXISTS - 26)) | (1L << (K_EXPLAIN - 26)) | (1L << (K_FAIL - 26)) | (1L << (K_FOR - 26)) | (1L << (K_FOREIGN - 26)) | (1L << (K_FROM - 26)) | (1L << (K_FULL - 26)) | (1L << (K_GLOB - 26)) | (1L << (K_GROUP - 26)) | (1L << (K_HAVING - 26)) | (1L << (K_IF - 26)) | (1L << (K_IGNORE - 26)) | (1L << (K_IMMEDIATE - 26)) | (1L << (K_IN - 26)) | (1L << (K_INDEX - 26)) | (1L << (K_INDEXED - 26)) | (1L << (K_INITIALLY - 26)))) != 0) || ((((_la - 90)) & ~0x3f) == 0 && ((1L << (_la - 90)) & ((1L << (K_INNER - 90)) | (1L << (K_INSERT - 90)) | (1L << (K_INSTEAD - 90)) | (1L << (K_INTERSECT - 90)) | (1L << (K_INTO - 90)) | (1L << (K_IS - 90)) | (1L << (K_ISNULL - 90)) | (1L << (K_JOIN - 90)) | (1L << (K_KEY - 90)) | (1L << (K_LEFT - 90)) | (1L << (K_LIKE - 90)) | (1L << (K_LIMIT - 90)) | (1L << (K_MATCH - 90)) | (1L << (K_NATURAL - 90)) | (1L << (K_NEXTVAL - 90)) | (1L << (K_NO - 90)) | (1L << (K_NOT - 90)) | (1L << (K_NOTNULL - 90)) | (1L << (K_NULL - 90)) | (1L << (K_OF - 90)) | (1L << (K_OFFSET - 90)) | (1L << (K_ON - 90)) | (1L << (K_OR - 90)) | (1L << (K_ORDER - 90)) | (1L << (K_OUTER - 90)) | (1L << (K_PLAN - 90)) | (1L << (K_PRAGMA - 90)) | (1L << (K_PRIMARY - 90)) | (1L << (K_QUERY - 90)) | (1L << (K_RAISE - 90)) | (1L << (K_RECURSIVE - 90)) | (1L << (K_REFERENCES - 90)) | (1L << (K_REGEXP - 90)) | (1L << (K_REINDEX - 90)) | (1L << (K_RELEASE - 90)) | (1L << (K_RENAME - 90)) | (1L << (K_REPLACE - 90)) | (1L << (K_RESTRICT - 90)) | (1L << (K_RIGHT - 90)) | (1L << (K_ROLLBACK - 90)) | (1L << (K_ROW - 90)) | (1L << (K_SAVEPOINT - 90)) | (1L << (K_SELECT - 90)) | (1L << (K_SET - 90)) | (1L << (K_TABLE - 90)) | (1L << (K_TEMP - 90)) | (1L << (K_TEMPORARY - 90)) | (1L << (K_THEN - 90)) | (1L << (K_TO - 90)) | (1L << (K_TRANSACTION - 90)) | (1L << (K_TRIGGER - 90)) | (1L << (K_UNION - 90)) | (1L << (K_UNIQUE - 90)) | (1L << (K_UPDATE - 90)) | (1L << (K_USING - 90)) | (1L << (K_VACUUM - 90)) | (1L << (K_VALUES - 90)) | (1L << (K_VIEW - 90)) | (1L << (K_VIRTUAL - 90)) | (1L << (K_WHEN - 90)) | (1L << (K_WHERE - 90)) | (1L << (K_WITH - 90)) | (1L << (K_WITHOUT - 90)))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)== Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 17:
			return where_expr_sempred((Where_exprContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean where_expr_sempred(Where_exprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 3);
		case 1:
			return precpred(_ctx, 2);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\u00a4\u00f4\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\3\2\3\2\7\2\61\n\2"+
		"\f\2\16\2\64\13\2\3\2\3\2\3\3\3\3\3\3\3\4\7\4<\n\4\f\4\16\4?\13\4\3\4"+
		"\3\4\6\4C\n\4\r\4\16\4D\3\4\7\4H\n\4\f\4\16\4K\13\4\3\4\7\4N\n\4\f\4\16"+
		"\4Q\13\4\3\5\3\5\3\5\5\5V\n\5\5\5X\n\5\3\5\3\5\3\5\5\5]\n\5\3\6\3\6\3"+
		"\6\3\6\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\7\bk\n\b\f\b\16\bn\13\b\3\b\3\b"+
		"\3\b\3\b\7\bt\n\b\f\b\16\bw\13\b\3\b\3\b\5\b{\n\b\3\b\3\b\3\b\3\b\3\b"+
		"\7\b\u0082\n\b\f\b\16\b\u0085\13\b\5\b\u0087\n\b\3\b\3\b\3\b\3\b\3\b\7"+
		"\b\u008e\n\b\f\b\16\b\u0091\13\b\5\b\u0093\n\b\3\b\3\b\5\b\u0097\n\b\3"+
		"\t\3\t\3\t\5\t\u009c\n\t\3\t\5\t\u009f\n\t\3\t\3\t\5\t\u00a3\n\t\3\t\5"+
		"\t\u00a6\n\t\5\t\u00a8\n\t\3\n\3\n\3\13\3\13\3\f\3\f\3\r\3\r\3\16\3\16"+
		"\3\17\3\17\3\17\3\17\5\17\u00b8\n\17\3\20\3\20\3\20\3\20\3\20\3\20\3\20"+
		"\5\20\u00c1\n\20\3\21\3\21\5\21\u00c5\n\21\3\22\3\22\3\22\3\22\3\22\3"+
		"\22\7\22\u00cd\n\22\f\22\16\22\u00d0\13\22\5\22\u00d2\n\22\3\22\3\22\3"+
		"\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\5\23\u00df\n\23\3\23\3\23"+
		"\3\23\3\23\3\23\3\23\7\23\u00e7\n\23\f\23\16\23\u00ea\13\23\3\24\3\24"+
		"\3\25\3\25\3\26\3\26\3\27\3\27\3\27\2\3$\30\2\4\6\b\n\f\16\20\22\24\26"+
		"\30\32\34\36 \"$&(*,\2\b\4\2\u009c\u009c\u009f\u009f\4\2%%@@\6\2\679n"+
		"n\u009d\u009d\u009f\u00a0\6\2\t\t\25\30\32\33ff\4\2\13\rll\5\2\349;qs"+
		"\u009b\2\u00ff\2\62\3\2\2\2\4\67\3\2\2\2\6=\3\2\2\2\bW\3\2\2\2\n^\3\2"+
		"\2\2\fb\3\2\2\2\16f\3\2\2\2\20\u00a7\3\2\2\2\22\u00a9\3\2\2\2\24\u00ab"+
		"\3\2\2\2\26\u00ad\3\2\2\2\30\u00af\3\2\2\2\32\u00b1\3\2\2\2\34\u00b7\3"+
		"\2\2\2\36\u00c0\3\2\2\2 \u00c2\3\2\2\2\"\u00c6\3\2\2\2$\u00de\3\2\2\2"+
		"&\u00eb\3\2\2\2(\u00ed\3\2\2\2*\u00ef\3\2\2\2,\u00f1\3\2\2\2.\61\5\6\4"+
		"\2/\61\5\4\3\2\60.\3\2\2\2\60/\3\2\2\2\61\64\3\2\2\2\62\60\3\2\2\2\62"+
		"\63\3\2\2\2\63\65\3\2\2\2\64\62\3\2\2\2\65\66\7\2\2\3\66\3\3\2\2\2\67"+
		"8\7\u00a4\2\289\b\3\1\29\5\3\2\2\2:<\7\4\2\2;:\3\2\2\2<?\3\2\2\2=;\3\2"+
		"\2\2=>\3\2\2\2>@\3\2\2\2?=\3\2\2\2@I\5\b\5\2AC\7\4\2\2BA\3\2\2\2CD\3\2"+
		"\2\2DB\3\2\2\2DE\3\2\2\2EF\3\2\2\2FH\5\b\5\2GB\3\2\2\2HK\3\2\2\2IG\3\2"+
		"\2\2IJ\3\2\2\2JO\3\2\2\2KI\3\2\2\2LN\7\4\2\2ML\3\2\2\2NQ\3\2\2\2OM\3\2"+
		"\2\2OP\3\2\2\2P\7\3\2\2\2QO\3\2\2\2RU\7L\2\2ST\7y\2\2TV\7v\2\2US\3\2\2"+
		"\2UV\3\2\2\2VX\3\2\2\2WR\3\2\2\2WX\3\2\2\2X\\\3\2\2\2Y]\5\16\b\2Z]\5\n"+
		"\6\2[]\5\f\7\2\\Y\3\2\2\2\\Z\3\2\2\2\\[\3\2\2\2]\t\3\2\2\2^_\7:\2\2_`"+
		"\7\3\2\2`a\5\16\b\2a\13\3\2\2\2bc\7:\2\2cd\7\3\2\2de\7\u009f\2\2e\r\3"+
		"\2\2\2fg\7\u0087\2\2gl\5\20\t\2hi\7\b\2\2ik\5\20\t\2jh\3\2\2\2kn\3\2\2"+
		"\2lj\3\2\2\2lm\3\2\2\2mo\3\2\2\2nl\3\2\2\2op\7P\2\2pu\5\22\n\2qr\7\b\2"+
		"\2rt\5\22\n\2sq\3\2\2\2tw\3\2\2\2us\3\2\2\2uv\3\2\2\2vz\3\2\2\2wu\3\2"+
		"\2\2xy\7\u0099\2\2y{\5$\23\2zx\3\2\2\2z{\3\2\2\2{\u0086\3\2\2\2|}\7S\2"+
		"\2}~\7+\2\2~\u0083\5\24\13\2\177\u0080\7\b\2\2\u0080\u0082\5\24\13\2\u0081"+
		"\177\3\2\2\2\u0082\u0085\3\2\2\2\u0083\u0081\3\2\2\2\u0083\u0084\3\2\2"+
		"\2\u0084\u0087\3\2\2\2\u0085\u0083\3\2\2\2\u0086|\3\2\2\2\u0086\u0087"+
		"\3\2\2\2\u0087\u0092\3\2\2\2\u0088\u0089\7t\2\2\u0089\u008a\7+\2\2\u008a"+
		"\u008f\5 \21\2\u008b\u008c\7\b\2\2\u008c\u008e\5 \21\2\u008d\u008b\3\2"+
		"\2\2\u008e\u0091\3\2\2\2\u008f\u008d\3\2\2\2\u008f\u0090\3\2\2\2\u0090"+
		"\u0093\3\2\2\2\u0091\u008f\3\2\2\2\u0092\u0088\3\2\2\2\u0092\u0093\3\2"+
		"\2\2\u0093\u0096\3\2\2\2\u0094\u0095\7g\2\2\u0095\u0097\5\34\17\2\u0096"+
		"\u0094\3\2\2\2\u0096\u0097\3\2\2\2\u0097\17\3\2\2\2\u0098\u00a8\7\n\2"+
		"\2\u0099\u009e\5\"\22\2\u009a\u009c\7$\2\2\u009b\u009a\3\2\2\2\u009b\u009c"+
		"\3\2\2\2\u009c\u009d\3\2\2\2\u009d\u009f\5\32\16\2\u009e\u009b\3\2\2\2"+
		"\u009e\u009f\3\2\2\2\u009f\u00a8\3\2\2\2\u00a0\u00a5\5\26\f\2\u00a1\u00a3"+
		"\7$\2\2\u00a2\u00a1\3\2\2\2\u00a2\u00a3\3\2\2\2\u00a3\u00a4\3\2\2\2\u00a4"+
		"\u00a6\5\32\16\2\u00a5\u00a2\3\2\2\2\u00a5\u00a6\3\2\2\2\u00a6\u00a8\3"+
		"\2\2\2\u00a7\u0098\3\2\2\2\u00a7\u0099\3\2\2\2\u00a7\u00a0\3\2\2\2\u00a8"+
		"\21\3\2\2\2\u00a9\u00aa\5\36\20\2\u00aa\23\3\2\2\2\u00ab\u00ac\5\36\20"+
		"\2\u00ac\25\3\2\2\2\u00ad\u00ae\5\36\20\2\u00ae\27\3\2\2\2\u00af\u00b0"+
		"\5\36\20\2\u00b0\31\3\2\2\2\u00b1\u00b2\t\2\2\2\u00b2\33\3\2\2\2\u00b3"+
		"\u00b8\7\u009d\2\2\u00b4\u00b5\7\u009d\2\2\u00b5\u00b6\7\b\2\2\u00b6\u00b8"+
		"\7\u009d\2\2\u00b7\u00b3\3\2\2\2\u00b7\u00b4\3\2\2\2\u00b8\35\3\2\2\2"+
		"\u00b9\u00c1\7\u009c\2\2\u00ba\u00c1\5,\27\2\u00bb\u00c1\7\u009f\2\2\u00bc"+
		"\u00bd\7\6\2\2\u00bd\u00be\5\36\20\2\u00be\u00bf\7\7\2\2\u00bf\u00c1\3"+
		"\2\2\2\u00c0\u00b9\3\2\2\2\u00c0\u00ba\3\2\2\2\u00c0\u00bb\3\2\2\2\u00c0"+
		"\u00bc\3\2\2\2\u00c1\37\3\2\2\2\u00c2\u00c4\5\26\f\2\u00c3\u00c5\t\3\2"+
		"\2\u00c4\u00c3\3\2\2\2\u00c4\u00c5\3\2\2\2\u00c5!\3\2\2\2\u00c6\u00c7"+
		"\5\30\r\2\u00c7\u00d1\7\6\2\2\u00c8\u00d2\7\n\2\2\u00c9\u00ce\5\26\f\2"+
		"\u00ca\u00cb\7\b\2\2\u00cb\u00cd\5\26\f\2\u00cc\u00ca\3\2\2\2\u00cd\u00d0"+
		"\3\2\2\2\u00ce\u00cc\3\2\2\2\u00ce\u00cf\3\2\2\2\u00cf\u00d2\3\2\2\2\u00d0"+
		"\u00ce\3\2\2\2\u00d1\u00c8\3\2\2\2\u00d1\u00c9\3\2\2\2\u00d2\u00d3\3\2"+
		"\2\2\u00d3\u00d4\7\7\2\2\u00d4#\3\2\2\2\u00d5\u00d6\b\23\1\2\u00d6\u00d7"+
		"\7\6\2\2\u00d7\u00d8\5$\23\2\u00d8\u00d9\7\7\2\2\u00d9\u00df\3\2\2\2\u00da"+
		"\u00db\5\26\f\2\u00db\u00dc\5(\25\2\u00dc\u00dd\5&\24\2\u00dd\u00df\3"+
		"\2\2\2\u00de\u00d5\3\2\2\2\u00de\u00da\3\2\2\2\u00df\u00e8\3\2\2\2\u00e0"+
		"\u00e1\f\5\2\2\u00e1\u00e2\7#\2\2\u00e2\u00e7\5$\23\6\u00e3\u00e4\f\4"+
		"\2\2\u00e4\u00e5\7s\2\2\u00e5\u00e7\5$\23\5\u00e6\u00e0\3\2\2\2\u00e6"+
		"\u00e3\3\2\2\2\u00e7\u00ea\3\2\2\2\u00e8\u00e6\3\2\2\2\u00e8\u00e9\3\2"+
		"\2\2\u00e9%\3\2\2\2\u00ea\u00e8\3\2\2\2\u00eb\u00ec\t\4\2\2\u00ec\'\3"+
		"\2\2\2\u00ed\u00ee\t\5\2\2\u00ee)\3\2\2\2\u00ef\u00f0\t\6\2\2\u00f0+\3"+
		"\2\2\2\u00f1\u00f2\t\7\2\2\u00f2-\3\2\2\2 \60\62=DIOUW\\luz\u0083\u0086"+
		"\u008f\u0092\u0096\u009b\u009e\u00a2\u00a5\u00a7\u00b7\u00c0\u00c4\u00ce"+
		"\u00d1\u00de\u00e6\u00e8";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}