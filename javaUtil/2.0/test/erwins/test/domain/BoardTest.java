
package erwins.test.domain;

import junit.framework.Assert;

import org.hibernate.criterion.Restrictions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import erwins.domain.board.Board;
import erwins.domain.enums.SomeType;
import erwins.util.lib.RandomStringUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:dataSourceTest.xml", "classpath:config.xml" })
public class BoardTest extends RootSptingTest {
	
	@Override
	protected boolean needLogin(){
        return false;
    }
	
	@Test
    public void board(){
    	boardSearch();  //미로그인 상태에서 서치.
    	login();
        boardSearch();
        String title = TEST_KEYWORD + RandomStringUtil.getRandomSring(10);
        boardSave(title);
        Board board = boardFind(title);
        boardComment(board.getId());
        boardUpdate(board.getId(),board.getTitle());
        boardDelete(board.getId());
    }

    protected void boardSearch(){
    	initTest();
        req.setRequestURI("/board.search.do");
        req.addParameter("pageNo", "1");
        req.addParameter("someType", "SERVER");
        req.addParameter("keyWord", "jak");
        boardController.search(req,resp);
        ajax.assertResponse(resp);
    }
    
    protected void boardSave(String title){
    	initTest();
        req.setRequestURI("/board.save.do");
        req.addParameter("title", title);
        req.addParameter("someType", SomeType.CLIENT.name());
        req.addParameter("contents",title+title);
        boardController.save(req,resp);
        ajax.assertResponse(resp);
    }
    
    protected Board boardFind(String title) {
        Board board = boardService.findUnique(Restrictions.eq("title", title));
        return board;
    }
    
    protected void boardUpdate(Integer id,String title){
    	initTest();
        req.setRequestURI("/board.save.do");
        req.addParameter("id", id.toString());
        req.addParameter("title", title+"2");
        req.addParameter("someType", SomeType.SMALL_TALK.name());
        req.addParameter("contents",title+title+"2");
        boardController.save(req,resp);
        ajax.assertResponse(resp);
        
        Board board = boardService.findByClick(id);
        
        Assert.assertEquals(board.getTitle(),  title+"2");
        Assert.assertEquals(board.getContent(),  title+title+"2");
    }

    protected void boardComment(Integer id){
        
        String comment = RandomStringUtil.getRandomSring(10);
        
        initTest();
        req.setRequestURI("/board.commentSave.do");
        req.addParameter("id", id.toString());
        req.addParameter("myComment", comment+"1");
        boardController.commentSave(req,resp);
        ajax.assertResponse(resp);
        
        initTest();
        req.setRequestURI("/board.commentSave.do");
        req.addParameter("id", id.toString());
        req.addParameter("myComment", comment+"2");
        boardController.commentSave(req,resp);
        ajax.assertResponse(resp);
        
        initTest();
        req.setRequestURI("/board.commentSave.do");
        req.addParameter("id", id.toString());
        req.addParameter("seq","1"); //2번을 수정한다.
        req.addParameter("myComment", comment+"3");
        boardController.commentSave(req,resp);
        ajax.assertResponse(resp);
        
        initTest();
        req.setRequestURI("/board.comment.remove.do");
        req.addParameter("id", id.toString());
        req.addParameter("seq","0");
        boardController.commentDelete(req,resp);
        ajax.assertResponse(resp);
        
        Board board = boardService.findByClick(id);
        
        Assert.assertEquals(board.getComments().size(),1);
        Assert.assertEquals(board.getComments().get(0).getMyComment(),comment+"3");
    }
    
    protected void boardDelete(Integer id){
    	initTest();
        req.setRequestURI("/board.commentSave.do");
        req.addParameter("id", id.toString());
        boardController.delete(req, resp);
        ajax.assertResponse(resp);
    }
    



}