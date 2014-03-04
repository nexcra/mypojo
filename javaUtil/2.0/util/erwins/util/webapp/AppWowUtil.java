
package erwins.util.webapp;

import lombok.Data;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;

import erwins.util.text.StringUtil;



/** Wow 도우미 ㅋ */
public abstract class AppWowUtil{
	
	/** 성공 여부를 리턴한다. */
	public static boolean addItemLevel(WowData data){
		try {
			String html = AppUtil.getUrlText("http://kr.battle.net/wow/ko/character/"+data.serverName+"/"+data.userName+"/simple", "UTF-8");
			for(String  line : Splitter.on('\n').split(html)){
				if(StringUtil.contains(line,"\"averageItemLevelEquipped\"" )){
					data.setAverageItemLevelEquipped(Integer.parseInt(CharMatcher.DIGIT.retainFrom(line)));
				}else if(StringUtil.contains(line,"\"averageItemLevelBest\"")){
					data.setAverageItemLevelBest(Integer.parseInt(CharMatcher.DIGIT.retainFrom(line)));
				}
			}
			return data.getAverageItemLevelBest()!=null && data.getAverageItemLevelEquipped()!=null;
		} catch (Exception e) {
			return false;
		}
	}
	
	@Data
	public static class WowData{
		private Integer averageItemLevelEquipped;
		private Integer averageItemLevelBest;
		private String serverName;
		private String userName;
	}
	
}
