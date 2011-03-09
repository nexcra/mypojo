package erwins.util.test{
	import com.adobe.serialization.json.*;
	
	import flash.utils.Proxy;
	import flash.utils.flash_proxy;
	
	import mx.controls.Alert;
	import mx.utils.*;
	public dynamic class DynamicProxyTest extends Proxy{
		
		/** * 서버인지? 반드시 자바스크립트를 설정해야 한다. **/
		flash_proxy override function callProperty(name:*, ... args):*{
			Alert.show(name);
			return this;
		}
		
	}
}