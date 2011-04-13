
package erwins.util.morph;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.util.Assert;

import erwins.util.exception.Check;
import erwins.util.lib.DayUtil;

public class DissolverTest{

    protected MockHttpServletRequest req; 
    
    @Before
    public void init(){
    	req = new  MockHttpServletRequest();
    }
    
    /** 더 복잡한 로직이 필요하면 map타입을 바꿀것. */
    @Test
    public void 배열검증(){
    	for(int i=0;i<5;i++){
    		req.addParameter("list.id", "50");
        	req.addParameter("list.number", "300");
        	req.addParameter("list.decimal", "24878.1548796");
        	req.addParameter("list.day", "20080624");
        	req.addParameter("list.adress", "240-789,서울 목동");
        	req.addParameter("list.objectFlag", "1");
        	req.addParameter("list.normalFlag", "off");
        	req.addParameter("list.ee", "BBB");
        	req.addParameter("list.date", "1280732259859");	
    	}
    	
    	DomainTestOld mock = Dissolver.instance().getBean(req, DomainTestOld.class);
    	List<DomainTestOld> list = mock.getList();
    	Check.isEquals(list.size(),5);
    	DomainTestOld sampel = list.get(0);
    	Check.isEquals(sampel.getId(),50L);
    	Check.isEquals(sampel.getNumber(),300);
    	Check.isEquals(sampel.getDecimal(),new BigDecimal("24878.1548796"));
    }
    
    @Test
    public void ManyToMany검증(){
    	req.addParameter("manyToMany.id", "666");
    	req.addParameter("manyToMany.id", "777");
    	req.addParameter("manyToMany.id", "888");
    	DomainTestOld mock = Dissolver.instance().getBean(req, DomainTestOld.class);
    	
    	Collection<DomainTestOld> manyToMany = mock.getManyToMany();
    	Check.isEquals(manyToMany.size(),3);
    	Iterator<DomainTestOld> i = manyToMany.iterator();
    	Check.isEquals(i.next().getId(),666L);
    	Check.isEquals(i.next().getId(),777L);
    	Check.isEquals(i.next().getId(),888L);
    }
    
    @Test
    public void 숫자검증(){
    	req.setParameter("id", "50");
    	req.setParameter("number", "300");
    	req.setParameter("decimal", "24878.1548796");
    	DomainTestOld mock = Dissolver.instance().getBean(req, DomainTestOld.class);
    	Check.isEquals(mock.getId(),50L);
    	Check.isEquals(mock.getNumber(),300);
    	Check.isEquals(mock.getDecimal(),new BigDecimal("24878.1548796"));
    }
    
    @Test
    public void 숫자검증_null(){
    	DomainTestOld mock = Dissolver.instance().getBean(req, DomainTestOld.class);
    	Assert.isNull(mock.getId());
    	Check.isEquals(mock.getNumber(),0);
    	Check.isEquals(mock.getDecimal(),BigDecimal.ZERO);
    }
    
    @Test
    public void ValueObject검증(){
    	req.setParameter("day", "20080624");
    	req.setParameter("adress", "240-789,서울 목동");
    	DomainTestOld mock = Dissolver.instance().getBean(req, DomainTestOld.class);
    	Check.isEquals(mock.getDay().toString(),"2008년06월24일");
    	mock.getDay().plus(1,1,1);
    	Check.isEquals(mock.getDay().toString(),"2009년07월25일");
    	
    	Check.isEquals(mock.getAdress().getPost(),"240-789");
    	Check.isEquals(mock.getAdress().getAdressName(),"서울 목동");
    }
    
    @Test
    public void Boolean검증(){
    	req.setParameter("objectFlag", "true");
    	req.setParameter("normalFlag", "on");
    	DomainTestOld mock = Dissolver.instance().getBean(req, DomainTestOld.class);
    	Check.isTrue(mock.getObjectFlag());
    	Check.isTrue(mock.isNormalFlag());
    }
    @Test
    public void Boolean검증2(){
    	req.setParameter("objectFlag", "y");
    	req.setParameter("normalFlag", "0");
    	DomainTestOld mock = Dissolver.instance().getBean(req, DomainTestOld.class);
    	Check.isTrue(mock.getObjectFlag());
    	Check.isTrue(!mock.isNormalFlag());
    }
    @Test
    public void Boolean검증_null(){
    	DomainTestOld mock = Dissolver.instance().getBean(req, DomainTestOld.class);
    	Assert.isNull(mock.getObjectFlag());
    	Check.isTrue(!mock.isNormalFlag());
    }
    @Test
    public void Enum검증(){
    	req.setParameter("ee", "AAA");
    	DomainTestOld mock = Dissolver.instance().getBean(req, DomainTestOld.class);
    	Check.isTrue(mock.getEe() == EnumSample.AAA);
    }
    
    @Test
    public void Date검증(){
    	req.setParameter("date", "1280732259859");
    	DomainTestOld mock = Dissolver.instance().getBean(req, DomainTestOld.class);
    	Check.isEquals(DayUtil.DATE_FOR_DB.get(mock.getDate()),"20100802");
    }
  
}

