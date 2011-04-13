package erwins.jsample;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * @author  Administrator
 */
public class UploadFileListener {
	private HttpServletRequest request;
	private HttpServletResponse response;
	private String path;
	private ProgressListener listener;

	public UploadFileListener(HttpServletRequest request, HttpServletResponse response, String path) {
		this.request = request;
		this.response = response;
		setPath(path);
	}
	/**
     * @param path
     * @uml.property  name="path"
     */
	public void setPath(String path) {
		this.path = path;
		File f = new File(path);
		if(!f.exists()) {
			f.mkdirs();
		}
	}
	/**
     * @param listener
     * @uml.property  name="listener"
     */
	public void setListener(ProgressListener listener) {
		this.listener = listener;
		upload();
	}
	private void upload() {
	    
	    boolean isMultipart = ServletFileUpload.isMultipartContent(request);
	    
	    if(!isMultipart) return;
	    
	    int yourMaxMemorySize = 1024 * 200;                 // threshold  값 설정 (0.2M?) 초기값은 0.01메가
        long yourMaxRequestSize = 1024 * 1024 * 1024 * 2;   //업로드 최대 사이즈 설정 (2G)
	    
        DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(yourMaxMemorySize);
        factory.setRepository(new File(path));
		
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setHeaderEncoding("EUC-KR"); //수정!!
		upload.setSizeMax(yourMaxRequestSize);          // 임시 업로드 디렉토리 설정
		upload.setProgressListener(listener);
		
		FileItem item = null;
		List<?> items;
		try {
			items = upload.parseRequest(request);
			for(Object aItem : items) {
				item = (FileItem)aItem;
				if(item.isFormField()) {
				    System.out.println(MessageFormat.format("name=[{0}] value=[{1}]", item.getFieldName(),item.getString()));
				}else{
                    String file = item.getName();
                    file = file.substring(file.lastIndexOf(File.separator) + 1);
                    
                    File uploadedFile = new File(path + File.separator + file);
                    int i=1;
                    while(uploadedFile.isFile() || uploadedFile.isDirectory()){                        
                        uploadedFile = new File(path + File.separator + i + file);
                    }
                    item.write(uploadedFile);		
                    //fileItem.get();  //메모리에 모두 할당
				}
			}
			report("Upload OK");
		} catch (FileUploadException e) {
			e.printStackTrace();
			report("Upload Fail");
		} catch (Exception e) {
			e.printStackTrace();
			report("Upload Fail");
		}
	}

	private void report(String message) {
		try {
			response.getWriter().print(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
