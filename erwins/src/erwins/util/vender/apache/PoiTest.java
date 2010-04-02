package erwins.util.vender.apache;

import java.io.File;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Test;

import erwins.util.exception.Val;
import erwins.util.lib.Files;
import erwins.util.vender.apache.PoiSheetReader.StringMapPoiCallback;



/**
 * row는 32767가 max이다. 
 */
public class PoiTest{
    
    private static final String TEST1 = "/test1.xls";
    private static final String TEST2 = "/test2.xls";
    
    @Test
    public void test(){
        addSheet();
        readAndWrite();
        readAndConfirm();
    }
    
    /** 다중 시트 추가를 테스트한다. */
    public void addSheet(){
        
        Poi poi = new Poi();
       
        String[] title1 = new String[]{"시작","시작","zz","zz","흑마"};
        String[] title2 = new String[]{"국가이름","맹구","맹구","맹구","흑마"};
        String[] title3 = new String[]{"국가이름","맹구2","맹구2","맹구2","땡구"};
        String[] title4 = new String[]{"국가이름","승한222222222222승한승한승한승한","땡구","2","1"};
        poi.addSheet("국가코드표",title1,title2,title3,title4);
        
        for(short i=3;i<10;i++){
            poi.addValues("12334123123123123.24","모델","222.224","2001","","15asdfasf한글맹ㄹㄴㅁㅇㄹㄴㅁ02");
        }
        
        poi.wrap();
        poi.getMerge(0).setAbleRow(new Integer[]{0,1}).setAbleCol(new Integer[]{0,1,6}).merge();
        poi.setAlignment();
        
        poi.setComments("물질정보에 등록한 단위규격에 맟주어 출고량을 입력해 주세요.", 0, 0, 2);
        
        poi.write(TEST1);
    }
    
    public void readAndWrite(){
        Poi poi = new Poi(TEST1);        
        poi.getMerge(0).setAbleRow(new Integer[]{0,1}).setAbleCol(new Integer[]{3,4,5}).merge();
        poi.setColumnWidth(0,0,600*50);
        poi.setStyle(poi.BODY_Left,0,2);
        poi.setStyle(poi.BODY_Right,0,1);
        poi.write(TEST2);
    }
    
    public void readAndConfirm(){
    	int count = 0;
		PoiReader reader = new PoiReader(TEST2);
		for(PoiSheetReader each : reader){
			count++;
			Val.isEquals("국가코드표", each.getSheetName());
			each.read(new StringMapPoiCallback(){
				@Override
				protected void process(Map<String, String> line) {
					//아무것도 하지 않는다.
				}
			});
		}
		Val.isEquals(count,1);
    }
    
    @AfterClass
    public static void delete(){
        Files.delete(new File(TEST1));
        Files.delete(new File(TEST2));
    }

}
