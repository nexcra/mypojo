
package erwins.test.domain;

import junit.framework.Assert;

import org.hibernate.criterion.Restrictions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import erwins.domain.Current;
import erwins.domain.label.Label;
import erwins.util.lib.RandomStringUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:dataSourceTest.xml", "classpath:config.xml" })
public class LabelTest extends RootSptingTest {
	
	@Test
    public void label() throws Exception {
        labelSearch();
        String labelName = RandomStringUtil.getRandomSring(10);
        labelSave(labelName);
        Label label = labelFind(labelName);
        labelDelete(label);
    }
    
    protected void labelSearch() throws Exception {
    	initTest();
        req.setRequestURI("/flex_/label.search.do");
        labelController.search(req, resp);
        ajax.assertResponse(resp);
    }
    
    protected void labelSave(String labelName) throws Exception {
    	initTest();
        req.setRequestURI("/flex_/label.save.do");
        req.addParameter("label", labelName);
        req.addParameter("description", labelName+" == test == ");
        req.addParameter("className","kkk");
        labelController.save(req, resp);
        ajax.assertResponse(resp);
    }
    
    protected Label labelFind(String labelName) {
        Label label = labelService.findUnique(
                Restrictions.eq("label", labelName),
                Restrictions.eq("user", Current.getUser())
                );
        Assert.assertEquals(label.getLabel(),labelName);
        return label;
    }
    
    protected void labelDelete(Label label) throws Exception {
    	initTest();
        req.setRequestURI("/flex_/label.remove.do");
        req.addParameter("id",label.getId().toString());
        req.addParameter("label",label.getLabel());
        labelController.delete(req, resp);
        ajax.assertResponse(resp);
        
    }

}