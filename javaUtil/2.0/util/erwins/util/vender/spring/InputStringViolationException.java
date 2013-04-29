package erwins.util.vender.spring;

import java.text.MessageFormat;

import javax.validation.ValidationException;

import erwins.util.vender.spring.StringArrayValidator.LineMetadata;

/** StringValidator 가 던지는 예외.
 * 여러 벨리데이션은 추가로 하나 더 만들자 */
public class InputStringViolationException extends ValidationException{

	private static final long serialVersionUID = -6083404627158480484L;
	
	private static final String FORMAT_1 = "ROW : {0} , NAME : {1} , 입력값 : {2}";
	private static final String FORMAT_2 = "ROW : {0} , NAME : {1} , 입력값 : {2} , 제한 : {3}";
	
	private final String violationName;
	private final String value;
	private final int row;
	private final LineMetadata lineMetadata;
	private Object constraint;
	private String sheetName;
	
	public InputStringViolationException(String violationName,String value, int row,LineMetadata lineMetadata) {
		this.violationName = violationName;
		this.value = value;
		this.row = row;
		this.lineMetadata = lineMetadata;
	}
	
	public InputStringViolationException(String violationName,String value, int row,LineMetadata lineMetadata,Object constraint) {
		this.violationName = violationName;
		this.value = value;
		this.row = row;
		this.lineMetadata = lineMetadata;
		this.constraint = constraint;
	}

	/** 임의로 생성된다. 기본 포맷을 사용하지 않을려면 별도 작성 */
	@Override
	public String getMessage() {
		return violationName + " " + getMetadataString();
	}
	
	public String getMetadataString() {
		String format = constraint == null ? FORMAT_1 : FORMAT_2;
		String message = MessageFormat.format(format, new Object[]{row,lineMetadata.name,value,constraint});
		if(sheetName!=null) message = "SHEET : " + sheetName + " , " + message;
		return message;
	}

	public String getValue() {
		return value;
	}

	public int getRow() {
		return row;
	}

	public LineMetadata getLineMetadata() {
		return lineMetadata;
	}

	public Object getConstraint() {
		return constraint;
	}

	public String getViolationName() {
		return violationName;
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}
	

}
