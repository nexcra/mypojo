
package erwins.webapp.myApp.mtgo;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import erwins.util.webapp.GenericAppEngineCacheDao;
import erwins.util.webapp.GenericAppEngineCacheService;

@Service
@Transactional(readOnly = true)
public class CardService extends GenericAppEngineCacheService<Card>{
    
	@Autowired private CardDao cardDao;
    
    public Collection<Card> findAll(){
    	return cardDao.findAll();
    }

	@Override
	protected GenericAppEngineCacheDao<Card> getDao() {
		return cardDao;
	}

}
