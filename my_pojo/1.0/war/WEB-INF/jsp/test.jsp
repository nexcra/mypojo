<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<script type="text/javascript">
Ext.require([ '*' ]);
Ext.onReady(function() {
	
    var store = new Ext.data.JsonStore({
        fields: ['type','name']
    });
		
	var dd = [{type: 'ccc',name: 'fldddoat'}]
	store.loadData(dd);
    
	var grid = Ext.create('Ext.grid.Panel', {
		store:store,
        stateful: true,
        stateId: 'stateGrid',
        columns: [
            {text : '타입',width : 80,dataIndex: 'type'},
            {text : '덱이름',flex : 1,dataIndex: 'name'},
            {text : '카드수',width : 50,dataIndex: 'sumOfCount'},
            {text : '가격',width : 50,dataIndex: 'sumOfPrice'},
            {text : '평균가',width : 50,dataIndex: 'avgOfPrice'},
            {text : '경기수',width : 50,dataIndex: 'gameCount'},
            {text : '승율',width : 50,dataIndex: 'winRate'}
        ],
        dockedItems: [{
            xtype: 'toolbar',
            items: ['<b>덱리스트</b>','->',{
                text:'신규덱등록',
                tooltip:'새로운 덱을 생성한다',
                handler: function(){ newDeckWinOpen(); }
            }]
        }],
        flex:1,
        width: '100%',
        border:false,
        autoScroll:true,
        viewConfig: {
            stripeRows: true
        }
    });
	
	var viewport = Ext.create('Ext.Viewport', {
		layout : 'border',
		items : [ {
			region : 'west',
			align : 'stretch',
			pack : 'start',
			width : 500,
			margins : '5 5 5 5',
			layout : 'vbox',
			items : [grid, {
				title : '승율',
				html : 'ffffffffff',
				height : 100,
				width : '100%',
				collapsible : true,
				collapseMode : 'mini',
				border:false
			} ]
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
	
	var form = Ext.create('Ext.form.Panel', {
		frame:true,
		border: false,
        fieldDefaults: {
            labelWidth: 60
        },
        defaultType: 'textfield',
        bodyPadding: 5,
        items: [
                {fieldLabel: '덱이름',name: 'name',anchor:'100%'},
                {xtype: 'checkboxgroup',fieldLabel: '덱 컬러',cls: 'x-check-group-alt',
                    items: [
                        {boxLabel: 'Item 1', name: 'cb-auto-1'},
                        {boxLabel: 'Item 2', name: 'cb-auto-2', checked: true},
                        {boxLabel: 'Item 3', name: 'cb-auto-3'},
                        {boxLabel: 'Item 4', name: 'cb-auto-4'},
                        {boxLabel: 'Item 5', name: 'cb-auto-5'}
                    ]
                },
                {fieldLabel: '덱 타입',name: 'name',anchor:'100%'}],
        buttons: [{
            text: 'Save',
            handler: function(){
            	$.send('/rest/mtgo/list',form.getValues(),function(message){
            		alert(message);
        		});
            }
        }]
    });
	var win = Ext.create('widget.window', {
        closable: true,closeAction: 'hide',
        width: 300,height: 150,
        title: '덱 등록/수정',
        layout: 'fit',
        items: form
  	});
    var newDeckWinOpen = function(){
	    if (win.isVisible()) win.hide(this);
	    else win.show(this);
    }
});
	
</script>

<div id="here"></div>