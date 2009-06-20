
package erwins.util.vender.apache;

import java.io.*;
import java.util.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hssf.util.Region;

import erwins.util.tools.Mapp;

/**
 * POI가 너무 길어서 나눔
 * @author  erwins(my.pojo@gmail.com)
 */
public abstract class PoiRoot{
    
    /**
     * @uml.property  name="wb"
     */
    protected HSSFWorkbook wb ;
    
    /** Header에 사용되는 스타일 */
    protected HSSFCellStyle HEADER;
    
    /** 수정 하지 말라는 뜻의? 회색 블록 */
    public HSSFCellStyle GRAY;
    
    /** thin 테두리를 가지는 일반적인 블록 */
    protected HSSFCellStyle BODY;
    public HSSFCellStyle BODY_Left;
    public HSSFCellStyle BODY_Right;
    
    protected HSSFFont font;
    
    /**  헤더길이 :  시트초기화시 설정된다. */
    protected List<Integer> headerRowCount = new ArrayList<Integer>();;
    
    /** 정렬에 사용되는 내가 정한 한글+영문의 폰트 크기 */
    protected static final short font11 = 600;
    //private static final short font10 = 550;
    
    /** 코멘트에 사용된다. 묻지마.. 나도 몰라. */
    protected HashMap<Integer,HSSFPatriarch> patriarchMap = new HashMap<Integer,HSSFPatriarch>();    
    
    protected FileInputStream stream;
    
    protected void init(){
        font = wb.createFont();
        font.setFontHeightInPoints((short)11);
        font.setFontName("맑은 고딕");
        //font.setItalic(true);
        //font.setStrikeout(true);
        
        HEADER = wb.createCellStyle();
        HEADER.setFillForegroundColor(HSSFColor.YELLOW.index);
        HEADER.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        HEADER.setVerticalAlignment((short)1);  //중앙정렬..
        HEADER.setAlignment((short)2);  //중앙정렬..
        boxing(HEADER);   
        HEADER.setFont(font);        
        
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
        
        GRAY = wb.createCellStyle();
        GRAY.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        GRAY.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        boxing(GRAY);
        GRAY.setFont(font);
        
        //sheet.shiftRows(2, 4, -1); //아래위 바꿈..        
    }
    
    /**
     * 스타일에 박스테두리 삽입 
     */
    private static void boxing(HSSFCellStyle style){
        //cellStyle.setWrapText( true ); //ㅋㅋ 박스안에 다넣기
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBottomBorderColor(HSSFColor.BLACK.index);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setLeftBorderColor(HSSFColor.BLACK.index);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setRightBorderColor(HSSFColor.BLACK.index);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setTopBorderColor(HSSFColor.BLACK.index);
        style.setAlignment((short)1);
    }    
    
    /**
     * 워크북을 리턴한다.
     * @uml.property  name="wb"
     */
    public HSSFWorkbook getWb(){
        return wb;
    }    
    
    // ===========================================================================================
    //                                    method
    // ===========================================================================================
   
    
    /**
     * 각 행을 실선을 둘러싼다.
     * 가장 긴 열에 맞추어 정렬한다.
     */
    public void wrap(){
        int sheetLength = wb.getNumberOfSheets();
        for(int i=0;i<sheetLength;i++){            
            wrapSheet(i);
        }
    }
    
    /**
     * 4글자 이상인 헤더칸에 맞추어 열 조정 
     * 개별 체크로.. 부하가 약간 있을 수 있음.
     * sheet1.autoSizeColumn((short)5); //한글이라그런기? 약간 작게 설정된다.
     */    
    @SuppressWarnings("unchecked")
    private void wrapSheet(int index){
        HSSFSheet sheet =  wb.getSheetAt(index);
        Mapp map = new Mapp();
        
        for (Iterator<HSSFRow> rows = sheet.rowIterator(); rows.hasNext(); ) {
            HSSFRow thisRow = rows.next();
            for (Iterator<HSSFCell> cells = thisRow.cellIterator(); cells.hasNext(); ) {
                HSSFCell thisCell=  cells.next();
                String value = thisCell.getRichStringCellValue().getString();
                int length = value.length();
                map.putMaxInteger(thisCell.getCellNum(), length);
                if(thisRow.getRowNum() < headerRowCount.get(index)){
                    thisCell.setCellStyle(HEADER);
                }else{
                    thisCell.setCellStyle(BODY);                
                }
            }
        }
        for(Object key : map.keySet()){
            Short sho = (Short)key;
            Integer length = map.getInteger(key);
            if(length > 20) length = 20; //맥스 제한..
            if(length > 4)
                sheet.setColumnWidth(sho,(short)(length * font11)); //한글 한글자당 550정도?
        }
    }
    
    /**
     * 강제 컬럼 너비 조정. 타이틀 화면 등의 컬럼이 강제 조정될때 사용.
     * wrap이 적용된 후 사용하자. 600*50정도 사이즈면 화면을 가득 채운다.
     */
    public void setColumnWidth(int index,int col,int size){
        wb.getSheetAt(index).setColumnWidth((short)col, (short)size);
    }
    
    /**
     * wrap이 적용된 후 사용하자.
     * 강제 정렬 등의 HSSFCellStyle을 조절할때 사용하자.
     * 헤더값 int를 피할려 그랬더니 file에서 로드하는거는 안되네. ㅠ 
     * 스타일이 Header와 배경이 같은것은 무시된다.
     */
    @SuppressWarnings("unchecked")
    public void setStyle(HSSFCellStyle style,int index,int ... cols){
        HSSFSheet sheet =  wb.getSheetAt(index);
        int headerSize =  headerRowCount.size() > index ? headerRowCount.get(index) : 0;
        for (Iterator<HSSFRow> rows = sheet.rowIterator(); rows.hasNext(); ) {
            HSSFRow thisRow = rows.next();
            if(thisRow.getRowNum() < headerSize) continue;
            for(int i : cols){
                HSSFCell thisCell=  thisRow.getCell(i);
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
        HSSFPatriarch patr = patriarchMap.get(sheetInd);        
        if(patr == null) patr = sheet.createDrawingPatriarch();
        patriarchMap.put(sheetInd, patr);
        
        HSSFRow row = sheet.getRow (rowNum);
        for (int column : columns) {
            HSSFCell cell = row.getCell(column);
            HSSFComment comment = patr.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short)4, 2, (short) 7, 6));
            comment.setString(new HSSFRichTextString(str)); 
            //comment1.setAuthor("한국환경자원공사");
            cell.setCellComment(comment);
        }
    }
    
    /**
     * 특정 시트의 X,Y로 부터 셀을 리턴
     */
    public HSSFCell findCell(int sheetInd,int x,int y){
        HSSFSheet sheet = wb.getSheetAt(sheetInd);
        HSSFRow row = sheet.getRow(y);
        if(row==null) row = sheet.createRow(y);
        HSSFCell cell = row.getCell(x);
        if(cell==null) cell = row.createCell((short)x);
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
        BODY.setAlignment((short)2);
        BODY.setVerticalAlignment((short)1);
    }    
    
    /**
     * 해당 시트의 가로/세로를 머지한다. 
     */
    public Merge getMerge(int index){
        return new Merge(wb.getSheetAt(index));         
    }    
    
    /**
     * @author  Administrator
     */
    public class Merge{
        private String[] lastValues = new String[100];
        private Integer[] startRows = new Integer[100];
        private HSSFSheet sheet;
        private HSSFRow row;
        private Integer startCol;
        private String lastValue = "";
        
        private Integer[] ableRow;
        private Integer[] ableCol;
        
        public Merge(HSSFSheet sheet){
            this.sheet = sheet;
        }
        
        @SuppressWarnings("unchecked")
        public void merge(){
            for (Iterator<HSSFRow> rows = sheet.rowIterator(); rows.hasNext(); ) {
                row = rows.next();
                int rowIndex = row.getRowNum();
                
                for (Iterator<HSSFCell> cells = row.cellIterator(); cells.hasNext(); ) {
                    HSSFCell thisCell =  cells.next();
                    int colIndex = thisCell.getCellNum();                   
                    String value = thisCell.getRichStringCellValue().getString();                    
                    mergeCol(rowIndex,colIndex, value);
                    mergeRow(rows, rowIndex, colIndex, value);
                }
            }
            setAlignment();
        }
        
        /**
         * 이전 값과 비교하여 merge를 결정한다. 
         */
        private void mergeCol(int rowIndex, int colIndex, String value) {
            if(!isColMergeAble(rowIndex)) return;
            if(lastValue.equals(value)){
                if(startCol == null ) startCol = colIndex-1;
                if(colIndex == row.getLastCellNum()-1){  //마지막일경우 발동
                    sheet.addMergedRegion(new Region(rowIndex,startCol.shortValue(),rowIndex,(short)(colIndex)));
                    startCol = null;
                }
            }else if(startCol!=null){
                sheet.addMergedRegion(new Region(rowIndex,startCol.shortValue(),rowIndex,(short)(colIndex-1)));
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
        
        /**
         * 이전 값과 비교하여 merge를 결정한다. 
         * 세로 방향의 값을 머지한다.
         */        
        private void mergeRow(Iterator<HSSFRow> rows, int rowIndex, int colIndex, String value) {
            if(!isRowMergeAble(rowIndex,colIndex)) return;            
            if(value.equals(lastValues[colIndex])){
                if(startRows[colIndex] == null ) startRows[colIndex] = rowIndex - 1; //최초 설정.
                if(!rows.hasNext()){  //마지막일경우 발동
                    sheet.addMergedRegion(new Region(startRows[colIndex],(short)colIndex,rowIndex,(short)(colIndex)));
                    startRows[colIndex] = null;
                }                
            }else if(startRows[colIndex] != null){
                sheet.addMergedRegion(new Region(startRows[colIndex],(short)(colIndex),rowIndex-1,(short)(colIndex)));
                startRows[colIndex] = null;
            }
            lastValues[colIndex] = value;
        }

        /**
         * 머지 가능한 열을 입력한다.헤더만 할 경우 헤더 로우를 입력한다.
         */
        public Merge setAbleRow(Integer ... ableRow) {
            this.ableRow = ableRow;
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
    
    /** 엑셀시트를 파일로 변경한다. */
    public static void write(String fileName, HSSFWorkbook workbook){
        File file = new File(fileName);
        FileOutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(file);
            workbook.write(fileOut);
            fileOut.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(),e);
        }
    }

    
}
