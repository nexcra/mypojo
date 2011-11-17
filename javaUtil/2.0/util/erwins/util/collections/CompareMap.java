package erwins.util.collections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/** 첨부파일용으로 일단 제작 */
public class CompareMap<ID,EXIST,INPUT>{
	
	private Map<ID,EXIST> exist = new HashMap<ID,EXIST>();
	private Map<ID,INPUT> input = new HashMap<ID,INPUT>();
	
	public void putExist(ID key,EXIST value){
		exist.put(key,value);
	}
	public void putInput(ID key,INPUT value){
		input.put(key,value);
	}
	
	/** input요청이 왔으나 기존 자료에 없는거 */
	public List<INPUT> mayInsert(){
		List<INPUT> insert = new ArrayList<INPUT>();
		for(Entry<ID,INPUT> each : input.entrySet()){
			if(exist.get(each.getKey())==null) insert.add(each.getValue());
		}
		return insert;
	}
	
	/** 기존 자료에 있으니 input에는 들어오지 않은 자료 */
	public List<EXIST> mayDelete(){
		List<EXIST> delete = new ArrayList<EXIST>();
		for(Entry<ID,EXIST> each : exist.entrySet()){
			if(input.get(each.getKey())==null) delete.add(each.getValue());
		}
		return delete;
	}

}
