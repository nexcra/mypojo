<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<script type="text/javascript">

Ext.require(['*']);

Ext.onReady(function() {
	var viewport = Ext.create('Ext.Viewport', {
        layout:'border',
        items:[{
            region:'덱리스트',
            id:'west-panel',
            title:'West',
            split:true,
            width: 300,
            minSize: 100,
            maxSize: 500,
            margins:'5 0 5 5',
            cmargins:'35 5 5 5',
            layout:'vbox',
            layoutConfig:{
                animate:true
            },
            items: [{
                html: 'aaaaaaaaaaa',
                title:'Navigation',
                autoScroll:true,
                border:true,
                iconCls:'nav'
            },{
                title:'Settings',
                html: 'bbbbbbbbbbb',
                border:false,
                autoScroll:true,
                iconCls:'settings'
            }]
        },{
            region:'center',
            margins:'35 5 5 0',
            layout:'column',
            autoScroll:true,
            defaults: {
                layout: 'anchor',
                defaults: {
                    anchor: '100%'
                }
            },
            items: [{
                columnWidth: 1/3,
                baseCls:'x-plain',
                bodyStyle:'padding:5px 0 5px 5px',
                items:[{
                    title: 'A Panel',
                    html: Ext.example.shortBogusMarkup
                }]
            },{
                columnWidth: 1/3,
                baseCls:'x-plain',
                bodyStyle:'padding:5px 0 5px 5px',
                items:[{
                    title: 'A Panel',
                    html: Ext.example.shortBogusMarkup
                }]
            },{
                columnWidth: 1/3,
                baseCls:'x-plain',
                bodyStyle:'padding:5px',
                items:[{
                    title: 'A Panel',
                    html: Ext.example.shortBogusMarkup
                },{
                    title: 'Another Panel',
                    html: Ext.example.shortBogusMarkup
                }]
            }]
        }]
    });
	
});
</script>

<div id="here" ></div>

