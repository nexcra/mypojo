package erwins.util.si.idGererator;

/**
 * 채번용 DAO 인터페이스
 * 테스트의 용이함을 위해 인터페이스로 만들었다.
 * @author sin
 */
public interface IncrementIdDao{
    
	/** oracle seq 등을 가져온다 */
    public long nextval();
    
}
