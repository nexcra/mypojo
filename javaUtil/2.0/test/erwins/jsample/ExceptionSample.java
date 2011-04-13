
package erwins.jsample;

import org.junit.Test;

/**
 * 예외는 연계처리 된다.
 * ex) throw new RuntimeException(e);
 * ex2) exception.initCause(new Throwable("asd"));
 */
class ExceptionSample {

    @Test
    public void Strings(){
        try {
            error1();
            error2();
        }
        catch (Exc e) {
            System.out.println(e);
        }        
        catch (RunExc e) {
            System.out.println(e);
        }
        catch (RuntimeException e) {
            e.printStackTrace();
            System.out.println(e);
        }           
        catch (Exception e) {
            System.out.println(e);
        }
    }

    public void error1() throws Exception{
        try {
            Exception exception = new Exception("exㅋㅋㅋㅋ 원본에러");
            exception.initCause(new Throwable("asd"));
            exception.printStackTrace();
            throw exception;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void error2(){
        throw new RunExc("login");
    }
    @SuppressWarnings("serial")
    private static class Exc extends Exception{
        @SuppressWarnings("unused")
		Exc(String str){
            super(str);
        }
    }
    @SuppressWarnings("serial")
    private static class RunExc extends RuntimeException{
        RunExc(String str){
            super(str);
        }
    }
    
    
    

}
