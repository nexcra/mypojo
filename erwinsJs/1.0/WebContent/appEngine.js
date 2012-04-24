/** 단지 기본이벤트처리 + null일때 작동 중지의 역활만을 한다. */
var GoogleAppEngine = new Object();
GoogleAppEngine.start = function(token,callback){
	if(token==null || token=='null' || token=='') return;
	if(this.init) this.init(); 
	this.channel = new goog.appengine.Channel(token);
	this.socket = this.channel.open();
	this.socket.onopen = this.onopen;
    this.socket.onmessage = function(message){
    	callback(JSON.parse(Base64.decode(message.data)));
    };
    this.socket.onerror = this.onerror ? this.onerror : function(error){
    	alert('오류가 발생하였습니다.\n'+error.message);
    };
    this.socket.onclose = function(close){
    	if(close!=null) alert('close.\n'+error.message);
    };
};