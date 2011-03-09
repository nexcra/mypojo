package erwins.util.root;

import java.io.Serializable;


/**
 * EntityId를 확장한다.
 * HibernateDao사용시 update할때 벨리데이션 체크를 해준다.
 * @author     erwins(my.pojo@gmail.com)
 */
public interface EntityOwnerValidator<ID extends Serializable> extends EntityId<ID>{
    
    /**
     * 1. 수정/삭제 등의 작업시 소유자와 작업자가 일치하는지 확인한다.
     * 대상 객체는 입력한 객체가 아니라 DB에 기저장된 자료의 userId이다.
     * 2. jsp에서 수정/삭제 등의 버튼이 보일지를 결정한다.
     */
    public void validateOwner();
    
}