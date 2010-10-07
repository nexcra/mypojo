package erwins.openApi{
	import com.adobe.serialization.json.*;
	import com.nhn.maps.NCoordType;
	import com.nhn.maps.NLatLng;
	import com.nhn.maps.NMap;
	import com.nhn.maps.controls.NMapBtns;
	import com.nhn.maps.controls.NZoomControl;
	import com.nhn.maps.enums.HorizontalAlignment;
	import com.nhn.maps.enums.VerticalAlignment;
	import com.nhn.maps.events.NMapEvent;
	import com.nhn.maps.events.NMarkEvent;
	import com.nhn.maps.overlays.NInfoWindow;
	import com.nhn.maps.overlays.NMark;
	import com.nhn.maps.overlays.NMarkParams;
	import com.nhn.maps.overlays.NPlaceMark;
	
	import erwins.util.http.Ajax;
	import erwins.util.http.Mediator;
	import erwins.util.json.Jsons;
	import erwins.util.lib.Alerts;
	
	import mx.collections.ArrayCollection;
	import mx.core.UIComponent;
	import mx.utils.*;
	
	public class NaverMapHelper{
		
		/** localhost/ */
		private static const LOCAL_KEY:String = "ac7298e783b0c0ebced2520878d43c35";
		
		private var me:NaverMapHelper = this as NaverMapHelper;
		private var _container:UIComponent;
		private var _map:NMap;
		private var _searchUrl:String;
		private var _saveUrl:String;
		private var _removeUrl:String;
		private var _saveCallback:Function;
		private var _removeCallback:Function;
		
		[Bindable] public var _markerList:ArrayCollection;
		
		/** 1. 지도 객체를 생성한다. 지도 컨테이너, 지도키, 높이, 너비를 인자값으로 설정한다. */
		public function instance(base:UIComponent,key:String = null):NMap{
			_container = base;
		    _map = new NMap(base, key==null ? LOCAL_KEY : key , base.width, base.height);
		    return _map
		}
		
		/** 2. 마커의 DB연동 URL을 세팅한다. */
		public function config(searchUrl:String,saveUrl:String,removeUrl:String):void{
			this._saveUrl = saveUrl;
			this._removeUrl = removeUrl;
			this._searchUrl = searchUrl;
		}
		
		/** 3. 초기화~ ㄱㄱ */
		public function defaultBuild(afterInit:Function):void{
			
		    /** 지도  좌표 타입을 위경도 좌표로 설정 */
		    _map.coordType = NCoordType.WGS84;
		    _map.wheelZoomEnabled = true; //휠은 IE에서만 된다.
		    _map.autoPanningEnabled = false;
		    
		    /** 지도에 초기화 이벤트를 등록한다. */
		    _map.addEventListener(NMapEvent.INIT, function onMapInitHandler(event:NMapEvent):void{
		    	
			    /** 지도 중심  좌표를 설정한다. 줌 레벨은 7이다. */
			    _map.setCenterAndZoom(new NLatLng(37.3660835, 127.1080041), 7);
			    
			    /** 줌 컨트롤러 객체를 생성한다. / 지도내에서의 수평/수직 정렬을 설정한다. */
			    var zoomControl:NZoomControl = new NZoomControl();
			    zoomControl.setAlign(HorizontalAlignment.RIGHT);
			    zoomControl.setVAlign(VerticalAlignment.TOP);
			    _map.addControl(zoomControl);
			    
			    /** 미니맵 객체를 생성한다. 설정방법은 줌 컴트롤러와 동일한다. */
			    //구려서 뺐다.
			    /*
			    var indexMap:NIndexMap = new NIndexMap();
			    indexMap.setAlign(HorizontalAlignment.RIGHT);
			    indexMap.setVAlign(VerticalAlignment.BOTTOM);
			    _map.addControl(indexMap);*/
			    
			    /** 지도선택 버튼이다. */
				var mapBtns:NMapBtns = new NMapBtns();
			    mapBtns.setAlign(HorizontalAlignment.RIGHT);
			    mapBtns.setVAlign(VerticalAlignment.TOP);
			    _map.addControl(mapBtns);
			    
			    var mediator:Mediator = new Mediator(_container);
			    var ajax:Ajax = new Ajax(_searchUrl);
				ajax.setMediator(mediator);
				ajax.send(null,function(json:Jsons):void{
					_markerList = json.getArray();
					for each(var eachMarker:Object in _markerList) addMarkerToDisplay(eachMarker);
					afterInit();
				});
			});
		}
		
		/** 실행시마다 새로운 윈도우 인스턴스를 반환한다. */
		public function newInfoWindow(isPlaceMarker:Boolean):NInfoWindow{
			var infoWindow:NInfoWindow = new NInfoWindow();
		    /** 정보창을 중심점으로 기즌으로 우측상단으로 정렬한다. */
		    infoWindow.setAlign(HorizontalAlignment.RIGHT);
		    infoWindow.setVAlign(VerticalAlignment.TOP);
		    
		    //마커 객체를 가리지 않게 정보창을 약간 이동시킨다.
		    if(isPlaceMarker) infoWindow.offset(20, 5); 
		    else infoWindow.offset(15, 0);
		    infoWindow.hideWindow();
		    _map.addOverlay(infoWindow);
		    return infoWindow;
		}
		
		/** 지도와 리스트에서 동시에 제거한다. */
		public function removeMarker(markerData:Object):void{
			var index:int;
			for(var i:int;i<_markerList.length;i++){
				if(_markerList[i].id == markerData.id){
					index = i;
					break;
				}
			}
			_markerList.removeItemAt(index);
			_map.removeOverlay(markerData.marker);
		}
		
		/**
		 * DB의 자료를 입력하는데도 사용함으로 list에는 추가하지 않는다. 
		 * 메타정보로 실제 마커를 지도에 등록한다. 또한 메타정보에 marker를 추가한다.
		 *  이 메타정보는 반드시? id(PK)를 가진다. */
		public function addMarkerToDisplay(markerData:Object):NMark{
			var markParams:NMarkParams = new NMarkParams();
	        markParams.dragEnabled = true;
	        markParams.label = markerData.label;
	        
	        var placeMark:Boolean = markerData.displayType=='NPlaceMark';
	        var point:NLatLng = new NLatLng(markerData.lat,markerData.lng);
	        var marker:NMark = placeMark ? new NPlaceMark(point, markParams)  : new NMark(point, markParams);
	        
	        var isDrag:Boolean = false;
	        
	        if(markerData.description!=null){
	        	var infoWindow:NInfoWindow = newInfoWindow(placeMark);
		        marker.addEventListener(NMarkEvent.MOUSE_OVER, function(event:NMarkEvent):void {
		            var markerPoint:NLatLng = event.args as NLatLng;
		            infoWindow.showWindow();
		            ///이 멍청한 마커는  HTML을 지원하면서도  \n이 있어야 칸넘김으로 인식된다.
		            infoWindow.set(markerPoint, markerData.description);
		            infoWindow.updatePoint();
		        });
				marker.addEventListener(NMarkEvent.MOUSE_OUT, function(event:NMarkEvent):void {
		            infoWindow.hideWindow();
		  		});
				marker.addEventListener(NMarkEvent.DRAG, function(event:NMarkEvent):void {
					isDrag = true;
		            infoWindow.hideWindow();
		  		});
	        }
	  		
	  		/** 드래그 되어 좌표가 바뀌었다면 수정해주자. 드래그 엔드 이벤트가 없어서 이렇게 한다 ㅠㅠ */
  			marker.addEventListener(NMarkEvent.CLICK,function(event:NMarkEvent):void {
  				if(isDrag){
  					var thisMarkPoint:NLatLng = marker.getLatLngPoint();
  					 markerData.lat = thisMarkPoint.lat;
  					 markerData.lng = thisMarkPoint.lng;
  				}
	  			NaverMapMarkerEditor.popUp(_container,me,markerData);
  			});
	  		markerData.marker = marker;
	        _map.addOverlay(marker);
	        return marker;
		}
		
		/** 바커 팝업 */
		public function newMarker(point:NLatLng):void{
			NaverMapMarkerEditor.popUp(_container,me,{lat:point.lat,lng:point.lng});
		}
		
		public function get saveUrl():String{
			return _saveUrl;
		}
		public function get removeUrl():String{ 
			return _removeUrl;
		}
		public function set saveCallback(callback:Function):void{
			_saveCallback = callback;
		}
		public function get saveCallback():Function{
			return _saveCallback;
		}
		public function set removeCallback(callback:Function):void{
			_removeCallback = callback;
		}
		public function get removeCallback():Function{
			return _removeCallback;
		}
	}
}