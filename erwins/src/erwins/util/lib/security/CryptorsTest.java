
package erwins.util.lib.security;

import org.junit.Test;

import erwins.util.exception.Val;
import erwins.util.lib.Strings;

public class CryptorsTest{

    @Test
    public void decrypt(){
        String org  = "qweqwe";
        String encrypted = Cryptors.encrypt(org,"my.pojo.keys@hotmail.com");
        String decrypted =  Cryptors.decrypt(encrypted, "my.pojo.keys@hotmail.com");
        Val.isTrue(org.equals(decrypted));
    }
    
    /**
     * 특수 문자의 경우 기본 제공되는 getByte()가 변형되어서 나온다. 실무에 쓰기는 힘들겠지만 참고할것
     */
    @Test    
    public void getByte(){
        byte[] aa = "123￵ﾗ￈￼ﾞﾠS ".getBytes();
        byte[] bb = Strings.getByte("123￵ﾗ￈￼ﾞﾠS ");
        
        int sumA = 0;
        int sumB = 0;
        for (byte a : aa) sumA += a;        
        for (byte b : bb) sumB += b;
        
        Val.isTrue(!aa.equals(bb));
        Val.isTrue(sumA != sumB);
    }

}
