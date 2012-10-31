
package erwins.util.web;

import java.io.File;
import java.io.InputStream;
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

import erwins.util.exception.ExceptionUtil;
import erwins.util.lib.CharEncodeUtil;
import erwins.util.lib.FileUtil;

/**
 * common을 이용한 파일 업로드.
 * ProgressListener는 사용하지 않는다.
 */
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
        public void readValue(String name,String value);
        /** 사용후 닫아주세요~ */
        public void readStream(String name,InputStream in);
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
                if(item.isFormField()) {
                    result.parameter.put(item.getFieldName(), item.getString(encoding.name()));
                }else{
                    String fileName = item.getName();
                    fileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1); //다시보기
                    File uploadedFile = new File(repositoryPath,fileName);
                    File renamed = fileUploadRename.nameTo(uploadedFile); //OS가 한글을 인식 못할 수 있음으로 실제 쓰기전 rename한다.
                    item.write(renamed); //fileItem.get();  //메모리에 모두 할당
                    result.source.put(fileName, renamed);
                }
            }
        } catch (Exception e) {
            ExceptionUtil.castToRuntimeException(e);
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
                String name = item.getFieldName();
                InputStream stream = item.openStream();
                if (item.isFormField()) {
                    uploadItemRead.readValue(name, Streams.asString(stream,encoding.name()));
                } else {
                    uploadItemRead.readStream(name, stream);
                }
            }
        } catch (Exception e) {
            ExceptionUtil.castToRuntimeException(e);
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
        public final Map<String,File> source = new HashMap<String,File>();
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

    /** 디폴트 값은 1024*2 인 2기가. */
    public void setMaxMb(int maxMb) {
        this.maxUploadMb = maxMb;
    }
    
    

}

