
package erwins.util.vender.apache;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import erwins.util.lib.Formats;
import erwins.util.lib.Strings;

/**
 * POI 패키지의 HSSF를 편리하게.. 헤더칸은 1칸 이라고 일단 고정 사각 박스를 예쁘게 채울려면 반드시 null에 ""를 채워 주자~
 * @author  erwins(my.pojo@gmail.com)
 */
public class Poi extends PoiRoot{
    
    // ===========================================================================================
    //                                    생성자
    // ===========================================================================================
    
    public Poi(HSSFWorkbook wb){
        this.wb = wb;
        init();
    }
    
    public Poi(){
        this.wb = new HSSFWorkbook();
        init();
    }
    
    public Poi(String fileName){
    	try {
            stream = new FileInputStream(fileName);
            POIFSFileSystem filesystem = new POIFSFileSystem(stream);        
            wb = new HSSFWorkbook(filesystem);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }finally{
        	close();
        }
        init();
    }
    
    public Poi(File file){
        try {
            stream = new FileInputStream(file);
            POIFSFileSystem filesystem = new POIFSFileSystem(stream);        
            wb = new HSSFWorkbook(filesystem);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }finally{
        	close();
        }
        init();
    }
    
    /**
     * 스트림을 닫아준다.. ㅠㅠ
     * File로 POI를 만들때 반드시 닫아주자.. 뭐 안해도 되긴 한디..
     */
    public void close(){
        if(stream!=null) try {
            stream.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }    
    
    
    // ===========================================================================================
    //                                     간편쓰기.
    // ===========================================================================================
    
    private HSSFSheet nowSheet;
    
    /**
     * 1. 시트를 만들고 0번째 로우에 헤더를 만든다.
     * 2. 시트의 가로 , 세로 열이 같다면 merge한다.
     */
    public void addSheet(String sheetname,String[] ... titless){
    	nowSheet = wb.createSheet(sheetname);
        HSSFRow row ;
        for(String[] titles : titless){
            row = createNextRow();
            for(int j=0;j<titles.length;j++)
                row.createCell(j).setCellValue(new HSSFRichTextString(titles[j]));    
        }
        headerRowCount.add(titless.length);
    }
    
    public HSSFRow createNextRow() {
        int i = nowSheet.getPhysicalNumberOfRows(); //시트가 순수 createRow로 생성한 로우 수를 반환한다. 즉 중간에 공백이 있으면 안된다.
        HSSFRow row = nowSheet.createRow(i);
        return row;
    }
    
    /**
     * 간단한 시트를 완성한다.
     * 기본 입력은 하이버네이트 기본인  List<Object[]> 이다.
     * 즉.. 순서가 있는 2차원 배열이어야 한다. (map이나 bean은 사용 못함)
     */
    public void makeSimpleSheet(List<Object[]> list){
        int sheetNum = wb.getActiveSheetIndex();
        HSSFSheet sheet = wb.getSheetAt(sheetNum);
        HSSFRow row = null;
        int header = headerRowCount.get(sheetNum);
        for(int i=0;i<list.size();i++){
            Object[] obj = list.get(i);
            row = sheet.createRow(i+header);
            for(int j=0;j<obj.length;j++){
                row.createCell(j).setCellValue(new HSSFRichTextString(Strings.toString(obj[j])));
            }
        }
    }    
    
    /** 기생성된 row에 i번째 컬럼 부터 value를 입력한다. */
    public void setValues(int i,Object ... values){
        HSSFRow row = createNextRow();
        for(Object each : values){
            String value = null;
            if(each==null) value="";
            else if(each instanceof Number) value = Formats.DOUBLE2.get((Number)each);
            else value = each.toString();
            row.createCell(i++).setCellValue(new HSSFRichTextString(value));    
        }
    }    
    
    /** sheet의 마지막에 row를 생성하고 value를 입력한다. */    
    public void addValues(Object ... values){
        setValues(0,values);
    }
    public void addValuesArray(Object[] values){
        setValues(0,values);
    }
    

    /*
    *//**
     * 오라클로 엑셀을 업로드 합니다.
     * 첫번째 로우 이름이 Table의 컬럼 이름이 됩니다.
     *//*
    public void uploadForOracle(String tableName,String ip,String sid,String id,String pass){
        new OracleUploader(tableName,ip,sid,id,pass);
    }
    
    // ===========================================================================================
    //                                   oracle uploader.. 완전 쓸모없어 보임.
    // ===========================================================================================    
    
    *//**
     * Key를 지정 후 insert or update하게 변경하자. num과 vchar를 구분하는 로직은 필요 없을듯.
     *//*
    private class OracleUploader{
        private static final String CREATE_TIME = "CREATE_TIME";
        private static final String TIMESTAMP = "TIMESTAMP";
        
        private List<String> cols = new ArrayList<String>();
        *//**
         * @uml.property  name="jdbc"
         * @uml.associationEnd  
         *//*
        JDBC jdbc;
        private String tableName;
        
        public OracleUploader(String tableName,String ip,String sid,String id,String pass){
            this.tableName = tableName.toUpperCase();
            try {
				jdbc = new JDBC(ip,sid,id,pass);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
            initSheet(0,0);
            run();
        }

        private void run() {
            try {
                makeTable();
                insertData();
                jdbc.commit();
            }
            catch (Exception e) {
                jdbc.rollback();
                throw new RuntimeException(e);
            }finally{
                jdbc.close();
            }            
        }

        private void insertData() throws SQLException {
            List<String> datas = null;
            int count = 0;
            while((datas = next()) != null){
                StringBuilder str = new StringBuilder("INSERT INTO "+tableName+" ");
                boolean first = true;
                str.append(" ( ");
                for(String col: cols){
                    if(first) first = false;
                    else  str.append(" , ");
                    str.append(col);
                }
                str.append(","+CREATE_TIME);
                str.append(" ) values (");
                
                first = true;
                if(datas.size() < cols.size()) throw new RuntimeException(count+" size가 col보다 작습니다.");
                for(int i=0;i<cols.size();i++){
                    String value = datas.get(i);
                    if(first) first = false;
                    else  str.append(" , ");
                    str.append("'");
                    str.append(value.replaceAll("'","\'")); //????
                    str.append("'");
                }
                str.append(","+"SYSDATE");
                str.append(" ) ");
                jdbc.update(str.toString());
                count++;
            }
        }

        *//**
         * 이놈들은 commit이라는게 없다. 조심 
         *//*
        private void makeTable() throws SQLException {
            cols = next();
            if(jdbc.isContain("select count(*) from user_tables where table_name = '"+tableName+"'")) return;
            StringBuilder str = new StringBuilder("CREATE TABLE "+tableName+" ");
            str.append("(");
            boolean isFirst = true;
            for(String row : cols){
                if(isFirst) isFirst = false;
                else str.append(","); 
                str.append(row);
                str.append(" VARCHAR2 (4000)");
            }
            str.append(","+CREATE_TIME + " "+TIMESTAMP+" ");
            str.append(")");
            jdbc.update(str.toString());
            jdbc.update("COMMENT ON TABLE "+tableName+" IS '"+Days.DATE.get()+" POI로 제작된 테이블 입니다.'");
        }
        
    }*/
    
    
}
