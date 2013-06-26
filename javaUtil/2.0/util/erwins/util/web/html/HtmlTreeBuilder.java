
package erwins.util.web.html;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.apache.ecs.html.*;

import erwins.util.reflexive.Connectable;
import erwins.util.reflexive.Connector;


public abstract class HtmlTreeBuilder<ID extends Serializable,T extends Connectable<ID,T>> extends Connector<ID,T>{
	
    protected UL root = new UL();
    
    public HtmlTreeBuilder() {}
    
    public abstract String toHtml(UL root);
    
    public String visit(Collection<T> children) {
        for(T each : children) reflexiveVisit(each,root);
        return toHtml(root);
    }
    
    protected void reflexiveVisit(T target,UL parent) {
    	LI li = new LI();
    	li.setNeedClosingTag(true);
    	buildLi(target,li);
        parent.addElement(li);
        
        List<T> children = target.getChildren();
        if(children==null || children.size()==0) return;
        
        UL ul = new UL();
        ul.setNeedClosingTag(true);
        buildUl(target,ul);
        li.addElement(ul);
        for(T each : children){
            reflexiveVisit(each,ul);
        }
    }
    
    protected abstract void buildLi(T target,LI li);
    protected abstract void buildUl(T target,UL ul);
}

/** ex) XX 디자인에 맞춘 빌더 *//*
public class KmaHtmlBuilder extends HtmlTreeBuilder<Long,MenuVO>{
	
	private final MenuVO current;
	
	public KmaHtmlBuilder(MenuVO current){
		this.current = current;
	}

	@Override
	public String toHtml(UL root) {
		root.setID("menu");
        root.setClass("dep01");
		return root.toString();
	}

	@Override
	protected void buildLiUl(MenuVO target,LI li, UL ul) {
		int i = getLevel(target);
    	boolean on = isContain(current,target);
		P p = new P();
        if(on) p.setClass("dep0"+i+"_tlt on");
        else p.setClass("dep0"+i+"_tlt");
        p.addElement(new A(target.getValue()==null ? "#" : target.getValue()).setTagText(target.getName()));
        li.addElement(p);
        
        
        ul.setClass("dep0"+(i+1));
        if(!on) ul.setStyle("display: none;");
        li.addElement(ul);
	}
}*/
