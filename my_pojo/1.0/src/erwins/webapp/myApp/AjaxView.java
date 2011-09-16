package erwins.webapp.myApp;

import erwins.util.morph.BeanToJson;
import erwins.util.morph.BeanToJsonForAppEbgine;
import erwins.util.vender.spring.AbstractAjaxView;
import erwins.util.web.ResponseOutConfig;

public class AjaxView extends AbstractAjaxView{
	
	public static final BeanToJson beanToJson = BeanToJsonForAppEbgine.create();
	private static final ResponseOutConfig config = new ResponseOutConfig();
	
	static{
		config.setXmlEscape(true);
	}
	
	public AjaxView() {
		super(beanToJson, config);
	}
	public AjaxView(Object message) {
		super(beanToJson, config);
		add(message);
	}
	public AjaxView(String message,Object ... args) {
		super(beanToJson, config);
		add(message,args);
	}

}
