
package erwins.util.lib;

import java.util.List;

import org.junit.Test;

import com.sun.syndication.feed.synd.SyndFeed;

import erwins.util.exception.runtime.Val;
import erwins.util.tools.StopWatch;

public class RssTest {

    @Test
    public void testInsertBoard() throws  Exception {
        List<SyndFeed> feeds = Rss.DEV_OKJSP.get();
        for(SyndFeed feed : feeds){
            Val.isNotEmpty(Rss.makeTable(feed));
        }
    }
    
    //@Test
    public void speed() throws  Exception {
        StopWatch.checkMe("okjsp");
        Rss.getFeeds("http://www.okjsp.pe.kr/rss/okjsp-rss2.jsp?bbs=lifeqna");
        StopWatch.checkMe("okjsp2");
        Rss.getFeeds("http://www.okjsp.pe.kr/rss/okjsp-rss2.jsp?bbs=howmuch");
        StopWatch.checkMe("자바캔");
        Rss.getFeeds("http://javacan.tistory.com/rss");
        StopWatch.checkMe("토비");
        Rss.getFeeds("http://toby.epril.com/?feed=rss2");
        StopWatch.checkMe("권남");
        Rss.getFeeds("http://rss.egloos.com/blog/kwon37xi");
        StopWatch.checkMe("권남_spring");
        Rss.getFeeds("http://kwon37xi.springnote.com/pages.rss");
        System.out.println(StopWatch.stopMe());
    }
}
