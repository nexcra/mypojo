package erwins.jsample;


import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class UploadServletSample extends HttpServlet {
    
    String upload_dir = null;
    @Override
    public void init(ServletConfig config) throws ServletException {
          super.init(config);  
          upload_dir = config.getServletContext().getRealPath("/upload/");
    }
 
    @SuppressWarnings("unchecked")
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
   
        // form type이 multipart/form-data 면 true 그렇지 않으면 false를 반환
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        
        if (isMultipart) {
            try {
                int yourMaxMemorySize = 1024 * 10;                 // threshold  값 설정
                long yourMaxRequestSize = 1024 * 1024 * 100;   //업로드 최대 사이즈 설정 (100M)

                File yourTempDirectory = new File(upload_dir);
                
                DiskFileItemFactory factory = new DiskFileItemFactory();
                factory.setSizeThreshold(yourMaxMemorySize);
                factory.setRepository(yourTempDirectory);                
    
                ServletFileUpload upload = new ServletFileUpload(factory);
                upload.setSizeMax(yourMaxRequestSize);          // 임시 업로드 디렉토리 설정
                upload.setHeaderEncoding("EUC_KR");               // 인코딩 설정
                
                /**
                 *  업로드 진행 상태 출력 (Watching progress)
                */
                ProgressListener progressListener = new ProgressListener(){
                   private long megaBytes = -1;
                   public void update(long pBytesRead, long pContentLength, int pItems) {
                       long mBytes = pBytesRead / 1000000;
                       if (megaBytes == mBytes) {
                           return;
                       }
                       megaBytes = mBytes;
                       System.out.println("We are currently reading item " + pItems);
                       if (pContentLength == -1) {
                           System.out.println("So far, " + pBytesRead + " bytes have been read.");
                       } else {
                           System.out.println("So far, " + pBytesRead + " of " + pContentLength
                                              + " bytes have been read.");
                       }
                   }
                };
                upload.setProgressListener(progressListener);   // 진행상태 리스너 추가
    
                String fieldName = null;
                String fieldValue = null;
                String fileName = null;
                String contentType = null;
                long sizeInBytes = 0;

                List items = upload.parseRequest(request);                
                Iterator iter = items.iterator();
                while (iter.hasNext()) {
                    FileItem item = (FileItem) iter.next();
   
                    // 정상적인 폼값 출력 및 처리
                    if (item.isFormField()) {
                        fieldName = item.getFieldName();
                        fieldValue = item.getString();
                        
                        System.out.println("-----+-----+-----+-----+-----+-----+-----+-----");
                        System.out.println("Field Name : "+fieldName);
                        System.out.println("Field Value : "+fieldValue);
                        System.out.println("-----+-----+-----+-----+-----+-----+-----+-----");
                       
                    // 업로드 파일 처리
                    } else {
                        fieldName = item.getFieldName();
                        fileName = item.getName();
                        contentType = item.getContentType();
                        sizeInBytes = item.getSize();
                        
                        System.out.println("-----+-----+-----+-----+-----+-----+-----+-----");
                        System.out.println("Field Name : "+fieldName);
                        System.out.println("File Name : "+fileName);
                        System.out.println("ContentType : "+contentType);
                        System.out.println("File Size : "+sizeInBytes);
                        System.out.println("-----+-----+-----+-----+-----+-----+-----+-----");
                        String savefile = fileName.substring(fileName.lastIndexOf("\\")+1, fileName.length());
                        File uploadedFile = new File(upload_dir+"\\"+savefile);
                        item.write(uploadedFile);
                    }
                }
 
            // 설정한 업로드 사이즈 초과시 exception 처리
            } catch (SizeLimitExceededException e) {
                e.printStackTrace();   
            // 업로드시 io등 이상 exception 처리
            } catch (FileUploadException e) {
                e.printStackTrace();
            // 기타 exception 처리
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }    
}