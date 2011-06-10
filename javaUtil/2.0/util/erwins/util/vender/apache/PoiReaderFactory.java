
package erwins.util.vender.apache;

import java.io.File;


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
    
}
