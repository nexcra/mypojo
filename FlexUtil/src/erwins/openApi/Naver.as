package erwins.openApi{
	import com.adobe.serialization.json.*;
	import com.nhn.maps.NCoordType;
	import com.nhn.maps.NLatLng;
	import com.nhn.maps.NMap;
	import com.nhn.maps.controls.NIndexMap;
	import com.nhn.maps.controls.NMapBtns;
	import com.nhn.maps.controls.NZoomControl;
	import com.nhn.maps.enums.HorizontalAlignment;
	import com.nhn.maps.enums.VerticalAlignment;
	import com.nhn.maps.events.NMapEvent;
	import com.nhn.maps.events.NMarkEvent;
	import com.nhn.maps.interfaces.IPoint;
	import com.nhn.maps.overlays.NInfoWindow;
	import com.nhn.maps.overlays.NMark;
	import com.nhn.maps.overlays.NMarkParams;
	
	import flash.display.DisplayObjectContainer;
	
	import mx.utils.*;
	
	public class Naver{
		
		private var _map:NMap;
		private var infoWindow:NInfoWindow;
		
		public function init(base:DisplayObjectContainer,fun:Function):void{
			/** 지도 객체를 생성한다. 지도 컨테이너, 지도키, 높이, 너비를 인자값으로 설정한다. */
		    _map = new NMap(base, "ac7298e783b0c0ebced2520878d43c35", base.width, base.height);
		    
		    /** 지도  좌표 타입을 위경도 좌표로 설정 */
		    _map.coordType = NCoordType.WGS84;
		    _map.wheelZoomEnabled = true;
		    _map.autoPanningEnabled = false;
		    
		    /** 지도에 초기화 이벤트를 등록한다. */
		    _map.addEventListener(NMapEvent.INIT, function onMapInitHandler(event:NMapEvent):void{
			    /** 지도 중심  좌표를 설정한다. 줌 레벨은 7이다. */
			    _map.setCenterAndZoom(new NLatLng(37.3660835, 127.1080041), 7);
			    
			    /** 줌 컨트롤러 객체를 생성한다. */
			    var zoomControl:NZoomControl = new NZoomControl();
			    
			    /** 지도내에서의 수평/수직 정렬을 설정한다. */
			    zoomControl.setAlign(HorizontalAlignment.RIGHT);
			    zoomControl.setVAlign(VerticalAlignment.TOP);
			    
			    /** 줌 컨트롤러를 지도에 추가한다. */
			    _map.addControl(zoomControl);
			    
			    /** 미니맵 객체를 생성한다. 설정방법은 줌 컴트롤러와 동일한다. */
			    var indexMap:NIndexMap = new NIndexMap();
			    indexMap.setAlign(HorizontalAlignment.RIGHT);
			    indexMap.setVAlign(VerticalAlignment.BOTTOM);
			    _map.addControl(indexMap);
			    
			    /** 지도선택 버튼이다. */
				var mapBtns:NMapBtns = new NMapBtns();
			    mapBtns.setAlign(HorizontalAlignment.RIGHT);
			    mapBtns.setVAlign(VerticalAlignment.TOP);
			    _map.addControl(mapBtns);
			    
			    fun(_map);
			});
		}
		
		public function addInfoWindow():NInfoWindow{
			infoWindow = new NInfoWindow();
		    /** 정보창을 중심점으로 기즌으로 우측상단으로 정렬한다. */
		    infoWindow.setAlign(HorizontalAlignment.RIGHT);
		    infoWindow.setVAlign(VerticalAlignment.TOP);
		    infoWindow.offset(15, 0); //마커 객체를 가리지 않게 정보창을 우측으로 15px 이동시킨다.
		    infoWindow.hideWindow();
		    _map.addOverlay(infoWindow);
		    return infoWindow;
		}
		
		public function addMarker(point:IPoint,label:String,description:String):NMark{
			var markParams:NMarkParams = new NMarkParams();
	        markParams.dragEnabled = true;
	        markParams.label = label;
	        
	        var marker:NMark = new NMark(point, markParams);
	        
	        marker.addEventListener(NMarkEvent.MOUSE_OVER, function(event:NMarkEvent):void {
	            var markerPoint:NLatLng = event.args as NLatLng;
	            infoWindow.showWindow();
	            infoWindow.set(markerPoint, description);
	            infoWindow.updatePoint();
	        });
        
			marker.addEventListener(NMarkEvent.MOUSE_OUT, function(event:NMarkEvent):void {
	            infoWindow.hideWindow();
	  		});
	        _map.addOverlay(marker);
	        return marker;
		}
	}
}