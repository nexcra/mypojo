
package erwins.util.lib;

import java.io.*;
import java.net.URLEncoder;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

/**
 * StringEscapeUtils를 확장한 추가 인코더
 * @author erwins(my.pojo@gmail.com)
 */
public abstract class Encoders extends StringEscapeUtils {

    private static Logger log = Logger.getLogger(Encoders.class);

    private static final String CAUSED_BY = "CAUSED BY";
    private static final String CAUSE = "CAUSE";

    // ===========================================================================================
    //                                 기본설정
    // ===========================================================================================
    private static String[] javaScriptescapes = new String[96];
    private static String[] htmlEscapes = new String[64];

    static {
        javaScriptescapes['\''] = "\\'";
        javaScriptescapes['"'] = "\\\"";
        javaScriptescapes['\\'] = "\\\\";
        javaScriptescapes['\r'] = "\\r";
        javaScriptescapes['\n'] = "\\n";

        htmlEscapes['<'] = "&lt;";
        htmlEscapes['>'] = "&gt;";
        htmlEscapes['&'] = "&amp;";
    }

    /**
     * javascript XML을 둘다 이스케이프 해준다. Flex에서 JSON을 사용할때 적용하면 된다. 특히 일반문자열을 json으로
     * 변환할려고 할때 사용한다. 원천적으로 데이터에는 '<>'등의 특수문자를 허용하면 안된다.
     * 음.. 이 구분은 경험치로 구한 것이다... 제길  
     */
    public static String escapeFlex(String str) {
        //return escapeXml(escapeJavaScript(str));
        return escapeXml(str.replaceAll("\"", "'"));  //replaceAll이 먼지 기억이 안난다... ㄷㄷ
    }
    
    /** \는 제외. */
    private static final String[] REG_EX = {"*","?","+","[","]","(",")","{","}","^","$",".","<","|"}; 

    /**
     * 일단 임시제작. *, ?, +, [, {, (, }, ^, $ => 추후 제작.
     */
    public static String escapeRegEx(String str) {
        str = str.replaceAll("\\\\", "\\\\\\\\");
        for(String each : REG_EX){
            str = str.replaceAll("\\"+each, "\\\\"+each);
        }
        //return escapeXml(escapeJavaScript(str));
        return str;
    }

    /**
     * 에러표시가 부담스럽다면..
     */
    public static void stackTrace(Throwable throwable) {
        stackTraceToStr(throwable);
        StringWriter stringWriter = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stringWriter));
        getRootCause(stringWriter.toString());
    }

    /**
     * 에러 메세지를 필터링하여 일정 개수만 보여준다.
     */
    private static void stackTraceToStr(Throwable e) {
        log.error(" ====== EXCEPTION ======");
        StackTraceElement[] elements = e.getStackTrace();
        int count = 0;
        for (StackTraceElement element : elements) {
            count++;
            if (count > 3) break;
            log.error(element.getClassName() + ".class >  " + element.getMethodName() + "()");
        }
    }

    /**
     * CAUSED_BY를 출력한다. 첫번째 라인만 출력한다.
     */
    private static void getRootCause(String stackTrace) {
        if (stackTrace.toUpperCase().lastIndexOf(CAUSED_BY) > 1) {
            stackTrace = stackTrace.substring(stackTrace.toUpperCase().lastIndexOf(CAUSED_BY) + 10);
        } else if (stackTrace.toUpperCase().lastIndexOf(CAUSE) > 1) {
            stackTrace = stackTrace.substring(stackTrace.toUpperCase().lastIndexOf(CAUSE) + 6);
        }
        log.error(" ====== CAUSED_BY ======");
        log.error(Strings.getFirst(stackTrace, "\n"));
    }

    public static String escapeUrl(String str) {
        return escapeUrl(str,CharSets.UTF_8);
    }
    public static String escapeUrl(String str,String encode) {
        try {
            return URLEncoder.encode(str, encode);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 자바소스에서 스크립트 생성후 HTML에 붙여넣기 전에 적용할것. StringEscapeUtils의 escapeJavaScript와는
     * 달리 한글은 변형되지 않음.
     */
    public static String escapeJavaScript2(String str) {
        if (str == null) return null;
        str = Strings.nvl(str);
        StringBuffer escapedString = new StringBuffer();
        char[] chars = str.toCharArray();
        int i = 0, j = 0;

        try {
            do {
                char c = chars[i++];
                String escape = (c < 96) ? javaScriptescapes[c] : null;
                if (escape != null) {
                    escapedString.append(chars, j, i - j - 1).append(escape);
                    j = i;
                }
            } while (true);
        }
        catch (IndexOutOfBoundsException ex) {
            escapedString.append(chars, j, chars.length - j);
        }
        return escapedString.toString();
    }

    /**
     * HTML 태그 인식 방지용. HTML태그가 적용되지 않고 그대로 출력됨. StringEscapeUtils.escapeHtml 와는
     * 달리 한글 변형 안된다.
     */
    public static String escapeXml2(String str) {
        str = Strings.nvl(str);
        StringBuffer escapedString = new StringBuffer();
        char[] chars = str.toCharArray();
        int i = 0, j = 0;

        try {
            do {
                char c = chars[i++];
                String escape = (c < 64) ? htmlEscapes[c] : null;
                if (escape != null) {
                    escapedString.append(chars, j, i - j - 1).append(escape);
                    j = i;
                }
            } while (true);
        }
        catch (IndexOutOfBoundsException ex) {
            escapedString.append(chars, j, chars.length - j);
        }
        return escapedString.toString();
    }

    /**
     * TextArea에서 입력받은 구문을 JSP상에서 HTML로 그대로 찍어줄때 사용한다. 개행 구문과 검색조건 강조의 기능이 있다.
     * 흠녀... 대소문자 구분 지랄같네.. ㅠㅠ 검색패키지 또는 파싱법 새로 도입하자.
     */
    public static String toHtmlFromTextArea(String str, String searchKey) {
        String temp = str.replaceAll("\n", "<br>");
        return searchKey == null ? temp : temp.replaceAll(searchKey, "<span style='color:red;font-style:italic;'>" + searchKey + "</span>");
    }

}