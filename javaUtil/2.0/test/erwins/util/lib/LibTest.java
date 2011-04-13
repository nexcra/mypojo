package erwins.util.lib;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import erwins.util.exception.Check;

public class LibTest{

    private static List<String> list = new ArrayList<String>();
    
    @Test
    public void strings(){
        String url[] = StringUtil.getUrlAndExtention("/D:/qwe.qwe.go");
        Check.isTrue(url[0].equals("D:/qwe.qwe"));
        Check.isTrue(url[1].equals("go"));

        String value = StringUtil.escapeAndUncapitalize("searchMapKey","search");
        Check.isTrue(value.equals("mapKey"));
        
        Check.isTrue(StringUtil.plus("08", "-10").equals("-2"));
    }
    
    @BeforeClass
    public static void init(){
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("F");
    }
    
    @Test
    public void sets(){
        Check.isTrue(StringUtil.joinTemp(list,",").equals("1,2,3,F"));
        Check.isTrue(StringUtil.joinTemp(CollectionUtil.inverse(list),",").equals("F,3,2,1"));
    }

}
