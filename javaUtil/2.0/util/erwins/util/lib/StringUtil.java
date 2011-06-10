
package erwins.util.lib;

import java.io.File;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

import erwins.util.collections.map.RequestMap;
import erwins.util.counter.Latch;

/**
 * apache의 StringUtils에 없는것을 정의한다.
 * 만들어놓고 한번도 안봄.. 나중에 정리하기.
 */
public class StringUtil extends StringUtils {
	
	/** WordUtils에 있는걸 가죠왔다.
	 * ex) WordUtils.abbreviate("123456789abcdefg", 0,맥스값,"..."같이 마지막에 들어갈 문자.) */
	public static String abbreviate(String org,int upper,String end){
		return WordUtils.abbreviate(org, 0,upper,"...");
	}
	
	/** org로 들어온 문자에 정해진 길이마다 separator를 추가한다.
	 * 보통 특정 길이마다 \n등을 넣을때 사용  */
	public static String splitByLength(String org,int length,String separator){
		int size = org.length();
		int root = size / length;
		if(root==0) return org;
		int in = 0;
		StringBuilder buff = new StringBuilder(); 
		for(int i=0;i<root;i++){
			buff.append(org.substring(in, in+length));
			buff.append(separator);
			in+= length;
		}
		buff.append(org.substring(in, size));
		return buff.toString();
	}
	
	/** 각각 길이별로 문자열을 잘라낸다. Text가 일정길이로 나뉘어진 형식일때 유효하다.
	 * org.length()+1 == nowLength;
	 * 마지막열은 길이값 무시하도록 추가. (trim()때문에..) */
	public static String[] splitEachLength(String org,int[] eachLength,int offset){
		String[] result = new String[eachLength.length];
		int nowLength = 0;
		for(int i=0;i<eachLength.length;i++){
			int length = eachLength[i];
			int subLength = nowLength + length;
			try {
				if(org.length() < subLength) result[i] = org.substring(nowLength, eachLength.length);
				else result[i] = org.substring(nowLength, subLength);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			nowLength += length + offset;
		}
		return result;
	}
	/** 각각 길이별로 문자열을 잘라낸다. Text가 일정길이로 나뉘어진 형식일때 유효하다.
	 * org.length()+1 == nowLength;
	 * 마지막열은 길이값 무시하도록 추가. (trim()때문에..) */
	public static String[] splitEachLength(String org,List<Integer> eachLength,int offset){
		String[] result = new String[eachLength.size()];
		int nowLength = 0;
		int size = eachLength.size();
		for(int i=0;i<size;i++){
			int length = eachLength.get(i);
			int subLength = nowLength + length;
			if(org.length() < subLength) result[i] = org.substring(nowLength, org.length());
			else result[i] = org.substring(nowLength, subLength);
			nowLength += length + offset;
		}
		//if(org.length() != (nowLength-1)) throw new RuntimeException(
				//"["+org.length()+"] / ["+(nowLength-1)+"] : Length is not match");
		return result;
	}
	
	public static void removeNullAndTrim(String[] strings){
		for(int i=0;i<strings.length;i++){
			String each = strings[i];
			if(each==null) strings[i] = "";
			else strings[i] = each.trim();
		}
	}

    /** size만큼 str과 seperator를 반복시킨다. */
    public static String iterateStr(String str,String seperator,int size) {
        StringBuilder b = new StringBuilder();
        for(int i=0;i<size;i++){
            if(i!=0) b.append(seperator);
            b.append(str);
        }
        return b.toString();
    }
    
    /**
     * strs들중 일부라도 매치가 되면 true를 리턴한다.
     */
    public static boolean isMatch(String body, String... strs) {
        for (String str : strs)
            if (StringUtil.contains(body, str)) return true;
        return false;
    }
    
    /**
     * prefix들중 일부라도 매치가 되면 true를 리턴한다.
     */
    public static boolean isStartsWithAny(String body, String... prefix) {
    	if(body==null) return false;
    	for (String each : prefix)
    		if (StringUtil.startsWith(body, each)) return true;
    	return false;
    }
    
    /**
     * strs들중 일부라도 매치가 되면 true를 리턴한다.
     */
    public static boolean isMatchIgnoreCase(String body, String... strs) {
        body = body.toUpperCase();
        for (String str : strs)
            if (StringUtil.contains(body, str.toUpperCase())) return true;
        return false;
    }

    /**
     * strs들중 정확히 매치가 되면 true를 리턴한다.
     */
    public static boolean isEquals(String body, String... strs) {
        if (body == null) return false;
        for (String str : strs)
            if (body.equals(str)) return true;
        return false;
    }

    /**
     * strs들중 정확히 매치가 되면 true를 리턴한다. 대소문자를 구분하지 않는다.
     */
    public static boolean isEqualsIgnoreCase(String body, String... strs) {
        if (body == null) return false;
        for (String str : strs)
            if (body.equalsIgnoreCase(str)) return true;
        return false;
    }

    /**
     * 첫번째 패턴까지의 문자열을 리턴한다. ex) getFirst("12345\qqq\asd","\") => 12345
     */
    public static String getFirst(String str, String pattern) {
        return str.substring(0, str.indexOf(pattern));
    }
    
    /**
     * 첫번째 패턴 이후로의 문자열을 리턴한다. 
     * ex) getFirstAfter("12345\qqq\asd","\") => qqq\asd
     * 매칭이 안되면 원본을 리턴한다.
     */
    public static String getFirstAfter(String str, String pattern) {
    	return str.substring(str.indexOf(pattern)+1,str.length() );
    }

    /**
     * 마지막 패턴 이후로의 문자열을 리턴한다. ex) getFirst("12345\qqq\asd","\") => asd
     */
    public static String getLast(String str, String pattern) {
        return str.substring(str.lastIndexOf(pattern) + 1);
    }

    /**
     * 확장자를 리턴한다. ex) getExtention("12345.qqq.asd") => "asd" ex)
     * getExtention("123asd") => ""
     */
    public static String getExtention(String str) {
        int index = str.lastIndexOf(".");
        if (index == -1) return "";
        return str.substring(index + 1);
    }
    
    /** 확장자를 리턴한다. 없으면 기본 문자를  그대로 리턴한다. */
    public static String getExtention2(String str) {
        int index = str.lastIndexOf(".");
        if (index == -1) return str;
        return str.substring(index + 1);
    }    
    
    /** .이 없으면 null을 리턴한다. */
    public static String[] getExtentions(String str) {
        return getLastOf(str,".");
    }
    
    /** 
     * 마자막 구분자를 기준으로 문자를 2개로 나눈다.
     * 파일의 확장자나 폴더/파일이름을 구분할때 사용된다.
     *  */
    public static String[] getLastOf(String str,String seperator) {
        String[] temp = new String[2];
        int index = str.lastIndexOf(seperator);
        if (index == -1) return null;
        temp[0] = str.substring(0, index);
        temp[1] = str.substring(index + 1);
        return temp;
    }

    /**
     * Url은 '/'를 포함하는 root부터 시작한다. ex) /D:/qwe.qwe.go => 'D:/qwe.qwe' and 'go'
     */
    public static String[] getUrlAndExtention(String url) {
        String[] str = new String[2];
        int index = url.lastIndexOf(".");
        if (index < 0) throw new RuntimeException(MessageFormat.format("[{0}] 확장자가 존재하지 않는 경로를 입력하셨습니다.", url));
        str[0] = url.substring(1, index);
        str[1] = url.substring(index + 1);
        return str;
    }

    /**
     * 다국어(한글등)이면 true를 리턴한다.
     */
    public static boolean isHan(char c) {
        if (Character.getType(c) == Character.OTHER_LETTER) { return true; }
        return false;
    }

    /**
     * 다국어(한글등)이면 true를 리턴한다.
     */
    public static boolean isHanAny(String str) {
        for (char c : str.toCharArray())
            if (Character.getType(c) == Character.OTHER_LETTER) { return true; }
        return false;
    }

    private static final String[] beanMethods = new String[] { "get", "set", "is" };

    /**
     * methodName(setter/geter/is)에서 fildName을 추출한다. 해당 조건이 아니면 null을 리턴한다.
     */
    public static String getFieldName(String name) {
        for (String type : beanMethods) {
            if (name.startsWith(type)) return escapeAndUncapitalize(name, type);
        }
        return null;
    }

    private static final String[] getterMethods = new String[] { "get", "is" };

    /**
     * methodName(geter/is)에서 fildName을 추출한다. 해당 조건이 아니면 null을 리턴한다.
     */
    public static String getterName(String name) {
        for (String type : getterMethods) {
            if (name.startsWith(type)) return escapeAndUncapitalize(name, type);
        }
        return null;
    }
    
    private static final String[] setterMethods = new String[] { "set"};
    
    /**
     * methodName(setter)에서 fildName을 추출한다. 해당 조건이 아니면 null을 리턴한다.
     */
    public static String setterName(String name) {
        for (String type : setterMethods) {
            if (name.startsWith(type)) return escapeAndUncapitalize(name, type);
        }
        return null;
    }

    /**
     * 사업자 등록번호인지 체크한다.
     */
    public static boolean isBusinessId(String str) {
        String[] strs = str.split(EMPTY);
        if (strs.length != 11) return false;
        int[] ints = new int[10];
        for (int i = 0; i < 10; i++)
            ints[i] = Integer.valueOf(strs[i + 1]);
        int sum = 0;
        int[] indexs = new int[] { 1, 3, 7, 1, 3, 7, 1, 3 };
        for (int i = 0; i < 8; i++) {
            sum += ints[i] * indexs[i];
        }
        int num = ints[8] * 5;
        sum += (num / 10) + (num % 10);
        sum = 10 - (sum % 10);
        return sum == ints[9] ? true : false;
    }


    /**
     * 주민등록번호인지 체크한다. 1. 주민등록번호의 앞 6자리의 수에 처음부터 차례대로 2,3,4,5,6,7 을 곱한다. 그 다음, 뒤
     * 7자리의 수에 마지막 자리만 제외하고 차례대로 8,9,2,3,4,5 를 곱한다. 2. 이렇게 곱한 각 자리의 수들을 모두 더한다.
     * 3. 모두 더한 수를 11로 나눈 나머지를 구한다. 4. 이 나머지를 11에서 뺀다. 5. 이렇게 해서 나온 최종 값을
     * 주민등록번호의 마지막 자리 수와 비교해서 같으면 유효한 번호이고 다르면 잘못된 값이다.
     */
    public static boolean isSid(String input) {
        input = getNumericStr(input);

        if (input.length() != 13) throw new RuntimeException("주민등록번호 자리수 13자리를 확인하기 바랍니다.");

        // 입력받은 주민번호 앞자리 유효성 검증============================
        String leftSid = input.substring(0, 6);
        String rightSid = input.substring(6, 13);

        int yy = Integer.parseInt(leftSid.substring(0, 2));
        int mm = Integer.parseInt(leftSid.substring(2, 4));
        int dd = Integer.parseInt(leftSid.substring(4, 6));

        if (yy < 1 || yy > 99 || mm > 12 || mm < 1 || dd < 1 || dd > 31) return false;

        int digit1 = Integer.parseInt(leftSid.substring(0, 1)) * 2;
        int digit2 = Integer.parseInt(leftSid.substring(1, 2)) * 3;
        int digit3 = Integer.parseInt(leftSid.substring(2, 3)) * 4;
        int digit4 = Integer.parseInt(leftSid.substring(3, 4)) * 5;
        int digit5 = Integer.parseInt(leftSid.substring(4, 5)) * 6;
        int digit6 = Integer.parseInt(leftSid.substring(5, 6)) * 7;

        int digit7 = Integer.parseInt(rightSid.substring(0, 1)) * 8;
        int digit8 = Integer.parseInt(rightSid.substring(1, 2)) * 9;
        int digit9 = Integer.parseInt(rightSid.substring(2, 3)) * 2;
        int digit10 = Integer.parseInt(rightSid.substring(3, 4)) * 3;
        int digit11 = Integer.parseInt(rightSid.substring(4, 5)) * 4;
        int digit12 = Integer.parseInt(rightSid.substring(5, 6)) * 5;

        int last_digit = Integer.parseInt(rightSid.substring(6, 7));

        int error_verify = (digit1 + digit2 + digit3 + digit4 + digit5 + digit6 + digit7 + digit8 + digit9 + digit10 + digit11 + digit12) % 11;

        int sum_digit = 0;
        if (error_verify == 0) {
            sum_digit = 1;
        } else if (error_verify == 1) {
            sum_digit = 0;
        } else {
            sum_digit = 11 - error_verify;
        }

        if (last_digit == sum_digit) return true;
        return false;
    }


    /**
     * 카멜 케이스를 "_" 형태로 연결한다. prototype의 underscore와는 달리 대문자 이다. ex) userName =>
     * USER_NAME
     */
    public static String getUnderscore(String str) {

        char[] chars = str.toCharArray();
        StringBuffer stringBuffer = new StringBuffer();
        for (char cha : chars) {
            if (cha >= 'A' && cha <= 'Z') stringBuffer.append('_');
            stringBuffer.append(cha);
        }
        return stringBuffer.toString().toUpperCase();
    }

    /** '_' or '-' 형태의 연결을 카멜 케이스로 변환한다. ex) USER_NAME => userName */
    public static String getCamelize(String str) {
        char[] chars = str.toCharArray();
        boolean nextCharIsUpper = false;
        StringBuffer stringBuffer = new StringBuffer();
        for (char cha : chars) {
            if (cha == '_' || cha == '-') {
                nextCharIsUpper = true;
                continue;
            }
            if (nextCharIsUpper) {
                stringBuffer.append(Character.toUpperCase(cha));
                nextCharIsUpper = false;
            } else stringBuffer.append(Character.toLowerCase(cha));
        }
        return stringBuffer.toString();
    }

    /** 두문자를 제거 후 첫 글자를 소문자로 바꾼다. ex) searchMapKey => mapKey */
    public static String escapeAndUncapitalize(String str, String header) {
        return StringUtils.uncapitalize(str.replaceFirst(header, EMPTY));
    }

    /**
     * 분자열을 바이트 배열로 변형한다. 일단은 Cryptor에서만 사용한다. String 기본의 getByte와는 특수만자 입력시
     * 다르다. 왜인지는.. 몰라.
     **/
    public static byte[] getByte(String str) {
        char[] chs = str.toCharArray();
        byte[] bytes = new byte[chs.length];
        for (int i = 0; i < chs.length; i++) {
            bytes[i] = (byte) chs[i];
        }
        return bytes;
    }

    /**
     * 바이트배열을 문자형으로 변형한다.
     **/
    public static String getStr(byte[] bytes) {
        char[] chars = new char[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            chars[i] = (char) bytes[i];
        }
        return String.valueOf(chars);
    }

    /**
     * 입력 문자열을 ''로 묶은 후 ,로 구분한다. 영감&할멈 => '영감','할멈'
     */
    public static String getBundleStr(List<String> bundle, String defaultValue) {
        if (bundle == null || bundle.size() == 0) return defaultValue;
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < bundle.size(); i++) {
            stringBuffer.append((i == 0) ? EMPTY : ",");
            stringBuffer.append("'");
            stringBuffer.append(bundle.get(i));
            stringBuffer.append("'");
        }
        return stringBuffer.toString();
    }
    
    /** 임시 포매팅. MessageFormat이랑 비슷하다. null이면 ''를 입력한다. */
    public static String format(String str,Object ... args) {
    	return formatNullable(str,"",args);
    }
    
    /** 배열 문제 때문에 String으로 입력을 제한한다. */
    public static String formatStr(String str,String ... args) {
    	if(CollectionUtil.isEmpty(args)) return str;
    	for(int i=0;i<args.length;i++) str = str.replaceAll("\\{"+i+"\\}", args[i]==null ? "" : args[i].toString());
    	return str;
    }
    
    /** 임시 포매팅. null이면 0을 입력한다. */
    public static String formatNullable(String str,String nullString,Object ... args) {
    	if(CollectionUtil.isEmpty(args)) return str;
    	for(int i=0;i<args.length;i++) str = str.replaceAll("\\{"+i+"\\}", args[i]==null ? nullString : args[i].toString());
    	return str;
    }    

    /**
     * 문자열을 특정 문자의 개수를 구한다.
     */
    public static int getCharCount(String str, char ch) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == ch) {
                count++;
            }
        }
        return count;
    }

    /**
     * 문자열을 구분하여 화면에 보여질 문자의 길이값을 리턴한다. 폰트 브라우저 등에 따라 가변적임으로 알아서 조정하자.
     */
    public static int getStrLength(String str) {
        int strLength = 0;
        int hangleHtmlWidth = 20;
        int elseHtmlWidth = 12;
        for (int i = 0; i < str.length(); i++) {
            if (Character.getType(str.charAt(i)) == 5) {
                strLength += hangleHtmlWidth;
            } else {
                strLength += elseHtmlWidth;
            }
        }
        return strLength;
    }

    /**
     * 두개의 String을 받아 산술연산(+) 후 String으로 리턴한다. ex) plus('08','-2') => 06
     */
    public static String plus(String str, String str2) {
        int len = str.length();
        String result = StringCalculator.Plus(str, str2, 0);
        return StringUtils.leftPad(result, len, "0");
    }
    
    /** 정수 덧셈을 한다. */
    public static Integer plusForInteger(String str, int str2) {
        return Integer.parseInt(str)+str2;
    }
    
    /** 배열을 더한다. Utils에 있을거 같은데 없네.. */
	public static String[] addArray(String[] org,String ... args){
		if(org==null) return args;
		String[] result = new String[org.length + args.length];
		for(int i=0;i<org.length;i++){
			result[i] = org[i];
		}
		for(int i=0;i<args.length;i++){
			result[org.length+i] = args[i];
		}
		return result;
	}    

    // ===========================================================================================
    //                                      숫자 치환            
    // ===========================================================================================    
    /**
     * String을 받아 숫자(절대값)형태만 추출해서 반환한다.
     */
    public static String getNumericStr(Object str) {
        if (str == null) return null;
        return getNumericStr(str.toString());
    }

    /**
     * String을 받아 숫자(절대값)형태만 추출해서 반환한다. 음수라면 따로 판별 컬럼을 나누자.
     * ==> 추후 정규식으로 바꾸자.
     * @return Parsing가능한 String값
     */
    public static String getNumericStr(String str) {
        if (str == null) return EMPTY;
        StringBuffer result = new StringBuffer();
        int dotCount = 0;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if ((c >= '0' && c <= '9')) result.append(c);
            else if ((c == '.' && dotCount < 1)) { //소수점 1개만 허용
                result.append(c);
                dotCount++;
            }
        }
        return result.toString();
    }

    /**
     * String을 받아 숫자(절대값)형태만 추출해서 반환. <br> null safe하다. 음수라면 따로 판별 컬럼을 나누자.
     */
    public static BigDecimal getDecimal(String str) {
        if (str == null || str.equals(EMPTY)) return BigDecimal.ZERO;
        boolean minus = str.startsWith("-");
        String temp = StringUtil.getNumericStr(str);
        if (temp.equals(EMPTY)) return BigDecimal.ZERO;
        if(minus) temp = "-" + temp;
        return new BigDecimal(temp);
    }

    /**
     * String을 받아 숫자(절대값)형태만 추출해서 반환. 음수라면 따로 판별 컬럼을 나누자.
     */
    public static double getDoubleValue(String str) {
        return Double.parseDouble(StringUtil.getNumericStr(str));
    }

    /**
     * String을 받아 숫자(절대값)형태만 추출해서 반환. 음수라면 따로 판별 컬럼을 나누자.
     */
    public static int getIntValue(String str) {
        return Integer.parseInt(StringUtil.getNumericStr(str));
    }

    /**
     * join시 디폴드값으로 ","를 준다.
     */
    /*
     * public static String join(List<?> list){ return join(list,","); }
     */

    /**
     * 배열을 seperators로 연결해서 반환한다. Weblogic 10.0/10.3에서 join사용(2.3이후버전)시 오류발생으로
     * 이것으로 대체
     */
    public static String joinTemp(List<?> list, String seperator) {
        StringBuffer stringBuffer = new StringBuffer();
        boolean first = true;
        for (Object string : list) {
            if (!first) stringBuffer.append(seperator);
            else first = false;
            stringBuffer.append(string);
        }
        return stringBuffer.toString();
    }

    /**
     * 배열을 seperators로 연결해서 반환한다. Weblogic 10.0/10.3에서 join사용(2.3이후버전)시 오류발생으로
     * 이것으로 대체
     */
    public static <T> String joinTemp(T[] list, String seperator) {
        StringBuffer stringBuffer = new StringBuffer();
        boolean first = true;
        for (Object string : list) {
            if (!first) stringBuffer.append(seperator);
            else first = false;
            stringBuffer.append(string);
        }
        return stringBuffer.toString();
    }
    
    /** '--'같은 문자열은 인코딩 변경시 바이트 코드가 변경?된다. 이를 확인하는 디버깅용 메소드 이다. */
    public static String getByteString(String line) {
    	StringBuilder b = new StringBuilder();
    	Latch l = new Latch();
    	for(byte each : line.getBytes()){
    		if(!l.next()) b.append("|"); 
    		b.append(each);
		}
    	return b.toString();
    }
    
    public static boolean isEmptyAny(String ... objs) {
    	for(String each : objs) if(isEmpty(each)) return true;
    	return false;
    }    
    
    /** txt에 비워드인 걸로 다 짤라서 몇개의 단어가 있는지 검사한다. 얼추 된다. ㅋㅋ */
    public static class WordCounter implements Iterable<Entry<String,Object>>{
    	
    	private boolean ignoreCase = false;
    	private RequestMap counter = new RequestMap();
    	
    	public void addCount(File file){
    		String txt = TextFileUtil.read(file).toString();
    		addCount(txt);
    	}
    	
    	/** 케이스 무시할 시 모두 소문자로 변경한다. */
    	public void addCount(String txt){
    		if(ignoreCase) txt = txt.toLowerCase();
    		String[] texts = txt.split("[^\\w]");
    		for(String each : texts){
    			if(each.equals("")) continue;
    			counter.plus(each);
    		}
    	}
    	
    	public void setIgnoreCase(boolean ignoreCase) {
			this.ignoreCase = ignoreCase;
		}

		public List<Entry<String,Object>> result(){
    		return counter.sortByValue(RequestMap.DESC);
    	}

		@Override
		public Iterator<Entry<String, Object>> iterator() {
			return result().iterator();
		}
		/** 디렉토리의 파일(txt,java 등)에서 단어를 추출/집계 후 가장 많이 사용된 순으로 정렬해서 리턴한다. */
		public static List<Entry<String,Object>> countDirectoryFile(File directory,String ... exts){
			WordCounter c = new WordCounter();
			Iterator<File> i = FileUtil.iterateFiles(directory,true,exts);
			while(i.hasNext()){
				File each = i.next();
				c.addCount(each);
			}
			return c.result();
		}
    }

    // ===========================================================================================
    //                                      NVL            
    // ===========================================================================================

    /**
     * null 또는 공백문자사를 처리 stripToEmpty을 대신한다.
     */
    public static String nvl(String str) {
        return nvl(str, EMPTY);
    }
    public static String nvlObject(Object obj,String escape) {
    	if(obj==null) return escape;
    	return nvl(obj.toString(), escape);
    }

    /**
     * null 또는 공백문자사를 처리 stripToEmpty을 대신한다.
     */
    public static String nvl(String str, String defaultStr) {
        return StringUtils.isEmpty(str) ? defaultStr : str.trim();
    }

    public static Integer nvl(Integer integer) {
        return (integer == null) ? 0 : integer;
    }

    public static Integer nvl(String str, Integer defaultint) {
        return StringUtils.isEmpty(str) ? defaultint : Integer.parseInt(str.trim());
    }

    /**
     * null safe한 toString
     */
    public static String toString(Object str) {
        if (str == null) return EMPTY;
        return str.toString();
    }

}
