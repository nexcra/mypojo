
package erwins.test.domain;

import junit.framework.Assert;

import org.hibernate.criterion.Restrictions;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import erwins.domain.Code;
import erwins.domain.book.Book;
import erwins.util.exception.RoleNotFoundException;
import erwins.util.exception.Check;
import erwins.util.exception.Check.ExceptionRunnable;
import erwins.util.lib.RandomStringUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:dataSourceTest.xml", "classpath:config.xml" })
public class BookTest extends RootSptingTest {
	
	/** 추후 collection형태로 바뀌어야 한다. */
	private Book willReomve;
	
    @Test
    public void book() throws Exception {
        bookState();
        bookSearch();
        String bookName = RandomStringUtil.getRandomSring(10);
        bookSave(bookName);
        final Book book = bookFind(bookName);
        willReomve = book;
        testLogin();
        Check.isThrowException(new ExceptionRunnable() {
			@Override
			public void run() throws Exception {
				bookService.save(book);
			}
		}, RoleNotFoundException.class);
        
        Check.isThrowException(new ExceptionRunnable() {
			@Override
			public void run() throws Exception {
				bookService.delete(book.getId());
			}
		}, RoleNotFoundException.class);
        
		login();
    }
    
    /** 도중에 오류가 나더라도 픽스쳐를 해제할 수 있어야 한다. */
    @After
    public void remove() throws Exception {
    	bookDelete(willReomve);
    }
    
    protected void bookState() throws Exception {
    	initTest();
        req.setRequestURI("/flex_/book_/labelState.do");
        bookController.labelState(req, resp);
        ajax.assertResponse(resp);
        
        initTest();
        req.setRequestURI("/flex_/book_/gradeCount.do");
        bookController.gradeCount(req, resp);
        ajax.assertResponse(resp);    
    }
    
    protected void bookSearch() throws Exception {
    	initTest();
        req.setRequestURI("/flex_/book_.search.do");
        req.addParameter("pageNo", "1");
        req.addParameter("keyWord", "jav");
        req.addParameter("labels.id", "3106");
        req.addParameter("labels.id", "3109");
        bookController.search(req, resp);
        ajax.assertResponse(resp);
        
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
    }
    
    protected void bookSave(String bookName) throws Exception {
    	initTest();
        req.setRequestURI("/flex_/book_.save.do");
        req.addParameter("bookName", bookName);
        req.addParameter("labels.id", "3106");
        req.addParameter("labels.id", "3109");
        req.addParameter("writer", TEST_KEYWORD);
        req.addParameter("translator", "javaman");
        req.addParameter("publicationYear", "1234");
        req.addParameter("grade", "54");
        req.addParameter("posession", "202");
        req.addParameter("description", bookName + "테스트잘됨?");
        bookController.save(req, resp);
        ajax.assertResponse(resp);
    }
    
    protected Book bookFind(String bookName) {
        Book book = bookService.findUnique(Restrictions.eq("bookName", bookName));
        Assert.assertEquals(book.getDescription(), bookName + "테스트잘됨?");
        return book;
    }
    
    protected void bookDelete(Book book) throws Exception {
    	initTest();
        req.setRequestURI("/flex_/book_.remove.do");
        req.addParameter("id",book.getId().toString());
        req.addParameter("bookName",book.getBookName());
        bookController.delete(req,  resp);
        ajax.assertResponse(resp);
    }

}