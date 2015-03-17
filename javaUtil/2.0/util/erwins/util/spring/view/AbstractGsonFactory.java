package erwins.util.spring.view;


import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.PostConstruct;

import org.springframework.http.MediaType;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/** 
 * GsonView의 설정파일을 관리한다.
 * Gson은 Vo에  인터페이스로 매핑되면 작동하지 않는다(리플렉션 하기 힘들어서??). 실제 클래스로 매핑하거나, 타입 변환기를 써주자.  
 * ex) gsonBuilder.registerTypeAdapter(Page.class, DelegateJsonSerializer.of(gsonRef));
 * ENUM 역시 변환해주자
 * ex) gsonBuilder.registerTypeHierarchyAdapter(EnumDescription.class, EnumJsonSerializer.of(gsonRef));
 * */
public abstract class AbstractGsonFactory implements GsonFactory{
	
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
    protected AtomicReference<Gson> gsonRef = new AtomicReference<Gson>();
/*    *//** 
     * 재귀호출을 방지하기 위한 내부변환기.
     * Page 같은 애들이 또한번 래핑되면 Gson 기본설정으로는 매핑이 안되서 하나더 만들었다.
     * gson 변환시 한번더 내부면환이 되어야 한다면 innerGson으로 호출하자.
     *  *//*
    private Gson innerGson;*/
    
    public GsonView get(){
    	Preconditions.checkNotNull(gson, "AbstractJsonFactory의 초기화를 진행해 주세요");
    	return new GsonView(gson,encoding,contentType,successKey,messageKey);
    }
    
    public GsonView get(Object msg){
    	return get().of(msg);
    }
    
    public Gson gson(){
    	return gson;
    }
    
    /** 단독으로 변환이 가능한 설정 */
    protected abstract void config(GsonBuilder gsonBuilder);
    
    /** 단독으로 변환이 불가능하고 innerGson을 사용해야 변환이 가능한 설정 *//*
    protected abstract void configWithInnerGson(GsonBuilder gsonBuilder,Gson innerGson);*/
    
    /** 문제가 되면 어노테이션 삭제하자. */
    @PostConstruct
    public void postConstruct(){
    	GsonBuilder gsonBuilder  = new GsonBuilder();
    	config(gsonBuilder);
    	gson = gsonBuilder.create();
        gsonRef.set(gson);
    	
    }
	
}
