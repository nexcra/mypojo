package erwins.util.dom;

import java.util.HashMap;
import java.util.List;


/**
 * 그 자체의 toString이 id가 된다. 각각 id가 달라야 함으로 각각 유일하게 만들어야 한다. 이들 id는 자바스크립트와 연결돤다.
 * @author     erwins(my.pojo@gmail.com)
 */
public enum DomSkin{
    
    /**
     * @uml.property  name="hEADER"
     * @uml.associationEnd  
     */
    HEADER("",""),
    
    /**
     * @uml.property  name="lEFT"
     * @uml.associationEnd  
     */
    LEFT("LEFT",""),
    
    /**
     * @uml.property  name="tREE"
     * @uml.associationEnd  
     */
    TREE("TREE","dhtmlgoodies_tree,dhtmlgoodies_sheet.gif");
    
    /**
     * @uml.property  name="id"
     */
    private String id;
    private String[] css;
    
    private DomSkin(String id,String css){
        this.id = id;
        this.css = css.split(",");
    }

    private static HashMap<DomSkin,String> dhtmlGoodesCache = new HashMap<DomSkin,String>();
    
    /**
     * dhtml을 메모리에 저장한다.
     **/
    public void set(List<Dom> doms){
        dhtmlGoodesCache.put(this, new DomParser(doms).setDomSkin(this).get());
    }
    
    /**
     * 저장된 dhtml을 가져온다.
     **/
    public String get(){
        return dhtmlGoodesCache.get(this);
    }

    /**
     * @return
     * @uml.property  name="id"
     */
    public String getId() {
        return id;
    }

    public String getFirstCss() {
        return css[0];
    }
    public String getSecondCss() {
        return css.length > 1 ? css[1] : "";
    }
    

    
    
	
}

