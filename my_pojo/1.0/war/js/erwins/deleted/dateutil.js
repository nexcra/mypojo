function dateUtil () {
	var MONTH_NAMES=new Array('January','February','March','April','May','June','July','August','September','October','November','December','Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec','1월','2월','3월','4월','5월','6월','7월','8월','9월','10월','11월','12월');
	var DAY_NAMES=new Array('Sunday','Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sun','Mon','Tue','Wed','Thu','Fri','Sat','일','월','화','수','목','금','토','일');
	dateUtil.prototype.LZ = function (x) {return(x<0||x>9?"":"0")+x};

	// 오늘 일자를 주어진 format 형식의 문자열로 반환한다.
	dateUtil.prototype.getCurrentDate = function (format) {
		var now = new Date ();
		return this.formatDate (now, format);
	}

	dateUtil.prototype.convertFormatDate = function (dateStr, oldformat, newformat) {
		return this.formatDate(this.getDateFromFormat (dateStr, oldformat), newformat);
	}

	dateUtil.prototype.lastDay = function (val, format) {
		date = this.getDateFromFormat (val, format);

		var newdate = this.formatDate (date, "yyyy.MM") + ".01";
		date = this.addDate (this.getDateFromFormat (newdate, "yyyy.MM.dd"), 1, "MM");
		date = this.addDate (date, -1, "dd");
		return this.formatDate (date, "dd");
	}

	// 입력된 date 를 주어진 format 형식으로 문자열을 반환한다.
	dateUtil.prototype.formatDate = function (date, format) {
		format=format+"";
		var result="";
		var i_format=0;
		var c="";
		var token="";
		var y=date.getYear()+"";
		var M=date.getMonth()+1;
		var d=date.getDate();
		var E=date.getDay();
		var H=date.getHours();
		var m=date.getMinutes();
		var s=date.getSeconds();
		var yyyy,yy,MMM,MM,dd,hh,h,mm,ss,ampm,HH,H,KK,K,kk,k;

		// Convert real date parts into formatted versions
		var value=new Object();
		if (y.length < 4) {y=""+(y-0+1900);}
		value["y"]=""+y;
		value["yyyy"]=y;
		value["yy"]=y.substring(2,4);
		value["M"]=M;
		value["MM"]=this.LZ(M);
		value["MMM"]=MONTH_NAMES[M-1];
		value["NNN"]=MONTH_NAMES[M+22];
		value["d"]=d;
		value["dd"]=this.LZ(d);
		value["E"]=DAY_NAMES[E+14];
		value["EE"]=DAY_NAMES[E];
		value["H"]=H;
		value["HH"]=this.LZ(H);
		if (H==0){value["h"]=12;}
		else if (H>12){value["h"]=H-12;}
		else {value["h"]=H;}
		value["hh"]=this.LZ(value["h"]);
		if (H>11){value["K"]=H-12;} else {value["K"]=H;}
		value["k"]=H+1;
		value["KK"]=this.LZ(value["K"]);
		value["kk"]=this.LZ(value["k"]);
		if (H > 11) { value["a"]="PM"; }
		else { value["a"]="AM"; }
		value["m"]=m;
		value["mm"]=this.LZ(m);
		value["s"]=s;
		value["ss"]=this.LZ(s);
		while (i_format < format.length) {
			c=format.charAt(i_format);
			token="";
			while ((format.charAt(i_format)==c) && (i_format < format.length)) {
				token += format.charAt(i_format++);
			}
			if (value[token] != null) { result=result + value[token]; }
			else { result=result + token; }
		}

		return result;
	}

	// 숫자여부 판정
	dateUtil.prototype._isInteger = function (val) {
		var digits="1234567890";
		for (var i=0; i < val.length; i++) {
			if (digits.indexOf(val.charAt(i))==-1) { return false; }
		}
		return true;
	}

	// 숫자로 반환
	dateUtil.prototype._getInt = function (str,i,minlength,maxlength) {
		for (var x=maxlength; x>=minlength; x--) {
			var token=str.substring(i,i+x);
			if (token.length < minlength) { return null; }
			if (this._isInteger(token)) { return token; }
		}
		return null;
	}

	// 입력된 문자열을 주어진 format 으로 date 유형으로 반환한다.
	dateUtil.prototype.getDateFromFormat = function (val,format) {
		val=val+"";
		format=format+"";
		var i_val=0;
		var i_format=0;
		var c="";
		var token="";
		var token2="";
		var x,y;
		var now=new Date();
		var year=now.getYear();
		var month=now.getMonth()+1;
		var date=1;
		var hh=now.getHours();
		var mm=now.getMinutes();
		var ss=now.getSeconds();
		var ampm="";

		while (i_format < format.length) {
			// Get next token from format string
			c=format.charAt(i_format);
			token="";
			while ((format.charAt(i_format)==c) && (i_format < format.length)) {
				token += format.charAt(i_format++);
			}
			// Extract contents of value based on format token
			if (token=="yyyy" || token=="yy" || token=="y") {
				if (token=="yyyy") { x=4;y=4; }
				if (token=="yy")   { x=2;y=2; }
				if (token=="y")    { x=2;y=4; }
				year=this._getInt(val,i_val,x,y);
				if (year==null) { return 0; }
				i_val += year.length;
				if (year.length==2) {
					if (year > 70) { year=1900+(year-0); }
					else { year=2000+(year-0); }
				}
			}
			else if (token=="MMM"||token=="NNN"){
				month=0;
				for (var i=0; i<MONTH_NAMES.length; i++) {
					var month_name=MONTH_NAMES[i];
					if (val.substring(i_val,i_val+month_name.length).toLowerCase()==month_name.toLowerCase()) {
						if (token=="MMM"||(token=="NNN"&&i>11)) {
							month=i+1;
							if (month>12) { month -= 12; }
							i_val += month_name.length;
							break;
						}
					}
				}
				if ((month < 1)||(month>12)){return 0;}
			}
			else if (token=="EE"||token=="E"){
				for (var i=0; i<DAY_NAMES.length; i++) {
					var day_name=DAY_NAMES[i];
					if (val.substring(i_val,i_val+day_name.length).toLowerCase()==day_name.toLowerCase()) {
						i_val += day_name.length;
						break;
					}
				}
			}
			else if (token=="MM"||token=="M") {
				month=this._getInt(val,i_val,token.length,2);
				if(month==null||(month<1)||(month>12)){return 0;}
				i_val+=month.length;}
			else if (token=="dd"||token=="d") {
				date=this._getInt(val,i_val,token.length,2);
				if(date==null||(date<1)||(date>31)){return 0;}
				i_val+=date.length;}
			else if (token=="hh"||token=="h") {
				hh=this._getInt(val,i_val,token.length,2);
				if(hh==null||(hh<1)||(hh>12)){return 0;}
				i_val+=hh.length;}
			else if (token=="HH"||token=="H") {
				hh=this._getInt(val,i_val,token.length,2);
				if(hh==null||(hh<0)||(hh>23)){return 0;}
				i_val+=hh.length;}
			else if (token=="KK"||token=="K") {
				hh=this._getInt(val,i_val,token.length,2);
				if(hh==null||(hh<0)||(hh>11)){return 0;}
				i_val+=hh.length;}
			else if (token=="kk"||token=="k") {
				hh=this._getInt(val,i_val,token.length,2);
				if(hh==null||(hh<1)||(hh>24)){return 0;}
				i_val+=hh.length;hh--;}
			else if (token=="mm"||token=="m") {
				mm=this._getInt(val,i_val,token.length,2);
				if(mm==null||(mm<0)||(mm>59)){return 0;}
				i_val+=mm.length;}
			else if (token=="ss"||token=="s") {
				ss=this._getInt(val,i_val,token.length,2);
				if(ss==null||(ss<0)||(ss>59)){return 0;}
				i_val+=ss.length;}
			else if (token=="a") {
				if (val.substring(i_val,i_val+2).toLowerCase()=="am") {ampm="AM";}
				else if (val.substring(i_val,i_val+2).toLowerCase()=="pm") {ampm="PM";}
				else {return 0;}
				i_val+=2;}
			else {
				if (val.substring(i_val,i_val+token.length)!=token) {return 0;}
				else {i_val+=token.length;}
			}
		}
		// If there are any trailing characters left in the value, it doesn't match
		if (i_val != val.length) { return 0; }
		// Is date valid for month?
		if (month==2) {
			// Check for leap year
			if ( ( (year%4==0)&&(year%100 != 0) ) || (year%400==0) ) { // leap year
				if (date > 29){ return 0; }
			}
			else { if (date > 28) { return 0; } }
		}
		if ((month==4)||(month==6)||(month==9)||(month==11)) {
			if (date > 30) { return 0; }
		}
		// Correct hours value
		if (hh<12 && ampm=="PM") { hh=hh-0+12; }
		else if (hh>11 && ampm=="AM") { hh-=12; }
		var newdate=new Date(year,month-1,date,hh,mm,ss);
		return newdate;
//		return newdate.getTime();
	}

	// 입력된 날짜와 주어진 value를 type 유형에 따라 더한 날짜 값을 반환한다.
	dateUtil.prototype.addDate = function (date, value, type) {
		var newDate = date;
		if (type == "yyyy") {
			newDate.setFullYear(newDate.getFullYear() + value);
		}else if (type == "MM") {
			newDate.setMonth(newDate.getMonth() + value);
		}else if (type == "dd") {
			newDate.setDate(newDate.getDate() + value);
		}else if (type == "hh" || type == "HH") {
			newDate.setHours(newDate.getHours() + value);
		}else if (type == "mm") {
			newDate.setMinutes(newDate.getMinutes() + value);
		}else if (type == "ss") {
			newDate.setSeconds(newDate.getSeconds() + value);
		}

		return newDate;
	}
};


// 2006-11-08 jinyoungsug
// 정상적인 형태가 아닌 비정상적인 형태의 입력후 달력선택시 시간 자동으로 붙이기..
// 시간 입력 필드의 형태가 yyyy.MM.dd 가 아닌 경우에 사용한다.
// 달력은 yyyy.MM.dd 형태로 입력되므로, 해당 입력필드에 필요한 정보를 넣기만 하면 된다.
// 시간 입력 필드의 필수 사항은 formatStr attribute 를 사용하는 전제로 개발됨.
function settingTime (obj, dateStr, dateFormat) {
	var str = dateStr == null ? obj.value : dateStr;
	var format = dateFormat == null ? obj.formatStr : dateFormat;

	dateutil = new dateUtil();
	var date = dateutil.getDateFromFormat (str, format);
	if (date <= 0) {
		obj.value = str.substring(0, 10);
		return '0';
	}

	if (date > new Date()) {
		date = new Date ();
	}

	obj.value = dateutil.formatDate (date, obj.formatStr);
	return obj.value;
}

//시간바를 이용한 기준시각 변경, 2011.02.07 by choiseong
function addMinute(objNm, move, tmFlg) {
	var dateutil = new dateUtil();
	var obj = $("#" + objNm);
	var date = dateutil.getDateFromFormat(obj.val(), 'yyyy.MM.dd.HH:mm');
	move = parseInt(move, 10);
	//m, D, H
	if (tmFlg == "m") {
		date = dateutil.addDate(date, move, "mm");
	} else if (tmFlg == "D") {
		date = dateutil.addDate(date, move, "dd");
	} else if (tmFlg == "H") {
		date = dateutil.addDate(date, move, "hh");
	} else {
		date = dateutil.addDate(date, move, "mm");
	}
	
	date = dateutil.formatDate(date, 'yyyy.MM.dd.HH:mm');
	obj.val(date);
	
	//검색 값이 별도로 존재하는 경우. 아래 추가.
	if ($("#searchDate")) $("#searchDate").val($("#stdTm").val()); 
	
	
	if(dateCheck(objNm, dateutil)) {
		return;
	}
  
}

