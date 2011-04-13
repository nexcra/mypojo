
package erwins.test.domain;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:dataSourceTest.xml", "classpath:config.xml" })
public class _NoLoginTest extends RootSptingTest  {

	@Test
    public void jsonDi() throws Exception {
		//System.out.println("===");
		System.out.println(bookService.test().getBookName());
	}

}
