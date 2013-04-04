
package erwins.util.vender.apache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.ListOrderedMap;


/**
 * Groovy용 팩토리.
 * 덕타입임으로 인터페이스가 필요 없다.
 */
public abstract class PoiReaderFactory{
	
	public static Object instance(String f){
		return instance(new File(f));
	}
    
	public static Object instance(File f){
		if(!f.exists()){
			String path = f.getAbsolutePath();
			f = new File(path+".xlsx");
			if(!f.exists()){
				f = new File(path+".xls");
				if(!f.exists()) throw new RuntimeException(path + " : FileNotFound");
			}
		}
		//if(!f.exists()) f = new File(f.getAbsolutePath()+".");
		String name = f.getName();
		if(name.endsWith(".xlsx")) return new PoiReader(f); 
		else if(name.endsWith(".xls")) return new PoiReader2002(f);
		else return new PoiReader(f); //없으면 걍 최신버전이라고 간주.
	}
	
	public static Map<String,List<String[]>> simpleMap(File file){
		try {
			return simpleMap(file.getName(),new FileInputStream(file));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	/** java에서 간단 읽기용. */
	public static Map<String,List<String[]>> simpleMap(String name,InputStream in){
		@SuppressWarnings("unchecked")
		Map<String,List<String[]>> xls = new ListOrderedMap();
		if(name.endsWith(".xls")) {
			PoiReader2002 reader = new PoiReader2002(in);
			for(PoiSheetReader2002 sheet: reader){
				List<String[]> lines = new ArrayList<String[]>();
				for(String[] xlsLine : sheet) lines.add(xlsLine);
				xls.put(sheet.getSheetName(),lines);
			}
		}else{
			PoiReader reader = new PoiReader(in);
			for(PoiSheetReader sheet: reader){
				List<String[]> lines = new ArrayList<String[]>();
				for(String[] xlsLine : sheet) lines.add(xlsLine);
				xls.put(sheet.getSheetName(),lines);
			}
		}
		return xls;
	}
    
}
