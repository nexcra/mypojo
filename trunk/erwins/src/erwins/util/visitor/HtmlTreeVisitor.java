
package erwins.util.visitor;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.apache.ecs.html.*;

import erwins.util.root.Connectable;

/** link부분에 pair의 value값이 들어간다. */
public class HtmlTreeVisitor<ID extends Serializable,T extends Connectable<ID, T>> implements Visitor<T>{
    private UL root = new UL();
    
    public HtmlTreeVisitor() {
    }

    /** TREE("TREE","dhtmlgoodies_tree","dhtmlgoodies_sheet.gif"); */
    public HtmlTreeVisitor(String id, String css) {
        if(id!=null) root.setID(id);
        if(css!=null) root.setClass(css);
    }
    
    public UL visit(Collection<T> children) {
        for(T each : children){
            each.accept(this);
        }
        return root;
    }
    public void visit(T parent) {
        reflexiveVisit(parent,root);
    }

    public void reflexiveVisit(T target,UL parent) {
        
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
