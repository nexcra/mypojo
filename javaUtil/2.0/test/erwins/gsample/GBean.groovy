package erwins.gsample

import org.junit.Test
/** 샘플 테스트용 GBean */
public class GBean{
	
	String name;
	int count;
	
	String value = 'a';
	def getValue(){
		return 'b';
	}
	
	@Test
	/** getter/setter 를 명시적으로 지정 안해도 된다. */
	public void run(){
		GBean bean = new GBean();
		bean.setName('book bean');
		assert bean.getName() == 'book bean';
		assert bean.properties.name == 'book bean';
		assert bean.properties.metaClass.toString().contains('[class erwins.gsample.GBean]');
		assert bean.dump().contains('erwins.gsample.GBean@');
		
		assert bean['name'] == 'book bean' //map처럼 접근도 가능하다.
		assert bean.q1 == '정의되지 않음.'
		
		bean.q1 = 'qwe'; 
		assert bean.count == 1
		
		assert bean.value == 'b'
		assert bean.@value == 'a' //변수에 다이렉트 접근
		
	}
	
	/** dot 필드를 재정의해준다. null을 호출하면 대신 이놈이 호출된다. */
	Object get (String name){
		return '정의되지 않음.';
	}
	/** dot 필드 세터를 재정의한다. */
	void set(String name,Object obj){
		count++;
	}
	
	/** 
	 * 요거 그루비 클래스 로더에서 사용. 
	 * 보라!! 타입이 명시되어있지 않다! 
	 **/
	public sum( a , b){
		return a + b;
	}
	
	public getMemen(){
		return 'asd';
	}
}
