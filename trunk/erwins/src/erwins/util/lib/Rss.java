
package erwins.util.lib;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.ecs.html.A;
import org.apache.ecs.wml.Td;
import org.apache.ecs.wml.Tr;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;


/**
 * @author     Administrator
 */
public enum Rss {
    
    /**
     * @uml.property  name="dEV_OKJSP"
     * @uml.associationEnd  
     */
    DEV_OKJSP(new String[]{
        "http://www.okjsp.pe.kr/rss/okjsp-rss2.jsp?bbs=lifeqna",
        "http://www.okjsp.pe.kr/rss/okjsp-rss2.jsp?bbs=bbs6",
        "http://www.okjsp.pe.kr/rss/okjsp-rss2.jsp?bbs=howmuch"
    }),
    
    /**
     * @uml.property  name="dEV_BLOG"
     * @uml.associationEnd  
     */
    DEV_BLOG(new String[]{
            "http://feeds2.feedburner.com/nokarma", //nokarma 
            "http://javacan.tistory.com/rss", //자바캔 
            "http://toby.epril.com/?feed=rss2", //토비
            "http://rss.egloos.com/blog/sunnykwak", //써니
            "http://rss.egloos.com/blog/kwon37xi", //권남.
            "http://chanwook.tistory.com/rss", //정찬욱
            "http://yunsunghan.tistory.com/rss", //윤성한(Max)
            "http://bcho.tistory.com/rss", //조대협
            "http://whiteship.me/rss" //백선
    }),
    /**
     * @uml.property  name="dEV_ETC"
     * @uml.associationEnd  
     */
    DEV_ETC(new String[]{
            "http://kwon37xi.springnote.com/pages.rss" //권남이
    }),
    /**
     * @uml.property  name="fRIENDS"
     * @uml.associationEnd  
     */
    FRIENDS(new String[]{
            "http://zeide.tistory.com/category" //gydud
    });
    
    private String[] rssUrls;
    
    private Rss(String[] rssUrls){
        this.rssUrls = rssUrls;
    }

    public List<SyndFeed> get(){
        return getFeeds(this.rssUrls);
    }
    
    private static SyndFeed getFeed(String url){
        try {
            URL feedUrl = new URL(url);
            SyndFeedInput input = new SyndFeedInput();
            return input.build(new XmlReader(feedUrl));
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(),e);
        }
    }

    /**
     * 간편 피드를 리턴합니다.
     * feed : getTitle, getAuthor
     * entry : getTitle, getAuthor, getDescription, getLink
     */
    public static List<SyndFeed> getFeeds(String ... urls){
        List<SyndFeed> list = new ArrayList<SyndFeed>();
        for(String url : urls) list.add(getFeed(url));
        return list;
    }
    
    /**
     * entry를 제너릭 타입으로 래핑하여 리턴한다.
     */
    @SuppressWarnings("unchecked")
    public static List<SyndEntry> getEntry(SyndFeed feed){
        return feed.getEntries();
    }
    
    /**
     * 흠냐.. tbody안에 들어갈 테이블 제작
     */
    public static String makeTable(SyndFeed feed){
        Tr tr = null;
        //Set<Tr> trs = new HashSet<Tr>();
        List<Tr> trs = new ArrayList<Tr>();
        List<SyndEntry> list = getEntry(feed);
        for(int i=0;i<list.size();i++){
            if(i%2==0) tr = new Tr();
            SyndEntry entry = list.get(i);
            setTrByEntry(tr, entry);
            Sets.addIfNotFound(trs, tr);
        }
        return Strings.joinTemp(trs,"");
    }

    private static void setTrByEntry(Tr tr, SyndEntry entry) {
        Td td1 = new Td();
        Td td2 = new Td();
        td1.setTagText(entry.getAuthor());
        //td1.setStyle("overflow:hidden,height=10");
        A a = new A();
        a.setHref(entry.getLink());
        a.setTagText(entry.getTitle());
        td2.addElement(a);
        
        tr.addElement(td1);
        tr.addElement(td2);
    }


}
