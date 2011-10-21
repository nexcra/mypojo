package erwins.webapp.myApp;

import erwins.util.root.DomainObject;
import erwins.util.root.EntityHibernatePaging;
import erwins.util.root.EntityId;

public interface RootEntity<T> extends DomainObject,EntityId<String>,Comparable<T>,EntityHibernatePaging{
	
}
