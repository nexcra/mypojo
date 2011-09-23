<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<script type="text/javascript">
Ext.require([ '*' ]);
Ext.onReady(function() {
	
	Ext.create('Ext.Panel', {
	    width: 500,
	    height: 400,
	    title: "VBoxLayout Panel",
	    layout: {
	        type: 'vbox',
	        align: 'center'
	    },
	    renderTo: document.body,
	    items: [{
	        xtype: 'panel',
	        title: 'Inner Panel One',
	        width: 250,
	        flex: 2
	    },
	    {
	        xtype: 'panel',
	        title: 'Inner Panel Two',
	        width: 250,
	        flex: 4
	    },
	    {
	        xtype: 'panel',
	        title: 'Inner Panel Three',
	        width: '50%',
	        flex: 4
	    }]
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
	
});
	
</script>

<div id="here"></div>