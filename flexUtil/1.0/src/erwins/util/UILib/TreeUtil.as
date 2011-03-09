package erwins.util.UILib{
	import com.adobe.serialization.json.*;
	
	import erwins.util.json.JsonParser;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Tree;
	import mx.utils.*;
	
	/** FLEX tree 사용시 유용한 기능.
	 * AdvancedGride에서도 된다. 위엄 쩌는 덕타입 */
	public class TreeUtil{
		
		/** 
		 * 특정 obj로 이동한다. parent객체를 모두 읽어와 root부터 트리를 풀어 나간다.
		 * expandAll되지 않은 상태의 객체는 getParent가 되지 않는다... 버그인듯.
		 * parent가 미리 세팅되어 있어야 한다. 
		 * */
		public static function select(tree:Object,item:Object):void{
			var stack:Array = JsonParser.getRoot(item);
			
			while(stack.length!=0){
				var now:Object = stack.pop();
				tree.expandItem(now,true);
			}
			
			//TreeUtil.expandAll(tree);
			tree.selectedItem = item;
			TreeUtil.focusSelectedItem(tree);
		}
		
		/** 전부열기 */
		public static function expandAll(tree:Object):void{
			for each(var item:Object in tree.dataProvider){
				tree.expandChildrenOf(item,true);
			}
		}
		
		/** 스크롤 이동. getItemIndex메소드는 Tree객체에만 있다. */
		public static function focusScroll(tree:mx.controls.Tree,item:Object):void{
			tree.scrollToIndex(tree.getItemIndex(item)) //인덱스~
		}
		
		/** 스크롤 이동. */
		public static function focusSelectedItem(tree:Object):void{
			tree.scrollToIndex(tree.selectedIndex) //인덱스~
		}

	}
}