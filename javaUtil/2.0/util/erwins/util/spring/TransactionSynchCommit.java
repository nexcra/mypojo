package erwins.util.spring;

import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/** 
 * static 범용으로 제작.
 * 한개만 등록된다고 가정하고 심플하게 제작함.
 * 
 * 다수 등록시에는 CsvLogWriter 처럼 코딩해야함
 */
public abstract class TransactionSynchCommit{
	
	@SuppressWarnings("unchecked")
	public static <T extends AfterCompletionAble> T  getResource(Class<?> clazz){
		if(!TransactionSynchronizationManager.isSynchronizationActive()) throw new IllegalStateException("transaction is not Avtive");
		return (T) TransactionSynchronizationManager.getResource(clazz);
	}
	
	/** 현재 트랜잭션이 끝날때, AfterCompletionCommitAble가 호출된다. */
	public static void registerSynchronization(final AfterCompletionAble able){
		TransactionSynchronizationManager.bindResource(able.getClass(), able);
		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter(){
			@Override
			public void afterCompletion(int status) {
				if(!TransactionSynchronizationManager.isSynchronizationActive()) throw new IllegalStateException("transaction is not Avtive"); 
				try{
					if(STATUS_COMMITTED == status) able.afterCompletionCommit();
					else able.afterCompletionRollback();
				}finally{
					TransactionSynchronizationManager.unbindResourceIfPossible(able.getClass());
				}
			}
		});
	}
	
	public static interface AfterCompletionAble{
		public void afterCompletionCommit();
		public void afterCompletionRollback();
	}
	
}
