<%@page import="erwins.webapp.myApp.mtgo.DeckType"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<script type="text/javascript">
Ext.require([ '*' ]);
Ext.onReady(function() {
	
    var deckStore = new Ext.data.JsonStore({
        fields: ['id','type','name','win','lose','colors','sumOfPrice','quantitys']
    });
    var gameCount =  function(val,metaData,record,rowIndex,colIndex,store,view) {
    	var data = record.data;
        return data.win + data.lose;
    }
    var winRateCal = function(data){
    	if(data.win==0) return 0+'%';
        return ( data.win/(data.win + data.lose) ) +'%';
    }
    var winRate =  function(val,metaData,record,rowIndex,colIndex,store,view) {
    	var data = record.data;
        return winRateCal(data);
    }
    
	var grid = Ext.create('Ext.grid.Panel', {
		store:deckStore,stateful: true,stateId: 'stateGrid',
        flex:1,width: '100%',border:false,autoScroll:true,
        columns: [
            {text : '타입',width : 80,dataIndex: 'type'},
            {text : '덱이름',flex : 1,dataIndex: 'name'},
            {text : '덱컬러',width : 60,dataIndex: 'colors',align:'center'},
            {text : '카드수',width : 50,dataIndex: 'quantitys',align:'right'},
            {text : '가격',width : 60,dataIndex: 'sumOfPrice',align:'right'},
            {text : '경기수',width : 50,renderer :gameCount,align:'right'},
            {text : '승율',width : 50,renderer :winRate,align:'right'}
        ],
        dockedItems: [{
            xtype: 'toolbar',
            items: ['<b>덱리스트</b>','->',{
                text:'신규덱등록',
                tooltip:'새로운 덱을 생성한다',
                handler: function(){ newDeckWinToggle(); }
            }]
        }],
        viewConfig: {stripeRows: true}
    });
	
	var currentData; 
	grid.getSelectionModel().on('selectionchange', function(sm, selectedRecord) {
        if (selectedRecord.length) {
        	var data = selectedRecord[0].data;
        	currentData = data;
        	Ext.getCmp('deckName').setText('<b>'+data.name+'</b>');
        	var text = '승패:'+data.win+'/'+data.lose+' (' + winRateCal(data) +  ')';
        	Ext.getCmp('deckRate').setText(text);
        }
    });
	
	var winConfigBar = {
		xtype: 'toolbar',height : 40,width : '100%',
		items: [{text:'<b>덱이름</b>',id:'deckName'},{text:'승패',id:'deckRate'},'->',
		        {text:'증가/감소',enableToggle: true,id:'isMinus'},'-',
		        {text:'<span style="color:blue;font-weight:bold;" >win</span>',handler: function(){
		        	if(currentData==null){
		        		Ext.example.msg('벨리데이션 체크','덱을 먼저 선택해주세요');
		        		return;
		        	}
		        	alert(currentData.id);
		        	//alert(Ext.getCmp('isMinus').pressed);
		        }},
		        {text:'<span style="color:red;font-weight:bold;" >lose</span>',handler: function(){
		        	newDeckWinToggle();
		        }}
		]
	}
	
	var viewport = Ext.create('Ext.Viewport', {
		layout : 'border',renderTo:'here',
		items : [ {
			region : 'west',align : 'stretch',pack : 'start',width : 600,
			margins : '5 5 5 5',layout : 'vbox',items : [grid,winConfigBar]
		}, {
			title:'zzz',
			region : 'center',
			margins : '5 5 5 0'
		},{
			title:'asdasd',
			region : 'south',
			margins : '10 5 5 0'
		}]
	});

    var deckTypeCombo = Ext.create('Ext.form.field.ComboBox', {
        fieldLabel: '덱타입',displayField: 'label',valueField: 'value',anchor:'100%',queryMode: 'local',name:'type',
        store: new Ext.data.JsonStore({ fields: ['value','label'],data: <%= DeckType.JSON %>})
    });
	
	var newDeckForm = Ext.create('Ext.form.Panel', {
		frame:true,border: false,fieldDefaults: { labelWidth: 60},
        defaultType: 'textfield',bodyPadding: 5,
        items: [
                {name: 'id',anchor:'100%',hidden:true,id:'deckId'},
                {fieldLabel: '덱이름',name: 'name',anchor:'100%'},
                {xtype: 'checkboxgroup',fieldLabel: '덱 컬러',
                    items: [
                        {boxLabel: '<span style="color:white">W</span>',name: 'colors',inputValue:'W'},
                        {boxLabel: '<span style="color:blue">U</span>',name: 'colors',inputValue:'U'},
                        {boxLabel: '<span style="color:black">B</span>',name: 'colors',inputValue:'B'},
                        {boxLabel: '<span style="color:red">R</span>',name: 'colors',inputValue:'R'},
                        {boxLabel: '<span style="color:green">G</span>',name: 'colors',inputValue:'G'}
                    ]
                },deckTypeCombo],
        buttons: [{
            text: 'Save',
            handler: function(){
            	$.send('/rest/mtgo/save',newDeckForm.getValues(),function(message){
            		Ext.example.msg('덱 등록/수정',message);
            		newDeckWinToggle();
            		refresh();
        		});
            }
        }]
    });
	var newDeckWin = Ext.create('widget.window', {
        closable: true,closeAction: 'hide',
        width: 300,height: 170,
        title: '덱 등록/수정',
        layout: 'fit',
        items: newDeckForm
  	});
    var newDeckWinToggle = function(){
	    if (newDeckWin.isVisible()) newDeckWin.hide(this);
	    else{
	    	Ext.getCmp('deckId').setValue(''); //혹시 모르니 초기화
	    	newDeckWin.show(this);
	    }
    }
    var refresh = function(){
    	$.send('/rest/mtgo/list',newDeckForm.getValues(),function(message){
    		deckStore.loadData(message);
    	});	
    }
    refresh();
});
	
</script>

<div id="here"></div>
