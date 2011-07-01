package erwins.util.root;

import java.io.Serializable;

/**
 * Entity의 interface
 */
public interface EntityId<ID extends Serializable> extends Serializable,DomainObject{
    
    public static final String ID_NAME = "id";

    /** entity의 Key를 반환한다. */
    public ID getId();
    
    /** entity의 Key를 입력한다. */
    public void setId(ID id);
    
}