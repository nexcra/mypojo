
package erwins.webapp.myApp.mtgo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import erwins.util.webapp.GenericAppEngineDao;
import erwins.util.webapp.GenericAppEngineService;
import erwins.webapp.myApp.Current;
import erwins.webapp.myApp.user.SessionInfo;

@Service
@Transactional(readOnly = true)
public class DeckService extends GenericAppEngineService<Deck>{
    
	@Autowired private DeckDao dao;
    
    public Collection<Deck> findAll(){
    	return dao.findAll();
    }
    
    public List<Card> getCardList(String id){
    	Deck server = dao.getById(id);
    	List<Card> loaded = new ArrayList<Card>();
    	loaded.addAll(server.getCards());
    	return server.getCards();
    }
    
    @Transactional
	public String saveOrMerge(Deck client){
		Date date = new Date();
		SessionInfo info = Current.getInfo().constraintLogin();
		if(client.getId()==null) {
			info.initGoogleId(client);
			client.setCreateDate(date);
			client.setUpdateDate(date);
			Deck newOne =  dao.saveOrUpdate(client);
			return newOne.getId();
		}else{
			Deck server =  dao.getById(client.getId());
			info.constraintByUser(server);
			server.setColors(client.getColors());
			server.setName(client.getName());
			server.setType(client.getType());
			server.setDescription(client.getDescription());
			server.setNote(client.getNote());
			server.setUpdateDate(date);;
			return server.getId();
		}
	}
	
	@Override
	@Transactional
	public void delete(String id){
		Deck server =  dao.getById(id);
		Current.getInfo().constraintLogin().constraintByUser(server);
		dao.delete(server);
	}
    
    @Transactional
    public void deckCal(String id){
    	Deck server = dao.getById(id);
    	Current.getInfo().constraintLogin().constraintByUser(server);
    	//new MTGO().loadCard(server.getCards());
    	TcgPlayer.loadCard(server.getCards());
    	BigDecimal sum = BigDecimal.ZERO;
    	for(Card each : server.getCards()){
    		if(each.getPrice()!=null){
    			BigDecimal carmSum = each.getPrice().multiply(new BigDecimal(each.getQuantity()));
    			sum = sum.add(carmSum);
    		}
    	}
    	server.setSumOfPrice(sum);
    }
    
    @Transactional
    public Deck updateWinRate(String id,boolean isWin,boolean isMinus){
    	Deck server = dao.getById(id);
    	Current.getInfo().constraintLogin().constraintByUser(server);
    	int count = isMinus ? -1 : 1;
    	if(isWin) server.setWin(server.getWin()+count);
    	else server.setLose(server.getLose()+count);
    	return server;
    }
    
    @Transactional
    public Deck resetWinRate(String id){
    	Deck server = dao.getById(id);
    	Current.getInfo().constraintLogin().constraintByUser(server);
    	server.setWin(0);
    	server.setLose(0);
    	return server;
    }
    
    /** 무조건 다 지우고 새로 입력한다 */
    @Transactional
    public void updadteCard(String id,List<Card> list){
    	Deck server = dao.getById(id);
    	Current.getInfo().constraintLogin().constraintByUser(server);
    	server.setCards(list);
    }

	@Override
	protected GenericAppEngineDao<Deck> getDao() {
		return dao;
	}

	public Collection<Deck> findByGoogleUserId(String googleUserId) {
		return dao.findByGoogleUserId(googleUserId);
	}
	

}
