package erwins.test.runAndSeeResult;
import org.junit.Test;

import erwins.util.tools.StopWatch;

public class SpeedOfDefaultAPI {

	/** 4~5배 이상의 속도차이가 난다. 그래봐야 1000개 해도 밀리세컨드 차이. */
	@Test
	public void t1() throws Exception {
		final String T = "테스트용문자열입니다. ㅇㅇㅇㅇ";
		System.out.println(StopWatch.load(new Runnable() {
			@Override
			public void run() {
				System.out.println("====");
			}
		}));
		System.out.println(StopWatch.load(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < 10000; i++)
					new String(T + i).getBytes();
			}
		}));
		System.out.println(StopWatch.load(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < 10000; i++) {
					String t = T + i;
					int size = t.length();
					byte[] bytebuf = new byte[size];
					for (int j = 0; j < size; j++) {
						bytebuf[j] = (byte) t.charAt(j);
					}
				}
			}
		}));

	}

}
