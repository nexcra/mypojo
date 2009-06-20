
package erwins.util.template;

import java.util.*;

import erwins.util.dom2.Dom;
import erwins.util.observer.Observer;
import erwins.util.observer.UpdateAble;
import erwins.util.root.Singleton;


/**
 * Code를  캐싱하는 Object입니다. 싱글톤 입니다. (싱글톤은 상속해서 구현하세요)
 * @author  erwins(my.pojo@gmail.com)
 */
@Singleton
public abstract class CodeManagerTemplit<T extends Dom<T>> implements UpdateAble{
    
    /**
     * @uml.property  name="list"
     */
    List<T> list = new ArrayList<T>();
    
    public void update(Observer observer,Object arg){
        
    }

    /**
     * @return
     * @uml.property  name="list"
     */
    public List<T> getList() {
        return Collections.unmodifiableList(list);
    }
    

}
