package erwins.util.vender.apache;

import java.util.Map;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.junit.AfterClass;
import org.junit.Test;

import erwins.util.validation.Precondition;
import erwins.util.vender.apache.PoiSheetReaderRoot.StringMapPoiCallback;



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
        
        CellStyle style = poi.buildStyle(poi.BLUE_FONT, HSSFColor.YELLOW.index2);
        
        for(short i=3;i<10;i++){
            poi.addValues("12334123123123123.24","모델","222.224","2001","","15asdfasf한글맹ㄹㄴㅁㅇㄹㄴㅁ02");
            if(i%2==0) poi.addStyle(style, 3);
        }
        
        poi.wrap();
        poi.getMerge(0).setAbleRow(new Integer[]{0,1}).setAbleCol(new Integer[]{0,1,6}).merge();
        poi.setAlignment();
        
        poi.setComments("물질정보에 등록한 단위규격에 맟주어 출고량을 입력해 주세요.", 0, 0, 2);
        
        poi.write(TEST1);
    }
    
    public void readAndWrite(){
        Poi poi = new Poi(TEST1);        
        poi.getMerge(0).setAbleRow(new Integer[]{0,1}).setAbleCol(new Integer[]{2,4,5}).merge();
        poi.setColumnWidth(0,0,60*50);
        poi.setCustomStyle(poi.BODY_Left,0,new int[]{0,3},7);
        poi.setCustomStyle(poi.BODY_Right,0,new int[]{1},4);
        poi.write(TEST2);
    }
    
    public void readAndConfirm(){
    	int count = 0;
		PoiReader2002 reader = new PoiReader2002(TEST2);
		for(PoiSheetReader2002 each : reader){
			count++;
			Precondition.isEquals("국가코드표", each.getSheetName());
			each.read(new StringMapPoiCallback(){
				@Override
				protected void process(Map<String, String> line) {
					//아무것도 하지 않는다.
				}
			});
		}
		Precondition.isEquals(count,1);
    }
    
    @AfterClass
    public static void delete(){
        //Files.delete(new File(TEST1));
        //Files.delete(new File(TEST2));
    }

}
