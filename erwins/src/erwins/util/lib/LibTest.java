package erwins.util.lib;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import erwins.util.exception.Val;

public class LibTest{

    private static List<String> list = new ArrayList<String>();
    
    @Test
    public void strings(){
        String url[] = Strings.getUrlAndExtention("/D:/qwe.qwe.go");
        Val.isTrue(url[0].equals("D:/qwe.qwe"));
        Val.isTrue(url[1].equals("go"));

        String value = Strings.escapeAndUncapitalize("searchMapKey","search");
        Val.isTrue(value.equals("mapKey"));
        
        Val.isTrue(Strings.plus("08", "-10").equals("-2"));
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
        Val.isTrue(Strings.joinTemp(list,",").equals("1,2,3,F"));
        Val.isTrue(Strings.joinTemp(Sets.inverse(list),",").equals("F,3,2,1"));
    }

}
