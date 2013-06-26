
package erwins.util.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import erwins.util.lib.FileUtil;
import erwins.util.text.CharEncodeUtil;
import erwins.util.vender.etc.OpenCsv;

/**
 * common을 이용한 파일 업로드.
 * ProgressListener는 사용하지 않는다.
 * 
 * 
 * 전자정부꺼 그냥 쓰자. 귀찮..
 */
@Deprecated
public class FileUploader{
    
    /** 이 값을 넘는 크기의 파일은 디스크에 임시 저장된다
     * 초기값은 0.01메가 */
    private static final int THRESHOLD = 1024 * 1024 * 50; //50메가
    
    /** 기본설정 */
    private static final FileUploadRename RENAME_DEFAULT = new FileUploadRename(){
        @Override
        public File nameTo(File uploadedFile) {
            return FileUtil.uniqueFileName(uploadedFile);
        }
    };
	
    private File repositoryPath;
    private Charset encoding = CharEncodeUtil.C_UTF_8;
    /** 엄로드 제한 용량 */
    private int maxUploadMb = 1024*2; //기본 2기가
    private FileUploadRename fileUploadRename = RENAME_DEFAULT;

    
    public static interface FileUploadRename{
        public File nameTo(File uploadedFile);
    }
    
    public static interface UploadItemRead{
        public void readValue(String fieldName,String value);
        /** 사용후 닫아주세요~ */
        public void readStream(String fieldName,String fileName,InputStream in);
    }
    
    /** value가 전부 나오고 난 뒤, stream이 나와야 한다. 
     * 한개의 스트림만을 지원한다. */
    public static abstract class UploadItemReadSimpleStream implements UploadItemRead{
    	public Map<String,String> param = new HashMap<String, String>();
		@Override
		public void readValue(String name, String value) {
			param.put(name, value);
		}
    }
    
    public static interface StringArrayCallback{
    	
    	public void readStringArray(String[] line,int row);
        
    }
    
    public static class UploadCsvStreamReader extends UploadItemReadSimpleStream{
    	public static final int BUFFER_SIZE = 4096;
    	private Charset encoding = CharEncodeUtil.C_MS949;
    	private final File uploadFile;
    	private final StringArrayCallback callback;
    	private String fieldName;
    	private String fileName;
		public UploadCsvStreamReader(File uploadFile, StringArrayCallback callback) {
			super();
			this.uploadFile = uploadFile;
			this.callback = callback;
		}
		@Override
		public void readStream(String fieldName,String fileName, InputStream in) {
			this.fieldName = fieldName;
			this.fileName = fileName;
			CSVWriter writer = null;
			CSVReader reader = null;
			try {
				Writer out = new OutputStreamWriter(new FileOutputStream(uploadFile),encoding);
				writer = new CSVWriter(out);
				reader = new CSVReader(new InputStreamReader(in, encoding));
				int i=0;
				for(String[] line=reader.readNext();line != null;line=reader.readNext()){
					callback.readStringArray(line,i++);
					writer.writeNext(line);
				}
				writer.flush();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}finally{
				OpenCsv.closeQuietly(reader);
				OpenCsv.closeQuietly(writer);
			}
		}
		public void setEncoding(Charset encoding) {
			this.encoding = encoding;
		}
		public String getFieldName() {
			return fieldName;
		}
		public String getFileName() {
			return fileName;
		}
    }
    
    private void validate(HttpServletRequest req){
        boolean isMultipart = ServletFileUpload.isMultipartContent(req);
        if(!isMultipart) throw new IllegalArgumentException("request is not multi/part");
    }

    /** THRESHOLD가 넘는 경우 임시 지정된 디렉토리에 템프파일이 쌓인다. 별도의 삭제 데몬을 사용하지 않음으로 적절히 삭제하자.
     * upload되는 파일은 유일이름으로 변경되어 오버라이딩 되지 않는다.
     *  */
    public UploadResult upload(HttpServletRequest req){
        validate(req);
        UploadResult result = new UploadResult();
        ServletFileUpload upload = getServletUpload(getFactory());
        try {
            FileItemIterator it = upload.getItemIterator(req); //upload.parseRequest(req); <<- 이거는 스트리밍 안되는듯
            while(it.hasNext()){
                FileItem item = (FileItem)it.next();
                String fieldName = item.getFieldName();
                //item.getf
                if(item.isFormField()) {
                    result.parameter.put(fieldName, item.getString(encoding.name()));
                }else{
                    String fileName = item.getName();
                    fileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1); //다시보기
                    File uploadedFile = new File(repositoryPath,fileName);
                    File renamed = fileUploadRename.nameTo(uploadedFile); //OS가 한글을 인식 못할 수 있음으로 실제 쓰기전 rename한다.
                    item.write(renamed); //fileItem.get();  //메모리에 모두 할당
                    result.source.put(fieldName, new UploadResultFile(renamed,fileName,fieldName));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }
    
    /** 실시간 대용량 처리나, 실시간 벨리데이션 체크 등을 하고싶을때 사용한다.
     * 스트림을 BlockingQueue으로 받아서 여러 스래드에 전달해주면 편하다.
     * StringCallback도중 멈출려면 ReadSkipException를 던지자 */
    public void uploadStream(HttpServletRequest req,UploadItemRead uploadItemRead){
        validate(req);
        ServletFileUpload upload = getServletUpload(null);
        try {
            FileItemIterator it = upload.getItemIterator(req); //upload.parseRequest(req); <<- 이거는 스트리밍 안되는듯
            while(it.hasNext()){ //이거 호출하면 아마 InputStream이 닫기는듯
                FileItemStream item = it.next();
                String fieldName = item.getFieldName();
                InputStream stream = item.openStream();
                if (item.isFormField()) {
                    uploadItemRead.readValue(fieldName, Streams.asString(stream,encoding.name()));
                } else {
                    uploadItemRead.readStream(fieldName,item.getName(),stream);
                }
            }
        } catch (Exception e) {
        	throw new RuntimeException(e);
        }
    }

    ///  =================  공통  ===================== 
    private ServletFileUpload getServletUpload(DiskFileItemFactory factory) {
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setHeaderEncoding(encoding.name());
        upload.setSizeMax(1024 * 1024 * maxUploadMb);
        //upload.setProgressListener(listener);  //리스너는 사용하지 않는다.
        return upload;
    }

    private DiskFileItemFactory getFactory() {
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(THRESHOLD);
        factory.setRepository(repositoryPath);
        return factory;
    }
    
    public static class UploadResult{
        public final Map<String,String> parameter = new HashMap<String,String>();
        public final Map<String,UploadResultFile> source = new HashMap<String,UploadResultFile>();
    }
    
    public static class UploadResultFile{
    	public final File file;
    	public final String orgFileName;
    	public final String fieldName;
		public UploadResultFile(File file, String orgFileName, String fieldName) {
			super();
			this.file = file;
			this.orgFileName = orgFileName;
			this.fieldName = fieldName;
		}
    }
    
    //=============== getter / setter ==================
    
    public File getRepositoryPath() {
        return repositoryPath;
    }
    public void setRepositoryPath(File repositoryPath) {
        this.repositoryPath = repositoryPath;
    }
    public void setMaxUploadMb(int maxUploadMb) {
        this.maxUploadMb = maxUploadMb;
    }
    /** 디폴트 값은 UTF-8 */
    public void setEncoding(Charset encoding) {
        this.encoding = encoding;
    }
    
    

}

