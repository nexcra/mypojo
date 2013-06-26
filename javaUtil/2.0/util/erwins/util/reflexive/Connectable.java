package erwins.util.reflexive;

import java.io.Serializable;
import java.util.List;

import erwins.util.root.EntityId;
import erwins.util.root.Pair;


/** 인터페이스를 inner class로 두면 이클립스 버그 발생. */
public interface Connectable<ID extends Serializable,T> extends EntityId<ID>,Comparable<T>,Pair{

    /**
     * 부모값을 반환한다. 부모가 없으면 null을 반환한다.
     */    
    public T getParent();
    public void setParent(T parent);
    
    /**
     * 자식을 추가한다.
     */
    public void addChildren(T child);
    
    /** 자식을 반환한다. 없으면 size가 0인 collection을 리턴한다. */
    public List<T> getChildren();

    
}

