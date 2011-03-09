package erwins.component{

import mx.containers.Canvas;
import mx.containers.VBox;
import mx.controls.Label;
import mx.effects.Move;

[Exclude(name="horizontalLineScrollSize", kind="property")]
[Exclude(name="horizontalPageScrollSize", kind="property")]
[Exclude(name="horizontalScrollBar", kind="property")]
[Exclude(name="horizontalScrollPolicy", kind="property")]
[Exclude(name="horizontalScrollPosition", kind="property")]
[Exclude(name="maxHorizontalScrollPosition", kind="property")]
[Exclude(name="maxVerticalScrollPosition", kind="property")]
[Exclude(name="verticalLineScrollSize", kind="property")]
[Exclude(name="verticalPageScrollSize", kind="property")]
[Exclude(name="verticalScrollBar", kind="property")]
[Exclude(name="verticalScrollPolicy", kind="property")]
[Exclude(name="verticalScrollPosition", kind="property")]
	
	/** 주워온거 수정. */
	public class NumberRollerInstance extends Canvas{
	
	    public function NumberRollerInstance(){
	        super();
	        horizontalScrollPolicy = "off";
	        verticalScrollPolicy = "off";
	    }
	
	    private var prevValue:int = 0;
	    private var valueChanged:Boolean;
	    private var container:VBox;
	
		/** 요게 무빙이펙트. */
	    private var moveTween:Move;
	
	
	    private var _value:int = 0;
	    public function set value(value:int):void{
	        _value = value;
	        valueChanged = true;
	        invalidateProperties();
	    }
	    [Inspectable(category="General", defaultValue="0")]
	    public function get value():int{
	        return _value;
	    }
	
	    private var _duration:Number = 1000;
	    public function set duration(value:Number):void{
	        _duration = value;
	    }
	
	 	/** 대략 옵션 */
		public var easingFunction:Function = null;
	
	    override protected function createChildren():void{
	        super.createChildren();
	        if (!container){
	            container = new VBox();
	            container.setStyle("horizontalAlign", "center");
	            container.setStyle("verticalAlign", "middle");
	
	            var instance:Label;
	            for (var i:int = 0; i < 10; i ++){
	                instance = new Label();
	                instance.text = String(i);
	                container.addChild(instance);
	                container.width = instance.textWidth;
	                container.height = instance.textHeight;
	            }
	            addChild(container);
	        }
	    }
	
	    override protected function commitProperties():void{
	        super.commitProperties();
	        if (valueChanged){
	            valueChanged = false;
	            callLater(showCurrentValue);
	        }
	    }
	
	    override protected function measure():void{
	        super.measure();
	        width = container.getExplicitOrMeasuredWidth() / 2;
	        height = container.getExplicitOrMeasuredHeight() / 10;
	    }
	
	    /** value에 들어온 데이터를 화면에 보여준다. */
	    private function showCurrentValue():void{
	        if (!initialized){
	            callLater(showCurrentValue);
	            return;
	        }
	
	        var toLabel:Label = Label(container.getChildAt(_value));
	        moveTween = new Move(container);
	        moveTween.yFrom = container.y;
	        moveTween.yTo = -toLabel.y;
	        moveTween.duration = _duration;
	        moveTween.easingFunction = easingFunction
	        moveTween.play();
	    }
	}
}