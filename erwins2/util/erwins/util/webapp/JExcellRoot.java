
package erwins.util.webapp;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.NumberFormat;
import jxl.write.WritableCell;
import jxl.write.WritableCellFeatures;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import erwins.util.web.WebUtil;


/** POI를 사용할 수 없는 환경에서 유용하다. ( ex) Google App Engine ) */
public abstract class JExcellRoot{
	
	protected final WritableWorkbook workbook;
	protected WritableSheet nowSheet;
	
	/** 기본 15. 글자가 작아서 머 적당한듯. */
	protected static final int DEFAULT_COLUMN_WIDTH = 15;
	protected static final NumberFormat DOUBLE_FORMAT = new NumberFormat("#,###.00");
	protected static final NumberFormat INT_FORMAT = new NumberFormat("0");
	protected static final WritableCellFormat HEADER;
	protected static final WritableCellFormat BODY;
	protected static final WritableCellFormat BODY_NUMBER;
	static{
		HEADER = new WritableCellFormat();
		BODY = new WritableCellFormat();
		BODY_NUMBER = new WritableCellFormat(DOUBLE_FORMAT);
		try {
			HEADER.setBackground(Colour.YELLOW);
			HEADER.setBorder(Border.ALL,BorderLineStyle.THIN );
			HEADER.setAlignment(Alignment.CENTRE);
			BODY.setBorder(Border.ALL,BorderLineStyle.THIN );
			BODY.setShrinkToFit(true); // 우미..
			BODY_NUMBER.setBorder(Border.ALL,BorderLineStyle.THIN );
			BODY_NUMBER.setShrinkToFit(true);
		} catch (WriteException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void addSheet(String title,String ... values){
		nowSheet = workbook.createSheet(title,workbook.getNumberOfSheets());
		int rowNum = nowSheet.getRows();
		for( int i=0;i<values.length;i++){
			Object each = values[i];
			WritableCell label = new Label(i, rowNum,each.toString());
			label.setCellFormat(HEADER);
			addCellWithoutThrow(label);
		}
		for( int i=0;i<values.length;i++){
			nowSheet.setColumnView(i,DEFAULT_COLUMN_WIDTH);
		}
	}
	
	/** 가로 너비를 세팅한다. */
	public void addSheetConfig(Integer ... widths){
		for( int i=0;i<widths.length;i++){
			Integer width = widths[i];
			if(width==null) continue;
			nowSheet.setColumnView(i,width);			
		}
	}
	
	public void write(){
		try {
			workbook.write();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}finally{
			close();
		}
	}

	/** 예외를 감싸는 닫기. */
	private void close() {
		try {
			workbook.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addValues(Object ... values){
		int rowNum = nowSheet.getRows();
		for( int i=0;i<values.length;i++){
			Object each = values[i];
			addValue(rowNum, i, each);
		}
	}
	
	protected void addValue(int rowNum, int i, Object each){
		if(each instanceof Long || each instanceof Integer){
			WritableCell num = new jxl.write.Number(i, rowNum, ((Number)each).longValue());
			num.setCellFormat(BODY);
			addCellWithoutThrow(num);
		}else if(each instanceof Number){
			WritableCell num = new jxl.write.Number(i, rowNum, ((Number)each).doubleValue());
			num.setCellFormat(BODY_NUMBER);
			addCellWithoutThrow(num);
		}else{
			if(each==null) each = ""; 
			WritableCell label = new Label(i, rowNum,each.toString());
			label.setCellFormat(BODY);
			addCellWithoutThrow(label);
		}
	}

	private void addCellWithoutThrow(WritableCell num){
		try {
			nowSheet.addCell(num);
		} catch (RowsExceededException e) {
			close();
			throw new RuntimeException(e);
		} catch (WriteException e) {
			close();
			throw new RuntimeException(e);
		}
	}
	
	/** ㄴ중에~ */
	public void addComment(Object ... values){
		WritableCellFeatures ff = new WritableCellFeatures();
		ff.setComment("코멘트 으잉~");
	}
	
	
	public JExcellRoot(File file){
		try {
			workbook = Workbook.createWorkbook(file);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	public JExcellRoot(HttpServletResponse resp){
		resp.setContentType(WebUtil.CONTENT_TYPE_DOWNLOAD);
		try {
			workbook = Workbook.createWorkbook(resp.getOutputStream());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	
}
