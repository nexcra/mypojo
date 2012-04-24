/**
 * Date객체의 포맷한 날짜를 반환한다. 로컬PC의 날자임으로 사용시 주의.
 * =============================================================================
 * Letters Component
 * -----------------------------------------------------------------------------
 * yyyy Year MM Month dd Date HH Hour mm Minute SS Second sss Milli-Second
 * =============================================================================
 */
Date.prototype.format = function(pattern) {
	var self = this;
	return pattern.replace(new RegExp("(yyyy|MM|dd|HH|mm|SS|sss)", "g"), function($1) {
		switch ($1) {
		case "yyyy":
			var year = self.getFullYear();
			return year.toString();
		case "MM":
			var month = self.getMonth() + 1;
			return month.toString().lpad(2);
		case "dd":
			var date = self.getDate();
			return date.toString().lpad(2);
		case "HH":
			var hour = self.getHours();
			return hour.toString().lpad(2);
		case "mm":
			var minute = self.getMinutes();
			return minute.toString().lpad(2);
		case "SS":
			var second = self.getSeconds();
			return second.toString().lpad(2);
		case "sss":
			var millisecond = self.getMilliseconds();
			return millisecond.toString().lpad(3);
		default:
			return "";
		}
	});
}

/** 현제 객체에 count만큼의 날자를 더한다. */ 
Date.prototype.plusDate = function(count) {
	this.setDate(this.getDate()+count);
	return this;
}

/**
 * 마지막 일자를 반환한다. Usage: date.getLastDate()
 */
Date.prototype.getLastDate = function() {
	var year = this.getFullYear();
	var month = this.getMonth() + 1;

	switch (month) {
	case 4:
	case 6:
	case 9:
	case 11:
		return 30;
	case 2:
		if (year % 4 == 0 && year % 100 != 0) {
			return 29;
		}
		if (year % 400) {
			return 29;
		}
		return 28;
	}
	return 31;
}
 