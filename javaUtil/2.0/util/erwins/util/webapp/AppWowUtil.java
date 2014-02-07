
package erwins.util.webapp;

import lombok.Data;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;

import erwins.util.text.StringUtil;



/** Wow 도우미 ㅋ */
public abstract class AppWowUtil{
	
	public static WowCharData getItemLevel(String serverName,String name){
		try {
			WowCharData data = new WowCharData();
			data.setName(name);
			String html = AppUtil.getUrlText("http://kr.battle.net/wow/ko/character/"+serverName+"/"+name+"/simple", "UTF-8");
			for(String  line : Splitter.on('\n').split(html)){
				if(StringUtil.contains(line,"\"averageItemLevelEquipped\"" )){
					data.setAverageItemLevelEquipped(Integer.parseInt(CharMatcher.DIGIT.retainFrom(line)));
				}else if(StringUtil.contains(line,"\"averageItemLevelBest\"")){
					data.setAverageItemLevelBest(Integer.parseInt(CharMatcher.DIGIT.retainFrom(line)));
				}
			}
			return data;
		} catch (Exception e) {
			return null;
		}
	}
	
	@Data
	public static class WowCharData{
		private Integer averageItemLevelEquipped;
		private Integer averageItemLevelBest;
		private String name;
	}
	
}
