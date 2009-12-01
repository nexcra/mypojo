
package erwins.util.reflexive;


/**
 * 클래스를 수정하지 않고도 다른 구현이 가능한 visitor를 구현한다.
 * @author erwins(my.pojo@gmail.com)
 */
public interface Visitor<T>{

    public void visit(T target);

    /** Element는 오직 Accept만을 가진다. */
    public interface Acceptor<T> {
        void accept(Visitor<T> v);
    }
}
