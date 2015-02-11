package erwins.util.spring.view;

import com.google.gson.Gson;




/** 
 * JsonFactory라는 이름을 쓰기위해 만듬. 특별한 용도 없다.
 * ex) @Resource private JsonFactory jsonFactory;
 **/
public interface GsonFactory{
	
    public GsonView get();
    public GsonView get(Object obj);
    public Gson gson();
	
}
