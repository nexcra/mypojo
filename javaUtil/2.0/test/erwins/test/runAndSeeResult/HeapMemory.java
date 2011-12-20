
package erwins.test.runAndSeeResult;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import erwins.gsample.GBean;
import erwins.util.lib.SystemUtil;

public class HeapMemory {

    @Test
    public void test() {
        final long maxSize = 50000; //5만
    	String result = SystemUtil.memoryTest(new Runnable() {
            List<GBean> list = new ArrayList<GBean>();
            public void run() {
                //문자열 1개 10만개면 에 25Mb
                for (int i = 0; i < maxSize ; i++) {
                	GBean b = new GBean();
                    b.setName("책이름책이름책이름책이름책이름" + i);
                    b.setCount(i + 300);
                    list.add(b);
                }
            }
        });
        System.out.println(result);
    }
}
