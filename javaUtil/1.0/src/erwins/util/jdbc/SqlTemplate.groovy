package erwins.util.jdbc

public final class SqlTemplate {
	
	/** 통신여건상 한번의 커넥션으로 데이터를 얻어와야 했다. 걍 원 쿼리용 템플릿임. 
	 * 제약조건은 복잡해셔서 빼버렸다. 
	 * FLEX의 ADVANCED그리드에서 이러한 FLAT데이터를 지원한다.
	 * 테이블명 | 테이블코멘트 | 컬럼명 | 컬럼타입 | 컬럼코멘트 순이다. */
	public static final String ORACLE_TABLE_INFO_BY_ONEQUERY = '''
		SELECT m.table_name, 
	       C.COMMENTs AS TAB_COMMENTS, 
	       M.COLUMN_NAME, 
	       m.data_type, 
	       cm.comments
	  FROM user_tab_columns m 
	   JOIN user_tab_comments c 
	       ON m.table_name = c.table_name  
	   JOIN user_col_comments cm 
	       ON cm.TABLE_NAME = M.TABLE_NAME 
	       AND cm.COLUMN_NAME = M.COLUMN_NAME 
	 WHERE m.table_name NOT LIKE 'BIN$%' 
	ORDER BY m.table_name,
	       M.COLUMN_NAME
		''';
	
   /** 테이블명 | '' | 컬럼명 | 컬럼타입 | '' 순이다. */
	public static final String MS_SQL_TABLE_INFO = '''
		select table_name as TABLE_NAME,'' as TAB_COMMENTS,column_name AS COLUMN_NAME,data_type AS DATA_TYPE,'' as COMMENTS
		from information_schema.columns  order by table_name
		''';
	
	/** 테이블명 | '' | 컬럼명 | 컬럼타입 | '' 순이다. */	
	public static final String MY_SQL_TABLE_INFO = '''
		select table_name as TABLE_NAME,'' as TAB_COMMENTS,column_name AS COLUMN_NAME,column_type AS DATA_TYPE,'' as COMMENTS
		from information_schema.columns  where table_schema != 'information_schema'  order by table_name
		''';	
}
