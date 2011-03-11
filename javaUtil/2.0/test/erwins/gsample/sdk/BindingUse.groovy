package erwins.gsample.sdk



import org.junit.Test
import groovy.lang.GroovyShell
/** evaluate는 실행할때마다 컴파일 한다. 애네들은 Java에서도 사용 가능하다. */
public class BindingUse{
    
    /** eval에 바인인딩까지 가능하다. */
    @Test
    public void binding(){
        def binding = new Binding();
        binding.mass = 22.3;
        binding.velocity = 10.6;
        
        def shell = new GroovyShell(binding);
        def excression = "mass * velocity ** 2 / 2";
        assert shell.evaluate(excression) == 1252.814;
        
        binding.setVariable("mass",25.4);  //객체임으로 도중에 수정해도 된다. binding.mass = 25.4;와 동일.
        assert shell.evaluate(excression) == 1426.972;
        
        binding.mul = {a,b -> a*b}; // script에 클로저 삽입도 가능하다.
        assert shell.evaluate("mul(3,5)") == 15;
		
		/** 동적으로 자바 계산식을 계산 가능. 중간의 변수는 DB에서 가져올수도 있다. */
		def ex = '''
			def a = 10
			def b = a * 2
			File f = new File("");
			return f.getAbsolutePath() + mass
		'''
		assert shell.evaluate(ex).endsWith('25.4') == true
    }
    
    /** 바인딩 중간에 생성되는 변수도 접근 가능하다.
     * def를 사용하여 정의했다면 지역변수가 됨으로 binding으로 꺼내지 못한다. */
    @Test
    public void binding2(){
        def binding = new Binding(x:6,y:4);
        def shell = new GroovyShell(binding);
        shell.evaluate('''
                xx = x * x;
                yy = y * y * y;
                '''
        );
        assert binding.getVariable("xx") == 36;
        assert binding.yy == 64;
    }
    
    /** 한번만 컴파일하고 재사용 가능한 스크립트 */
    @Test
    public void binding3(){
        def shell = new GroovyShell();
        def script = shell.parse("mass * velocity ** 2 / 2");
        script.binding.mass = 22.3;
        script.binding.velocity = 10.6;
        assert script.run() == 1252.814;
        
        script.binding.setVariable("mass",25.4);
        assert script.run() == 1426.972;
    }
    
}

