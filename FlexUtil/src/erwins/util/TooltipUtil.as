package erwins.util{
	import com.adobe.serialization.json.*;
	import com.google.maps.Color;
	
	import erwins.component.PanelToolTip;
	import erwins.util.effect.EventEffect;
	
	import flash.events.MouseEvent;
	
	import mx.controls.Alert;
	import mx.controls.listClasses.ListBase;
	import mx.core.UIComponent;
	import mx.events.ListEvent;
	import mx.events.ToolTipEvent;
	import mx.managers.ToolTipManager;
	import mx.utils.*;
	
	/** tooltip에 대응하는 속성은 텍스트 이다. 따라서 내부이벤트에서 바꿔줘야 한다. */
	public class TooltipUtil{
		
		/** 고정된 타입의 툴팁을 생성한다. */
		public static function addStaticTooltip(base:UIComponent,title:String,description:String):void{
			
			var ptt:PanelToolTip = new PanelToolTip(); //사용자 정의형 퉅팁 객체 생성
			ptt.title = title;
			ptt.text = description;
			ptt.setStyle("backgroundColor",Color.YELLOW);
			//backgroundColor
			
			//EventEffect.showHideIris(ptt);
			
			var fun:Function = function(event:ToolTipEvent):void {
				event.toolTip = ptt;
			}
			
			base.addEventListener(MouseEvent.MOUSE_OVER,function(event:MouseEvent):void{
				event.target.addEventListener(ToolTipEvent.TOOL_TIP_CREATE,fun);
				event.target.toolTip = description;
			});
			
			base.addEventListener(MouseEvent.MOUSE_OUT,function(event:MouseEvent):void{
		        event.target.toolTip = null;
		        event.target.removeEventListener(ToolTipEvent.TOOL_TIP_CREATE,fun);
			});
		}
		
		/** Grid에  PanelToolTip 형태의 툴팁을 추가한다.*/
		public static function addGrid(base:ListBase,title:String,description:String):void{
			
			if(base==null) Alert.show('base is null!!','Error');
			
			ToolTipManager.showEffect = EventEffect.showFade;
			ToolTipManager.showDelay = 2000;
			
			ToolTipManager.hideDelay = Infinity; //요거 테스ㅡㅌ.
			ToolTipManager.scrubDelay = 100; //default
			
			var targetData:Object;
			
			var fun:Function = function(event:ToolTipEvent):void {
				var ptt:PanelToolTip = new PanelToolTip(); //사용자 정의형 퉅팁 객체 생성
				ptt.title = targetData[title];
				ptt.text = targetData[description];
				event.toolTip = ptt;
			}
			
			base.addEventListener(ListEvent.ITEM_ROLL_OVER,function(event:ListEvent):void{
				targetData = event.itemRenderer.data;
				event.target.addEventListener(ToolTipEvent.TOOL_TIP_CREATE,fun);
				event.target.toolTip = event.itemRenderer.data[description];
			});
			
			base.addEventListener(ListEvent.ITEM_ROLL_OUT,function(event:ListEvent):void{
		        event.target.toolTip = null;
		        event.target.removeEventListener(ToolTipEvent.TOOL_TIP_CREATE,fun);
			});
		}
		
		
	}
}