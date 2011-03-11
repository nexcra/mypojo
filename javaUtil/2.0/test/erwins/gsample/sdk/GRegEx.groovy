package erwins.gsample.sdk


import org.junit.Test
import erwins.util.tools.*

/** Groovy의 정규표현식. */
public class GRegEx{
    
    /** 기본 */
    @Test
    public void regEx1(){
        def twister = 'she sells sea shells at the sea shore of seychelles' ;

        // =~는 매쳐를 리턴한다!!
        def finder = (twister =~ /s.a/) ;
        assert finder instanceof java.util.regex.Matcher ;

        // ==~ 는 풀매칭 실시한다!!
        def WORD = /\w+/;
        def matches = (twister ==~ /($WORD $WORD)*/);
        assert matches instanceof java.lang.Boolean ;
		assert ( twister ==~ /she sells sea shells at the sea ..... of seychelles/ ) == true;

        def wordsByX = twister.replaceAll(WORD, 'x');
        assert wordsByX == 'x x x x x x x x x x';
    }
    
    @Test
    public void regEx2(){
        
        def myFairStringy = 'The rain in Spain stays mainly in the plain!'

        //words that end with 'ain': \b\w*ain\b
        def BOUNDS = /\b/
        def rhyme = /$BOUNDS\w*ain$BOUNDS/
        
        //스트링의 기본메서드 사용
        def found = ''
        myFairStringy.eachMatch(rhyme) { match -> found += match + ' '};
        assert found == 'rain Spain plain '

        //매쳐를 사용
        found = ''
        (myFairStringy =~ rhyme).each { match -> found += match + ' '};
        assert found == 'rain Spain plain '

        def cloze = myFairStringy.replaceAll(rhyme){ it-'ain'+'___' };
        assert cloze == 'The r___ in Sp___ stays mainly in the pl___!'
    }
    
    @Test
    public void regEx3(){
        
        // ~연산자로 정규식을 만든다.  isCase 는 풀매칭을 말한다. 
        assert (~/..../).isCase('bear')

        switch('bear'){
            case ~/..../ : assert true; break
            default      : assert false
        }

        def beasts = ['bear','wolf','tiger','regex'];

		assert beasts.grep(~/..../) == ['bear','wolf'];
		assert beasts.grep(~/..../) == beasts.findAll{it==~/..../};
                     
    }
}
