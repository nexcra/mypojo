
package erwins.test.util;

import org.junit.AfterClass;
import org.junit.Test;

import erwins.domain.etc.FOManager;
import erwins.domain.etc.FileObject;
import erwins.util.exception.runtime.Val;

public class FOManagerTemplitTest{
    
    @Test
    public void save(){
        FileObject obj = FOManager.instance().get();        
        Val.isNotEmpty(obj);
        FileObject obj2 = new FileObject();
        obj2.add("newOne1", "새롭게 새거 생성1");
        obj2.add("newOne2", "새롭게 새거 생성2");
        FOManager.instance().save(obj2);
    }
    
    @Test
    public void load(){
        FileObject obj = FOManager.instance().get();
        Val.isNotEmpty(obj);
    }
    
    @AfterClass
    public static void tearDownAfterClass(){
        FOManager.instance().delete();
    }
   
}
