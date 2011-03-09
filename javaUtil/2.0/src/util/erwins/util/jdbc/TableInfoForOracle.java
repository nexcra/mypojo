package erwins.util.jdbc;

import java.sql.SQLException;


/**
 * Oracle SQL을 생성해 보아요~ 노가다는 싫어. 소스가 저질이다. ㅠㅠ. 그루비로 짰으면 좀더 엘레강스 했을것을..
 */
public class TableInfoForOracle extends TableInfos{
	
	//private static final String DICTIONARY_TABLE
	
	/** 세션동안 constraint를 중지하는  오라클 명령어 */
	public static final String NO_CONSTRAINT = "alter session set constraint  = deferred";
	/** 세션동안 constraint를 중지를 복구하는  오라클 명령어 */
	public static final String RE_CONSTRAINT = "alter session set constraint  = immediate";
	
	/** 그냥 한번 빼봤어.. 사용할일은 없을듯. */
	public static final String USER_TABLES = "select a.TABLE_NAME\"name\",b.COMMENTS\"comment\" " +
			"from USER_tables a join USER_TAB_COMMENTS b on a.TABLE_NAME = b.TABLE_NAME";
    
    /** 테이블/컬럼 정보를 로드한다. */
    public TableInfoForOracle(JDBC jdbc) throws SQLException{
        super(jdbc);
        
        tables = jdbc.select(USER_TABLES,TableInfo.class);
        
        StringBuilder temp = new StringBuilder();
        temp.append("SELECT M.COLUMN_NAME\"name\",");
        temp.append(" (select 'true'");
        temp.append("  from USER_CONS_COLUMNS A join USER_CONSTRAINTS B on A.CONSTRAINT_NAME = B.CONSTRAINT_NAME");
        temp.append("  where B.CONSTRAINT_TYPE = 'P'");
        temp.append("  and A.COLUMN_NAME = M.COLUMN_NAME");
        temp.append("  and A.TABLE_NAME = M.TABLE_NAME )\"pk\" ");
        temp.append("from user_tab_columns M ");
        temp.append("where M.table_name = ");
        String columnScanSql = temp.toString();
        for(TableInfo each : tables) each.columns = jdbc.select(columnScanSql + "'"+each.name+"'",ColumnInfo.class);
    }
}


