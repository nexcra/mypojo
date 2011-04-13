
package erwins.test.domain;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:dataSourceTest.xml", "classpath:config.xml" })
public class BookTest2 extends RootSptingTest {
	
	
    @Test
    public void bookSearch() {
    	initTest();
        req.setRequestURI("/flex_/book_.search.do");
        //req.addParameter("pageNo", "1");
        req.addParameter("bookName", "j");
        //req.addParameter("labels.id", "3106");
        //req.addParameter("labels.id", "3109");
        //bookController.search2(req, resp);
        ajax.assertResponse(resp);
        
        /*
        initTest();
        req.setRequestURI("/flex_/book_.search.do");
        req.addParameter("pageNo", "1");
        req.addParameter("labels.id", "3109");
        bookController.search(req, resp);
        ajax.assertResponse(resp);
        
        initTest();
        req.setRequestURI("/flex_/book_.search.do");
        req.addParameter("pageNo", "1");
        req.addParameter("keyWord", "jav");
        req.addParameter("posession", Code.connector.$$(2).get(0).getId().toString());
        bookController.search(req, resp);
        ajax.assertResponse(resp);
        */        
    }
    

}