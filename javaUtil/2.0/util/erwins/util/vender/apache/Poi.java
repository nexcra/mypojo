
package erwins.util.vender.apache;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFComment;
import org.apache.poi.hssf.usermodel.HSSFHyperlink;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

import erwins.util.collections.map.RequestMap;
import erwins.util.lib.DayUtil;
import erwins.util.lib.StringUtil;
import groovy.lang.Closure;

/**
 * POI 패키지의 HSSF를 편리하게.. 헤더칸은 1칸 이라고 일단 고정 사각 박스를 예쁘게 채울려면 반드시 null에 ""를 채워 주자~
 */
public class Poi extends PoiRoot{
	
	public static final int LIMIT_ROW = 32767; 
    
    // ===========================================================================================
    //                                    생성자 XSSF
    // ===========================================================================================
    
    public Poi(HSSFWorkbook wb){
        this.wb = wb;
        init();
    }
    
    public Poi(){
        this.wb = new HSSFWorkbook();
        init();
    }
    
    public Poi(String fileName){
    	buildPoiByFile(new File(fileName));
        init();
    }
    public Poi(File file){
    	buildPoiByFile(file);
    	init();
    }

	private void buildPoiByFile(File file) {
		try {
            stream = new FileInputStream(file);
            POIFSFileSystem filesystem = new POIFSFileSystem(stream);
            wb = new HSSFWorkbook(filesystem);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }finally{
        	if(stream!=null) try {
                stream.close();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
	}
	
	/** 웬만하면 사용하지 말자. */
	public Workbook getWorkbook(){
		return this.wb;
	}
	public int getNumberOfSheets(){
		return this.wb.getNumberOfSheets();
	}
    
    // ===========================================================================================
    //                                     간편쓰기.
    // ===========================================================================================
    
    private HSSFSheet nowSheet;
    
    /** groovy땜에 하나 더 만듬 ㅠㅠ */
    public void addSheet(String sheetname,List<String> titles){
    	addSheet(sheetname,titles.toArray(new String[titles.size()]));
    }
    
    /** 기존 만들어진 문서에 값만 변경할때 사용하자. */
    public void setSheetAt(int index){
    	nowSheet = wb.getSheetAt(index);
    }
    
    /**
     * 1. 시트를 만들고 0번째 로우에 헤더를 만든다.
     * 2. 시트의 가로 , 세로 열이 같다면 merge한다.
     */
    public void addSheet(String sheetname,String[] ... titless){
    	nowSheet = wb.createSheet(sheetname);
        HSSFRow row ;
        for(String[] titles : titless){
        	if(titles==null) continue; //groovy에서 ...에 1개만 넣으면 인식을 못한다. 대충 땜빵
            row = createNextRow();
            for(int j=0;j<titles.length;j++)
                row.createCell(j).setCellValue(new HSSFRichTextString(titles[j]));    
        }
        headerRowCount.add(titless.length);
    }
    
    private HSSFRow createNextRow() {
        int i = nowSheet.getPhysicalNumberOfRows(); //시트가 순수 createRow로 생성한 로우 수를 반환한다. 즉 중간에 공백이 있으면 안된다.
        return nowSheet.createRow(i);
    }
    
	private HSSFRow currentRow() {
    	int i = nowSheet.getPhysicalNumberOfRows(); //시트가 순수 createRow로 생성한 로우 수를 반환한다. 즉 중간에 공백이 있으면 안된다.
    	return nowSheet.getRow(i-1);
    }
    
    /**
     * 간단한 시트를 완성한다.
     * 기본 입력은 하이버네이트 기본인  List<Object[]> 이다.
     * 즉.. 순서가 있는 2차원 배열이어야 한다. (RequestMap이나 bean은 사용 못함)
     */
    public void makeSimpleSheet(List<Object[]> list){
        int sheetNum = wb.getActiveSheetIndex();
        HSSFSheet sheet = wb.getSheetAt(sheetNum);
        HSSFRow row = null;
        int header = headerRowCount.get(sheetNum);
        for(int i=0;i<list.size();i++){
            Object[] obj = list.get(i);
            row = sheet.createRow(i+header);
            for(int j=0;j<obj.length;j++){
                row.createCell(j).setCellValue(new HSSFRichTextString(StringUtil.toString(obj[j])));
            }
        }
    }
    
    /** 파일다운로드링크, 페이지 이동링크 등등 */
    public void addUrlHyperlink(int cellnum,String url){
    	HSSFRow row = currentRow();
    	addHyperlink(row,cellnum, url,HSSFHyperlink.LINK_URL );
    }

    /** ex) db.eachWithIndex { k,i -> p.addHyperlink i+1, 0, k.TABLE_NAME, 'A', 1 } */
	private void addHyperlink(HSSFRow row,int cellnum, String linkText ,int linkType) {
		HSSFCell cell =  row.getCell(cellnum);
    	HSSFHyperlink link = new HSSFHyperlink(linkType);
    	link.setAddress(linkText);
    	cell.setHyperlink(link);
    	pairs.add(new PoiCellPair(cell, LINKED));
	}
	
	/** 현재 로우에 링크달기 */
    public void addHyperlink(int cellnum,String sheetName,String column,int rownum){
    	HSSFRow row = currentRow();
    	addHyperlink(row,cellnum, toLink(sheetName, column, rownum),HSSFHyperlink.LINK_DOCUMENT );
    }
    
    /** 특정 로우에 링크달기 */
    public void addHyperlink(int rowNum,int cellnum,String sheetName,String column,int rownum){
    	HSSFRow row = nowSheet.getRow(rowNum);
    	addHyperlink(row,cellnum, toLink(sheetName, column, rownum),HSSFHyperlink.LINK_DOCUMENT );
    }
    /** 특정  시트의 특정 로우에 링크달기 */
    public void addHyperlink(int index,int rowNum,int cellnum,String sheetName,String column,int rownum){
    	HSSFRow row = wb.getSheetAt(index).getRow(rowNum);
    	addHyperlink(row,cellnum, toLink(sheetName, column, rownum),HSSFHyperlink.LINK_DOCUMENT );
    }
    /** Groovy용 간단 링크달기. 쿨하니깐 성능따윈 신걍쓰지 않는다.
     * 각각의 리스트로부터 동일한 값이 있는 걸 매핑시킨다. */
    @SuppressWarnings("rawtypes")
	public void addHyperlink(List<Map> list,List<Map> target,String orgKey,String targetKey,int linkCell,String sheetName,String linkedCell){
    	int headerSize = headerRowCount.get(wb.getSheetIndex(nowSheet.getSheetName()));
    	for(int i=0;i<list.size();i++){
    		Object value = list.get(i) .get(orgKey);
    		for(int j=0;j<target.size();j++){
    			Object targetValue = target.get(j).get(targetKey);
    			if(value==null || targetValue==null ) continue;
    			if(!value.equals(targetValue) ) continue;
    			addHyperlink(i+headerSize,linkCell,sheetName,linkedCell,j+headerSize+1); //headerSize가 동일하다고 일단 가정
    			break;
    		}
    	}
    }
    
    @SuppressWarnings("rawtypes")
	public void addHyperlink(List<Map> list,List<Map> target,String orgKey,String targetKey,int linkCell,String sheetName){
    	addHyperlink(list, target, orgKey, targetKey, linkCell, sheetName,"A");
    }
    
    
    /** 그루비용으로 급조 */
    public void setHeaderComments(List<String> comments){
        HSSFPatriarch patr = getOrCreatePatriarch(nowSheet);
        Row row = nowSheet.getRow(0);
        for(short i=0;i<row.getLastCellNum();i++){
        	Cell cell = row.getCell(i);
        	String commentString = comments.get(i);
        	if(commentString==null) continue;
            HSSFComment comment = patr.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short)4, 2, (short) 7, 6)); //의미불명
            comment.setString(new HSSFRichTextString(commentString)); 
            cell.setCellComment(comment);
        }
    }    
    
    /** column link는 A ,B 이런식으로 네이밍된다. 
     * ㅅㅂ.. 게다가 시트번호로는 또 안되네. */
    private String toLink(String sheetName,String column,int rownum){
    	return sheetName+"!"+column+rownum;
    }
    
    /** 객체지향이라서 가능한 문법~  */
    public void addStyle(CellStyle style,int cellnum){
    	Row row = currentRow();
    	Cell cell =  row.getCell(cellnum);
    	pairs.add(new PoiCellPair(cell, style));
    }
    
    /** row를 만들고 i번째 컬럼 부터 value를 입력한다.? i는 왜넣었을까..ㅋ */
    public void addValues(int i,Object ... values){
        HSSFRow row = createNextRow();
        addValues(i, row, values);
    }
    
    /** ROUNDUP( A@+B@,2) 이런식으로\
     * 추가적인 내용은 확장해서 쓰자. 
     * */
    public static class PoiFormula{
    	private final String formula;
    	public PoiFormula(String formula) {this.formula = formula ;}
    	public String toFormula(int rowNum){
    		String value = formula.replaceAll("@-1",String.valueOf(rowNum-1));
    		return value.replaceAll("@",String.valueOf(rowNum));
    	}
    }

    /** 최종메소드?  숫자와 문자는 구분한다. 뿌잉뿌잉~! */
	private void addValues(int i, HSSFRow row, Object... values) {
		for(Object each : values){
            if(each instanceof Number){
            	Number number =  (Number)each;
            	row.createCell(i++).setCellValue(number.doubleValue());
            }else if(each instanceof PoiFormula){
            	String name = row.getSheet().getSheetName();
            	int index = wb.getSheetIndex(name);
            	int count = headerRowCount.get(index);
            	PoiFormula pf = (PoiFormula) each;
            	String formula = pf.toFormula(row.getRowNum()+count);
            	row.createCell(i++).setCellFormula(formula);
            }else{
            	String value = null;
                if(each==null) value="";
                else if(each instanceof Date) value = DayUtil.DATE.get((Date)each);
                else value = each.toString();
                row.createCell(i++).setCellValue(new HSSFRichTextString(value));
            }
        }
		/*
        for(Object each : values){
            String value = null;
            if(each==null) value="";
            else if(each instanceof Long || each instanceof Integer) value = Formats.INT.get((Number)each);
            else if(each instanceof Number) value = Formats.DOUBLE2.get((Number)each);
            else if(each instanceof Date) value = Days.DATE.get((Date)each);
            else value = each.toString();
            row.createCell(i++).setCellValue(new HSSFRichTextString(value));
        }
        */
	}
	
	public void addImage(HSSFClientAnchor anchor,File file){
		addImage(nowSheet,anchor,file);
	}
	public void addImage(HSSFClientAnchor anchor,String file){
		addImage(nowSheet,anchor,new File(file));
	}
    
	/** 나중에 입력값이 아닌 셀타입에 따라 바뀌게 만들자.
	 *  -> 이거 수정해야함 */
	@Deprecated
    public void changeValues(int rowIndex,Object ... values){
    	HSSFRow row = nowSheet.getRow(rowIndex);
    	int i=0;
    	for(Object each : values){
            String value = null;
            if(each==null) value="";
            else if(each instanceof Number){
            	Number number =  (Number)each;
            	row.getCell(i).setCellValue(number.doubleValue());	
            	continue;
            }
            else if(each instanceof Date) value = DayUtil.DATE.get((Date)each);
            else value = each.toString();
            row.getCell(i++).setCellValue(new HSSFRichTextString(value));
        }
    }
	
	/** ex) p.changeDate 0, 4, 1, { it+10 } */
	@SuppressWarnings("rawtypes")
	public void changeDate(int sheetIndex,int rowNum,int colNum, Closure closure){
		Cell cell = findCell(sheetIndex, rowNum, colNum);
		Date value = (Date) closure.call(cell.getDateCellValue());
		cell.setCellValue(value);
	}
	@SuppressWarnings("rawtypes")
	public void changeValue(int sheetIndex,int rowNum,int colNum, Closure closure){
		Cell cell = findCell(sheetIndex, rowNum, colNum);
		if(cell.getCellType()==Cell.CELL_TYPE_NUMERIC) cell.setCellValue((Double)closure.call(cell.getNumericCellValue())); 
		else if(cell.getCellType()==Cell.CELL_TYPE_STRING) cell.setCellValue( (String) closure.call(cell.getRichStringCellValue().getString()));
	}
    
    public void addValuesCollection(@SuppressWarnings("rawtypes") Collection values){
    	addValues(0,values.toArray());
    }
    public void addValuesArray(Object[] values){
    	addValues(0,values);
    }
    
    /** sheet의 마지막에 row를 생성하고 value를 입력한다. */    
    public void addValues(Object ... values){
    	addValues(0,values);
    }
    
    /** 컬럼 순서같은건 없다. 간단메소드로서 사용에 주의할것.
     * Collection인 컬럼은 무시한다. */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setListedMap(String sheetname,List<Map> list){
		if(list.size()==0) return;
		//String[] colums = (String[]) list.get(0).keySet().toArray(new String[list.get(0).keySet().size()]);
		List colList = new ArrayList();
		Map sample = list.get(0);
		for(Object o : sample.entrySet()){
			Entry e = (Entry)o;
			if(e.getValue() instanceof Collection) continue;
			colList.add(e.getKey());
		}
		String[] colums = (String[]) colList.toArray(new String[colList.size()]);
		this.addSheet(sheetname, colums);
		
		for(Map each : list){
			Object[] values = new Object[colums.length]; 
			for(int i=0;i<colums.length;i++){
				Object value = each.get(colums[i]);
				if(value instanceof Collection) continue;
				values[i] = value == null? "" : value ;
			}
			this.addValuesArray(values);
		}
    }    
    public void setListedRequestMap(String sheetname,List<RequestMap> list){
		if(list.size()==0) return;
		String[] colums =  list.get(0).keySet().toArray(new String[list.get(0).keySet().size()]);
		this.addSheet(sheetname, colums);
		
		for(RequestMap each : list){
			String[] values = new String[colums.length]; 
			for(int i=0;i<colums.length;i++){
				values[i] = each.getString(colums[i]);
			}
			this.addValuesArray(values);
		}
    }
    
}
