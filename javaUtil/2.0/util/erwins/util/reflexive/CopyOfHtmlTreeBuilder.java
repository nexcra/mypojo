
package erwins.util.reflexive;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.apache.ecs.html.*;


/** 필요하다면 오버라이드 하자. */
public class CopyOfHtmlTreeBuilder<ID extends Serializable,T extends Connectable<ID, T>>{
    protected UL root = new UL();
    
    /** 디폴트. */
    public CopyOfHtmlTreeBuilder() {}

    /** TREE("TREE","dhtmlgoodies_tree","dhtmlgoodies_sheet.gif"); */
    public CopyOfHtmlTreeBuilder(String id, String css) {
        if(id!=null) root.setID(id);
        if(css!=null) root.setClass(css);
    }
    
    public UL visit(Collection<T> children) {
        for(T each : children) reflexiveVisit(each,root);
        return root;
    }
    
    protected void reflexiveVisit(T target,UL parent) {
        LI li = new LI();
        li.setNeedClosingTag(true); //API에는 필요없다고 나오지만 실제 없으면 닫는 태그가 생성되지 않는다.
        li.addElement(new A(target.getValue()).setTagText(target.getName()));
        parent.addElement(li); //앞으로 옮기기.
        
        List<T> children = target.getChildren();
        if(children.size()==0) return;
        UL ul = new UL();
        li.addElement(ul);
        for(T each : children){
            reflexiveVisit(each,ul);
        }
    }
}
