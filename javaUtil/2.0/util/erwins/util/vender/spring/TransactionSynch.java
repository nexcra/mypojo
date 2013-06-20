package erwins.util.vender.spring;

import java.util.List;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/** 
 * 이걸 확장해서 Spring컨테이너에 등록(싱글톤)하자. (static을 쓰지 않는 이유 : 커밋,롤백 시에 스프링컨테이너에 등록된 빈이 사용될 가능성이 높다.)
* 여기서 나타나는 인스턴스가 트랜잭션 내에서  create될때 트랜잭션이 끝나면 afterCompletionCommit / afterCompletionRollback를 호출해준다.
* 물론 커밋 이후에 실행됨으로 내부커밋이 실패하더라도 DB커밋이 롤백되지는 않는다.
* ex) : 로직에 카드결계 연계소스가 포함되어있다.  시스템의 DB트랜잭션이 롤백되면 전송했던 카드결제 요청을 취소해야 한다. (그냥 놔두면 결재됨)
* !!중요!! DB트랜잭션을 기준으로 하는것임으로 이 안에서 ThreadLocal 기반의 DB리소스를 써서는 안된다. 
* 
* @Inject private Provider<XpayTrensaction> xpayTrensaction;  이런식으로 사용.  Provider를 get()할때 트랜잭션 내에 있어야한다.
* 
* 상속을 싫어하거나, 구조적으로 다른객체의 상속을 받아야 된다면 별도 Factory를 만들어 쓸것.
 */
public abstract class TransactionSynch extends TransactionSynchronizationAdapter{

	/** 생성될때 등록된다. */
	public TransactionSynch(){
		TransactionSynchronizationManager.registerSynchronization(this);
	}
	
	/** 맨 처음꺼 걍 가져온다. 한개 이상 등록되어있으면 곤란함. 체크 안함. */
	public TransactionSynch getTransactionSynch(){
		List<TransactionSynchronization> synchs = TransactionSynchronizationManager.getSynchronizations(); 
		for(TransactionSynchronization each : synchs){
			if(TransactionSynch.class.isInstance(each)) return (TransactionSynch) each;
		}
		return null;
	}
	
	@Override
	public void afterCompletion(int status) {
		if(!TransactionSynchronizationManager.isSynchronizationActive()) throw new IllegalStateException("transaction is not Avtive"); 
		try{
			if(STATUS_COMMITTED == status) afterCompletionCommit();
			else afterCompletionRollback();
		}finally{
			//TransactionSynchronizationManager.unbindResourceIfPossible(worker.getClass().getName()); //리소스 있으면 추가로 풀기
		}
	}
	
	protected void afterCompletionCommit(){
		// non
	}
	protected void afterCompletionRollback(){
		// non
	}
	
}
