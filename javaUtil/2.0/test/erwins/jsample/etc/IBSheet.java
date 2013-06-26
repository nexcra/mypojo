
package erwins.jsample.etc;



import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.ecs.xml.XML;
import org.apache.ecs.xml.XMLDocument;

import erwins.util.web.WebUtil;

/**
 * 국산 버그 덩어리 X-Internet IBSheet를 래핑한다. <br>
 * null 입력시 ""을 입력한다.
 * 1. 스크립트 버그 (select의 value가 ''이면 이름이 인자로 넘어감) <br>
 * 2. save메소드시 쿼리스트링이 get방식으로 넘어가는 문제 ('+'값 인식 불가로 인증서 해시값 전송시 문제) <br> 
 * resp => 나중에 요거 삭제.
 */
public class IBSheet{
    
    HttpServletResponse resp;
    
    public static final String COLOR_RED = "RED";
    public static final String COLOR_BLUE = "BLUE";
    public static final String COLOR_YELLOW = "YELLOW";
    
    public static final String INSERT = "I";
    public static final String UPDATE = "U";
    public static final String DELETE = "D";
    
    public IBSheet(HttpServletResponse response){
        resp = response;
    }
    
    public IBSheet(HttpServletResponse response,List<Object[]> list,int skip){
        resp = response;
        makeSimpleSheet(list, skip);
    }
    
    public IBSheet(HttpServletResponse response,List<Object[]> list){
        resp = response;
        makeSimpleSheet(list, 1);
    }
    
    /** 간단매핑. */
    private void makeSimpleSheet(List<Object[]> list, int skip) {
        for(Object[] strings : list){
            newRow();
            for(int i=0;i<skip;i++) add("");
            for(Object str : strings) addObj(str);
        }
        out(list.size());
    }    
    
    private List<List<String[]>> rows = new ArrayList<List<String[]>>();
    private List<String[]> cols;
    /** 0:data,1:color,2:toolTip **/
    String[] data;
    private String noDataMessage = "조회된 데이터가 없습니다.";;
    
    public void newRow(){
        cols = new ArrayList<String[]>();
        rows.add(cols);
    }
    
    /**
     * iBatis등 Map으로 받을때 String과  BigDecimal, 두 유형인 자료를 입력할때 간편하게 사용한다. 
     * 따로 입력해야지만 추가 속성 등을 입력 할 수 있다.
     */
    public IBSheet addObj(Object value) throws RuntimeException{
        if(value==null || value instanceof String) return add((String)value);
        else if(value instanceof BigDecimal) return add((BigDecimal)value);
        throw new RuntimeException(value.getClass() + "is Not Supported Type");
    }
    
    /**
     * iBatis등 Map으로 받을때 String과  BigDecimal, 두 유형인 자료를 입력할때 간편하게 사용한다. 
     */
    public void addIbatisMap(Map<String,Object> map,String ... keys){
        for(String key : keys){
            addObj(map.get(key));
        }
    }
    
    public IBSheet add(String value) throws RuntimeException{
        if(value==null) value = "";
        data = new String[]{value,null,null};
        if(cols==null) throw new RuntimeException("Rows Not Found");
        cols.add(data);
        return this;
    }
    public IBSheet add(BigDecimal value){
        if(value==null) value = BigDecimal.ZERO;
        add(value.toPlainString());
        return this;
    }
    public IBSheet add(Long value){
        if(value==null) add("");
        else add(String.valueOf(value));
        return this;
    }
    public IBSheet add(Integer value){
        if(value==null) add("");
        else add(String.valueOf(value));
        return this;
    }

    /**
     * 직전 data의 color를 지원한다.
     * HTML 기본 이름밖에 사용할 수 없는듯... RED 등 
     */
    public IBSheet setColor(String value){
        data[1] = value;
        return this;
    }
    /**
     * 직전 data의 toopTip을 입력한다.
     */
    public IBSheet setToolTip(String value){
        data[2] = value;
        return this;
    }
    
    public void printError(String resultMsg){
        PrintWriter out = WebUtil.getWriter(resp);
        out.print("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        out.print("<ERROR>");
        out.print("<MESSAGE> <![CDATA[");
        out.print(resultMsg);
        out.print("]]> </MESSAGE>");
        out.print("</ERROR>");
    }
    
    public void setNodataMessage(String noDataMessage){
        this.noDataMessage = noDataMessage;
    }
    
    public void out(int maxCount){
        
        XML xmlBody = new XML("SHEET");
        
        XML fieldinfo = new XML("DATA");
        fieldinfo.addAttribute("TOTAL", maxCount);
        
        if(maxCount==0){
            XML td = new XML("TD");            
            td.setTagText(noDataMessage);
            
            XML nodatat = new XML("NONEDATA");
            nodatat.addElement(td);
            fieldinfo.addElement(nodatat);
            xmlBody.addElement(fieldinfo);
        }else{
            makeFileInfo(fieldinfo);
            xmlBody.addElement(fieldinfo);
        }
        
        XMLDocument x = new XMLDocument();
        x.setCodeset("UTF-8");
        x.addElement(xmlBody);
        x.output( WebUtil.getWriter(resp));
    }
    
    /**
     * 오즈레포트 전용의 fileInfo를 생성한다.
     */
    private void makeFileInfo(XML fieldinfo){
        for(List<String[]> row : rows){
            XML tr = new XML("TR");
            for(String[]  data : row){                
                XML td = new XML("TD");
                td.setTagText("<![CDATA[" + data[0] + "]]>");
                if(data[1] != null) td.addAttribute("COLOR", data[1]);                                                   
                if(data[2] != null) td.addAttribute("TOOL-TIP", data[2]);
                tr.addElement(td);
            }
            fieldinfo.addElement(tr);
        }
    }
    
    /**
     * IBSheet용 콤보를 리턴한다. 
     * EX) sheet.InitDataCombo(0,"itemTypeCd",<%=Code.~~%>); 
     */
    /*
    public static String getIBSheetComboBox(List<Dom> doms){
        StringBuffer optionName = new StringBuffer();
        StringBuffer optionValue = new StringBuffer();
        
        boolean isFirst = true;
        
        optionName.append("\"");
        optionValue.append("\"");
        
        //제조실적용 비플라스틱 요율정보
        //현재년도 요율정보를 콤보박스로 리턴
        
        for(Dom dom : doms){            
            if(isFirst) isFirst = false;
            else {
                optionName.append("|");
                optionValue.append("|");
            }
            optionName.append(dom.getName());
            optionValue.append(dom.getId());
        }
        optionName.append("\"");
        optionValue.append("\"");
        
        return optionName.toString() +","+ optionValue.toString();
    }    
*/
    
}
