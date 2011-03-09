package erwins.swtUtil.lib;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Item;

import erwins.util.reflexive.Connectable;

@SuppressWarnings("unchecked")
public class AbstractGenerator<T extends Connectable>{
	
	protected Image leafItemImage = null;
	protected Image nodeItemImage = null;

	public void setLeafItemImage(Image leafItemImage) {
		this.leafItemImage = leafItemImage;
	}

	public void setNodeItemImage(Image nodeItemImage) {
		this.nodeItemImage = nodeItemImage;
	}
	
	protected boolean isLeaf(T each) {
		boolean isLeaf = each.getChildren().size()==0;
		return isLeaf;
	}
	
	protected void writeDefaultItem(T each, Item item) {
		item.setText(each.getName());
		item.setData(each);
		boolean isLeaf = isLeaf(each);
		if(isLeaf && leafItemImage!=null) item.setImage(leafItemImage);
		else if(!isLeaf && nodeItemImage!=null) item.setImage(nodeItemImage);
	}
	
	
}
