
package erwins.test.runAndTakeResult;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

import org.junit.Test;

import erwins.util.collections.map.RequestMap;
import erwins.util.jdbc.JDBC;
import erwins.util.lib.StringUtil;
import erwins.util.vender.apache.Poi;

public class DB_SqlGenerate{
    @Test
    public void simsa() throws SQLException{
    	String sql = "";
    	sql+="SELECT a.BOOK_NM, a.PUBLISH,a.AUTHOR,a.ETC,a.PAGE,a.PRICE,a.PUBL_DATE, a.SCAN_CD,COUNT(*) "; 
    	sql+="FROM TB_BOOK a ";
    	sql+="GROUP BY a.BOOK_NM, a.publish,a.AUTHOR,a.ETC,a.PAGE,a.PRICE,a.PUBL_DATE, a.SCAN_CD ";
    	sql+="HAVING COUNT(*) > 1 ";
    	sql+="ORDER BY a.BOOK_NM ";
    	
    	JDBC jdbc = JDBC.oracleInstance("", "",true, "", "loms", "loms");
    	Poi poi = new Poi();
    	poi.addSheet("서적정보",new String[]{"책이름(링크)","출판사","저자","기타","페이지수","가격","발행일","중복건수"});
    	
    	String url = "http://e-simsa.or.kr/contents/{0}.pdf";
    	
    	List<RequestMap> maps = jdbc.select(sql);
    	for(RequestMap each : maps){
    		poi.addValues(each.getString("BOOK_NM"),each.getString("PUBLISH"),each.getString("AUTHOR")
    				,each.getString("ETC"),each.getString("PAGE"),each.getString("PRICE")
    				,each.getString("PUBL_DATE"),each.getString("COUNT(*)"));
    		String code = each.getString("SCAN_CD");
    		poi.addUrlHyperlink(0,StringUtil.format(url,code));
    	}
    	File xls = new File("D:/book1.xls");
    	poi.wrap();
    	poi.write(xls);
    }	

}