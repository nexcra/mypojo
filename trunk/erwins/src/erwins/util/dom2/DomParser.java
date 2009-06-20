package erwins.util.dom2;

import java.util.List;

import org.apache.ecs.html.*;


/**
 * @author erwins(my.pojo@gmail.com)
 */
public class DomParser<T extends Dom<T>>{
    
    private List<T> baseDoms;
    //private DomSkin domSkin = DomSkin.TREE;
    private String id;
    private String css1;
    private String thisIsNoChildCss;
    
    
    public DomParser(List<T> doms,String id,String css,String css2){
        baseDoms = doms;
        this.id = id;
        this.css1 = css;
        this.thisIsNoChildCss = css2;
    }

    /**
     * 자식 객체를 구해서 UL로 담고 각 자식마다. 재귀호출 한다.
     **/
    private UL getUl(T thisDom,LI thisLi){
        List<T> thisDoms = T.getChild(baseDoms, thisDom);
        if(thisDoms.size()==0) return null;
        UL ul = new UL();        
        for(T dom : thisDoms){
            LI li = new LI();
            li.setNeedClosingTag(true); //API에는 필요없다고 나오지만 실제 없으면 닫는 태그가 생성되지 않는다.
            li.addElement(new A(dom.getData()).setTagText(dom.getName()));
            //이건 폴더트리일경우에만 적용.            
            treeSetup(dom, li);
            
            UL childUl = getUl(dom,li); //자식을 구해서 모두 담는다.
            if(childUl!=null) li.addElement(childUl);             
            ul.addElement(li);
        }
        return ul;
    }

    /**
     * 트리 객체인 경우 특수한 설정.. ㅠㅠ
     */
    private void treeSetup(T dom, LI li) {
        if(thisIsNoChildCss == null) return;
        //이벤트 전파를 중지한다... FF에만 동작한다. 니미..
        //li.setOnMouseOver("this.style.textDecoration='underline';event.stop();"); 
        //li.setOnMouseOut("this.style.textDecoration='none';");
        if(dom.isNoChild()) li.setClass(thisIsNoChildCss);
    }
    
    /**
     * DOM을 해석하여 tree구조의 Apache UL을 리턴한다.
     * Root는 무시하며 2레벨 유닛부터 표현한다.
     **/
    public String get(){
        T root = T.getChild(baseDoms, null).get(0);
        UL ul = getUl(root,null);
        if(ul==null) return "no file";
        if(id!=null) ul.setID(id);
        if(css1!=null) ul.setClass(css1);
        return ul.toString();
    }
    /**
     * DOM을 해석하여 tree구조의 Apache UL을 리턴한다.
     * Root는 무시하며 2레벨 유닛부터 표현한다.
     **/
    public String getForNoRoot(){
        UL ul = getUl(null,null);
        if(ul==null) return "no file";
        if(id!=null) ul.setID(id);
        if(css1!=null) ul.setClass(css1);        
        return ul.toString();
    }
    
}

