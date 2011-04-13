package erwins.util.morph;


import java.util.Map;

import erwins.util.valueObject.ValueObject;

/**
 * 도메인 테스트 도우미. ,로 문자를 등분한다.
 */
@SuppressWarnings("serial")
public class AdressSample implements ValueObject{
	
	private String post;
	private String adressName;

	@SuppressWarnings("rawtypes")
	@Override
	public void initValue(Object obj) {
		if(Map.class.isInstance(obj)){
			Map map = (Map)obj;
			post = (String)map.get("post");
			adressName = (String)map.get("adressName");
		}else{
			String[] temp = obj.toString().split("\\,");
			post = temp[0];
			adressName = temp[1];	
		}
	}

	@Override
	public Object returnValue() {
		return post + "," + adressName;
	}

	public String getPost() {
		return post;
	}

	public void setPost(String post) {
		this.post = post;
	}

	public String getAdressName() {
		return adressName;
	}

	public void setAdressName(String adressName) {
		this.adressName = adressName;
	}

	
	
	
	
	
	
	
	
}