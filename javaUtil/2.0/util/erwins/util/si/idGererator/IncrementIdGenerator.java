package erwins.util.si.idGererator;

import javax.annotation.Resource;

/**
 * 오라클 시퀀스 같은 Increment 베이스의 채번기이다. Hibernate의 시퀀스를 흉내내어 만들었다. 
 * 동일 DB를 사용하는 모든 어플리케이션에서 빠른 성능과 무결성을 보장한다.
 * 모든 채번은 WAS에서만 이루어진다. DB에서 SEQ를 따지 않는다.
 * 오라클 시퀀스 1개당 LIMIT만큼의 키를 뻥튀기 / 캐싱 하여 사용한다
 * @author sin
 */
public class IncrementIdGenerator {

	/** 오라클 시퀀스 1개당 10000개의 키를 사용한다. */
    private static final Integer DEFAULT_LIMIT = 10000;
	
    @Resource private IncrementIdRepository incrementIdRepository;

    /** 변경시 조심해야 한다. */
    private int limit = DEFAULT_LIMIT;
    private long currentValue = 1;
    private long maxValue = 0;
    
    /** 키값을 가져온다. 
     * 1을   로드한 경우   10001~  20000 까지의 키를 리턴한다.
     * 109를 로드한 경우 1090001~2000000 까지의 키를 리턴한다. */
    public synchronized Long nextval(){
        if(currentValue > maxValue){
            Long oracleSeq = incrementIdRepository.nextval();
            currentValue = oracleSeq * limit + 1;
            maxValue = (oracleSeq+1) * limit;
        }
        return currentValue++;
    }
    
    public String nextvalString(){
        return String.valueOf(nextval());
    }

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public void setSeqDao(IncrementIdRepository seqDao) {
		this.incrementIdRepository = seqDao;
	}
    
    
}
