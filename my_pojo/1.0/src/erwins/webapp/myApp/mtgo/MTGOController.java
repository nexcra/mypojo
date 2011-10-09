package erwins.webapp.myApp.mtgo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;

import erwins.util.lib.CollectionUtil;
import erwins.util.lib.StringUtil;
import erwins.util.morph.MapToBean;
import erwins.util.temp.AppUploader;
import erwins.webapp.myApp.AjaxTextView;
import erwins.webapp.myApp.AjaxView;
import erwins.webapp.myApp.Current;
import erwins.webapp.myApp.RequestToMapForApp;
import erwins.webapp.myApp.RootController;
import erwins.webapp.myApp.user.SessionInfo;

@Controller
@RequestMapping("/mtgo/*")
public class MTGOController extends RootController {

	@Autowired
	private MapToBean mapToBean;
	@Autowired
	private RequestToMapForApp requestToMap;
	@Autowired
	private DeckService deckService;

	@RequestMapping("/page")
	public String normal() {
		return "mtgo/page";
	}

	@RequestMapping("/list")
	public View list(HttpServletRequest req) {
		SessionInfo info = Current.getInfo();
		List<Deck> list = (List<Deck>)deckService.findByGoogleUserId(info.getUser().getId());
		CollectionUtil.sort(list);
		for(int i=0;i<list.size();i++){
			list.get(i).setRownum(i+1);
		}
		return new AjaxView(list);
	}
	
	@RequestMapping("/cardList")
	public View cardList(HttpServletRequest req) {
		String id = req.getParameter("id");
		List<Card> cards = deckService.getCardList(id);
		Collections.sort(cards);
		return new AjaxView(cards);
	}
	
	
	@RequestMapping("/deckCal")
	public View deckCal(HttpServletRequest req) {
		String id = req.getParameter("id");
		deckService.deckCal(id);
		return new AjaxView("정상적으로 덱이 업데이트 되었습니다");
	}

	@RequestMapping("/save")
	public View save(HttpServletRequest req) {
		Deck deck = mapToBean.build(requestToMap.toMap(req), Deck.class);
		SessionInfo info = Current.getInfo();
		info.setGoogleId(deck);
		deckService.saveOrMerge(deck);
		return new AjaxView("정상적으로 저장되었습니다.");
	}

	@RequestMapping("/delete")
	public View delete(HttpServletRequest req) {
		String id = req.getParameter("id");
		deckService.delete(id);
		return new AjaxView("정상적으로 삭제되었습니다.");
	}

	@RequestMapping("/updateWinRate")
	public View updateWinRate(HttpServletRequest req) {
		String id = req.getParameter("id");
		boolean isWin = requestToMap.getBoolean(req, "isWin");
		boolean isMinus = requestToMap.getBoolean(req, "isMinus");
		Deck deck = deckService.updateWinRate(id, isWin, isMinus);
		return new AjaxView(deck);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping("/upload")
	public View upload(HttpServletRequest req,HttpServletResponse resp) {
		Map<String,Object> param = new AppUploader().uploadTextFile(req);
		String id = (String)param.get("id");
		List<String> list = (List<String>)param.get("deckFile");
		list.remove(0);
		
		List<Card> cars = new ArrayList<Card>();
		for (String each : list) {
			Integer quantity = StringUtil.getIntValue(StringUtil.getFirst(each, ","));
			String cardName = StringUtil.getFirstAfter(each, ",").replaceAll("\"","");
			cardName = cardName.replaceAll("Æ","Ae");
			Card card = new Card();
			card.setQuantity(quantity);
			card.setCardName(cardName);
			cars.add(card);
		}
		deckService.updadteCard(id,cars);
		return new AjaxTextView("{'success':true}"); //EXT js 는 일케 해야함.. ㅅㅂ 짱나. 한글도 깨진다.
	}

}
