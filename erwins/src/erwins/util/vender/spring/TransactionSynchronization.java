package erwins.util.vender.spring;

import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/** 정상 커밋이 된 후에 이게 실행된다. 주의!  이게 실패하더라도 DB커밋이 실패하진 않는다. */
public class TransactionSynchronization extends TransactionSynchronizationAdapter{
	
	private TransactionAbleFactory key;
	
	public TransactionSynchronization( TransactionAbleFactory key) {
		this.key = key;
	}
	
	@Override
	public void afterCompletion(int status) {
		if(!TransactionSynchronizationManager.isSynchronizationActive()) throw new IllegalStateException("transaction is not Avtive"); 
		if(!TransactionSynchronizationManager.hasResource(key)) throw new IllegalStateException("Resource must not be null"); 
		TransactionAble worker = (TransactionAble)TransactionSynchronizationManager.getResource(key);
		try{
			if(STATUS_COMMITTED == status) worker.comiit();
			else worker.rollback();
		}finally{
			TransactionSynchronizationManager.unbindResource(key);	
		}
		
	}
	
}
