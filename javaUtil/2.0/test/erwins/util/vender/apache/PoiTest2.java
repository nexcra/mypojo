package erwins.util.vender.apache;

import java.util.Map;

import org.junit.Test;

import erwins.util.vender.apache.PoiSheetReaderRoot.StringMapPoiCallback;


public class PoiTest2{
    
    private static final String TEST2 = "/test11.xlsx";
    
    @Test
    public void test(){
        readAndWrite();
    }
    
    public void readAndWrite(){
    	PoiReader reader = new PoiReader(TEST2);
		for(PoiSheetReader each : reader){
			System.out.println(each.getSheetName());
			each.read(new StringMapPoiCallback(){
				@Override
				protected void process(Map<String, String> line) {
					System.out.println(line.values());
				}
			});
		}
    }


}
