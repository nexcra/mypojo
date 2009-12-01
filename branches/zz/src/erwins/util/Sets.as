package erwins.util{
	import com.adobe.serialization.json.*;
	
	import mx.collections.ArrayCollection;
	import mx.utils.*;
	
	public class Sets{
		
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
	}
}