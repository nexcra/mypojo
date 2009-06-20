
package erwins.util.lib;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.PatternMatchUtils;

/**
 * RegEx
 */
public abstract class RegEx {
    
    /**
     * Spting의 simpleMatch
     */
    public static boolean simpleMatch(String pattern,String str) {
        return PatternMatchUtils.simpleMatch(pattern, str);
    }
    
    /**
     * E-mail인지? 
     */
    public static boolean isEmail(String email) {
        Pattern p = Pattern.compile("^(?:\\w+\\.?)*\\w+@(?:\\w+\\.)+\\w+$");
        Matcher m = p.matcher(email);
        return m.matches();
    }
    
    /**
     * 태그 잘라내기
     */
    public static String stripHTML(String htmlStr) {
        Pattern p = Pattern.compile("<(?:.|\\s)*?>");
        Matcher m = p.matcher(htmlStr);
        return m.replaceAll("");
    }    
    
    /**
     * 문자열을 치환하여 HTML링크를 만들어 준다.
     * 게시판 댓글 등에 사용 
     */
    public static String linkedText(String sText) {
        Pattern p = Pattern.compile(
                "(http|https|ftp)://[^\\s^\\.]+(\\.[^\\s^\\.]+)*");
        Matcher m = p.matcher(sText);

        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, 
                    "<a href='" + m.group()+"'>" + m.group() + "</a>");
        }
        m.appendTail(sb);

        return sb.toString();
    }      
    
    /**
     * 특정 단어 필터링. 
     */
    public static String filterText(String sText) {
        Pattern p = Pattern.compile("fuck|shit|개새끼", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(sText);

        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            //System.out.println(m.group());
            m.appendReplacement(sb, maskWord(m.group()));
        }
        m.appendTail(sb);
        
        //System.out.println(sb.toString());
        return sb.toString();
    } 
    
    /**
     * 단어에 *를 해주자.
     */
    public static String maskWord(String word) {
        StringBuffer buff = new StringBuffer();
        char[] ch = word.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            if (i < 1) {
                buff.append(ch[i]);
            } else {
                buff.append("*");
            }
        }
        return buff.toString();
    }    
    
    
    /*
     * 1. 숫자이외의 문자가 있는지
/^[0-9]+$/
2. 알파벳이외의 문자가 있는지
/^[a-zA-Z]+$/
3. 한글만 찾기
/[가-힝]/
4. 한글과 알파벳만 찾기
/[가-힝a-zA-Z]/
5. 한글만 있는지
/^[가-힝]*$/
6. 전화번호 체크
/^[0-9]{2,3}-[0-9]{3,4}-[0-9]{4}$/
7. 정확한 날짜인지 체크
/^[0-9]{4}-[0-9]{2}-[0-9]{2}$/
8. 도메인이 정확힌지 체크
/^[.a-zA-Z0-9-]+.[a-zA-Z]+$/
9. E-mail이 정확한지 체크
/^[_a-zA-Z0-9-]+@[._a-zA-Z0-9-]+\.[a-zA-Z]+$/
 * */
    
}