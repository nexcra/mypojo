package erwins.util.morph;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

/** 하드코딩 방지용 간이파서.  */
public class RequestToMap  {
    
    protected final HttpServletRequest req;
    
    public RequestToMap(HttpServletRequest req){
        this.req = req;
    }
    
    @SuppressWarnings("rawtypes")
    public Map<String,Object> getMap(String prefix){
        Map<String,Object> map = new HashMap<String,Object>();
        Enumeration e = req.getParameterNames();
        while(e.hasMoreElements()){
            String name = (String)e.nextElement();
            if(!name.startsWith(prefix)) continue;
            String[] values = req.getParameterValues(name);
            String key = name.substring(prefix.length()+1);
            if(values.length==0) map.put(key,StringUtils.EMPTY);
            else if(values.length==1) map.put(key,values[0]);
            else map.put(name,values);
        }
        return map;
    }
    
    @SuppressWarnings("rawtypes")
    public Map<String,Object> getMap(){
        Map<String,Object> map = new HashMap<String,Object>();
        Enumeration e = req.getParameterNames();
        while(e.hasMoreElements()){
            String name = (String)e.nextElement();
            String[] values = req.getParameterValues(name);
            if(values.length==0) map.put(name,StringUtils.EMPTY);
            else if(values.length==1) map.put(name,values[0]);
            else map.put(name,values);
        }
        return map;
    }
    
    @SuppressWarnings("rawtypes")
    public List<Map<String,Object>> getList(String prefix){
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
        Enumeration e = req.getParameterNames();
        while(e.hasMoreElements()){
            String name = (String)e.nextElement();
            if(!name.startsWith(prefix)) continue;
            String[] values = req.getParameterValues(name);
            String key = name.substring(prefix.length()+1);
            for(int i=0;i<values.length;i++){
                Map<String,Object> map = nullSafeGet(list,i);
                map.put(key,values[i]);
            }
        }
        return list;
    }
    
    private static Map<String,Object> nullSafeGet(List<Map<String,Object>> list , int index){
        if(list.size() <= index){
            int count = list.size() - index + 1;
            for(int i=0;i<count;i++){
                Map<String,Object> map = new HashMap<String,Object>();
                list.add(map);    
            }
        }
        return list.get(index);
    }
    
}
