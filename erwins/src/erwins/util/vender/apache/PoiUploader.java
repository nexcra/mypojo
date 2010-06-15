
package erwins.util.vender.apache;

import java.io.File;
import java.sql.SQLException;

import erwins.util.jdbc.JDBC;
import erwins.util.lib.Strings;
import erwins.util.vender.apache.PoiSheetReaderRoot.StringColimnPoiCallback;


/**
 * oracle uploader.. 간단 업로드/통계쿼리용.성능 구림.
 * 나중에 다시 만들자.
 */
public class PoiUploader{
	
	private final PoiReader poi;
	private final JDBC jdbc;
	
	/** date타입 지정 주의  
	 * ex) 오라클 : 2010-01-29 09:22:15.0 => jdbc.setDefaultTimeStampFormat(); */
	public PoiUploader(File file,JDBC jdbc){
		this.poi = new PoiReader(file);
		this.jdbc = jdbc;
	}
	
	/** 한방에 전부 로드한다. 메모리 주의! */
    public void upload(){
    	try {
			for(final PoiSheetReader sheet : poi){
				sheet.read(new StringColimnPoiCallback(){
					private StringBuilder prefix;
					@Override
					protected void readColimn(String[] line) {
						if(prefix==null){
							prefix = new StringBuilder("INSERT INTO ");
							prefix.append(sheet.getSheetName());
							prefix.append(" (");
							prefix.append(Strings.joinTemp(column,","));
							prefix.append(" ) VALUES (");
						}
						StringBuilder sql = new StringBuilder(prefix);
						boolean first = true;
				        for (Object string : line) {
				            if (!first) sql.append(",");
				            else first = false;
				            sql.append( string.equals("") ? "null" : "'"+ string+"'");
				        }
						sql.append(")");
						try {
							jdbc.update(sql.toString());
						} catch (SQLException e) {
							throw new RuntimeException(e);
						}
					}
				});
			}
			jdbc.commit();
		} catch (RuntimeException e) {
			jdbc.rollback();
			throw e;
		} catch (SQLException e) {
			jdbc.rollback();
			throw new RuntimeException(e);
		}

    }
	
    /**
     * 임시 테이블이라 무조건 VC2(4000) 이다. 
     * 애네들은 커밋이라는게 없다. 주의!   */
    public void makeTable(String tableName,String[] colims) throws SQLException{
    	
        try { //야메 테이블 테스트 ㅋㅋ
			jdbc.isContain("select count(*) from " + tableName);
		} catch (SQLException e) {
			return;
		}
		
        StringBuilder str = new StringBuilder("CREATE TABLE "+tableName+" ");
        str.append("(");
        boolean isFirst = true;
        for(String row : colims){
            if(isFirst) isFirst = false;
            else str.append(","); 
            str.append(row);
            str.append(" VARCHAR2 (4000)");
        }
        str.append(")");
        jdbc.update(str.toString());
        jdbc.update("COMMENT ON TABLE "+tableName+" IS ' POI로 제작된 테이블 입니다.'");
    }
    
}
