package erwins.util.effect{
	import com.adobe.ac.mxeffects.CubeRotate;
	import com.adobe.ac.mxeffects.DistortionConstants;
	import com.adobe.ac.mxeffects.Flip;
	import com.adobe.serialization.json.*;
	
	import mx.core.Application;
	import mx.core.UIComponent;
	import mx.effects.AddChildAction;
	import mx.effects.RemoveChildAction;
	import mx.effects.Sequence;
	import mx.events.EffectEvent;
	import mx.utils.*;
	
	/** 3D 랜덤 이펙트...  실패작.
	 * 각체가 state가 완성되기 전에는 생성되지 않음으로 null에러가 난다. */
	public class RandomEffect{
		
		private var _cubeRotate:CubeRotate;
		private var _flip:Flip;
		
		private var app:Application;
		private var fromState:UIComponent;
		private var toState:UIComponent;
		
		public function RandomEffect(app:Application,fromState:UIComponent,toState:UIComponent):void{
			this.fromState = fromState;
			this.toState = toState;
			//this.fromState = toState;
			//this.toState = fromState;
		}
		
		public function get flip():Flip{
			if(_flip) return _flip;
			_flip = new Flip( fromState );
			_flip.siblings = [ toState ];
			_flip.direction = DistortionConstants.RIGHT;
			_flip.duration = 1000;
			return _flip;
		}
		
		public function run():void{
			var fl:Flip = this.flip;
			fl.addEventListener( EffectEvent.EFFECT_END, function(e:EffectEvent):void{
				app.removeChild( fromState );
				app.addChild( toState );
			} );
			fl.play();
		}
		
		public function get seq():Sequence{
			var seq:Sequence = new Sequence();
			seq.addChild(this.flip);
			
			var rem:RemoveChildAction = new RemoveChildAction();
			rem.target = fromState;
			seq.addChild(rem);
			
			var add:AddChildAction = new AddChildAction();
			add.target = toState;
			seq.addChild(add);
			return seq;
		}
		
		public function get cubeRotate():CubeRotate{
			if(_cubeRotate) return _cubeRotate;
			_cubeRotate = new CubeRotate();
			_cubeRotate = new CubeRotate( fromState );
			_cubeRotate.siblings = [ toState ];
			_cubeRotate.direction = DistortionConstants.RIGHT;
			_cubeRotate.duration = 1000;
			return _cubeRotate;
		}

		
		
	}
}