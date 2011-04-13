
package erwins.jsample;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings("serial")
class CrazyCode{

    /**
     * 깔끔한 static 초기화
     */
    @SuppressWarnings("unused")
    private static final Set<String> VALID_CODES = new HashSet<String>() {{
        add("XZ13s");
        add("AB21/X");
        add("YYLEX");
        add("AR2D");
     }};
     
     /**
      * 한자의 공백문자(charCode값은 12288)와 일반 공백문자가 다르다. 하지만 구분하기 쉽지 않다.
      */
     @Test
     public void emptyText(){
         Assert.assertTrue("　".equals(" "));
     }
}
