package erwins.util.jdbc

/** 어디까지나 임시 클래스임. */
public class OracleSqlTemplit {
	
	/** 통신여건상 한번의 커넥션으로 데이터를 얻어와야 했다. 걍 원 쿼리용 템플릿임. 
	 * 제약조건은 복잡해셔서 빼버렸다. 
	 * FLEX의 ADVANCED그리드에서 이러한 FLAT데이터를 지원한다. */
	public static final String TABLE_INFO_BY_ONEQUERY = '''
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
	/*
	public static final String TABLE_INFO_BY_ONEQUERY = '''
		SELECT m.table_name, 
		C.COMMENTs AS TAB_COMMENTS, 
		M.COLUMN_NAME, 
		m.data_type, 
		CONSTRAINT_TYPE ,
		cm.comments 
		FROM user_tab_columns m 
		JOIN user_tab_comments c 
		ON m.table_name = c.table_name 
		LEFT JOIN USER_CONS_COLUMNS a 
		ON A.COLUMN_NAME = M.COLUMN_NAME 
		AND A.TABLE_NAME = M.TABLE_NAME 
		LEFT JOIN USER_CONSTRAINTS b 
		ON a.CONSTRAINT_NAME = b.CONSTRAINT_NAME 
		JOIN user_col_comments cm 
		ON cm.TABLE_NAME = M.TABLE_NAME 
		AND cm.COLUMN_NAME = M.COLUMN_NAME 
		WHERE m.table_name NOT LIKE 'BIN$%' 
		ORDER BY m.table_name,
		M.COLUMN_NAME
		'''; */
}
