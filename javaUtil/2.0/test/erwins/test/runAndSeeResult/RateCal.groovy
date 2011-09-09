package erwins.test.runAndSeeResult

import java.text.DecimalFormat

import org.junit.Test

import erwins.util.groovy.GroovyMetaUtil

/** 금리 계산 */
public class RateCal{
    
    @Test
    public void renameTo(){
		GroovyMetaUtil.number()
		
		println 300/12
		
		def 자본금  = 100000000.0
		def 연이룰 = 0.0395
		
		계산(자본금,20,연이룰);
    }
	
	public 계산(자본금,year,연이룰){
		def 연이자 = 자본금 * 연이룰
		println "자본금 = ${자본금.won()} / 연이울  $연이룰"
		println "연이자 = ${연이자.won()}"
		println "월이자 = ${(연이자/12).won()}"
		연금생활(자본금,20,연이룰)
	}
	
	public 복리계산(money,year,rate){
		year.times{ money += money*rate  }
		return money
	}
	
	public 연금생활(원금,year,rate){
		def 월급 = 200000.0
		while(true){
			def money = 원금
			year.times{
				money -= 월급*12;
				money += money*rate
			}
			if(money < 0) break;
			월급 = 월급 + 10000
		}  
		println "${year}년간 ${월급.won()}으로 연금생활 가능"
	}
    
    
}

