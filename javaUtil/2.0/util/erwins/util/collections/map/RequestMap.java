package erwins.util.collections.map;

import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSON;
import erwins.util.morph.BeanToJson;
import erwins.util.openApi.Google;


/** key를 String으로 고정하고 HttpServletRequest를 수용  */
@SuppressWarnings("serial")
public class RequestMap extends SimpleMap<String>{
    
    //protected _Log log = _LogFactory.instance(this.getClass());
    
    public RequestMap(){
    	super();
    }
    public RequestMap(Map<String,Object> map){
    	super(map);
    }
    
    /** putAll(req.getParameterMap()) 을 쓰지 않는다. -> 이놈은 전부 배열로 들어감. */
    public RequestMap(HttpServletRequest req){
    	
        @SuppressWarnings("unchecked")
		Enumeration<String> enumeration = req.getParameterNames();
        
        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();            
            String[] values = req.getParameterValues(name);  //null을 리턴하지는 않는다.
            if (values.length == 1) put(name, values[0]);
            else  put(name, values);
        }
        
        //이하는 간이 테스트 로직
        /*
        if(log.isDebugEnabled()){
            Enumeration<String> parameterNames =  req.getParameterNames();
            List<String> empty = new ArrayList<String>();
            JSONObject parameter = new JSONObject();            
            JSONObject parameters = new JSONObject();
            
            while(parameterNames.hasMoreElements()){
                String name = parameterNames.nextElement();
                String[] values = req.getParameterValues(name);
                if(values.length==0) continue;                
                if(values.length==1){
                    if(values[0].equals("")) empty.add(name);                       
                    else parameter.put(name, values[0]);                     
                }else{
                    JSONArray array = new JSONArray();
                    for(int i=0;i<values.length;i++) array.add(i,values[i]);
                    parameters.put(name, array);
                }
            }
            if(empty.size()!=0) log.debug("[HTML] Named.. But Empty : " + StringUtil.joinTemp(empty,","));
            if(!parameter.isEmpty()) log.debug("[HTML] Single Parameter : " + parameter);
            if(!parameters.isEmpty()) log.debug("[HTML] Array Parameters : " + parameters);
        }
        */
    }


    // ===========================================================================================
    //                                    기타 잡스킬
    // ===========================================================================================    

    public String googleChart(int width,int height){
        return Google.getChart(theMap,width,height);
    }
    
    public JSON json(){
        return BeanToJson.create().build(this);
    }
    
}