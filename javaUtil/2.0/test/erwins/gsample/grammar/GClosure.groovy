package erwins.gsample.grammar


import org.junit.Test
import groovy.lang.Closure
public class GClosure{
    
    /** 인자가 1개인 Closure에 반복자를 주어 실행한다.  */
    public int runTest(repeat,Closure worker = null){
        assert worker.getParameterTypes().size() == 1;  //동적으로 파라메터도 알 수 있다.
        repeat.times{worker(it)}
        return 50
    }
    
    /** 
     * ECMA스크립트처럼 속성(멤버필드)을 가진 동적클로저를 리턴할 수 있다.   
     * 누산기 : 여기서 최초값으로 초기호되며 n은 처럼 행동한다(command객체임으로)
     * 아래는 스크립트의 var fun:function = function():function{ return  ~~ } 와 동일. 
     * 즉! 이 function은 새로운 객체 정의(protected class foo{ ~~ })와 동등한 의미이다. 
     * */
    def foo(n){
		//def n = 0;  대충 이런 의미.
		return { n += it}  
	}
    
    /** ECMA스크립트의 용법과 동일하다. */
    @Test
    public void closure(){
        //기본
        def adder = {x,y -> return x+y };
        assert adder(2,4) == 6 ;
        
        //커리 사용 : 말그대로 기본 들어가는 반찬..
        def adder2 = adder.curry(2);  
        assert adder2(4) == 6 ;
        
        //클로저 생성. 이게 이해가 안되면 문제가 있다.
        def a = foo(1);
        assert a(1) == 2;
        assert a(4) == 6;
        assert a(2) == 8;
        
        //메소드 내에서도 선언이 가능하다.
        def qqq = {q,w->return { q=q+1; w=w+1; q+w+it};}
        def temp = qqq(3,4);
        assert temp(1) == 10;
        assert temp(2) == 13;
        assert temp(10) == 23;
        
        int i = 0;
        assert runTest(100,{ i+= it}) == 50;
    }
    
	/** 클로저를 감싸서 쉬운 예외처리가 가능하다. => 이건 좀 ㅄ인듯 */
	@Test
	void sample(){
	    def quiet = {
    		try{
    			it.call()
    		}catch(Exception e){
				assert e.message == "sample"
    		}
	    }
		quiet{ throw new RuntimeException("sample") }
	}
	
	/**
	 * 객체가 있을때만 사용 가능한 메소드 클로저. (.&로 표기)
	 * 객체에 있는 메소드를 클로저로 분리/사용할 수 있다.
	 */
    @Test
    public void methodClosure(){
        def MethodClosureSample first = new MethodClosureSample (6);
        def MethodClosureSample second = new MethodClosureSample (5);
        def Closure firstClosure = first.&validate
        assert  [1,2,3].collect(firstClosure).join(',') == '12,24,36' //collect : 어레이에 클로저 적용후 다시 컬렉션화
        assert [1,2,3].collect(second.&validate).join(',') == '10,20,30'
        
        /** 오버로딩 인식. */
        def MultiMethodSample instance = new MultiMethodSample()
        def Closure multi = instance.&mysteryMethod
        assert 10 == multi ('string arg')
        assert 3 == multi (['list', 'of', 'values'])
        assert 14 == multi (6, 8)
        
		// ============ 어려움 =============== 
        // 메소드 클로저의 배열화! 중요!!  일반적으로 반복되는 배열을 collect하지만 요기서는 클로저를 collect해보자.
		// 특이하게, qq에 클로저가 파라메터로 들어가야 하지만,이는 대신에 collect에 들어간 클로저로 대체된다. 
		// 내부적으로 클로저는 여러번 호출됨으로 배열과 같은 효과를 지닌다.
        assert this.&domain.collect{it*2} == [4, 6, 8, 10, 12] ;
    }
	
	/** 이 메소드는 클로저화 되어 collect될 수 있다.
	 * 아래는 [1..5]*.each{ c(it+1) }와 동일한 표현이다. */
	def domain(c){
	    [1..5]*.each{c it+1};  
	}
}


/** 클로저 사용지 자동으로 오버로딩을 인식한다. */
class MultiMethodSample {
    int mysteryMethod (String value) {
        return value.length()
    }
    int mysteryMethod ( List list) {
        return list.size()
    }
    int mysteryMethod (int x, int y) {
        return x+y
    }
}

class MethodClosureSample {
    int limit
    MethodClosureSample (int limit) {
        this.limit = limit
    }
    int validate (int value) {
        return value * limit * 2;
    }
}
