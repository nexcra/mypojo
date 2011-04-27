package erwins.util.root;


/**
 * 객체가 수정될때 바뀌어야 할 값을 세팅해준다. ORM에서만 사용된다. 
 */
public interface EntityMerge<T>{
    
    /**
     * client에서 가져온 값을 대입해 준다.
     */    
    public void mergeByClientValue(T client);
    
}