
package erwins.util.lib;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
     * OK JSP
     */
    DEV_OKJSP(new String[]{
        "http://www.okjsp.pe.kr/rss/okjsp-rss2.jsp?bbs=lifeqna",
        "http://www.okjsp.pe.kr/rss/okjsp-rss2.jsp?bbs=bbs6",
        "http://www.okjsp.pe.kr/rss/okjsp-rss2.jsp?bbs=howmuch",
        "http://www.okjsp.pe.kr/rss/okjsp-rss2.jsp?bbs=techtrend"
    }),
    
    /**
     * 개발자 블로그
     */
    DEV_BLOG(new String[]{
            "http://feeds2.feedburner.com/nokarma", //nokarma 
            "http://javacan.tistory.com/rss", //자바캔
            "http://toby.epril.com/?feed=rss2", //토비
            "http://feeds.feedburner.com/ahnyounghoe", //안영회
            "http://kwon37xi.springnote.com/pages.rss",
            //"http://rss.egloos.com/blog/kwon37xi", //권남.
            "http://yunsunghan.tistory.com/rss", //윤성한(Max)
            "http://bcho.tistory.com/rss", //조대협
            "http://rss.egloos.com/blog/aeternum", //누구?
            "http://feeds.feedburner.com/allofsoftware", //all of 소프트웨어 저자. 개념PM?
            //"http://rss.egloos.com/blog/sunnykwak", //써니
            "http://chanwook.tistory.com/rss", //정찬욱
            "http://whiteship.me/rss", //백선
            "http://grails.tistory.com/rss" //그루비??
    }),
    DEV_ETC(new String[]{
            "http://kwon37xi.springnote.com/pages.rss" //권남이
    });
    /*,
    FRIENDS(new String[]{
            "http://zeide.tistory.com/category" //gydud
    });*/
    
    private static Log log =LogFactory.getLog(Rss.class);
    
    private String[] rssUrls;
    
    private Rss(String[] rssUrls){
        this.rssUrls = rssUrls;
    }
    
    private List<SyndFeed> cached = null;

    /** 캐싱된 객체를 얻어온다. 최초 로드시 좀 걸릴 수 있다. */
    public synchronized List<SyndFeed> getCached(){
        if(cached==null) cached = get();
        return cached;
    }
    
    public void refresh(){
        List<SyndFeed> list = getFeeds(this.rssUrls);
        synchronized(this){
            cached = list;
        }
    }
    
    /** 캐시를 안하고 실시간으로 얻어온다. */
    public List<SyndFeed> get(){
        return getFeeds(this.rssUrls);
    }
    
    /** 실제 시간이 걸리는 부분? */
    private static SyndFeed getFeed(String url){
        try {
            URL feedUrl = new URL(url);
            SyndFeedInput input = new SyndFeedInput();
            return input.build(new XmlReader(feedUrl));
        }
        catch (Exception e) {
            log.debug(e.getMessage());
            return null;
        }
    }

    /**
     * 간편 피드를 리턴합니다.
     * feed : getTitle, getAuthor
     * entry : getTitle, getAuthor, getDescription, getLink
     */
    public static List<SyndFeed> getFeeds(String ... urls){
        List<SyndFeed> list = new ArrayList<SyndFeed>();
        for(String url : urls)
            list.add(getFeed(url));
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
        if(feed==null) return "";
        Tr tr = null;
        List<Tr> trs = new ArrayList<Tr>();
        List<SyndEntry> list = getEntry(feed);
        for(int i=0;i<list.size();i++){
            //6개만 보이도록 하자.
            if(i==6)break; 
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
