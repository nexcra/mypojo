package erwins.util.root;


/**
 * iBatis등 직접 parent를 매핑할 수 없을때 사용한다.
 */
public interface TreeObject<T>{

    /**
     * entity의 부모 Key를 반환한다.
     */
    public T getParentId();
}