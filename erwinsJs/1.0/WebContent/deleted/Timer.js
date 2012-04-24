function Timer() {}
Timer.prototype = {
	setFunction : function(func) {
		this.func = func;
	},
	setTick : function(tick) {
		this.tick = tick;
	},
	start : function() {
		this.timer = setInterval(this.func, this.tick || 1000);
	},
	stop : function() {
		if(null != this.timer) clearInterval(this.timer);
	}
}
/*
var timer = new Timer();
timer.setFunction(messageMe);
timer.setTick(5000);
timer.start(); */