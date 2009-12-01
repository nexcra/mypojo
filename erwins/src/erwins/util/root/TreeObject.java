package erwins.util.root;


/**
 * Entity의 interface
 * @author erwins(my.pojo@gmail.com)
 */
public interface TreeObject<T>{

    /**
     * entity의 부모 Key를 반환한다.
     */
    public T getIdParent();
    
}