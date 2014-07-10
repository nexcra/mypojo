package erwins.util.spring.view;




/** 
 * JsonFactory라는 이름을 쓰기위해 만듬. 특별한 용도 없다.
 * ex) @Resource private JsonFactory jsonFactory;
 **/
public interface JsonFactory{
	
    public JsonView get();
	
}
