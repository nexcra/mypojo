package erwins.util.vender.spring;

import org.springframework.transaction.support.TransactionSynchronizationManager;

/** 이걸 확장해서 Spring컨테이너에 등록하자. 
 * 여기서 나타나는 인스턴스가 트랜잭션 내에서  create될때 트랜잭션이 끝나면 commit / rollback을 자동으로 호출해준다.
 * 물론 커밋 이후에 실행됨으로 내부커밋이 실패하더라도 DB커밋이 롤백되지는 않는다. */
public abstract class TransactionAbleFactory{
	
	protected abstract TransactionAble createNew();
	
	
	/** 하나의 트랜잭션 내에서는 동일한 인스턴스가 리턴된다. 이 조건때문에 bine한다.  */
	public TransactionAble instance() {
		if(TransactionSynchronizationManager.hasResource(this)) return existInstance();
		return createNewInstance();
	}
	
	/** regist만 된다면 콜백은 동작한다. */
	private TransactionAble createNewInstance(){
		TransactionAble instance = createNew();
		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization(this));
		TransactionSynchronizationManager.bindResource(this, instance);
		return instance;
	}
	
	private TransactionAble existInstance(){
		return (TransactionAble)TransactionSynchronizationManager.getResource(this);
	}
	
}
