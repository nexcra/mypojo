
package erwins.test.domain;

import org.hibernate.SessionFactory;
import org.hibernate.cache.SingletonEhCacheProvider;
import org.hibernate.stat.Statistics;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jmx.snmp.tasks.Task;

import erwins.util.exception.Check;
import erwins.util.lib.StringUtil;
import erwins.util.vender.hibernate.HibernateStatisticsBuilder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:dataSourceTest.xml", "classpath:config.xml" })
public class EtcTest extends RootSptingTest {
	
	@Autowired
	SessionFactory factory;
	
	@Test
    public void cache() throws Exception {
		//CacheManager cacheManager = CacheManager.create();
		//Cache cache = cacheManager.getCache("simpleBeanCache");
		//System.out.println(cache.getName());
		SingletonEhCacheProvider asd;
		Task a;
		//System.out.println(new CacheManager("D:/ehcache.xml").getCache("simpleBeanCache").getName());
		
    }
	
    @Test
    public void file() throws Exception {
        req.setParameter("folderName","open");
        fileController.search(req, resp);
    }
    
    @Test /** DWR등 */
    public void etc() throws Exception {
    	Check.isEquals(dwrService.escapeUrl("qwe<>"), "qwe%3C%3E");
    }
    
    @Test
    public void statistics() throws Exception {
    	Statistics stats = factory.getStatistics();
    	HibernateStatisticsBuilder builder = new HibernateStatisticsBuilder(stats);
    	builder.setCss("sort");
    	Check.isTrue(StringUtil.isMatch(builder.build(),"Query","sort"));
    }
    /*
    @Test
    public void btn() throws Exception {
        Btn btn = new Btn();
        String str = btn.role(Role.ADMIN).script("alert('zzzz');").build(Mode.SAVE);
        Assert.assertTrue(Strings.contains(str, "alert"));
        
        //관리자 권한 임시 삭제.
        Current.getUser().getRoles().remove(Role.ADMIN);
        
        //객체검사.
        Book book = new Book();
        book.setId(1);
        book.setUser(Current.getUser());
        str = btn.pk(book).role(Role.USER).build(Mode.SAVE);
        Assert.assertTrue(Strings.contains(str, "button"));
        
        //사용자가 다르면 실패.
        User user = new User();
        user.setId(999);
        book.setUser(user);
        str = btn.pk(book).role(Role.USER).build(Mode.SAVE);
        Assert.assertTrue(Strings.isEmpty(str));
        
        //관리자 요구이면 실패.
        str = btn.role(Role.ADMIN).build(Mode.SAVE);
        Assert.assertTrue(Strings.isEmpty(str));
        
        //관리자 권한 원복.
        Current.getUser().getRoles().add(Role.ADMIN);
        str = btn.role(Role.ADMIN).build(Mode.SAVE);
        Assert.assertTrue(Strings.contains(str, "button"));
    }*/

}
