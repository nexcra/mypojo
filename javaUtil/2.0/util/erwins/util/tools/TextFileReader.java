
package erwins.util.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.commons.io.IOUtils;

import erwins.util.lib.StringUtil;
import erwins.util.reflexive.FolderIterator;
import erwins.util.root.StringCallback;
import groovy.lang.Closure;

/**
 * 스트리밍 읽기를 지원한다.
 * 대용량에 사용하자.
 */
@SuppressWarnings(value={"unchecked","rawtypes"})
public class TextFileReader{
	
	/** 엑셀로 받아온 대용량 데이터는 탭으로 변환해 처리하자. */
	public static final String TAB = "	";
    
    /** 디폴트로  UTF_8을 사용한다. */
    private String encoding = "UTF-8";
    
	public TextFileReader(){}
    public TextFileReader(String encoding) { this.encoding = encoding;}
    public TextFileReader setEncoding(String encoding) { this.encoding = encoding; return this;}

    public void read(File file, StringCallback callback) {
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            //reader = new BufferedReader(new FileReader(path)); => 이거는 안씀?
            isr = new InputStreamReader(new FileInputStream(file), encoding);
            br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                if (line.length() == 0) continue;
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
    
    /** 모든 토큰을 유지하도록 split해서 자료를 넘겨준다. */
    public static abstract class StringArrayCallback implements StringCallback{
    	/**  디폴트로 파이프라인(|)을 사용한다. */
        private String columnSeparator = "\\|";
        public StringArrayCallback(){}
        public StringArrayCallback(String columnSeparator){ this.columnSeparator = columnSeparator;}
		@Override
		public void process(String line) {
			String[] result = StringUtil.splitPreserveAllTokens(line, columnSeparator);
			process(result);
		}
		protected abstract void process(String[] line);
    }
    
    /** 첫 라인을 컬럼 메타데이터로 보고 MAP으로 매핑시켜 준다. 컬럼과 열이 맞지 않으면 무시한다. 이 데이터는 trim된다.
     *  데이터에 Line구분자가 아닌 \r같은 문구가 있을수 있으니 적절히 전버전과 일치시켜 준다. (윈도우와 유닉스 머신의 차이)
     *  이렇게 하는 이유는 기존 text작성 프로그램을 수정할 수 없기 때문이다. */
    public abstract static class StringMapCallback extends StringArrayCallback{
    	private boolean first = true;
    	private String[] column = null;
    	private int columnLength;
    	//private String[] before;
    	public StringMapCallback(){ }
        public StringMapCallback(String columnSeparator){ super(columnSeparator);}
		@Override
		public void process(String[] line) {
			if(first){
				column = new String[line.length];
				boolean camelize = camelize();
				for(int i=0;i<line.length;i++) column[i] =  camelize ? StringUtil.getCamelize(line[i]) : line[i];
				first = false;
				columnLength = column.length;
				init(line);
				return;
			}
			//잘려진 구문이 있는지 체크한다. 줄바꿈은 무시한다.  -> 이거 반복하도록 나중에 수정할것!
			/*
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
			}*/
			Map<String,String> result = new ListOrderedMap();
			for(int i=0;i<columnLength;i++){
				result.put(column[i],line[i]==null ? null : line[i].trim());
			}
			process(result);
		}
		protected abstract void process(Map<String,String> line);
		protected boolean camelize(){return false;};
		protected void init(String[] line){};
    }
    
    /** Groovy용이다. 근데 그루비 쓸정도면 걍 스트리밍으로 안읽는게 더 나은듯 . 고로 이 api는 망했다 */
    public void read(File file,String columnSeparator,final Closure init, final Closure callback){
		read(file,new StringMapCallback(columnSeparator){
			@Override
			protected void process(Map<String, String> map) {
				callback.call(map);
			}
			@Override
			protected void init(String[] line) {
				if(init!=null) init.call(new Object[]{line});
			}
        });
    }
	public void read(File file,String columnSeparator,final Closure callback){
    	read(file,columnSeparator,null,callback);
    }
    public void read(File file,final Closure callback){
    	read(file,new StringCallback() {
			@Override
			public void process(String line) {
				callback.call(line);
			}
		});
    }
    /** 
     * 간단한 스캐너 이다. root를 기준으로 모든 파일을 읽어 line을 반환한다.
     * text가 아니면 오류가 날듯. ㅋㅋ
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