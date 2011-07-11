
package erwins.webapp.myApp.mapLabel;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import erwins.util.webapp.GenericAppEngineDao;
import erwins.util.webapp.GenericAppEngineService;

@Service
@Transactional(readOnly = true)
public class MapLabelService extends GenericAppEngineService<MapLaebl>{
    
	@Autowired private MapLabelDao mapLabelDao;
    
    public Collection<MapLaebl> findAll(){
    	return mapLabelDao.findAll();
    }

	@Override
	protected GenericAppEngineDao<MapLaebl> getDao() {
		return mapLabelDao;
	}

}
