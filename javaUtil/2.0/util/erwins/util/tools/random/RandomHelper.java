package erwins.util.tools.random;
import java.util.Date;
import java.util.Random;


/** 간단한 테스트용 랜덤제작기. */
@SuppressWarnings("serial")
public class RandomHelper  extends Random{
	
	/** 기존 500 -> 0~499 를 1~500으로 바꾼다.
	 * 0 이하가 입력되면 n을 리턴한다. */
	public int getInt(int n){
		if(n <= 0) return n;
		int next = nextInt(n); 
		return next + 1;
	}
	
	private static final int forRandomDate = 315532;
	
	/** 시작일과 연도를 입력하면 그 사이의 랜덤한 일자를 구해준다. */
	public Date getDate(Date date,int betweenYear){
		return new Date(date.getTime() + ( nextInt(forRandomDate) * 100000L * betweenYear ) );
	}
	

}
