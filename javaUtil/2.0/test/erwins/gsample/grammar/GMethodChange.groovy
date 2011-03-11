package erwins.gsample.grammar



import org.junit.Test
import erwins.util.tools.*

/** 동적으로 메소드 변경 가능. */
public class GMethodChange{

	/** 캐시  최적화가 아마 1.5 이후에 진행된듯하다. 1.5이전은 안해도 정상작동한다. */
    @Test
    public void search(){
        
        assert 1 == new SampleController().action();
        
        SampleDomain.metaClass.sample= {-> 2}
        assert 2 == new SampleController().action();
         
        
        SampleDomain.metaClass.sample={-> 3}
		assert 2 == new SampleController().action(); //캐시에 남아있기 때문에 여전히 2로 나온다.
		
		SampleDomain.metaClass = null //invalidate해 주자. 이러면 다시 클래스가 로드?된다. 
		SampleDomain.metaClass.sample={-> 3}
        assert 3 == new SampleController().action() //원하던 값인 3이 나온다.
   }
}

/** 자동으로 마지막 인자가 리턴된다. */
class SampleDomain{
	public sample() {1}
}
 
class SampleController{
	def action = { new SampleDomain().sample() }
}
 

