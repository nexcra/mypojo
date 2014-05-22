package erwins.util.spring;

import java.util.List;

import com.google.common.collect.Lists;

/** LineMetadata의 간단버전
 * ex) LineMetadatas.newInstance("accId","maxBidCost","ad.adId","peric.pericId").get()  */
public class LineMetadatas{
	
	private List<LineMetadata> lineMetadatas = Lists.newArrayList();
	
	public static LineMetadatas of(String ... fieldNames){
		LineMetadatas vo = new LineMetadatas();
		int index = 0;
		for(String fieldName : fieldNames){
			vo.add(index++, fieldName, fieldName);
		}
		return vo;
	}
	
	public LineMetadatas add(Integer index,String fieldName,String name){
		lineMetadatas.add(new LineMetadata(index,fieldName,name));
		return this;
	}
	
	public LineMetadatas add(Integer index,String fieldName){
		lineMetadatas.add(new LineMetadata(index,fieldName,fieldName));
		return this;
	}
	
	public List<LineMetadata> get(){
		return lineMetadatas;
	}
	
}