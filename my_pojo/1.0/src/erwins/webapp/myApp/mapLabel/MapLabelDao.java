
package erwins.webapp.myApp.mapLabel;

import java.util.Collection;

import org.springframework.stereotype.Repository;

import erwins.util.webapp.GenericAppEngineDao;

@Repository
public class MapLabelDao extends GenericAppEngineDao<MapLaebl>{
    
	private static final String ORDER = "googleUserId";
	
	@Override
	public Collection<MapLaebl> findAll(){
		return  findAll(ORDER);
	}
	
	public Collection<MapLaebl> getByGoogleUserId(String googleUserId){
		Collection<MapLaebl> result = getJdoTemplate().find(getPersistentClass(),"googleUserId == PgoogleUserId","String PgoogleUserId",googleUserId);
		return result;
	}

}
