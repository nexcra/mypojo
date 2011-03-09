package erwins.component{
	
import mx.controls.ComboBox;
import mx.controls.DataGrid;
import mx.core.ClassFactory;
import mx.events.FlexEvent;
import mx.events.ListEvent;

/**
 * 드롭다운을 DataGride로 나타낸다. (Advanced는 안됨)
 * 드롭다운 목록이 필수이고 (딴걸 선택했다가 다시 null로 세팅하기 힘들다.) 데이터의 표현이 풍부해야 하는 경우에 사용해 주자. 
 */
public class ComboList extends ComboBox{
	
	/** 직접 생성하는게 아니다!. 내부적으로 동적으로 생성해주는듯. */
	public function ComboList(){
		this.dropdownFactory = new ClassFactory(DataGrid);
	}
	
	private var dropdownSelectedIndex:int;
	
	
	/** selectedField를 지정하면 라벨 이름은 고정된다. 
	 * 즉 리스트는 이클래스를 단순히 많이 보여주기위해 사용하며 선택하는 컬럼은 무관하다고 본다. */
	private var _selectedFieldFixed:Boolean = false;
	private var _selectedField:String;
	public function set selectedField(value:String):void {
		_selectedField = value;
		_selectedFieldFixed = true;
	}
	
	/** 컬럼인포를 지정한다. 없으면 디폴트. */
	private var _colimns:Array;
	public function set colimns(value:Array):void {
		_colimns = value;
	}
	
	/** selectedItem은 내부적으로 하나를 더 가진다. */
	private var _selectedItem:Object;
    override public function set selectedItem(data:Object):void {
	 	super.selectedItem = data; 
		_selectedItem = data;
		if (dropdown) dropdown.selectedItem = data;
		this.invalidateProperties();
		//this.invalidateDisplayList();
	}
	override public function get selectedItem():Object {
		return _selectedItem;
	}	
	
	private var _dropdownHeight:Number;
	public function set dropdownHeight(value:Number):void {
		_dropdownHeight = value;
		invalidateProperties();
	}
	
	/* =============================== override ================================= */

	/**
	 * 리스트가 선택?될때 마다 새로운 객체를 생성하는듯 하다. 따라서 _colimns을 여기서 지정해준다.
	 * 매번 재생성됨으로 약참조 해준다.
	 **/
	override protected function downArrowButton_buttonDownHandler(event:FlexEvent):void{
		if(_colimns) DataGrid(this.dropdown).columns = _colimns;
        if (dropdown && !isNaN(_dropdownHeight)) {
        	if (dropdown.height != _dropdownHeight) dropdown.height = _dropdownHeight;
        }
        super.downArrowButton_buttonDownHandler(event);
		dropdown.addEventListener(ListEvent.ITEM_CLICK, function(event:ListEvent):void {
			dropdownSelectedIndex = event.rowIndex;
	    	selectedIndex = event.rowIndex;
			//dropdown.selectedIndex = dropdownSelectedIndex;//??
			selectedItem = dropdown.selectedItem;
			//invalidateDisplayList();  //구지 invalidate할 필요는 없을듯.
			
			if(!_selectedFieldFixed){
				var columnIndex:int = event.columnIndex;
				_selectedField = DataGrid(dropdown).columns[columnIndex].dataField;
				dispatchEvent(event);
				invalidateProperties();	
			}
			
		}, false,0,true);
    }
	
	/** 프로퍼티가 바뀌면 다음 업데이트 시기에 디스플레이를 갱신. */
	override protected function commitProperties():void{
		super.commitProperties();
		invalidateDisplayList();
	}
	
	/**
	 * 왜 여기에 구지 넣었는지는 의문??
	 **/
	override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void {
		super.updateDisplayList(unscaledWidth, unscaledHeight);
		if (selectedItem) {
			textInput.text = selectedItem[_selectedField];
            textInput.invalidateDisplayList();
            textInput.validateNow();
		} 
	}
}
}