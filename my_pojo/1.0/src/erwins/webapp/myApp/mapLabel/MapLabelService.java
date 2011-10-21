
package erwins.webapp.myApp.mapLabel;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import erwins.util.webapp.GenericAppEngineDao;
import erwins.util.webapp.GenericAppEngineService;
import erwins.webapp.myApp.Current;
import erwins.webapp.myApp.user.SessionInfo;

@Service
@Transactional(readOnly = true)
public class MapLabelService extends GenericAppEngineService<MapLaebl>{
    
	@Autowired private MapLabelDao dao;
    
    public Collection<MapLaebl> findAll(){
    	return dao.findAll();
    }
    
	@Transactional
	public String saveOrMerge(MapLaebl client){
		Date date = new Date();
		SessionInfo info = Current.getInfo().constraintLogin();
		if(client.getId()==null) {
			info.initGoogleId(client);
			client.setCreateDate(date);
			client.setUpdateDate(date);
			MapLaebl newOne =  dao.saveOrUpdate(client);
			return newOne.getId();
		}else{
			MapLaebl server =  dao.getById(client.getId());
			info.constraintAdminOrUser(server);
			server.setDescription(client.getDescription());
			server.setDisplayType(client.getDisplayType());
			server.setLabel(client.getLabel());
			server.setLat(client.getLat());
			server.setLng(client.getLng());
			server.setUpdateDate(date);;
			return server.getId();
		}
	}
	
	@Override
	@Transactional
	public void delete(String id){
		MapLaebl server =  dao.getById(id);
		Current.getInfo().constraintLogin().constraintAdminOrUser(server);
		dao.delete(server);
	}

	@Override
	protected GenericAppEngineDao<MapLaebl> getDao() {
		return dao;
	}

}
