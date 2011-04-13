package erwins.gsample.grammar


import org.junit.Test

public class GCollection{
	
	@Test
	public void mapUse(){
		def map = [1:"hello", 2:"there"]
		for( e in map ){
		  assert e.value
		}
	}
	
	@Test
	public void stepUse(){
		def ss = 0;
		5.step(100,7){ss+=it} // 해석 => 5에서 시작하여 100까지 7씩 증가시키면서 루프
		assert ss == 707
		
		def sum=0.0;
		//이건 실수임
		0.step(Math.PI * 2 , Math.PI/2){ sum+= it; }
		assert sum == 15;
		def sum2=0.0;
		
		// 0.0으로 해야 빅데시말로 인정
		0.0.step(Math.PI*2,Math.PI/2){ sum+= it; }
		assert sum == 24.4247779607693796;
	}
	
	@Test
	public void map(){
		def x = 'a'
		assert ['x':1] == [x:1] //일반적으로 map의 key는 문자열로 취급
		assert ['a':1] == [(x):1] //구지 변수로 쓰려면 ()로 감싸야 한다.
		
		def myMap = [a:1, b:2, c:3]
		assert myMap.entrySet() instanceof Collection
		assert myMap.any   {entry -> entry.value > 2  }
		assert myMap.every {entry -> entry.key   < 'd'}
	}
	
	@Test
	public void list(){
		def list = ['a', 'b', 'c', 'b', 'd'];
		assert list[-1] == 'd'; //-1이면 맨 마지막게 선택
		assert list.count('b') == 2
		assert list.min() == 'a'
		assert list.max() == 'd'
		
		assert list.any{it > 'b'} == true
		assert list.every{it > 'b'} == false
		
		assert list.join('-') == 'a-b-c-b-d';
		//2개의 인자중 첫번째 인자가 inject되고 최후에 리턴된다. 
		assert list.inject('start'){sum,it -> sum += it} == 'startabcbd'
		assert list.sum() == 'abcbd'
		
		// 클로저로 true / false가 들어간다.  ==> grep과 동일?한듯.
		assert list.findAll{it > 'b'}.size() ==2
		
		//findAll과는 달리 클로저로 결과를 다시만든 후 배열 객체를 리턴한다.
		assert list.collect{it+'!'}.join() == 'a!b!c!b!d!'
		
		//collect의 두번째 사용방법.
		def list2 = ['a','b','c','d']
		def newList = []
		list.collect( newList ) { it.toUpperCase() }
		assert newList == ['A', 'B', 'C', 'B', 'D'] 
		
		def x = [1, 1, 1]
		assert [1]== x.unique(); //unique가 기본
		
		x += ['b'];
		x << 'c';
		assert x == [1, "b", "c"]; //기본연산자로 추가 가능.
		
		//List형의 인자로 범위지정 가능.
		def myList = ['a', 'b', 'c', 'd', 'e', 'f']
		assert myList[0..2]  == ['a', 'b', 'c']
		assert myList[0, 2, 4] == ['a', 'c', 'e']
		myList[0..2] = ['x', 'y', 'z']
		assert myList == ['x', 'y', 'z', 'd', 'e', 'f']
		myList[3..5] = []
		assert myList == ['x', 'y', 'z']
		myList[1..1] = ['y', '1', '2']
		assert myList == ['x', 'y', '1', '2', 'z']
		
	}
}
