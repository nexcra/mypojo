
package erwins.webapp.myApp.mtgo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import erwins.util.webapp.GenericAppEngineDao;
import erwins.util.webapp.GenericAppEngineService;

@Service
@Transactional(readOnly = true)
public class DeckService extends GenericAppEngineService<Deck>{
    
	@Autowired private DeckDao deckDao;
    
    public Collection<Deck> findAll(){
    	return deckDao.findAll();
    }
    
    public List<Card> getCardList(String id){
    	Deck server = deckDao.getById(id);
    	List<Card> loaded = new ArrayList<Card>();
    	loaded.addAll(server.getCards());
    	return server.getCards();
    }
    
    public void deckCal(String id){
    	Deck server = deckDao.getById(id);
    	new MTGO().loadCard(server.getCards());
    	BigDecimal sum = BigDecimal.ZERO;
    	for(Card each : server.getCards()){
    		if(each.getMoney()!=null) sum = sum.add(each.getMoney());
    	}
    	server.setSumOfPrice(sum);
    }
    
    @Transactional
    public Deck updateWinRate(String id,boolean isWin,boolean isMinus){
    	Deck server = deckDao.getById(id);
    	int count = isMinus ? -1 : 1;
    	if(isWin) server.setWin(server.getWin()+count);
    	else server.setLose(server.getLose()+count);
    	return server;
    }
    
    /** 무조건 다 지우고 새로 입력한다 */
    @Transactional
    public void updadteCard(String id,List<Card> list){
    	Deck server = deckDao.getById(id);
    	server.setCards(list);
    }

	@Override
	protected GenericAppEngineDao<Deck> getDao() {
		return deckDao;
	}

	public Collection<Deck> findByGoogleUserId(String googleUserId) {
		return deckDao.findByGoogleUserId(googleUserId);
	}
	

}
