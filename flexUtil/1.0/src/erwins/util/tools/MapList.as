package erwins.util.tools {
	import com.adobe.serialization.json.*;
	
	import mx.collections.ArrayCollection;
	import mx.utils.*;
	
	/** get(key)처럼 Map의 기능을 흉내낸 List */
	public  class MapList{
		
		private var _list:ArrayCollection;
		private var _key:String;
		
		public function MapList(key:String,list:Array=null){
			_key = key;
			_list = list == null ? new ArrayCollection() : new ArrayCollection(list);
		}
		
		public function addItem(item:Object):void{
			_list.addItem(item);
		}
		 
		public function getItem(keyValue:String):Object{
			for each(var eachItem:Object in _list){
				if(eachItem[_key] == keyValue) return eachItem;
			}
			return null;
		} 
		public function getItemAt(index:int):Object{
			return _list.getItemAt(index);
		} 
		
	}
}