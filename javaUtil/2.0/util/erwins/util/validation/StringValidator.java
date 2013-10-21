package erwins.util.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Range;

import erwins.util.spring.InputStringViolationException;
import erwins.util.text.StringUtil;
import erwins.util.validation.StringArrayValidator.LineMetadata;

/** javax 패키지의 ValidationException을 던진다.
 * 여기서는 입력값에 대한 무결성검사만 진행하며, NK같은 검사는 별도로 진행해야 한다.
 * 디폴트로 포매터가 있고, 예외를 받는 시점에서 별도 포매터를 설정해줄 수도 있다
 * 타입에 무관한 체크는 반드시 null을 리턴해야 한다. ex) REQUIRED
 * 각 타입에 따른 벨리데이터가 반드시 하나이상 존재해야 하며, 여러 벨리데이터가 들어갈 경우 모두 동일 타입을 리턴해야 한다.
 * @see FlatDataBinder
 *   */
@Deprecated
public interface StringValidator{
	
	/** null을 리턴하면 형변환을 하지 않겠다는 의미이다 */
	public Object validate(String value,int row,LineMetadata lineMetadata);
	
	//============================== 타입 무관 ==========================================//
    
	/** 필수입력항목 */
    public static StringValidator REQUIRED = new StringValidator(){
    	private static final String NAME = "해당 필드는 필수 입력 항목입니다.";
		@Override
		public Object validate(String value, int row,LineMetadata lineMetadata) {
			if(Strings.isNullOrEmpty(value)) throw new InputStringViolationException(NAME,value,row,lineMetadata); 
			return null;
		}
    };
    
	/** 비필수입력항목 */
    public static StringValidator NOT_REQUIRED = new StringValidator(){
    	private static final String NAME = "해당 필드는 입력할 수 없는 항목입니다.";
		@Override
		public Object validate(String value, int row,LineMetadata lineMetadata) {
			if(!Strings.isNullOrEmpty(value)) throw new InputStringViolationException(NAME,value,row,lineMetadata);  
			return null;
		}
    };
    
    //============================== String ==========================================//
    
    /** 사용 가능한 문자열을 제한한다.
     * IP 주소 : ([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})
     * ex) new RegExFullMatch("[가-힣a-zA-Z0-9\\s\\[\\]()\\~\\+\\-\\%\\.\\/]*") */
    public static class RegExFullMatch implements StringValidator{
    	private static final String NAME = "해당 필드에 사용할 수 없는 문자열이 존재합니다.";
    	private final Pattern regEx;
		public RegExFullMatch(String matchRegEx) {
			this.regEx = Pattern.compile(matchRegEx);
		}
		@Override
		public Object validate(String value, int row,LineMetadata lineMetadata) {
			if(Strings.isNullOrEmpty(value)) return "";
			Matcher matcher = regEx.matcher(value);
			if(!matcher.matches()){
				String constraint = matcher.replaceAll("");
				throw new InputStringViolationException(NAME,value,row,lineMetadata,constraint); 
			}
			return value;
		}
    }
    
    public static class MaxLength implements StringValidator{
    	private static final String NAME = "해당 필드는 최대입력값을 넘지 말아야 합니다.";
    	private final int maxSize;
		public MaxLength(int maxSize) {
			this.maxSize = maxSize;
		}
		@Override
		public Object validate(String value, int row,LineMetadata lineMetadata) {
			if(Strings.isNullOrEmpty(value)) return "";
			if(value.length() > maxSize) throw new InputStringViolationException(NAME,value,row,lineMetadata,maxSize);
			return value;
		}
    }
    
    public static class MaxByte implements StringValidator{
    	private static final String NAME = "해당 필드는 최대byte를 넘지 말아야 합니다.";
    	private final int maxByteSize;
		public MaxByte(int maxByteSize) {
			this.maxByteSize = maxByteSize;
		}
		@Override
		public Object validate(String value, int row,LineMetadata lineMetadata) {
			if(Strings.isNullOrEmpty(value)) return "";
			if(StringUtil.getByteSize(value) > maxByteSize) throw new InputStringViolationException(NAME,value,row,lineMetadata,maxByteSize);
			return value;
		}
    }
    
    /** 샘플 중 하나의 값이어야 함 */
    public static class EqualsAny implements StringValidator{
    	private static final String NAME = "해당 필드는 제한값중 하나가 입력되어야 합니다.";
    	private final String[] samples;
		public EqualsAny(String ... samples) {
			this.samples = samples;
		}
		@Override
		public Object validate(String value, int row,LineMetadata lineMetadata) {
			if(Strings.isNullOrEmpty(value)) return "";
			if(!StringUtil.isEquals(value, samples)) {
				String example = Joiner.on(',').join(samples);
				throw new InputStringViolationException(NAME,value,row,lineMetadata,example);
			}
			return value;
		}
    }
    
    //============================== Long ==========================================//
    
    /** 숫자포맷 / 입력범위 검증 */
    public static class IsLongNumber implements StringValidator{
    	private static final String NAME1 = "해당 필드에는 숫자가 입력되어야 합니다.";
    	private static final String NAME2 = "해당 필드의 숫자범위가 유효하지 않습니다.";
    	private Range<Long> range;
		public IsLongNumber(Range<Long> range) { this.range = range; }
		public IsLongNumber(int size) {
			long max = (long) (Math.pow(10, size) - 1);
			this.range = Range.open(-max, max); 
		}
		public IsLongNumber() { }
		@Override
		public Object validate(String value, int row,LineMetadata lineMetadata) {
			if(Strings.isNullOrEmpty(value)) return null;
			try {
                Long longValue  = Long.parseLong(value);
                if(range!=null) if(!range.contains(longValue))  throw new InputStringViolationException(NAME2,value,row,lineMetadata,range);
                return longValue;
            } catch (NumberFormatException e) {
            	throw new InputStringViolationException(NAME1,value,row,lineMetadata);
            }
		}
    }
    
    //============================== Integer ==========================================//
    
    public static interface FieldToStringAble<T>{
    	public String fieldToString(T value);
    }
    
    
    /** 오라클 등에서 boolean을 표현할때 int를 쓰는경우 vo에서도 이렇게 할때 사용
     * 인자로 2개만 입력해야 하며, 1,0을 나타낸다 */
    public static class IsBooleanInt implements StringValidator,FieldToStringAble<Integer>{
    	private static final String NAME = "해당 필드는 다음 항목중 하나의 값이 입력되어야 합니다.";
    	private final String[] samples;
		public IsBooleanInt(String ... samples) {
			this.samples = samples;
			Preconditions.checkArgument(samples.length == 2);
		}
		@Override
		public Object validate(String value, int row,LineMetadata lineMetadata) {
			if(Strings.isNullOrEmpty(value)) return null; //디폴트 없고 걍 무시
			if(samples[1].equals(value)) return 1;
			else if(samples[0].equals(value)) return 0; 
			else {
				String example = Joiner.on(',').join(samples);
				throw new InputStringViolationException(NAME,value,row,lineMetadata,example);
			}
		}
		public String fieldToString(Integer value) {
			return samples[value];
		}
    }
    

}
