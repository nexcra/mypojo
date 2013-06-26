package erwins.jsample.etc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import org.apache.ecs.xml.XML;
import org.apache.ecs.xml.XMLDocument;
import org.apache.log4j.Logger;

import erwins.util.text.StringEscapeUtil;
import erwins.util.text.StringUtil;
import erwins.util.web.WebUtil;

/**
 * 국산 레포팅 툴 오즈의 XML문을 생성합니다.
 */
public class Oz{
    
    private Logger log = Logger.getLogger(this.getClass());
    
    private static final String DATA_SET_NODE_NAME = "sql_retireflow";    
    private static final String RECORD_SET_NODE_NAME =  "flow";

    private HashMap<String,String> fieldInfos = new HashMap<String,String>();
    private List<HashMap<String,String>> datas = new ArrayList<HashMap<String,String>>();
    private HashMap<String,String> thisList;
    
    public void addNewData(){
        thisList = new HashMap<String,String>();
        datas.add(thisList);
    }
    
    public void addData(String name,String value){
        value = StringUtil.nvl(value);
        value = StringEscapeUtil.escapeXml(value);
        fieldInfos.put(name,"string"); //대부분 String만을 사용함으로 추후 확장성만 남기고 나머지는 생략한다. 
        thisList.put(name,value);
    }
    
    /**
     * DATA_SET_NODE_NAME = "sql_retireflow";   
     * RECORD_SET_NODE_NAME =  "flow";
     */
    public void out(HttpServletResponse resp){
        
        //response.setContentType("text/xml; charset=utf-8");
        //PrintWriter out = getWriter();            
        
        //str.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        XML xmlBody = new XML(DATA_SET_NODE_NAME);
        
        //1. 필드정보 생성
        XML fieldinfo = new XML("fieldinfo");
        makeFileInfo(fieldinfo);
        xmlBody.addElement(fieldinfo);
        
        //2. 데이타 정보 생성
        XML retireflow = new XML("retireflow");
        makeRetireflow(retireflow);
        xmlBody.addElement(retireflow);

        //3. 문서 출력
        //수정..
        XMLDocument x = new XMLDocument();
        x.setCodeset("UTF-8");
        x.addElement(xmlBody);
        x.output(WebUtil.getWriter(resp));
        
        //out.print("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        //out.print(xmlBody);        
        if(log.isDebugEnabled()) log.debug(xmlBody);
        
    }
    
    /**
     * 오즈레포트 전용의 fileInfo를 생성한다.
     */
    private void makeFileInfo(XML fieldinfo){
        for(String  key : fieldInfos.keySet()){
            XML xml = new XML("field");
            xml.addAttribute("name",key);
            xml.addAttribute("type",fieldInfos.get(key));                   
            fieldinfo.addElement(xml);
        }
    }
    /**
     * 오즈레포트 전용의 retireflow를 생성한다.
     */
    private void makeRetireflow(XML retireflow){
        for(HashMap<String,String> fieldInfos : datas){
            XML dataTag = new XML(RECORD_SET_NODE_NAME);
            for(Entry<String,String> entry: fieldInfos.entrySet() ){
                XML xml = new XML(entry.getKey());
                xml.setTagText(entry.getValue());            
                dataTag.addElement(xml);
            }
            retireflow.addElement(dataTag);
        }
    }
    
}
