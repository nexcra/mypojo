
package erwins.util.tools;

import org.junit.Before;

import erwins.util.collections.map.RequestMap;
import erwins.util.exception.Check;
import erwins.util.root.Pair;

public class MappTest {
    
    @Before
    public void init(){
    	
    }
    
    private static enum QQ{
    	A,B,C;
    }
    private static enum WW implements Pair{
    	AA,BB,CC;

		@Override
		public String getName() {
			return this.name().toLowerCase();
		}

		@Override
		public String getValue() {
			return this.name().toLowerCase();
		}
    }

    /** 두가지 버전을 지원한다. */
    @org.junit.Test
    public void enumCast(){
        RequestMap map = new RequestMap();
        map.put("1", "A");
        map.put("2", "B");
        
        map.put("5", "bb");
        map.put("6", "cc");
        
        Check.isEquals(map.getEnum(QQ.class, "1"), QQ.A);
        Check.isEquals(map.getEnum(QQ.class, "2"), QQ.B);
        
        Check.isEquals(map.getEnum(WW.class, "5"), WW.BB);
        Check.isEquals(map.getEnum(WW.class, "6"), WW.CC);
        
    }


}
