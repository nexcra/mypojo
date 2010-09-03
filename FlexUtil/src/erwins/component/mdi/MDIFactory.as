package erwins.component.mdi{
	import flexlib.mdi.containers.MDICanvas;
	import flexlib.mdi.effects.effectsLib.MDIVistaEffects;
	
	import mx.controls.Label;
	import mx.controls.TextArea;
	import mx.core.ScrollPolicy;
	import mx.core.UIComponent;
	
	public class MDIFactory{
		
		private var canvas:MDICanvas;
		private var base:UIComponent;
		
		public function MDIFactory(base:UIComponent){
			this.base = base;
		}
		
		/** 늦은 초기화를 수행한다. "backgroundAlpha",0 은 안해도 되는듯. */
		private function init():void{
			if(canvas!=null) return; 
			canvas = new MDICanvas();
			canvas.percentHeight = 100;
			canvas.percentWidth = 100;
			canvas.horizontalScrollPolicy = ScrollPolicy.OFF;
			canvas.verticalScrollPolicy = ScrollPolicy.OFF;
			canvas.effectsLib = flexlib.mdi.effects.effectsLib.MDIVistaEffects; //이게 젤 괜찮은듯.
			base.addChild(canvas);
		}
		
		/** ex) mdi.addTextWindow("도움말 입니다.",200,150).text = "내용입력" */
		public function addTextWindow(title:String,width:int,height:int):Label{
			init();
			var instance:MDIText = new MDIText();
			instance.width = width;
			instance.height = height;
			instance.title = title;
			//instance.setStyle("titleFontSize",25);
			//instance.titleBarOverlay.setStyle("fontSize",50);
			canvas.windowManager.add(instance);
			return instance.textLabel;
		}
		
		public function addTextArea(title:String,width:int,height:int):TextArea{
			init();
			var instance:MDITextArea = new MDITextArea();
			instance.width = width;
			instance.height = height;
			instance.title = title;
			//instance.setStyle("titleFontSize",25);
			//instance.titleBarOverlay.setStyle("fontSize",50);
			canvas.windowManager.add(instance);
			return instance.textLabel;
		}

	}
}