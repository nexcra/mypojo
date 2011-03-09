package erwins.util.json{
	import com.adobe.serialization.json.*;
	
	import erwins.util.lib.Strings;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.utils.*;
	
		
	public class JsonParser{
		
		private var root:ArrayCollection;
		private var result:ArrayCollection; //각종 결과에 사용하자.
		private var parent:Object; //???
		private var key:String; //검색 등에 사요하자.
		
		/**
		 * 예외 체크~
		 **/
		public function JsonParser(root:ArrayCollection){
			this.root = root;
			this.result = new ArrayCollection();
		}
		
		/** label이 key인것을 찾아 Array로 리턴한다. */
		public function scan(key:String):ArrayCollection{
			this.key = key;
			scanSelf(root);
			return result;
		}
		
		/**
		 * 가장 최초의  children을 반환한다. 
		 * combo등에서 최초 것을 대입해줄때 사용
		 * */
		public static function getFirst(obj:Object):Object{
			if(obj[0]['children']){
				return getFirst(obj[0]['children'])
			}else return obj[0];
		}
		
		/** 재귀호출~ */
		private function scanSelf(list:ArrayCollection,parent:Object=null):void{
			for each(var item:Object in list){
				item['parent'] = parent;
				if(Strings.isContainIgnoreCase(item.label,key)){
					result.addItem(item);
				}
				if(item.children){
					var array:Array = item.children;
					scanSelf(new ArrayCollection(array) as ArrayCollection,item);
				}
			}
		}
		
		/** 
		 * obj 위로 1개의 parent가 있다면 stack에는 1개의 자료가 들어간다.
		 * 마지막에 들어가는 자료가 root 이다.
		 * */
		public static function getRoot(obj:Object):Array{
			var stack:Array = new Array();
			var temp:Object = obj;
			for(var i:int=0;i<50;i++){
				var parent:Object = temp['parent'];
				if(!parent) return stack;
				temp = parent;
				stack.push(parent);
			}
			Alert.show('No root!');
			return null;
		}
		
	}
}
