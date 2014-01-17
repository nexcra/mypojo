package erwins.util.counter;

import java.io.Serializable;


/**
 * 맵으로 숫자세기.  MultiSet이 int 단위밖에 안되서 대체 사용한다 (로그 카운트용) 
 * @author sin
 */
@SuppressWarnings("serial")
public class MapCounter extends MapIdCounter<String> implements Serializable{
    
}
