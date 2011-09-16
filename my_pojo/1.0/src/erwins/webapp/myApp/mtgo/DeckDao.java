
package erwins.webapp.myApp.mtgo;

import java.util.Collection;

import org.springframework.stereotype.Repository;

import erwins.util.webapp.GenericAppEngineDao;

@Repository
public class DeckDao extends GenericAppEngineDao<Deck>{
    
	private static final String ORDER = "googleUserId";
	
	@Override
	public Collection<Deck> findAll(){
		return  findAll(ORDER);
	}
	
	public Collection<Deck> getByGoogleUserId(String googleUserId){
		Collection<Deck> result = getJdoTemplate().find(getPersistentClass(),"googleUserId == PgoogleUserId","String PgoogleUserId",googleUserId);
		return result;
	}

}
