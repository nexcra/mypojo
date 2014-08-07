package erwins.util.spring.view;


import javax.annotation.PostConstruct;

import org.springframework.http.MediaType;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/** JsonView의 설정파일을 관리한다. */
public abstract class AbstractJsonFactory implements JsonFactory{
	
	private static final String ENCODING = "UTF-8";
	/**  IE에서 application/json로 보내면 다운로드가 되는 현상이 있다. 아래로 변경해준다 */
	private static final String CONTENT_TYPE =  MediaType.TEXT_HTML.toString(); //text/plain을 쓰기도 한다.
	private static final String SUCCESS_KEY = "success";
	private static final String MESSAGE_KEY = "message";
	
	protected String encoding = ENCODING;
	protected String contentType = CONTENT_TYPE;
	protected String successKey = SUCCESS_KEY;
	protected String messageKey = MESSAGE_KEY;
    protected Gson gson;
    
    public JsonView get(){
    	Preconditions.checkNotNull(gson, "AbstractJsonFactory의 초기화를 진행해 주세요");
    	return new JsonView(gson,encoding,contentType,successKey,messageKey);
    }
    
    protected abstract void config(GsonBuilder gsonBuilder);
    
    /** 문제가 되면 어노테이션 삭제하자. */
    @PostConstruct
    public void postConstruct(){
    	GsonBuilder gsonBuilder  = new GsonBuilder();
    	config(gsonBuilder);
        gson = gsonBuilder.create();
    }
	
}
