package erwins.jsample;

import java.math.BigDecimal;

import junit.framework.Assert;

import org.junit.Test;

import erwins.domain.book.Book;

@SuppressWarnings(value={ "cast","null"})
public class Reference{

    /**
     * 래퍼런스 null의 특징
     * 불변 객체이든 아니든 객체는 래퍼런스임으로 null을 할당해도 
     * 다른 참조가 있으면 GC되지 않는다. 주의
     */
    @Test
    public void instanceod(){
        String qwe = null;
        Assert.assertFalse(qwe instanceof String);
    }
    
    /** 배열은 불변객체가 아니다. 주의! clone을 명시적으로 사용해 주어야 한다. */
    @Test
    public void array(){
        String[] qwe = new String[]{"1","2"};
        ArrayTest asd = new  ArrayTest();
        asd.setTemp(qwe);
        asd.setTempClone(qwe);
        qwe[1] = "qweqwe";
        Assert.assertEquals(asd.getTemp()[1], "qweqwe");
        Assert.assertEquals(asd.getTempClone()[1], "2");
    }
    
    /**
     * 래퍼런스 null의 특징
     * 불변 객체이든 아니든 객체는 래퍼런스임으로 null을 할당해도 
     * 다른 참조가 있으면 GC되지 않는다. 주의
     */
    @Test
    public void immutableNull(){
        BigDecimal a= new BigDecimal("123");
        BigDecimal b= a;
        a=null;
        Assert.assertTrue(b != null);
        
        Book A = new Book();
        Book B = A;
        A = null;
        Assert.assertTrue(B!=null);
    }
    
    /**
     * 불변 클래스의 특징. Integer, Long등도 역시 불변 클래스이다!
     * 내부 멤버필드를 바꿀 수 있는 방법이 없다!
     * +등의 계산시 새로운 객체가 생성됨으로 기존 래퍼런스는 남게 된다.
     */
    @Test
    public void immutable(){
        BigDecimal a= new BigDecimal("123");
        BigDecimal b= a;
        a= a.add(new BigDecimal("123"));
        Assert.assertTrue(a != b);
        
        Book A = new Book();
        Book B = A;
        A.setId(50);
        B.setId(100);
        Assert.assertTrue(B==A);
    }
    
    /**
     * 메소드에는 복제된 레퍼런스가 넘어감으로 당연히 안된다. 
     */
    @Test
    public void String(){
        String aa = "1111";
        change(aa);
        Assert.assertTrue("1111".equals(aa));
    }
    
    /**
     * 메소드에는 복제된 레퍼런스가 넘어감으로 당연히 안된다2.
     */
    @Test
    public void Decimal(){
        BigDecimal aa = new BigDecimal("1111");
        change(aa);
        Assert.assertTrue(new BigDecimal("1111").equals(aa));
    }
    
    /**
     * 당근 됨..
     */
    @Test
    public void Obj(){
        Rf aa = new Rf("1111");
        change(aa);
        Assert.assertTrue(!aa.value.equals("1111"));
    }
    /**
     * 당연 안됨
     */
    @Test
    public void Obj2(){
        Rf aa = new Rf("1111");
        changeReference(aa);
        Assert.assertTrue(aa.value.equals("1111"));
    }
    
    
    
    public void change(String str){
        str = "2222";
    }
    public void change(BigDecimal str){
        str.add(new BigDecimal("2222") );
    }
    
    public void change(Rf str){
        str.value = "2222";
    }
    public void changeReference(Rf str){
        str = new Rf("2222");
    }
    
    /** 
     * 테스트용 가변 클래스.
     * */
    private static class Rf{
        public String value;
        public Rf(String str){
            value = str;
        }
    }
    
    private static class ArrayTest{
        private String[] temp;
        private String[] tempClone;

        public String[] getTemp() {
            return temp;
        }

        public void setTemp(String[] temp) {
            this.temp = temp;
        }

        public String[] getTempClone() {
            return tempClone;
        }

        public void setTempClone(String[] tempClone) {
            this.tempClone = tempClone.clone();
        }
        
        
        
    }

    
    
    
    
    
    
    
    

}
