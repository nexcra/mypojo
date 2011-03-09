package erwins.util.UILib{
	import com.adobe.serialization.json.*;
	
	import erwins.component.ListToolTip;
	import erwins.component.PanelToolTip;
	import erwins.util.effect.EventEffect;
	
	import flash.events.MouseEvent;
	
	import mx.collections.ArrayCollection;
	import mx.core.FlexSprite;
	import mx.core.UIComponent;
	import mx.events.ListEvent;
	import mx.events.ToolTipEvent;
	import mx.managers.ToolTipManager;
	import mx.utils.*;
	
	/** tooltip에 대응하는 속성은 텍스트 이다. 따라서 내부이벤트에서 바꿔줘야 한다. */
	public class TooltipUtil{
		
		/** 고정된 타입의 툴팁을 생성한다. 기본 색상은 노란색이다. */
		public static function addStaticTooltip(base:UIComponent,title:String,description:String,color:int=0xF9FF00):void{
			
			var ptt:PanelToolTip = new PanelToolTip(); //사용자 정의형 퉅팁 객체 생성
			ptt.title = title;
			ptt.text = description;
			ptt.setStyle("backgroundColor",color);
			
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
		
		/** Grid에  PanelToolTip 형태의 툴팁을 추가한다.
		 * AdvancedDataGrid때문에 타겟을 Listbase가 아닌 FlexSprite로 지정했다.
		 * */
		public static function addPanelToolTip(base:FlexSprite,title:String,description:String):void{
			
			if(base==null) throw new Error('base is null!!');
			ToolTipManager.showEffect = EventEffect.showFade;
			ToolTipManager.showDelay = 2000;
			
			ToolTipManager.hideDelay = Infinity; //요거 테스ㅡㅌ.
			ToolTipManager.scrubDelay = 100; //default
			
			var targetData:Object;
			
			var fun:Function = function(event:ToolTipEvent):void {
				var ptt:PanelToolTip = new PanelToolTip(); //사용자 정의형 퉅팁 객체 생성
				ptt.title = targetData[title]==null ? title : targetData[title];
				ptt.text = targetData[description];
				event.toolTip = ptt;
			}
			
			base.addEventListener(ListEvent.ITEM_ROLL_OVER,function(event:ListEvent):void{
				targetData = event.itemRenderer.data;
				event.target.addEventListener(ToolTipEvent.TOOL_TIP_CREATE,fun);
				event.target.toolTip = event.itemRenderer.data[description];
				//event.target.toolTip = "1"; //아무값이나 상관없다. 단지 트리거일뿐.
			});
			
			base.addEventListener(ListEvent.ITEM_ROLL_OUT,function(event:ListEvent):void{
		        event.target.toolTip = null;
		        event.target.removeEventListener(ToolTipEvent.TOOL_TIP_CREATE,fun);
			});
		}
		
		/** Grid에  ListToolTip 형태의 툴팁을 추가한다. list가 비어있으면 툴팁을 추가하지 않는다. */
		public static function addListToolTip(base:FlexSprite,title:String,description:String,tooltipColumsInfo:Array):void{
			
			if(base==null) throw new Error('base is null!!');
			
			ToolTipManager.showEffect = EventEffect.showFade;
			ToolTipManager.showDelay = 2000;
			
			ToolTipManager.hideDelay = Infinity; //요거 테스ㅡㅌ.
			ToolTipManager.scrubDelay = 100; //default
			
			var targetData:Object;
			
			var fun:Function = function(event:ToolTipEvent):void {
				
				var array:Array =  targetData[description] as Array;
				if(array==null || array.length==0) return;
				
				var ptt:ListToolTip = new ListToolTip();
				var tipTitle:String = targetData[title];
				ptt.title = tipTitle==null ? title : tipTitle;
				ptt.columns = tooltipColumsInfo;
				ptt.dataProvider = new ArrayCollection(array);
				event.toolTip = ptt;
			}
			
			base.addEventListener(ListEvent.ITEM_ROLL_OVER,function(event:ListEvent):void{
				targetData = event.itemRenderer.data;
				event.target.addEventListener(ToolTipEvent.TOOL_TIP_CREATE,fun);
				event.target.toolTip = "1"; //아무값이나 상관없다. 단지 트리거일뿐.
			});
			
			base.addEventListener(ListEvent.ITEM_ROLL_OUT,function(event:ListEvent):void{
		        event.target.toolTip = null;
		        event.target.removeEventListener(ToolTipEvent.TOOL_TIP_CREATE,fun);
			});	
		}		
		
		
	}
}