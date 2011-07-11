
package erwins.util.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;

import erwins.util.lib.FileUtil;
import erwins.util.lib.StringUtil;
import erwins.util.tools.TextFileReader.StringMapCallback;

/**
 * 복잡한 예외처리 등을 할 수 없지만 간단한거 할때 좋다.
 * 전부 메모리에서 처리하니 간단한거만 할것!
 */
public class TextFile{
	
	/** 엑셀로 받아온 대용량 데이터는 탭으로 변환해 처리하자. */
	public static final String TAB = "	";
    
    /** 디폴트로  UTF_8을 사용한다. */
    private String encoding = "UTF-8";
    private String columnSeparator = TAB;
    private String lineSeparator = IOUtils.LINE_SEPARATOR;
    private File root;
    public TextFile setRoot(File root) {
		this.root = root;
		return this;
	}
    public TextFile setRoot(String root) {
    	this.root = new File(root);
    	return this;
    }
	public TextFile setColumnSeparator(String columnSeparator) {
		this.columnSeparator = columnSeparator;
		return this;
	}
	public void setLineSeparator(String lineSeparator) {
		this.lineSeparator = lineSeparator;
	}
	public TextFile(){}
    public TextFile(String encoding) { this.encoding = encoding;}
    public TextFile setEncoding(String encoding) { this.encoding = encoding; return this;}
    
    public List<Map<String, String>> readAsMap(String file){
    	return readAsMap(new File(root,file));
    }
    public List<Map<String, String>> readAsMap(File file){
    	final List<Map<String, String>> result = new ArrayList<Map<String, String>>();
    	TextFileReader reader = new TextFileReader(encoding);
    	reader.read(file, new StringMapCallback(columnSeparator){
			@Override
			protected void process(Map<String, String> line) {
				result.add(line);
			}
    	});
    	return result;
    }

    public void writeByMap(String file,Collection<Map<String,Object>> list){
    	writeByMap(new File(root,file),list);
    }
    
    /** Collection이면 무시한다. */
    public void writeByMap(File file,Collection<Map<String,Object>> list){
    	List<String> result = new ArrayList<String>();
    	boolean first = true;
    	for(Map<String,Object> each : list){
    		if(first){
    			List<String> header = new ArrayList<String>();
    			for(Entry<String,Object> eachCol : each.entrySet()){
    				if(eachCol.getValue() instanceof Collection)  continue;
    				header.add(eachCol.getKey());
    			}
    			result.add(StringUtil.join(header, columnSeparator));
    			first = false;
    		}
    		List<String> line = new ArrayList<String>();
			for(Object value : each.values()){
				if(value instanceof Collection) continue;
				line.add(value.toString());
			}
			result.add(StringUtil.join(line, columnSeparator));
    	}
    	try {
			FileUtil.writeLines(file,encoding, result, lineSeparator);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
    }
    
}