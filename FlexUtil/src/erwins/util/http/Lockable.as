package erwins.util.http{
	
	public interface Lockable{
		function get locked():Boolean;
		function set locked(locked:Boolean):void ;
	}
}