package erwins.webapp.myApp.mtgo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		String userId = req.getParameter("userIdForDeckList");
		if(StringUtil.isEmpty(userId)){
			SessionInfo info = Current.getInfo();	
			userId = info.getUser().getId();
		}
		List<Deck> list = (List<Deck>)deckService.findByGoogleUserId(userId);
		CollectionUtil.sort(list);
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
		List<String[]> rows = parseCsv(list);
		
		String[] header = rows.get(0);
		boolean isCollection = header[0].equals("Card Name") ; //콜렉션에서 내려받기한애 
		
		rows.remove(0);
		
		List<Card> cars = new ArrayList<Card>();
		
		for (String[] each : rows) {
			Card card = new Card();
			if(isCollection){
				card.setQuantity(StringUtil.getIntValue( each[1]  ));
				card.setCardName(each[0]);
			}else{
				card.setQuantity(StringUtil.getIntValue( each[0]  ));
				card.setCardName(each[1]);
			}
			card.setCardName(card.getCardName().replaceAll("Æ","Ae")); //이거 WAS에서는 안된다.. ㅅㅂ
			cars.add(card);
		}
		deckService.updadteCard(id,cars);
		return new AjaxTextView("{'success':true}"); //EXT js 는 일케 해야함.. ㅅㅂ 짱나. 한글도 깨진다.
	}
	
	private static final String SEPARATOR = "mtgo_line_separator";
	private static final Pattern IN_DATA = Pattern.compile("\".+\""); // ""로 둘러싸인놈
	
	/** 임시로 만든 파서이다. 
	 *  MTGO는 ,가 들어가는놈을 ""로 묶어서 보낸다. */
	private static List<String[]> parseCsv(List<String> list){
		List<String[]> result = new ArrayList<String[]>();
		for (String string : list) {
			Matcher m = IN_DATA.matcher(string);
	        if(m.find()){
	        	String text =  m.group();
	        	text = text.replaceAll("\"", "").replaceAll(",",SEPARATOR);
	        	String replaced = m.replaceAll(text);
	        	String[] resultRow = replaced.split(",");
	        	for (int i = 0; i < resultRow.length; i++)  resultRow[i] = resultRow[i].replaceAll(SEPARATOR, ",").trim() ;
	        	result.add(resultRow);	
	        }else{
	        	String[] resultRow = string.split(",");
	        	for (int i = 0; i < resultRow.length; i++)  resultRow[i] = resultRow[i].replaceAll(SEPARATOR, ",").trim() ;
	        	result.add(string.split(","));	
	        }
		}
		return result;
	}

}
