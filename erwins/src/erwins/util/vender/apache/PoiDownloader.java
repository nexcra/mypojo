
package erwins.util.vender.apache;

import java.util.List;

import erwins.util.jdbc.JDBC;
import erwins.util.tools.Mapp;


/**
 * DB -> Excell. 단순 백업용으로 역변환은 일단 고려하지 않는다.
 * 페이징 처리는 나중에 하자~ (귀차나)
 */
public class PoiDownloader{
	
	private final Poi poi;
	private final JDBC jdbc;
	
	public PoiDownloader(Poi poi,JDBC jdbc){
		this.poi = poi;
		this.jdbc = jdbc;
	}
	
	private static final String SQL = "select * from ";
    
	/** 한방에 전부 로드한다. 메모리 주의! */
    public void loadTable(String tableName){
    	List<Mapp> list = jdbc.select(SQL + tableName);
    	poi.setListedMapp(tableName, list);
    }
    
    
    
}
