
package erwins.util.vender.apache;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFComment;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;

import com.google.common.base.Strings;

import erwins.util.root.exception.IORuntimeException;

/**
 * POI가 너무 길어서 나눔
 * this를 리턴하는데가 있어서 public이여야 한다.
 */
public abstract class PoiRoot{
	
    protected HSSFWorkbook wb ;
    
    /** Header에 사용되는 스타일 */
    protected CellStyle HEADER;
    
    /** 수정 하지 말라는 뜻의? 회색 블록 */
    public CellStyle GRAY;
    
    /** thin 테두리를 가지는 일반적인 블록 */
    public CellStyle BODY;
    public CellStyle BODY_Left;
    public CellStyle BODY_Right;
    
    public CellStyle BODY_RED;
    
    public CellStyle LINKED;
    
    protected HSSFFont font;
    
    /** 수정금지! */
    public HSSFFont BLUE_FONT;
    /** 수정금지! */
    public HSSFFont RED_FONT;
    /** 수정금지! -가 그어진 삭제용 */
    public HSSFFont GRAY_FONT;
    
    /**  헤더길이 :  시트초기화시 설정된다. */
    protected List<Integer> headerRowCount = new ArrayList<Integer>();;
    
    /** 코멘트에 사용된다. 묻지마.. 나도 몰라. */
    protected Map<String,HSSFPatriarch> patriarchMap = new HashMap<String,HSSFPatriarch>();    
    
    protected HSSFPatriarch getOrCreatePatriarch(HSSFSheet sheet){
    	HSSFPatriarch patr = patriarchMap.get(sheet.getSheetName());
    	if(patr==null){
    		patr = sheet.createDrawingPatriarch();
    		patriarchMap.put(sheet.getSheetName(), patr);
    	}
    	return patr; 
    }
    
    protected FileInputStream stream;
    
    protected void init(){
        font = wb.createFont();
        font.setFontHeightInPoints((short)11);
        font.setFontName("맑은 고딕");
        
        BLUE_FONT = wb.createFont();
        BLUE_FONT.setFontHeightInPoints((short)11);
        BLUE_FONT.setFontName("맑은 고딕");
        BLUE_FONT.setItalic(true);
        BLUE_FONT.setColor(HSSFColor.BLUE.index);
        
        RED_FONT = wb.createFont();
        RED_FONT.setFontHeightInPoints((short)11);
        RED_FONT.setFontName("맑은 고딕");
        RED_FONT.setItalic(true);
        RED_FONT.setColor(HSSFColor.RED.index);
        
        GRAY_FONT = wb.createFont();
        GRAY_FONT.setFontHeightInPoints((short)11);
        GRAY_FONT.setFontName("맑은 고딕");
        GRAY_FONT.setStrikeout(true);
        GRAY_FONT.setColor(HSSFColor.GREY_80_PERCENT.index);
        
        HEADER = wb.createCellStyle();
        HEADER.setFillForegroundColor(HSSFColor.YELLOW.index);
        HEADER.setFillPattern(CellStyle.SOLID_FOREGROUND);
        HEADER.setVerticalAlignment((short)1);  //중앙정렬..
        HEADER.setAlignment((short)2);  //중앙정렬..
        boxing(HEADER);   
        HEADER.setFont(font);
        HEADER.setWrapText(true);
        
        BODY = wb.createCellStyle();
        boxing(BODY);
        BODY.setFont(font);
        
        BODY_Left = wb.createCellStyle();
        boxing(BODY_Left);
        BODY_Left.setFont(font);
        BODY_Left.setAlignment((short)1);
        BODY_Left.setVerticalAlignment((short)1);
        
        BODY_Right = wb.createCellStyle();
        boxing(BODY_Right);
        BODY_Right.setFont(font);
        BODY_Right.setAlignment((short)3);        
        BODY_Right.setVerticalAlignment((short)1);
        //BODY_Number.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00")); // 나중에 사용하자.
        
        BODY_RED = buildStyle(RED_FONT, null);
        BODY_RED.setAlignment((short)2);
        BODY_RED.setVerticalAlignment((short)1);
        
        GRAY = wb.createCellStyle();
        GRAY.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        GRAY.setFillPattern(CellStyle.SOLID_FOREGROUND);
        boxing(GRAY);
        GRAY.setFont(font);
        
        LINKED = wb.createCellStyle();
        boxing(LINKED);
        LINKED.setFont(BLUE_FONT);
        LINKED.setVerticalAlignment((short)1);
        //sheet.shiftRows(2, 4, -1); //아래위 바꿈..        
    }
    
    /**
     * 스타일에 박스테두리 삽입 
     */
    private static void boxing(CellStyle style){
        //cellStyle.setWrapText( true ); //ㅋㅋ 박스안에 다넣기
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBottomBorderColor(HSSFColor.BLACK.index);
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setLeftBorderColor(HSSFColor.BLACK.index);
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setRightBorderColor(HSSFColor.BLACK.index);
        style.setBorderTop(CellStyle.BORDER_THIN);
        style.setTopBorderColor(HSSFColor.BLACK.index);
        style.setAlignment((short)1);
    }
    
    /** 간단 스타일 빌드. addStyle과 한께 쓰지ㅏ.
     * ex) HSSFColor.GREY_25_PERCENT.index  */
    public CellStyle buildStyle(HSSFFont font,Short foregroundColor){
    	CellStyle style = wb.createCellStyle();
    	boxing(style);
    	if(font!=null) style.setFont(font);
    	if(foregroundColor!=null){
    		style.setFillForegroundColor(foregroundColor);
    		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
    	}
    	//style.setVerticalAlignment((short)1);  //중앙정렬..
    	//style.setAlignment((short)2);  //중앙정렬..
    	return style;
    } 
    
    /**
     * 워크북을 리턴한다.
     * @uml.property  name="wb"
     */
    public HSSFWorkbook getWb(){
        return wb;
    }    
    public void writeProtectWorkbook(String password,String username){
    	wb.writeProtectWorkbook(password, username);
    }    
    
    // ===========================================================================================
    //                                    method
    // ===========================================================================================
   
    
    /**
     * 각 행을 실선을 둘러싼다.
     * 가장 긴 열에 맞추어 정렬한다.
     */
    public PoiRoot wrap(){
        int sheetLength = wb.getNumberOfSheets();
        for(int i=0;i<sheetLength;i++){            
            wrapSheet(i);
        }
        for(PoiCellWidth each : poiCellWidths) each.accept();
        for(PoiCellPair each : pairs) each.accept();
        return this;
    }
    
    /** new HSSFClientAnchor(0,0,200,100,(short)1,1,(short)5,7) 의 경우
	 * 1:1 ~ 5:7 칸의 범위에 이미지가 형성되며 여기서 xy좌표에 +-가 된다.
	 * 즉 이미지의 실제 사이즈를 잘 알아야 이 값을 조정할 수 있다. */
    public void addImage(HSSFSheet sheet,ClientAnchor anchor,File file){
    	Drawing patriarch = sheet.getDrawingPatriarch();
    	if(patriarch==null) patriarch = sheet.createDrawingPatriarch();
        anchor.setAnchorType( 2 );//??
        patriarch.createPicture(anchor, loadPicture(file ));
    }

    /** 어디서 주워온거 */
    protected int loadPicture(File file){
        int pictureIndex;
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;
        try {
            try {
				fis = new FileInputStream( file);
				bos = new ByteArrayOutputStream( );
				int c;
				while ( (c = fis.read()) != -1) {
				    bos.write( c );
				}
				pictureIndex = wb.addPicture( bos.toByteArray(), HSSFWorkbook.PICTURE_TYPE_JPEG  );
			} catch (IOException e) {
				throw new IORuntimeException(e);
			}
        } finally {
        	IOUtils.closeQuietly(bos);
        	IOUtils.closeQuietly(fis);
        }
        return pictureIndex;
    }
    /**
     * 직접 조절하다가 API로 변경
     */    
    private void wrapSheet(int index){
    	int headerRowLength = headerRowCount.get(index);
        HSSFSheet sheet =  wb.getSheetAt(index);
        for (Iterator<Row> rows = sheet.rowIterator(); rows.hasNext(); ) {
            Row thisRow = rows.next();
            for (Iterator<Cell> cells = thisRow.cellIterator(); cells.hasNext(); ) {
                Cell thisCell=  cells.next();
                int thisCellType = thisCell.getCellType();
                if(thisRow.getRowNum() < headerRowLength){
                    thisCell.setCellStyle(HEADER);
                }else{
                	if(thisCellType==Cell.CELL_TYPE_NUMERIC) thisCell.setCellStyle(BODY_Right);
                	else if(thisCellType==Cell.CELL_TYPE_FORMULA) thisCell.setCellStyle(BODY_Right);
                	else thisCell.setCellStyle(BODY_Left);
                    //thisCell.setCellStyle(BODY);                
                }
            }
        }
        int size =  sheet.getRow(0).getLastCellNum();
        //자동 사이즈 조정 : ㄴ이놈은 데이터가 다 들어간 뒤 적용해줘야 하는듯 하다.
        for(int i=0;i<size;i++) sheet.autoSizeColumn(i);
        //고정영역 조정 : 취향이 아니라면 설정하지 말자.
        sheet.createFreezePane(0, headerRowLength);
    }
    
    /**
     * 강제 컬럼 너비 조정. 타이틀 화면 등의 컬럼이 강제 조정될때 사용.
     * wrap이 적용된 후 사용하자. 600*50정도 사이즈면 화면을 가득 채운다.
     */
    @Deprecated
    public void setColumnWidth(int index,int col,int size){
        wb.getSheetAt(index).setColumnWidth(col,size);
    }
    
    /** wrap 후에 예쁘게 포장할때 사용
     * auto 정렬이 잘 되지 않을때 사용하자. (잘 안되더라)  */
    public void setColumnWidth(int sheetIndex,Integer ... columnLengths){
    	HSSFSheet sheet = wb.getSheetAt(sheetIndex);
    	for(int i=0;i<columnLengths.length;i++){
    		sheet.setColumnWidth(i, columnLengths[i] * 1000);	
    	}
    }
    
    /** wrap이 적용된 후 사용하자.  일반적인 경우는 PoiCellPair를 쓰는게 더 좋다. */
    public void setCustomStyle(CellStyle style,int sheetIndex,int[] cols,int ... rows){
        HSSFSheet sheet =  wb.getSheetAt(sheetIndex);
        for(int rowIndex : rows){
        	Row thisRow = sheet.getRow(rowIndex);
            for(int i : cols){
                Cell thisCell=  thisRow.getCell(i);
                thisCell.setCellStyle(style);
            }
        }
    }
    
    /**
     * 특정 시트의 특정 컬럼에 코멘트 추가
     * <br> 입력 순으로 1.시트 2.로우 3.컬럼... 
     */
    public void setComments(String str,int sheetInd,int rowNum,int ... columns){
        HSSFSheet sheet = wb.getSheetAt(sheetInd);
        HSSFPatriarch patr = getOrCreatePatriarch(sheet);
        Row row = sheet.getRow (rowNum);
        for (int column : columns) {
            Cell cell = row.getCell(column);
            HSSFComment comment = patr.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short)4, 2, (short) 7, 6));
            comment.setString(new HSSFRichTextString(str)); 
            cell.setCellComment(comment);
        }
    }
    
    /**
     * 특정 시트의 X,Y로 부터 셀을 리턴 각 번호는 다들 0부터 시작한다.
     */
    public Cell findCell(int sheetIndex,int rowNum,int colNum){
        HSSFSheet sheet = wb.getSheetAt(sheetIndex);
        Row row = sheet.getRow(rowNum);
        if(row==null) return null;
        Cell cell = row.getCell(colNum);
        return cell;
    }
 
    
    // ===========================================================================================
    //                                    머지..
    // ===========================================================================================    
    
    /**
     * 머지 후 정렬이 풀리는 것을 방지한다.
     */
    public void setAlignment(){
        HEADER.setAlignment((short)2);
        HEADER.setVerticalAlignment((short)1);
        //BODY.setAlignment((short)2);
        //BODY.setVerticalAlignment((short)1);
        BODY_Left.setAlignment((short)1);
        BODY_Left.setVerticalAlignment((short)1);
    }    
    
    /**
     * 해당 시트의 가로/세로를 머지한다.
     * Wrap 이전?에 호출되어야 한다. 
     * ex) p.getMerge(1).setAbleRow(0).setAbleCol(0).merge();
     */
    public Merge getMerge(int index){
        return new Merge(wb.getSheetAt(index));         
    }    
    
    public class Merge{
        private String[] lastValues = new String[1000];
        private Integer[] startRows = new Integer[1000];
        private Integer[] startKeyRows = new Integer[1000];
        private HSSFSheet sheet;
        private Row row;
        private Integer startCol;
        private String lastValue = "";
        private String lastKeyValue = "";
        
        private Integer[] ableRow;
        private Integer[] ableCol;
        private Integer keyCol;
        private Integer[] ableKeyCol;
        private Map<Integer,Integer[]> with = new HashMap<Integer, Integer[]>();
        
        public Merge(HSSFSheet sheet){
            this.sheet = sheet;
        }
        
        public Merge setWithMerge(Integer tergerIndex,Integer ... colIndexs){
        	with.put(tergerIndex, colIndexs);
        	return this;
        }
        
        /**머지된 컬럼은 자동조정이 되지 않는다. 따라서 가본값을 준다. 
         * 보통 10으로 주면 됨 */
        public void merge(int isAdjust){
        	for(Integer each : ableCol){
        		poiCellWidths.add(new PoiCellWidth(this.sheet,each,600*isAdjust));	
        	}
        	merge();
        }
        
        public void merge(){
            for (Iterator<Row> rows = sheet.rowIterator(); rows.hasNext(); ) {
                row = rows.next();
                int rowIndex = row.getRowNum();
                String currentKeyValue = null;
                if(keyCol!=null) currentKeyValue = getVaueToString(row.getCell(keyCol)); 
                
                for (Iterator<Cell> cells = row.cellIterator(); cells.hasNext(); ) {
                    Cell thisCell =  cells.next();
                    int colIndex = thisCell.getColumnIndex();
                    
                    mergeRowByKey(rows,rowIndex, colIndex, currentKeyValue);
                    
                    String value = getVaueToString(thisCell);                    
                    mergeCol(rowIndex,colIndex, value);
                    mergeRow(rows, rowIndex, colIndex, value);
                }
                lastKeyValue = currentKeyValue;
            }
            setAlignment();
        }

        /** 머지를 비교하기 위해 숫자도 문자로 취급한다. */
		private String getVaueToString(Cell thisCell) {
			String value = "";
			if(thisCell.getCellType()==Cell.CELL_TYPE_STRING) value = thisCell.getRichStringCellValue().getString();
			else if(thisCell.getCellType()==Cell.CELL_TYPE_NUMERIC) value = String.valueOf(thisCell.getNumericCellValue());
			return value;
		}
        
		/** 컬럼머지가 가능하면  헤더로 판단하고 스킵한다. */
        private void mergeRowByKey(Iterator<Row> rows, int rowIndex, int colIndex,String currentKeyValue) {
			if(currentKeyValue==null) return;
			if(isColMergeAble(rowIndex)) return; //
			if(!isRowKeyMergeAble(rowIndex,colIndex)) return;
			if(currentKeyValue.equals(lastKeyValue)){
				if(startKeyRows[colIndex] == null ) startKeyRows[colIndex] = rowIndex - 1; //최초 설정.
			    if(!rows.hasNext()){  //마지막일경우 발동
			    	sheet.addMergedRegion(new CellRangeAddress(startKeyRows[colIndex],rowIndex,colIndex,colIndex));
			    	startKeyRows[colIndex] = null;
			    }
			}else if(startKeyRows[colIndex] != null){
				sheet.addMergedRegion(new CellRangeAddress(startKeyRows[colIndex],rowIndex-1,colIndex,colIndex));
				startKeyRows[colIndex] = null;
			}
        }
        
        /**
         * 이전 값과 비교하여 merge를 결정한다.  가로 머지이다.. 이름 ㅅㅂ같이 지었네.. ㅈㅅ. ㅋㅋ
         */
		private void mergeCol(int rowIndex, int colIndex, String value) {
            if(!isColMergeAble(rowIndex)) return;
            if(lastValue.equals(value)){
                if(startCol == null ) startCol = colIndex-1;
                if(colIndex == row.getLastCellNum()-1){  //마지막일경우 발동
                    //sheet.addMergedRegion(new Region(rowIndex,startCol.shortValue(),rowIndex,(short)(colIndex)));
                	sheet.addMergedRegion(new CellRangeAddress(rowIndex,rowIndex,startCol.intValue(),colIndex));
                    startCol = null;
                }
            }else if(startCol!=null){
                //sheet.addMergedRegion(new Region(rowIndex,startCol.shortValue(),rowIndex,(short)(colIndex-1)));
            	sheet.addMergedRegion(new CellRangeAddress(rowIndex,rowIndex,startCol.intValue(),colIndex-1));
                startCol = null;                    
            }
            lastValue = value;
        }

        /**
         * 가로 머지가 가능한지?
         * null이면 모두 가능하다고 판단한다. 
         */
        private boolean isColMergeAble(int rowIndex) {
            if(ableRow==null) return true;
            for(int thisAbleRow :ableRow) if( thisAbleRow == rowIndex) return true;
            return false;
        }
        
        /**
         * 세로 머지가 가능한지?
         * null이면 모두 불가능하다고 판단한다. 
         * 가로 머지가 가능한 열은 헤더로 판한하고 세로 머지도 가능하다고 본다.  (헤더 정보도 가지고 있지만 확장을 위해..)
         * 가로세로 중복의 경우 비교행을 지나서 판별하는 로직이 있음으로 -1을 한것을 같이 비교해 준다.
         */
        private boolean isRowMergeAble(int rowIndex,int colIndex) {
            if(isColMergeAble(rowIndex) || isColMergeAble(rowIndex-1)) return true;
            if(ableCol==null) return false;
            for(int thisAbleCol :ableCol) if( thisAbleCol == colIndex) return true;
            return false;
        }
        
        private boolean isRowKeyMergeAble(int rowIndex,int colIndex) {
        	if(ableKeyCol==null) return false;
        	for(Integer thisAbleCol :ableKeyCol) if( thisAbleCol == colIndex) return true;
        	return false;
        }
        
        /**
         * 이전 값과 비교하여 merge를 결정한다. 
         * 세로 방향의 값을 머지한다.
         */        
		private void mergeRow(Iterator<Row> rows, int rowIndex, int colIndex, String value) {
            if(!isRowMergeAble(rowIndex,colIndex)) return;            
            if(value.equals(lastValues[colIndex])){
                if(startRows[colIndex] == null ) startRows[colIndex] = rowIndex - 1; //최초 설정.
                if(!rows.hasNext()){  //마지막일경우 발동
                    //sheet.addMergedRegion(new Region(startRows[colIndex],(short)colIndex,rowIndex,(short)(colIndex)));
                	sheet.addMergedRegion(new CellRangeAddress(startRows[colIndex],rowIndex,colIndex,colIndex));
                	mergeWith(rowIndex, colIndex);
                    startRows[colIndex] = null;
                }                
            }else if(startRows[colIndex] != null){
                //sheet.addMergedRegion(new Region(startRows[colIndex],(short)(colIndex),rowIndex-1,(short)(colIndex)));
            	sheet.addMergedRegion(new CellRangeAddress(startRows[colIndex],rowIndex-1,(colIndex),colIndex));
            	mergeWith(rowIndex-1, colIndex);
                startRows[colIndex] = null;
            }
            lastValues[colIndex] = value;
        }

		private void mergeWith(int rowIndex, int colIndex) {
			Integer[] withs = with.get(colIndex);
			if(withs==null) return;
			for (Integer integer : withs) {
				System.err.println(startRows[colIndex]);
				System.err.println(rowIndex);
				sheet.addMergedRegion(new CellRangeAddress(startRows[colIndex],rowIndex,integer,integer));
			}				
		}

        /**
         * 머지 가능한 열을 입력한다.헤더만 할 경우 헤더 로우를 입력한다.
         */
        public Merge setAbleRow(Integer ... ableRow) {
            this.ableRow = ableRow;
            return this;
        }
        /**
         * Key기준으로 머지할 로우를 선택한다.
         * 각 행의 key끼리 같으면 해당 행의 값과 상관없이 머지한다.
         */
        public Merge setAbleKeyCol(Integer  keyCol, Integer ... ableKeyCol) {
            this.keyCol = keyCol;
            this.ableKeyCol = ableKeyCol;
            return this;
        }  

        /**
         * 머지할 컬럼을 입력한다. null이면 모두 불가능하다고 판단한다.
         */
        public Merge setAbleCol(Integer ... ableCol) {
            this.ableCol = ableCol;
            return this;
        }
    }
    
    // ===========================================================================================
    //                                    출력 부분
    // ===========================================================================================
    
    /** 엑셀 시트를  ServletOutputStream으로 출력한다.*/    
    public void write(HttpServletResponse response){
        write(response,wb);
    }
    
    /** 엑셀 시트를  ServletOutputStream으로 출력한다.*/
    public static void write(HttpServletResponse response, HSSFWorkbook workbook) {
        response.setContentType("application/vnd.ms-excel"); //charset=utf-8
        try {
            ServletOutputStream out = response.getOutputStream();        
            workbook.write(out);
            out.flush();
        }
        catch (Exception e) {
            //MS-IE에서 사용자의 다운로드 취소
        }
    }
    
    /** 엑셀시트를 파일로 변경한다.  */
    public void write(String fileName){
        write(fileName,wb);
    }
    public void write(File file){
    	write(file,wb);
    }
    
    public static void write(File file, HSSFWorkbook workbook){
        FileOutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(file);
            workbook.write(fileOut);
            fileOut.close();
        }
        catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }
    
    /** 엑셀시트를 파일로 변경한다. */
    public static void write(String fileName, HSSFWorkbook workbook){
    	if(fileName.indexOf(".") < 0) fileName += ".xls";
        write(new File(fileName), workbook);
    }
    
    /* ================================================================================== */
	/*                             부분  스타일 적용                                                       */
	/* ================================================================================== */
    
    protected List<PoiCellPair> pairs = new ArrayList<PoiCellPair>();
    
    /** 일괄 wrap 후 부분적으로 셀을 초기화해주기 위해 사용한다. */
    public static class PoiCellPair{
    	private final Cell cell;
    	private final CellStyle cellStyle;
    	public PoiCellPair(Cell cell,CellStyle cellStyle){
    		this.cell = cell;
    		this.cellStyle = cellStyle;
    	}
    	/** 개별 셀 스타일 조정. */
    	public void accept(){
    		cell.setCellStyle(cellStyle);
    	}
    }
    
    protected List<PoiCellWidth> poiCellWidths = new ArrayList<PoiCellWidth>();
    
    /** 일괄 wrap 후 부분적으로 컬럼너비를 조절하기 위해서 사용된다.
     * 머지된 컬럼은 오토사이징이 안되서 이렇게 해준다.   600*50정도 사이즈면 화면을 가득 채운다. */
    protected static class PoiCellWidth{
    	private final HSSFSheet sheet;
    	private final int columnIndex;
    	private final int width;
    	protected PoiCellWidth(HSSFSheet sheet,int columnIndex,int width){
    		this.sheet = sheet;
    		this.columnIndex = columnIndex;
    		this.width = width;
    	}
    	/** 개별 시트 너비 조정 */
    	public void accept(){
    		sheet.setColumnWidth(columnIndex, width);
    	}
    }
    
    /** 보통 두개가 같이 쓰이니 간단 표현식 추가 */
    public void setCustomStyleAndsetComments(int sheetIndex,CellStyle style,String comments,int[] row,int[] col){
    	setCustomStyle(style, sheetIndex, col, row);
    	if(!Strings.isNullOrEmpty(comments) && row.length==1){
    		setComments(comments, sheetIndex, row[0], col);	
    	}
    }
    
}
