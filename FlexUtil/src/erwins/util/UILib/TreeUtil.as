package erwins.util.UILib{
	import com.adobe.serialization.json.*;
	
	import erwins.util.json.JsonParser;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Tree;
	import mx.utils.*;
	
	/** FLEX tree 사용시 유용한 기능. */
	public class TreeUtil{
		
		public static const IS_BRANCH:String = "isBranch";
    	public static const CHILDREN:String = "children";
		
		/** json 트리의 모든 노드를 방문하며 function을 실행한다. */
		public static function parse(json:ArrayCollection,fun:Function):void{
			for each(var obj:Object in json){
				fun(obj);
				if(obj[CHILDREN] is Array) parse(new ArrayCollection(obj.children),fun);
			}
		}
		
		/** 
		 * 특정 obj로 이동한다. parent객체를 모두 읽어와 root부터 트리를 풀어 나간다.
		 * expandAll되지 않은 상태의 객체는 getParent가 되지 않는다... 버그인듯.
		 * parent가 미리 세팅되어 있어야 한다. 
		 * */
		public static function select(tree:mx.controls.Tree,item:Object):void{
			var stack:Array = JsonParser.getRoot(item);
			
			while(stack.length!=0){
				var now:Object = stack.pop();
				tree.expandItem(now,true);
			}
			
			//TreeUtil.expandAll(tree);
			tree.selectedItem = item;
			TreeUtil.focusScroll(tree,item);
		}
		
		/** 전부열기 */
		public static function expandAll(tree:mx.controls.Tree):void{
			for each(var item:Object in tree.dataProvider){
				tree.expandChildrenOf(item,true);
			}
		}
		
		/** 스크롤 이동. */
		public static function focusScroll(tree:mx.controls.Tree,item:Object):void{
			tree.scrollToIndex(tree.getItemIndex(item)) //인덱스~
		}

	}
}