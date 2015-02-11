package erwins.util.root;

/** 
 * Spring tag에서 HTML을 구성할때 사용된다.
 * map 에 tag.put(nv.getValue(), nv.getName()) 형식으로 입력됨
 *  */
public interface NameValue{
    
	public String getName() ;
	public String getValue();
	
}
