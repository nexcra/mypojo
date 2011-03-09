package erwins.util.lib{
	import com.adobe.serialization.json.*;
	
	import erwins.util.json.Jsons;
	
	import mx.collections.ArrayCollection;
	import mx.utils.*;
	
	public class Sets{
		
	    /** json 트리의 모든 노드를 방문하며 function을 실행한다. */
		public static function parse(json:Array,fun:Function):void{
			for each(var obj:Object in json){
				fun(obj);
				if(obj[Jsons.CHILDREN] is Array) parse(obj[Jsons.CHILDREN],fun);
			}
		}		
		
		/**
		 * arrayCollection을 deepCopy한다.
		 * 내장객체는 Object만 복사된다.
		 **/		
		public static function deepCopy(org:ArrayCollection):ArrayCollection{
			var temp:ArrayCollection = new  ArrayCollection();
			for each(var item:Object in org){
				var deep:Object = new Object();
				for(var key:String in item){
					var value:Object = item[key];
					deep[key] = value;
				}
				temp.addItem(deep);
			}
			return temp;
		}
		
		/** 얕은 복사를 한다. **/		
		public static function copy(org:ArrayCollection):ArrayCollection{
			var temp:ArrayCollection = new  ArrayCollection();
			for each(var item:Object in org){
				temp.addItem(item);
			}
			return temp;
		}
		
		/** 동일 객체의 value를 다른 key로 복제한다.
		 * id등이 없을때 lable로 대체하기 위해서 등으로 사용한다. **/		
		public static function copyValue(org:Array,fromKey:String,toKey:String):void{
			parse(org,function(item:Object):void{
				item[toKey] = item[fromKey];
			});
		}
		
		/** 얕은 복사를 한 후 첫행을 제외시킨다. */
		public static function copyIgnoreFirst(org:ArrayCollection):ArrayCollection{
			var copy:ArrayCollection = copy(org);
			copy.removeItemAt(0);
			return copy;
		}
		
		/**
		 * Object의 name에 해당하는값을 join한다.
		 **/		
		public static function join(array:Object,name:String,seperator:String=''):String{
			var str:String = '';
			if(array==null) return str;
			for each(var item:Object in array){
				if(str!='') str += seperator;
				str += item[name];
			}
			return str;
		}
		
		/**
		 * Tree구조의 데이터의 노드는 버리고 마지막 데이터만 리턴한다.
		 * 그러고보니 순서가 문제네.. ㅅㅂ 
		 **/		
		public static function toPlatData(input:Array):Array{
			var result:Array = new Array();
			toPlatDataProcess(input,result);
			return result;
		}
		
		/** array에서 특정 key로 object를 구해온다. */
		public static function getByKey(array:Array,key:String,value:Object):Object{
			for each(var item:Object in array) if(item[key]==value) return item;
			return null;
		}		
		
		private static function toPlatDataProcess(input:Array,result:Array):void{
			for each(var item:Object in input){
				if(item[Jsons.CHILDREN]==null) result.push(item);
				else toPlatDataProcess(item[Jsons.CHILDREN] as Array,result);
			}
		}
	}
}