package erwins.gsample.grammar


import org.junit.Test
import erwins.util.lib.FileUtil;
import erwins.util.tools.*
import groovy.time.TimeCategory;

/** Groovy의 문법들. */
public class GGrammer{
    
    /**  기본이 되는 GString. String에 부가기능이 달린 객체이다. {}안에 자바연산자가 들어간다. */
    @Test
    public void gstring(){
        def name = "철수"
		def text = "내이름은 $name 입니다"
		assert name instanceof String
		assert text instanceof GString
		assert text == "내이름은 철수 입니다"
		
		def foxtype = 'quick'
		def foxcolor = ['b', 'r', 'o', 'w', 'n']
		assert "The $foxtype ${foxcolor.join()} fox" == "The quick brown fox"
		
		def x = "It is currently ${ new Date() }"
		assert x.values[0] instanceof Date
		def y = "It is currently ${ writer -> writer << new Date() }"
		assert y.values[0] instanceof Closure
		
		def deepest = {-> "deepest"}
		def deep = {-> "deeper and $deepest"}
		assert "how deep is deep? $deep" == "how deep is deep? deeper and deepest"
    }
	
	/** 마지막 인자로 클로저가 들어가면  그 클로저를 외부로 뺄 수 있다. 매우 유용한 특성이다. */
	def sample =  {a,b -> b(a)  }
	@Test
	public void last(){
		assert sample('a', { return it} ) == 'a'
		assert sample('a') {return it} == 'a'
	}
	
	/** 멤버필드로 클로저를 담을 수 있다. 이런 형식은 지역변수로 등록이 불가능하다. (지역변수는 걍 {}로 등록)
	 * 클로저 안에 다시 클로저가 들어갈 수 있다.  */
	def localMethod() {
		def localVariable = new java.util.Date()
		def nestedClos = {
			assert owner.class.name
		}
		nestedClos();
		return { return localVariable }
	}
	@Test
	public void innerMethod(){
		  def clos = localMethod()
		  clos()
	}
	
    /** 
     * ?연산자로 null피하기. ?가 nul을 만나면 즉시 null이 리턴됨.
     * toString시 예외 대신 null문자열이 리턴된다.  
     * */
    @Test
    public void nullException(){
    	def map = [a:[b:[c:1]]];
    	assert map.a.b.c == 1;
    	assert map?.a?.x?.c == null;
    	
    	//assert map?.toString() == '{a={b={c=1}}}';
    	assert map?.toString() == '[a:[b:[c:1]]]'; //Groovy 버전이 1.5에서 1.7대로 넘어가면서 변화가생김.
    	map = null;
    	assert map?.toString() == null;
    	assert map.toString() != null;
    	assert map.toString() == 'null';
    }
    
    /** 
     * Expando를 이용하여 클로저를 변수처럼 사용 가능하다.
     * but Groovy에서만 사용 가능하며 자료형은 지원하지 않는다.
     * (Map을 사용해도 비슷하게 됨.. 왜 이런걸 만들었지? */
    @Test
    public void expando(){
        def boxer = new Expando();
        boxer.grr = 'grr!';
        boxer.crazy = {times -> return grr * times};
        assert 'grr!grr!grr!' ==  boxer.crazy(3);
		
		def map = [a:'zzz'];
		map.b = {it*3}
		assert map.b('qq') == 'qqqqqq'
		assert map.b(3) == 9
    }
    
    /** 다양한 range를 제공한다. */
    @Test
    public void range(){
        def store = '';
        10.times{store += 'x'}
        assert store == 'xxxxxxxxxx'

        store = ''
        1.upto(5) { number ->store += number}
        assert store == '12345'

        store = ''
        2.downto(-2) { number -> store += number + ' '}
        assert store == '2 1 0 -1 -2 '

        store = ''
        0.step(0.5, 0.1 ){ number -> store += number + ' '}
        assert store == '0 0.1 0.2 0.3 0.4 '
        
        def today     = new Date();
        def yesterday = today-1 ;  //Date형의 기본연산은 일단위 이다.
        assert (yesterday..today).size() == 2 ; 
        
		//이게 쓸만해 보임.
        def age = 36;
        switch(age){
            case 16..20 : assert false; break;
            case 21..50 : assert true; break;
            case 51..65 : assert false; break;
            default: throw new IllegalArgumentException();
        }
    }
	
    /** '*'를 쓰면 배열을 강제로 펼친다. Map도 가능 (확산 연산자) */
    @Test
    public void spreadOperation(){
		def i=0 ;
		[1..5]*.each{i += it}  //펼치면 정상적인 List가 된다.
		assert i == 15;
		[1..5].each{assert it+1 == [1, 2, 3, 4, 5, 1]} //범위형을 직접 each하면 객체다. 즉 1회만 each된다.
		
        def range = (1..3);
        assert [0,1,2,3] == [0,*range];
        def map = [a:1,b:2];
        assert [a:1,b:2,c:3] == [c:3,*:map]; //map은 *:map이다.
		
		//개념을 확실히 알아두자. (아래는 collect로도 가능)
		assert [1, 3, 5] == ['a', 'few', 'words']*.size()
    }
	
	/**
	* 1.6에서 추가. 이제 리턴 인자를 2개 이상 받을 수 있다. ㅋ 개굿~
	* ex) def (lat, long) = geocode("Paris, France");
	**/
   @Test
   public void multipleArgs(){
	   def (a, b) = [1, 2]
	   assert a == 1
	   assert b == 2
	   
	   def elements = [1, 2]
	   def (d, e, f) = elements
	   assert d == 1
	   assert e == 2
	   assert f == null
   }
   
   /**   finally구문은 절대 리턴되지 않음을 주의 */
   @Test
   public void autoReturn(){
	   def method = { bool->
		   try {
			   if (bool) throw new Exception("foo")
			   1
		   } catch(e) {
			   2
		   } finally {
			   3
		   }
	   }
	   assert method(false) == 1;
	   assert method(true) == 2;
   }
   
   /** 입맛대로 오버라이딩이 가능하다.  */
   @Test
   public void over(){
	   def aMap = [:] ;
	   aMap.getMetaClass().putAt << {String name, value-> aMap[999] = '테스트1'}
	   aMap.getMetaClass().putAt << {List names, Map values-> aMap[999] = '테스트2'}
	   
	   aMap["n"] = "v" //오버라이딩한 String을 적용
	   assert aMap[999] == '테스트1';
	   assert aMap['n'] == null;
	   
	   aMap[5] = "v" //오버라이딩 안한 Int형은 정상작동
	   assert aMap[5] == 'v'
	   
	   aMap["n", "n2"] = [n:"2", n2:"3"] //오버라이딩 적용
	   assert aMap["n", "n2"] == null
	   assert aMap[999] == '테스트2';
   }
   
   //@Bindable로 Swing등에서 Flex처럼 자동 바인딩이 가능.
   //@Grab(group = 'org.mortbay.jetty', module = 'jetty-embedded', version = '6.1.0')  maven등에서 다운로드 해서 캐시에 저장된다?
   
   
   /* 이건 안되네..  final키워드가 내부객체라 그런가?  나중에 테스트   http://groovy.codehaus.org/Immutable+AST+Macro
   @Immutable
   public final class Coordinates3 {
	   def latitude, longitude
   }
   def c1 = new Coordinates3(latitude: 48.824068, longitude: 2.531733)
   def c2 = new Coordinates3(48.824068, 2.531733)
   
   @Newify([Coordinates, Path]) //애도 나중에 실험. new연산자 생략.
   def build = {
	   Path(
		   Coordinates(48.824068, 2.531733),
		   Coordinates(48.857840, 2.347212),
		   Coordinates(48.858429, 2.342622)
	   )
   }
   
		   */
    
}
