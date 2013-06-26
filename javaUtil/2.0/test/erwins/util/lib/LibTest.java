package erwins.util.lib;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import erwins.util.text.StringUtil;
import erwins.util.validation.Precondition;

public class LibTest{

    private static List<String> list = new ArrayList<String>();
    
    @Test
    public void strings(){
        String url[] = StringUtil.getUrlAndExtention("/D:/qwe.qwe.go");
        Precondition.isTrue(url[0].equals("D:/qwe.qwe"));
        Precondition.isTrue(url[1].equals("go"));

        String value = StringUtil.escapeAndUncapitalize("searchMapKey","search");
        Precondition.isTrue(value.equals("mapKey"));
        
        Precondition.isTrue(StringUtil.plus("08", "-10").equals("-2"));
    }
    
    @BeforeClass
    public static void init(){
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("F");
    }
    
    

}
