<%@page import="erwins.webapp.myApp.mtgo.DeckType"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<script type="text/javascript">
Ext.require([ '*' ]);
Ext.onReady(function() {
	
	// ============================= 덱리스트 ===================================
	var currentData;
	var currettSelection; //API를 더 숙지할 필요가 있다
	
    var deckStore = new Ext.data.JsonStore({
        fields: ['id','type','name','win','lose','colors','sumOfPrice','description']
    });
    var cardStore = new Ext.data.JsonStore({
    	//fields: ['cardName','type','rarity','cost','price','edition','matchSize','url','quantity']
    	fields: ['rownum','cardName','type','rarity','cost','edition','matchSize','url','quantity'
    	         ,{name: 'price', sortDir: 'DESC', sortType: 'asFloat', type: 'double'}]
    });
    var winRateCal = function(data){
    	if(data.win==0) return 0+'%';
    	var rate = data.win / (data.win + data.lose);
    	rate = Math.round(rate*100*100)/100;
        return  rate ;
    }
    var winRateRenderer =  function(val,metaData,record,rowIndex,colIndex,store,view) {
    	var data = record.data;
    	var rate = winRateCal(data);
    	if(rate > 70) return  (rate+'%').toSpan('red',true);
    	else if(rate > 60) return  (rate+'%').toSpan('blue',true);
    	else if(rate > 50) return  (rate+'%').toSpan('green');
    	else return  rate+'%';
    }
    var decknameRenderer =  function(val,metaData,record,rowIndex,colIndex,store,view) {
    	var data = record.data;
    	var type = data.type;
    	var label = data.name;
    	if(type=='standard') label = label.toSpan('blue',true);
    	else if(type=='pauper') label = label.toSpan('red');
        return label
    }
    var descriptionRenderer =  function(val,metaData,record,rowIndex,colIndex,store,view) {
    	var data = record.data;
    	metaData.tdAttr = 'data-qtip="'+data.description+'"'
        return val
    }
    
    var W = '<span style="background:white;color:black;font-weight:bold;">&nbsp;W&nbsp;</span>';
    var U = '<span style="background:blue;color:white;font-weight:bold;">&nbsp;U&nbsp;</span>';
    var B = '<span style="background:black;color:white;font-weight:bold;">&nbsp;B&nbsp;</span>';
    var R = '<span style="background:red;color:white;font-weight:bold;">&nbsp;R&nbsp;</span>';
    var G = '<span style="background:green;color:white;font-weight:bold;">&nbsp;G&nbsp;</span>';
    var deckcolorRenderer =  function(val,metaData,record,rowIndex,colIndex,store,view) {
    	var data = record.data;
    	var colors = data.colors;
    	var label = [];
    	for(var i=0;i<colors.length;i++){
    		var c = colors[i];
    		if(c=='W') label.push(W);
    		else if(c=='U') label.push(U);
    		else if(c=='B') label.push(B);
    		else if(c=='R') label.push(R);
    		else if(c=='G') label.push(G);
    	}
        return label.join('');
    }
	var deckGrid = Ext.create('Ext.grid.Panel', {
		store:deckStore,stateful: true,stateId: 'stateGrid',selModel: Ext.create('Ext.selection.CheckboxModel'),
        flex:1,width: '100%',border:false,autoScroll:true,
        columns: [
			Ext.create('Ext.grid.RowNumberer'),
            {text : '타입',width : 80,dataIndex: 'type'},
            {text : '덱컬러',width : 85,renderer:deckcolorRenderer,align:'right'},
            {text : '덱이름',flex : 1,renderer :decknameRenderer},
            {text : '비고',width : 180,dataIndex: 'description',renderer:descriptionRenderer},
            {text : '가격($)',width : 60,dataIndex: 'sumOfPrice',align:'right'},
            {text : '승',width : 40,dataIndex: 'win',align:'right'},
            {text : '패',width : 40,dataIndex: 'lose',align:'right'},
            {text : '승율',width : 60,renderer :winRateRenderer,align:'right'}
        ],
        dockedItems: [{
            xtype: 'toolbar',
            items: ['<b>덱리스트</b>','->',
                {text:'랜덤덱 선택',handler:function(){
                	var selection = deckGrid.getSelectionModel().getSelection();
                	var randomInt = Math.floor(Math.random() * selection.length);
                	var selectedDeck = selection[randomInt].data;
                	Ext.example.msg('랜덤댁 선택',selectedDeck.name.toSpan('red',true)+'<br><br>'+selectedDeck.description);
                	send({type:'mtgoRandomDeck',message:selectedDeck.name,size:selection.length});
                }},
                {text:'리스트 새로고침',handler:function(){ refresh(); }},
                {id:'deckUpdateBtn',disabled:true,text:'덱 수정/삭제',tooltip:'이미 생성된 덱의 정보를 수정한다.',handler:function(){ newDeckWinToggle(currentData); }},
                {text:'신규덱등록',tooltip:'새로운 덱을 생성한다',handler:function(){ newDeckWinToggle(); }}
            ]
        }],
        viewConfig: {stripeRows: true}
    });
	
	// ============================= 카드 리스트 ===================================
	
	var cardnameRenderer =  function(val,metaData,record,rowIndex,colIndex,store,view) {
    	var data = record.data;
    	var imgTag = "<img src='"+ data.imageUrl +"' >";
    	metaData.tdAttr = 'data-qtip="'+imgTag+'"'
    	var rarity = data.rarity;
    	var label = data.cardName;
    	if(rarity=='Mythic Rare') label = label.toSpan('red',true);
    	else if(rarity=='Rare') label = label.toSpan('blue',true);
    	else if(rarity=='Uncommon') label = label.toSpan('green',true);
        return label
    }
	
	var cardGrid = Ext.create('Ext.grid.Panel', {
		store:cardStore,flex:1,width: 660,border:false,autoScroll:true,
        columns: [
            {text : '카드이름',flex : 1,dataIndex: 'name',renderer :cardnameRenderer},
            {text : '수량',width : 40,dataIndex: 'quantity',align:'center'},
            {text : '타입',width : 140,dataIndex: 'type'},
            {text : '희귀도',width : 70,dataIndex: 'rarity'},
            {text : '발비',width : 50,dataIndex: 'cost'},
            {text : '가격($)',width : 50,dataIndex: 'price',align:'right'},
            {text : '에디션',width : 130,dataIndex: 'edition'}
            //{text : 'URL',width : 150,dataIndex: 'url'}
        ],
        dockedItems: [{
            xtype: 'toolbar',
            items: [{text:'<b>덱이름</b>',id:'deckName'},'->',{text:'',id:'loadingMessage'},
                {text:'증가/감소',enableToggle: true,id:'isMinus'},'-',
		        {text:'<span style="color:blue;font-weight:bold;" >win</span>',id:'winBtn',disabled: true,handler: function(){updateCount(true);}},
		        {text:'<span style="color:red;font-weight:bold;" >lose</span>',id:'loseBtn',disabled: true,handler: function(){updateCount(false);}},'-',
				{id:'deckCalBtn',disabled:true,text:'덱 가격산정',tooltip:'업로드된 덱의 가격을 산정한다.',handler:function(){
					var loadingMessage = '카드의 가격 정보를 가져오는중입니다. 잠시 기다려주세요 '.toSpan('red',true);
            		Ext.getCmp('loadingMessage').setText(loadingMessage);
					$.send('/rest/mtgo/deckCal',{id:currentData.id},function(message){
						Ext.getCmp('loadingMessage').setText();
	            		Ext.example.msg('덱 가격산정',message);
	            		refresh();
	        		});
				}}
            ]
        }],
        viewConfig: {stripeRows: true},
        listeners : {   ///아오~~ 몰라서 걍 일케 진행
        	'cellclick' : function(grid,index,cellIndex,e){ // do something }
        		if(cellIndex !=0) return;
        		var url = e.data.url;
        		window.open(url,"카드","width=660px,height=580px");
        		//window.open(e.data.imageUrl,"카드","width=660px,height=580px");
        	}
		}
    });
	
	// ============================= 덱 수정삭제 팝업 ===================================
    var deckTypeCombo = Ext.create('Ext.form.field.ComboBox', {
    	id:'deck.type',fieldLabel: '덱타입',displayField: 'label',valueField: 'value',anchor:'100%',queryMode: 'local',name:'type',
        store: new Ext.data.JsonStore({ fields: ['value','label'],data: <%= DeckType.JSON %>})
    });
	
	var newDeckForm = Ext.create('Ext.form.Panel', {
		frame:true,border: false,fieldDefaults: { labelWidth: 60},
        defaultType: 'textfield',bodyPadding: 5,
        items: [
                {id:'deck.id',name: 'id',anchor:'100%',hidden:true},
                {id:'deck.name',fieldLabel: '덱이름',name: 'name',anchor:'100%'},
                {id:'deck.description',fieldLabel: '비고',name: 'description',anchor:'100%'},
                {id:'deck.colors',xtype: 'checkboxgroup',fieldLabel: '덱 컬러',
                    items: [
                        {boxLabel: '<span style="color:white">W</span>',name: 'colors',inputValue:'W'},
                        {boxLabel: '<span style="color:blue">U</span>',name: 'colors',inputValue:'U'},
                        {boxLabel: '<span style="color:black">B</span>',name: 'colors',inputValue:'B'},
                        {boxLabel: '<span style="color:red">R</span>',name: 'colors',inputValue:'R'},
                        {boxLabel: '<span style="color:green">G</span>',name: 'colors',inputValue:'G'}
                    ]
                },deckTypeCombo],
        buttons: [
            {text: 'delete',id:'deleteBtn',handler: function(){
            	$.send('/rest/mtgo/delete',newDeckForm.getValues(),function(message){
            		Ext.example.msg('덱 등록/수정',message);
            		newDeckWinToggle();
            		refresh();
        		});
            }},
        	{text: 'Save',handler: function(){
            	$.send('/rest/mtgo/save',newDeckForm.getValues(),function(message){
            		Ext.example.msg('덱 등록/수정',message);
            		newDeckWinToggle();
            		refresh();
        		});
            }}
        ]
    });
	var newDeckWin = Ext.create('widget.window', {
        closable: true,closeAction: 'hide',width: 300,height: 200,
        title: '덱 등록/수정',layout: 'fit',items: newDeckForm
  	});
    var newDeckWinToggle = function(data){
	    if (newDeckWin.isVisible()) newDeckWin.hide();
	    else{
	    	newDeckWin.show();
	    	if(data==null){
	    		Ext.getCmp('deck.id').setValue(''); //혹시 모르니 초기화
	    		Ext.getCmp('deck.name').setValue('');
	    		Ext.getCmp('deck.description').setValue('');
	    		Ext.getCmp('deck.colors').setValue({colors:''});
	    		Ext.getCmp('deck.type').setValue('');
	    		Ext.getCmp('deleteBtn').setDisabled(true);
	    	}else{
	    		Ext.getCmp('deck.id').setValue(data.id);
	    		Ext.getCmp('deck.name').setValue(data.name);
	    		Ext.getCmp('deck.description').setValue(data.description);
	    		Ext.getCmp('deck.colors').setValue({colors:data.colors});
	    		Ext.getCmp('deck.type').setValue(data.type);
	    		Ext.getCmp('deleteBtn').setDisabled(false);
	    	}
	    }
    }	
	
	// ============================= 이벤트 ===================================

	/** 덱리스트 클릭 */
	var selectionchange = function(sm, selectedRecord) {
        if (selectedRecord.length==1) {
        	currettSelection = selectedRecord;
        	currentData = selectedRecord[0].data;
        	Ext.getCmp('deckName').setText('<b>'+currentData.name+'</b>');
        	refreshCard();
        }
        Ext.getCmp('winBtn').setDisabled(selectedRecord.length === 0);
        Ext.getCmp('loseBtn').setDisabled(selectedRecord.length === 0);
        Ext.getCmp('deckUpdateBtn').setDisabled(selectedRecord.length === 0);
        Ext.getCmp('deckCalBtn').setDisabled(selectedRecord.length === 0);
        Ext.getCmp('deckUpload.deckFile').setDisabled(selectedRecord.length === 0);
    }
	deckGrid.getSelectionModel().on('selectionchange',selectionchange );
	
	/** 덱 승리/패배 펑션 */
	var updateCount = function(isWin){
		var loadingMessage = '덱 정보를 업데이트중입니다... 잠시 기다려주세요 '.toSpan('red',true);
    	Ext.getCmp('loadingMessage').setText(loadingMessage);
    	var param = {id:currentData.id,isWin:isWin,isMinus:Ext.getCmp('isMinus').pressed};
    	$.send('/rest/mtgo/updateWinRate',param,function(deckInfo){
    		Ext.getCmp('loadingMessage').setText();
    		var msg = isWin ? '승리'.toSpan('blue',true) : '패배'.toSpan('red',true);
    		Ext.example.msg(deckInfo.name + ' : '+msg,'승패 : ' + deckInfo.win +'/'+ deckInfo.lose );
    		refresh(currettSelection);
    	});	
	}
	
	var uploadPanel = Ext.create('Ext.form.Panel', {
        frame: true,height : 40,width : '100%',bodyPadding: '2 2 0',
        items: [
			{xtype: 'textfield',id:'deckUpload.id',name: 'id',anchor:'100%',hidden:true},
			{xtype: 'filefield',id:'deckUpload.deckFile',name: 'deckFile',buttonOnly: true,hideLabel: true,buttonText: '덱 업로드',width : '100%',disabled:true,
            listeners: {'change': function(fb, v){
            		var loadingMessage = '덱 정보를 업로드중입니다. 잠시 기다려주세요 '.toSpan('red',true);
            		Ext.getCmp('loadingMessage').setText(loadingMessage);
            		Ext.getCmp('deckUpload.id').setValue(currentData.id);
                	var form = this.up('form').getForm();
                	form.submit({
                        url: '/rest/mtgo/upload',waitMsg: '파일 업로드 중입니다....',
	                	success: function(form, action) { 
	                		refresh();
	                		Ext.example.msg('업데이트','덱 리스트가 갱신되었습니다');
	                		Ext.getCmp('loadingMessage').setText();
	                	},
	                	failure: function(form, action) { //실패로 나온다.. 걍 쓰자 ㅅㅂ 
	                		refresh();
	                		Ext.example.msg('업데이트','덱 리스트가 갱신되었습니다');
	                		Ext.getCmp('loadingMessage').setText();
	                	}
                    });}}}
			]
    });
	
    var refresh = function(selection){
    	$.send('/rest/mtgo/list',newDeckForm.getValues(),function(message){
    		deckStore.loadData(message);
    		if(selection!=null) deckGrid.getSelectionModel().select(selection);
    	});	
    }
    var refreshCard = function(){
    	var loadingMessage = '카드 정보를 로딩중입니다... 잠시 기다려주세요 '.toSpan('red',true);
    	Ext.getCmp('loadingMessage').setText(loadingMessage);
    	$.send('/rest/mtgo/cardList',{id:currentData.id},function(message){
    		cardStore.loadData(message);
    		Ext.getCmp('loadingMessage').setText();
    	});
    }
    
	// ============================= 빌드 ===================================
	
	/** margins은 순서대로 상우하좌 이다. (시계방향) 하단 30을 줘야 ㅅㅂ 와꾸가 맞는다. 아마 헤더부분 때문인듯 */
	var viewport = Ext.create('Ext.Viewport', {
		layout : 'border',renderTo:'here',
		items : [ {region : 'west',align : 'stretch',pack : 'start',width : 780,margins : '5 5 30 5',layout : 'vbox',items : [deckGrid]}, 
		          {region : 'center',margins : '5 5 5 0' ,items : [uploadPanel,cardGrid] }]
	});
    
    refresh();
});
	
</script>

<div id="here"></div>
