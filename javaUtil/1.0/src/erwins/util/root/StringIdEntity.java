package erwins.util.root;

import java.io.Serializable;


/**
 * String으로 된 ID를 가진다... 사용 금지.
 * @author erwins(my.pojo@gmail.com)
 */
public interface StringIdEntity extends Serializable{

    /**
     * entity의 Key를 반환한다.
     */
    public String getId();
    
    
}