package erwins.gsample.dsl

import java.math.RoundingMode;

import org.junit.Test;


public class Quiz{
	
	/** 학생 50명인 학급이 있다. 이 안에 생일이 같은 사람이 1쌍이라도 있을 확율은 얼마일까? */
	@Test
	void test(){
		def allSame = [1..50]*.inject(1.0){ sum , it -> sum *= (365-it)/365 }[0]
		def rate = (( 1 - allSame  ) * 100 ).setScale(2,RoundingMode.HALF_DOWN)
		println "$rate %"
	}
}

