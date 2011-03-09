package erwins.swtUtil.lib;

import java.util.ArrayList;
import java.util.List;

import erwins.util.reflexive.Connectable;
import erwins.util.reflexive.Visitor;

@SuppressWarnings("serial")
public class SimpleTreeItem implements Connectable<String,SimpleTreeItem>{
	
	private String id;
	private String name;
    private SimpleTreeItem parent;
    private List<SimpleTreeItem> children = new ArrayList<SimpleTreeItem>();
	

	@Override
	public void addChildren(SimpleTreeItem child) {
		children.add(child);
	}

	@Override
	public List<SimpleTreeItem> getChildren() {
		return children;
	}

	@Override
	public SimpleTreeItem getParent() {
		return parent;
	}

	@Override
	public void setParent(SimpleTreeItem parent) {
		this.parent = parent;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public int compareTo(SimpleTreeItem o) {
		return id.compareTo(o.getId());
	}

	@Override
	public void accept(Visitor<SimpleTreeItem> v) {
		v.visit(this);
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getValue() {
		return id;
	}
	
	@SuppressWarnings("unchecked")
	public static void addItemIfNoChildren(Connectable root,String name){
		if(root.getChildren().size()==0){
    		SimpleTreeItem temp = new SimpleTreeItem();
    		temp.setName(name);
    		root.addChildren(temp);
    	}
	}
	
}
