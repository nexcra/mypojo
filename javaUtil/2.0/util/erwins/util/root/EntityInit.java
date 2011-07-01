package erwins.util.root;


/**
 * HIbernate Dao 직전 기본값을 세팅해준다. 
 * 보통 입력일/수정일 등을 표시할때 사용된다.
 */
public interface EntityInit{
    
    /**
     * 기본 설정값을 세팅한다.
     * genericDao같이 한곳에서만 적용하게 만들자.
     */    
    public void initValue();
    
}