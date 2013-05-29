/** Ext.js를 사용하기 위한 도구 */   
var Ex = new Object();

/** 라디오의 경우 클릭직후와 라디오 포커스가 옮겨간 후 2가지 체인지 이벤트가 일어난다.
 * 우리가 원하는건 마지막 이벤트 임으로 2개가 다중 선택된 상태인 첫번째 이벤트는 스킵해준다. */ 
Ex.extRadioChange = function(callback){
	return function(field,newValue,oldValue,eOpts){
	   var key; 
	   for(var k in newValue) key = k;
	   var value = newValue[key];
	   if(value instanceof Array) return; //2개가 오면 스킵
	   callback(field,newValue,oldValue,eOpts,value);
	};
};

/** 기본 숫자를 포매팅해주는 렌더러.  네이밍중복있을까봐 2 붙임. */
var toNumber2 = function(val,metaData,record,rowIndex,colIndex,store,view) {
	if($.isEmpty(val)) return '0';
	var val = val.toNumber().format();
	return val;
};

/** 간단색 변환. ifCallback으로 예외사항을 둘 수 있다. */
Ex.toColor = function(color,bold,ifCallback){
	if(bold==null) bold = false;
	var renderer = function(val,metaData,record,rowIndex,colIndex,store,view) {
		if(val==null) return '';
		var data = record.data;
		if(ifCallback!=null) if(!ifCallback(data)) return val;
      return val.toString().toSpan(color,bold);
  };
  return renderer;
};

/** 툴팁을 달아주는 렌더러. 자체 렌더링 기능은 없다. */
Ex.tooltipRederer = function(tooltipKey){
	var renderer = function(val,metaData,record,rowIndex,colIndex,store,view) {
		var data = record.data;
      Ex.addTrTooltip(metaData,data[tooltipKey]);
      return val;
  };
  return renderer;
};

/** 렌더러를 조합해서 결과물을 생성한다. 첫번째 것부터 적용한다.
* ex) dataIndex: 'hitRate',renderer:Ex.toRenderer(toNumber2,toBlue) */
Ex.toRenderer = function(){
	var rendererSet = arguments;
	var renderer = function(val,metaData,record,rowIndex,colIndex,store,view) {
		var returnVal = val;
		for(var i=0;i<rendererSet.length;i++){
			var currentRenderer = rendererSet[i];
			returnVal = currentRenderer(returnVal,metaData,record,rowIndex,colIndex,store,view);
		}
		return returnVal;
  };
  return renderer;
};


/** 렌더러 적용시 컬러 변환할때 사용한다.
* rowValue : 렌더러 적용된 값 */
//사용금지
Ex.redererColor = function(color,bold){
	if(bold==null) bold = false;
	var callback = function(rowValue,data){
		return rowValue.toSpan(color,bold);	
	};
	return callback;
}; 



/** Ext 버전 자동인풋. 추후 확장하자. */ 
Ex.setInput =  function(json,prefix){
	   if(prefix==null) prefix = '';
	   for(var key in json){
		   var realKey = prefix + key;
		   var dom = Ext.getCmp(realKey);
		   if(dom==null) continue;
		   var xtype = dom.xtype;
		   
		   if(xtype == 'textfield' || xtype == 'combobox' || xtype == 'textarea') dom.setValue(json[key]);
		   else if(xtype == 'radiogroup'){
			   var index = json[key] ? 0 : 1;
			   dom.items.items[index].setValue(true);
		   };
	   };
};


/** xtype:'textfield'의  listeners의value로 입력
ex) {xtype:'textfield',fieldLabel: 'Query',id:'Query',value:'청바지',listeners:enterListener(memGet)} */
Ex.enterListener = function(callback){
	return {specialkey:function(field , eventObj){
		    if(eventObj.getKey() == Ext.EventObject.ENTER){ callback(); }
	}};
};

/** 툴팁 추가~ */
Ex.addTrTooltip = function(metaData,data){
	if(data==null) return;
	data = data.replaceAll('\n','<BR>');  //치환해준다
	metaData.tdAttr = 'data-qtip="'+data+'"';
};
/** 배열 문자열 자료 간단추가 */
Ex.addTrArrayTooltip = function(metaData,datas){
	if(datas!=null){
  	var index = 1;
  	var dataArray = $.map(datas,function(v){ return '<p>'+ index++ + ' : ' + v  + '</p>' ; });
      Ex.addTrTooltip(metaData,dataArray.join('<BR>'));
  }
};

/** 그리드의 각 로우에 마우스 우클릭 이벤트를 넣어준다. items은 Action의 배열이다. 
* ex) viewConfig:Ex.setGridContext([stopAction,deleteAction]), */
Ex.setGridContext = function(items){
 var contextMenu = Ext.create('Ext.menu.Menu', { items: items});
 return  {
     stripeRows: true,
     listeners: {
         itemcontextmenu: function(view, rec, node, index, e) {
             e.stopEvent();
             var data =  rec.data;
             for(var i=0;i<items.length;i++){
          	   var currentItem = items[i]; 
          	   currentItem.data = data;
          	   //if(currentItem.callback != null) currentItem.callback(data);
             }
             contextMenu.showAt(e.getXY());
             return false;
         }
     }
 };
};

//추가
/** 링크를 달아주는 랜더러 */
Ex.linkRederer = function(link){
	var renderer = function(val,metaData,record,rowIndex,colIndex,store,view) {
		var data = record.data;
		var linkData = data[link];
		if(linkData==null || linkData =='') return val;
		return "<a style='cursor: pointer;' href='"+linkData+"' >"+val+"</a>";
	};
	return renderer;
};

/** http://www.aaronwest.net/blog/index.cfm/2009/5/9/Scrolling-problem-with-ExtJS-grids-in-Safari 
 * 흠.. 잘 모르겠음 */
if(Ext.isSafari){ 
	Ext.override(Ext.grid.GridView, { 
		layout : function(){ 
			this.scroller.dom.style.position = 'static'; 
		} 
	}); 
}