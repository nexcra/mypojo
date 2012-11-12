package erwins.util.vender.springBatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import erwins.util.root.PairObject;

/**
 * 스프링배치의 기본VO
 * 자세한것은 스프링배치 홈페이지의 문서를 참고
 * @author sin
 */
public class BatchParameter{

    private String shortContext;
    private String typeCd;
    private String keyName;
    private String stringVal;
    private Date dateVal;
    private Long longVal;
    private Double doubleVal;
    
    /** 이름으로 정렬 */
    private static final Comparator<PairObject> PARAM_COMPARATOR =  new Comparator<PairObject>() {
        @Override
        public int compare(PairObject o1, PairObject o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };
    
    /** 스프링 배치 파라메터의 구조에 따른다. */
    public List<PairObject> covertShortContext(){
        List<PairObject> list = new ArrayList<PairObject>();
        JSONObject body = JSONObject.fromObject(shortContext);
        Object map = body.get("map");
        if( !(map instanceof JSONObject )) return list;
        
        JSON json = (JSON) ((JSONObject)map).  get("entry");
        if(json.isArray()){
            JSONArray array = (JSONArray)json;
            for(int i=0;i<array.size();i++) toSimplePair(list, array.getJSONObject(i));
        }else toSimplePair(list, (JSONObject) json);
        Collections.sort(list,PARAM_COMPARATOR);
        return list;
    }

    /** covertShortContext를 key,value의 값으로 변경한다 */
    @SuppressWarnings("unchecked")
    private void toSimplePair(List<PairObject> list, JSONObject each) {
        Set<String> keys = each.keySet();
        if(keys.size()==1){ //{"string":["append","2012_04_10_1118"]}
            JSONArray value = each.getJSONArray("string");
            
            list.add(new PairObject(value.getString(0),value.get(1).toString()));
        }else{ //,{"string":"keywordSize","int":0} 이런식의 구조
            String value = each.getString("string");
            if(keys.contains("int")) list.add(new PairObject(value, each.get("int").toString()));
            if(keys.contains("long")) list.add(new PairObject(value, each.get("long").toString()));
            if(keys.contains("double")) list.add(new PairObject(value, each.get("double").toString())); //요건 확인안됨
        }
    }
    
    public String getShortContext() {
        return shortContext;
    }
    public void setShortContext(String shortContext) {
        this.shortContext = shortContext;
    }
    public String getTypeCd() {
        return typeCd;
    }
    public void setTypeCd(String typeCd) {
        this.typeCd = typeCd;
    }
    public String getKeyName() {
        return keyName;
    }
    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }
    public String getStringVal() {
        return stringVal;
    }
    public void setStringVal(String stringVal) {
        this.stringVal = stringVal;
    }
    public Date getDateVal() {
        return dateVal;
    }
    public void setDateVal(Date dateVal) {
        this.dateVal = dateVal;
    }
    public Long getLongVal() {
        return longVal;
    }
    public void setLongVal(Long longVal) {
        this.longVal = longVal;
    }
    public Double getDoubleVal() {
        return doubleVal;
    }
    public void setDoubleVal(Double doubleVal) {
        this.doubleVal = doubleVal;
    }
    
    

}
