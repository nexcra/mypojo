package erwins.util.root;


/**
 * HIbernate Dao 직전 기본값을 세팅해준다.
 * @author     erwins(my.pojo@gmail.com)
 */
public interface EntityInit{
    
    /**
     * 기본 설정값을 세팅한다.
     * genericDao같이 한곳에서만 적용하게 만들자.
     */    
    public void initValue();
    
}