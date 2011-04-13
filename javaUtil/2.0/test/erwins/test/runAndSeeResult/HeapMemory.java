
package erwins.test.runAndSeeResult;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import erwins.domain.SystemInfo;
import erwins.domain.book.Book;

public class HeapMemory {

    @Test
    public void test() {
        String result = SystemInfo.memoryTest(new Runnable() {
            List<Book> list = new ArrayList<Book>();
            public void run() {
                //문자열 1개 10만개면 에 25Mb
                for (int i = 0; i < 100000; i++) {
                    Book b = new Book();
                    b.setBookName("책이름책이름책이름책이름책이름" + i);
                    b.setDescription("sdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasd" + i);
                    b.setGrade(new BigDecimal(i + 300));
                    list.add(b);
                }
            }
        });
        System.out.println(result);

    }
}
