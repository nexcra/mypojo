package erwins.util.root;

import java.io.Serializable;

/**
 * Entity의 interface
 * @author erwins(my.pojo@gmail.com)
 */
public interface EntityId<ID extends Serializable> extends Serializable{
    
    public static final String ID_NAME = "id";

    /**
     * entity의 Key를 반환한다.
     */
    public ID getId();
    
    /**
     * entity의 작성자인 User클래스의 Key를 반환한다.
     */
    /*
    public ID getUserId();*/    
    
}