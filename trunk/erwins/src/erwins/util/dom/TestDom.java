
package erwins.util.dom;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.ecs.html.LI;


/**
 * @author  Administrator
 */
public class TestDom extends TestCase {

    List<Dom> doms2 = new ArrayList<Dom>();
    /**
     * @uml.property  name="dom"
     * @uml.associationEnd  
     */
    Dom dom;

    @Override
    protected void setUp(){
        
        dom = new Dom();
        dom.setId("ROOT");
        dom.setLevel(1);
        dom.setName("아이템들");
        doms2.add(dom);
        
        dom = new Dom();
        dom.setId("11");
        dom.setIdParent("ROOT");
        dom.setLevel(1);
        dom.setName("숫자들");
        doms2.add(dom);
        
        dom = new Dom();
        dom.setId("1101");
        dom.setIdParent("11");
        dom.setName("1번재 숫자.");
        dom.setLevel(2);
        doms2.add(dom);
        
        dom = new Dom();
        dom.setId("1102");
        dom.setIdParent("11");
        dom.setName("2번재 숫자.");
        dom.setLevel(2);
        doms2.add(dom);        
        
        dom = new Dom();
        dom.setId("22");
        dom.setIdParent("ROOT");
        dom.setLevel(1);
        dom.setName("문자들");
        doms2.add(dom);

    }

    public void testInsertBoard(){

        DomParser d = new DomParser(doms2);
        LI li = new LI();
        li.setNeedClosingTag(true);
        
        //System.out.println("".split(",")[0]);

    }

}
