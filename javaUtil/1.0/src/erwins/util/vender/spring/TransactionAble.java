package erwins.util.vender.spring;

/** 커밋 / 롤백에서 오류가 나지 않도록 주의하자. 트랜잭션 내에서 모두 검증이 되어야 한다. */
public interface TransactionAble{
	
	public void comiit();
		
	public void rollback();
	
}
