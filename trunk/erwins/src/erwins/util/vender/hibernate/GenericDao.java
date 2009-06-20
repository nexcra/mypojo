package erwins.util.vender.hibernate;

import java.io.Serializable;

import erwins.util.tools.SearchMap;

public interface GenericDao<Entity, ID extends Serializable>{

    public void findBy(SearchMap map);


}
