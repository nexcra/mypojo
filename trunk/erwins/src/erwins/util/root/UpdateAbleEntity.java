package erwins.util.root;


/**
 * HibernateDao사용시 벨리데이션 체크와 update를 수행해준다.
 * @author     erwins(my.pojo@gmail.com)
 */
public interface UpdateAbleEntity<T,ID> extends DefaultEntity<ID>{
    
    /**
     * client에서 받아온 자료를 사용하겨 수정 작업을 한다.
     */
    public void update(T client);
    
    /**
     * 수정/삭제 등의 작업시 소유자와 작업자가 일치하는지 확인한다.
     */
    public void validate();
    
}