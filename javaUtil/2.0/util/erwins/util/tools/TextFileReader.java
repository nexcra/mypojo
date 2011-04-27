
package erwins.util.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import erwins.util.lib.CharEncodeUtil;
import erwins.util.lib.CollectionUtil;
import erwins.util.lib.StringUtil;
import erwins.util.reflexive.FolderIterator;
import erwins.util.root.StringCallback;

/**
 * 복잡한 예외처리 등을 할 수 없지만 간단한거 할때 좋다. 
 * 마지막 라인에 \\가 들어가면 다음라인까지 이어서 한줄로 취급한다.
 * 나중에 세퍼레이터 입력받게 만든 후 테스트/수정 하자.
 */
public class TextFileReader{
    
    /** 
     * 이 구문이 오면 다음 구문과 합쳐서 전체를 한줄로 인식한다.
     * SQL등을 한줄로 읽어 파싱할때 사용된다.
     *  */
    private String lineSeparator = "\\|";
    
    public TextFileReader(){}
    public TextFileReader(String lineSeparator){this.lineSeparator = lineSeparator;}
    
    public void setLineSeparator(String lineSeparator) { this.lineSeparator = lineSeparator;}

    /** 디폴트로  UTF_8을 사용한다. */
    public void read(File file, StringCallback callback){
        read(file,callback,CharEncodeUtil.UTF_8);
    }
    
    public void read(File file, StringCallback callback,String encoding) {
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            //reader = new BufferedReader(new FileReader(path)); => 이거는 안씀?
            isr = new InputStreamReader(new FileInputStream(file), encoding);
            br = new BufferedReader(isr);
            String line = null;

            while ((line = br.readLine()) != null) {
                if(lineSeparator!=null){
                    line = line.trim();
                    if (line.length() == 0) continue;
                    while (line.endsWith("\\")) {
                        line = line.substring(0, line.length() - 1);
                        line += br.readLine().trim();// =>잠재적 오류이다. 수정하다. 
                    }
                }
                callback.process(line);
            }
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        finally {
        	IOUtils.closeQuietly(isr);
        	IOUtils.closeQuietly(br);
        }
    }
    
    /** 모든 토큰을 유지하도록 split해서 자료를 넘겨준다. SEPERATOR들은 오버라이드 하자. */
    public static abstract class StringArrayCallback implements StringCallback{
    	/** replaceAll로 치환시 "\\"를 반드시 추가해 주어야 한다. */
    	public static final String SEPARATOR = "|";
    	public static final String LINE_SEPARATOR = IOUtils.LINE_SEPARATOR_UNIX;
    	public static final String SEPARATE_ESCAPER = "@erwins-separator@";
    	public static final String LINE_SEPARATE_ESCAPER = "@line-separator@";
    	
		@Override
		public void process(String line) {
			String SEPERATOR = seperator();
			String SEPERAT_ESCAPER = seperatEscaper();
			String[] result = StringUtil.splitPreserveAllTokens(line, SEPERATOR);
			//이스케이프 해준다.
			for(int i=0;i<result.length;i++){
				if(StringUtil.contains(result[i], SEPERAT_ESCAPER)){
					result[i] = result[i].replaceAll(SEPERAT_ESCAPER,SEPERATOR);
				}
				if(StringUtil.contains(result[i], LINE_SEPARATE_ESCAPER)){
					result[i] = result[i].replaceAll(LINE_SEPARATE_ESCAPER,LINE_SEPARATOR);
				}
			}
			process(result);
		}
		protected String seperator(){return SEPARATOR;};
		/** SEPERATOR로 사용되는 문자가 해당 문자열로 이미 존재할때 이것으로 치환되어있어야 한다. */
		public String seperatEscaper(){return SEPARATE_ESCAPER;};
		
		protected abstract void process(String[] line);
    }
    
    /** 첫 라인을 컬럼 메타데이터로 보고 MAP으로 매핑시켜 준다. 컬럼과 열이 맞지 않으면 무시한다. 이 데이터는 trim된다.
     *  데이터에 Line구분자가 아닌 \r같은 문구가 있을수 있으니 적절히 전버전과 일치시켜 준다. (윈도우와 유닉스 머신의 차이)
     *  이렇게 하는 이유는 기존 text작성 프로그램을 수정할 수 없기 때문이다. */
    public static abstract class StringMapCallback extends StringArrayCallback{
    	private boolean first = true;
    	private String[] column = null;
    	private int columnLength;
    	private String[] before;
		@Override
		public void process(String[] line) {
			if(first){
				column = new String[line.length];
				boolean camelize = camelize();
				for(int i=0;i<line.length;i++) column[i] =  camelize ? StringUtil.getCamelize(line[i]) : line[i];
				first = false;
				columnLength = column.length;
				return;
			}
			//잘려진 구문이 있는지 체크한다. 줄바꿈은 무시한다.
			if(columnLength!=line.length){
				if(before==null){
					before = line;
					return;
				}
				String[] sum = CollectionUtil.mergeForLineSeperated(before, line);
				if(sum.length!=columnLength){
					before = line;
					return;
				}
				line = sum;
				before = null;
			}
			Map<String,String> result = new HashMap<String,String>();
			for(int i=0;i<columnLength;i++){
				result.put(column[i],line[i]==null ? null : line[i].trim());
			}
			process(result);
		}
		protected abstract void process(Map<String,String> line);
		protected boolean camelize(){return false;};
    }    
    
    /** 
     * 간단한 스캐너 이다. root를 기준으로 모든 파일을 읽어 line을 반환한다.
     * text가 아니면 오류가 날듯..
     * */
    public static void scan(File folder,StringCallback callback){
        FolderIterator i =  new FolderIterator(folder);
        while(i.hasNext()){
            File file = i.next();
            if(!file.isFile()) continue;
            new TextFileReader().read(file,callback);
        }
    }
    
}