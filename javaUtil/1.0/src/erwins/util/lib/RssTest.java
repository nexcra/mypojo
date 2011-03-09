
package erwins.util.lib;

import java.util.List;

import org.junit.Test;

import com.sun.syndication.feed.synd.SyndFeed;

import erwins.util.exception.Val;

public class RssTest {

    @Test
    public void testInsertBoard() throws  Exception {
        List<SyndFeed> feeds = Rss.DEV_OKJSP.get();
        for(SyndFeed feed : feeds){
            Val.isNotEmpty(Rss.makeTable(feed));
        }
    }
}
