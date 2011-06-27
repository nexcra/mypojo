package erwins.test.runAndTakeResult



import static org.junit.Assert.*

import org.junit.Test

/** ER-WIN에서 엔티티정의서 뽑기
 * 먼저 ER-WIN의 report기능으로 도메인이 나와야 한다. */
class ERWinToXlsTest {

	@Test
	void test2(){
		ERWinToXls toXls = new ERWinToXls()
		toXls.add 'd:/SIN', { it['Table Name'].startsWith('META:') }
		toXls.write 'd:/result'
	}
}
