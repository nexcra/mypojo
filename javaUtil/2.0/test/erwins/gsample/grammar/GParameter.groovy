package erwins.gsample.grammar


import org.junit.Test

public class GParameter{
    
    /** inject를 이용한 누적값 계산 */
    def sumWithList( List args){
        return args.inject(0){sum,i->sum+=i};
    }
    
    /** map을 이용한 named 파라메터 */
    def sumNamed(Map args){
		// args.get(it,4) => get의 값이 null일 경우 디폴트 초기화
        ['a','b','c'].each{args.get(it,4)} 
        return args.a+ args.b + args.c;
    }
    
    /** 가변인자 역시 가능. */
    def optional(a,Object[] args){
        return a + sumWithList(args.toList());
    }
    
    @Test
    public void parameter(){
        def obj = new GParameter();
        assert obj.sumWithList([1,2,3]) == 6;
        
        assert obj.sumNamed(a:1,b:1,c:5) == 7;
        assert obj.sumNamed(a:1) == 9;
        
        assert obj.optional(2,5,'7') == '257'; //하나라도 문자이면 문자열 더하기가 된다.
        assert obj.optional(2,5,7) == 14;
        assert obj.optional(2,5,7,6) == 20;
        
        String temp = 'opti'+'ona'; //다이나믹 메소드도 사용 가능.
        assert obj."${temp}l"(2,5,7,6) == 20;
    }
    
	/** 초기화메소드가 없어도, 또는 초기화를 아에 지정하지 않아도 정상작동 */
    @Test
    public void creation(){
        def v = new Vender(name: 'andro'); //네임드 파라메터 생성자.
        assert v.getName() == 'andro';
        Vender2 v3 = ['oldman',"nono"];  //묵시적 생성.
        assert v3.getName() == 'oldman'; 
    }
}

class Vender{
    String name,product;
}
class Vender2{
    String name,product;
    public Vender2(name,product){
        this.name = name;
        this.product = product;
    }
}
