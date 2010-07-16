package erwins.swt;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import erwins.util.lib.Files;

public class StoreForList<T extends Serializable>{

	//getAbsolutePath()를 하지 않으면 안된다. ㅠㅠ
	public static final File ROOT = new File(new File("").getAbsolutePath(),"erwinsSWT");
	
	static{
		if(!ROOT.exists()) ROOT.mkdir();
	}
	
	public StoreForList(String key){
		store = new File(ROOT,key);
	}
	
	private final File store;
	
	public void add(T item){
		List<T> list = get();
		list.add(item);
		setObject(store, list);
	}
	
	public void remove(T item){
		List<T> list = get();
		list.remove(item);
		setObject(store, list);
	}
	
	public List<T> get(){
		List<T> list = null;
		try {
			list = Files.getObject(store);
		} catch (Exception e) {
			if(e.getCause() instanceof java.io.ObjectStreamException){
				System.out.println("저장된 자원과 버전이 달라 초기화 됩니다.");
				Files.delete(store);
			}else throw new RuntimeException(e); 
		}
		if(list==null) list = new ArrayList<T>();
		return list;
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
