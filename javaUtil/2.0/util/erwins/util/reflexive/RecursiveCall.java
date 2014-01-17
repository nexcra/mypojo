package erwins.util.reflexive;

import java.io.Serializable;
import java.util.List;

import org.apache.ecs.html.A;
import org.apache.ecs.html.LI;
import org.apache.ecs.html.UL;
import org.springframework.core.convert.converter.Converter;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import erwins.util.tools.StringAppender;


/** 패턴을 유사하게 만들기 위해 합쳤다. */
public abstract class RecursiveCall{
	
	/** HTML의 UL / LI 태그를 만들어 낸다. 각종 메뉴, 트리 등에 사용될 수 있다.
	 * DATA만 남긴 후 HTML에서 조합하는거도 괜찮지만, 성능이 안나오는 경우가 많고 편의상 이렇게 한다. (JSP에서 재귀호출이 되나? ) */
	public static <ID extends Serializable,VO extends Connectable<ID,VO>> UL buildHtml(List<VO> roots,Converter<VO,String> toHref) {
		UL ul = new UL();
    	for(VO each : roots){
    		LI li = new LI();
    		li.setNeedClosingTag(true); //UL과는 달리 LI는 기본설정으로 엔드태그가 없다.
    		A a = new A();
    		a.setTagText(each.getName());
    		List<VO> children = each.getChildren();
    		if(children.size()==0){
    			String href = toHref.convert(each);
    			a.setHref(href);
    			li.addElement(a);
    		}else{
    			a.setHref("#");
    			li.addElement(a);
    			UL childUl = buildHtml(children,toHref);
    			li.addElement(childUl);
    		}
    		ul.addElement(li);
    	}
    	return ul;
    }
	
	private static final Joiner LINE = Joiner.on('\n').skipNulls();
	
	/** ext.js의 메뉴를 생성한다. 프로젝트별로 잘 수정해서 사용할것
	 * ==> 당연히 이렇게 하면 안되고 data만 넘긴 다음 스크립트에서 조립해서 사용해야 한다. 여긴 그냥 귀찮아서 이렇게 놔둠 */
	@Deprecated
	public static <ID extends Serializable,VO extends Connectable<ID,VO>> String buildExtMenue(List<VO> roots,Converter<VO,String> toHref) {
		List<String> list = Lists.newArrayList();
    	for(VO each : roots){
    		List<VO> children = each.getChildren();
    		if(children.size()==0){
    			//String text = "{icon:leaveIcon,text:'"+each.getName()+"',handler: function(){ location.href =  '"+toHref.convert(each)+"'; } },"; 
    			String text = "{icon:leaveIcon,text:'"+each.getName()+"',path:'"+toHref.convert(each)+"',handler:extMenuGoHandler},";
    			list.add(text);
    		}else{
    			StringAppender menu = new StringAppender();
    			menu.appendLine("Ext.create('Ext.menu.Menu', {");
    			menu.appendLine("style: { overflow: 'visible'},");
    			menu.appendLine("items: [");
    			menu.appendLine(buildExtMenue(children,toHref));
    			menu.append("]})");
    			String text = "{icon:folderIcon,text:'"+each.getName()+"',menu: "+ menu +" },";
    			list.add(text);
    		}
    	}
    	return LINE.join(list);
    }
	
	/** JsTree를 생성한다. 프로젝트별로 잘 수정해서 사용할것
	 * js트리 사용 방식이 굉장히 이상하다.. 내가 착각하는지도 모르겠다. 암튼 맘에안듬 */
	public static <ID extends Serializable,VO extends Connectable<ID,VO>> JsonArray buildJsTree(List<VO> roots,Converter<VO,JsonObject> toHref) {
		JsonArray array = new JsonArray();
    	for(VO each : roots){
    		JsonObject codeAttr = toHref.convert(each);
    		codeAttr.addProperty("id", each.getId().toString());
    		codeAttr.addProperty("name", each.getName());
    		codeAttr.addProperty("isLeaf", each.getChildren().size()==0);
    		
    		JsonObject codeData = new JsonObject();
			codeData.addProperty("title", each.getName());
			
			JsonObject codeJson = new JsonObject();
			codeJson.add("data",codeData);
			codeJson.add("metadata", codeAttr);
			codeJson.add("attr", codeAttr);
			array.add(codeJson);
			
			List<VO> children = each.getChildren();
			if(children.size()!=0)  codeJson.add("children", buildJsTree(children,toHref));
    	}
		return array;
	}
}
