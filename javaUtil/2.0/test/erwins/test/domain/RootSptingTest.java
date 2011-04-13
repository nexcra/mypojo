
package erwins.test.domain;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import erwins.domain.Ajax;
import erwins.domain.Current;
import erwins.domain.DwrService;
import erwins.domain.board.BoardController;
import erwins.domain.board.BoardService;
import erwins.domain.book.BookController;
import erwins.domain.book.BookService;
import erwins.domain.file.FileController;
import erwins.domain.label.LabelController;
import erwins.domain.label.LabelService;
import erwins.domain.user.UserController;
import erwins.domain.user.UserService;

/**
 * 이놈은 테스트마다 개별 객체를 생성함으로 세션(쿠키)을 유지할려면 req는 static해야 한다. 
 * ThreadLocal에 생성되면 현재의request와는 무관하게 user가 남는다!. 테스트시 주의
 */
public abstract class RootSptingTest {
    
    protected static final String TEST_KEYWORD  = "testtest";
    
    @Autowired protected UserController userController;
    @Autowired protected BookController bookController;
    @Autowired protected LabelController labelController;
    @Autowired protected BoardController boardController;
    @Autowired protected FileController fileController;
    
    @Autowired protected BookService bookService;
    @Autowired protected DwrService dwrService;
    @Autowired protected BoardService boardService;
    @Autowired protected LabelService labelService;
    @Autowired protected UserService userService;
    
    @Autowired Ajax ajax;
    
    
    protected static MockHttpServletRequest req = new  MockHttpServletRequest();
    protected MockHttpServletResponse resp ;// = new MockHttpServletResponse();


    protected boolean needLogin() {
        return true;
    }
    
    
    @BeforeClass
    public static void setUpClass() throws Exception {
    	Current.saveUserToSession(req,null);
    }
    
    @Before
    public void setUp() throws Exception {
        initTest();
        if(needLogin() && !Current.isLogin()) login();
    }
    
    /** req로 전송 전에 반드시 호출해 주어야 한다.  session은 건드리지 않는다. */
    protected void initTest(){
        req.setMethod("POST");
        req.removeAllParameters();
        resp = new MockHttpServletResponse(); //resp.resetBuffer();
    }

    protected void login(){
    	initTest();
        req.setRequestURI("/user/login.login.do");
        req.addParameter("loginId", "erwins");
        req.addParameter("password", "245");
        userController.login(req, resp);
        Assert.assertTrue(Current.isLogin());
        ajax.assertResponse(resp);
    }
    
    protected void testLogin() throws Exception {
    	initTest();
    	req.setRequestURI("/user/login.login.do");
    	req.addParameter("loginId", "test");
    	req.addParameter("password", "245");
    	userController.login(req, resp);
    	Assert.assertTrue(Current.isLogin());
    	ajax.assertResponse(resp);
    }
    
    

}