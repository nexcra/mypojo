package erwins.util.json{
	import com.adobe.serialization.json.*;
	
	import mx.utils.*;
	
	/** Alert을 디버깅용으로 찍을때 유일하게 사용된다. */
	public class JsonDebug{
		
		private var json:Object;
		private var buff:String = '';
		
		public function JsonDebug(json:Object){
			this.json = json;
		}
		
		/** json을 문자열 형태로 파싱한다. 디버깅 용이다. */
		public function parse():String{
			reflexive(json);
			return buff;
		}
		
		private function reflexive(parent:Object,parnetName:String=''):void{
			for(var key:String in parent){
				var obj:Object = parent[key];
				var name:String = parnetName + '/' + key;
				if(obj==null){
					buff += name + ' is NULL \n';
					continue;
				}
				if(Jsons.isReflexive(obj)) reflexive(obj,name);
				else buff += name + ' : ' + obj.toString() + '\n';
			}
			
		}
		
		
	}
}
