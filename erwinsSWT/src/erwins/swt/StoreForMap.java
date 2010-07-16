package erwins.swt;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import erwins.util.lib.Files;

public class StoreForMap<T extends Serializable>{

	//getAbsolutePath()를 하지 않으면 안된다. ㅠㅠ
	public static final File ROOT = new File(new File("").getAbsolutePath(),"erwinsSWT");
	
	static{
		if(!ROOT.exists()) ROOT.mkdir();
	}
	
	public StoreForMap(String key){
		store = new File(ROOT,key);
	}
	
	private final File store;
	
	public void put(String key,T item){
		Map<String,T> map = get();
		map.put(key,item);
		setObject(store, map);
	}
	
	public void remove(String key){
		Map<String,T> map = get();
		map.remove(key);
		setObject(store, map);
	}
	
	public T get(String key){
		return get().get(key);
	}
	
	private Map<String,T> get(){
		Map<String,T> map = null;
		try {
			map = Files.getObject(store);
		} catch (Exception e) {
			if(e.getCause() instanceof java.io.ObjectStreamException){
				System.out.println("저장된 자원과 버전이 달라 초기화 됩니다.");
				Files.delete(store);
			}else throw new RuntimeException(e); 
		}
		if(map==null) map = new HashMap<String,T>();
		return map;
	}
	
	/** 임시메소드~ */
	public static void setObject(File file, Object obj) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = new FileOutputStream(file);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(obj);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (fos != null)
					fos.close();
				if (oos != null)
					oos.close();
			} catch (IOException e) {
				// 무시한다.
			}
		}
	}	


}
