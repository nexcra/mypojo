package erwins.util.vender.etc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.commons.io.output.FileWriterWithEncoding;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

/**
 * 간단 래핑한다. 
 * CSVReader reader = new CSVReader(new FileReader("yourfile.csv"), '\t', '\'', 2); 처럼 옵션 조절 가능
 * 스트림기능을 쓸려면 별도로 코딩할것 ex) CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(f), "MS949"));
 */
public class OpenCsv{
	
	/** 기본값 UTF-8 , MS-OFFICE로 읽을 경우 EUC-KR로 해야 한글이 깨지지 않는다.  */
	private String encoding = "UTF-8";
    
	public String getEncoding() {
		return encoding;
	}
	
	public OpenCsv setEncoding(String encoding) {
		this.encoding = encoding;
		return this;
	}

	/** key를 첫번째 열에 담는다.
	 * Date의 경우 숫자형으로 담지만, 읽을땨는 역변환이 안된다. 알아서 처리할것.
	 * 적절한? 컨버터가 필요해 보인다. */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public  void writeMap(File file,List<Map> list){
		if(list.size()==0) return;
		List<String[]> entries = new ArrayList<String[]>();
		List colList = new ArrayList();
		Map sample = list.get(0);
		for(Object o : sample.entrySet()){
			Entry e = (Entry)o;
			if(e.getValue() instanceof Collection) continue;
			colList.add(e.getKey());
		}
		String[] colums = (String[]) colList.toArray(new String[colList.size()]);
		entries.add(colums);
		
		for(Map each : list){
			String[] values = new String[colums.length]; 
			for(int i=0;i<colums.length;i++){
				Object value = each.get(colums[i]);
				if(value instanceof Collection) continue;
				
				if(value instanceof Date) values[i] = String.valueOf(((Date)value).getTime());
				else values[i] = value == null? "" : value.toString() ;
			}
			entries.add(values);
		}
		writeAll(file,entries);
    } 
	
	
    public  void writeAll(File file,List<String[]> entries){
    	CSVWriter writer = null;
    	try {
            writer = new CSVWriter(new FileWriterWithEncoding(file,encoding));
            writer.writeAll(entries);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally{
			try {
				if (writer != null) writer.close();
			} catch (Exception e2) {
				
			}
		}
    }
    
    public  List<String[]> readAll(File file){
    	CSVReader reader = null;
    	try {
			//reader = new CSVReader(new FileReader(file));
    		reader = new CSVReader(new InputStreamReader(new FileInputStream(file),encoding));
			return reader.readAll();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}finally{
			try {
				if (reader != null) reader.close();
			} catch (Exception e2) {
				
			}
		}
    }
    
    @SuppressWarnings("unchecked")
	public  List<Map<String,String>> readAsMap(File file){
    	List<Map<String,String>> result = new ArrayList<Map<String,String>>();
    	List<String[]> datas = readAll(file);
    	if(datas.size() <= 1) return result;
    	String[] header = datas.get(0);
    	for (int i = 1; i < datas.size(); i++) {
			String[] data = datas.get(i);
    		Map<String,String> map = new ListOrderedMap();
    		for (int j = 0; j < header.length; j++) {
    			map.put(header[j], data[j]);	
    		}
    		result.add(map);
		}
    	return result;
    }
    
    public static void closeQuietly(CSVWriter writer){
    	if(writer!=null){ 
			try {
				writer.close();
			} catch (IOException e1) {
				throw new RuntimeException(e1);
			}
		}
    }
    
    public static void closeQuietly(CSVReader reader){
    	if(reader!=null){ 
			try {
				reader.close();
			} catch (IOException e1) {
				throw new RuntimeException(e1);
			}
		}
    }
    
    /**
     * 	private CSVWriter writer;
	
	public OpenCsv(String fileName){
		try {
			this.writer = new CSVWriter(new FileWriter(fileName));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	public OpenCsv(File file){
		try {
			this.writer = new CSVWriter(new FileWriter(file));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
     */
    
    
}
