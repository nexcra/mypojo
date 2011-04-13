package erwins.util.template;



/**
 * Observer패턴시 사용하는 인터페이스.  T로 바꾸자.
 * @author erwins(my.pojo@gmail.com)
 */
public interface UpdateAble{
    public void update(Observer observer,Object arg);
    
}