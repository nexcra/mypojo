package erwins.util.observer;



/**
 * Observer패턴시 사용하는 인터페이스
 * @author erwins(my.pojo@gmail.com)
 */
public interface UpdateAble{

    public void update(Observer observer,Object arg);
    
}