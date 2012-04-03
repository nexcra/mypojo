package erwins.util.temp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

public class AppUploader{
	
	private String encode = "EUC-KR";
	private long fileSizeMax = 1024*1024*2; //2메가?
	
	/** csv같은 파일을 받기 위함이다. 
	 * 일단 현재버전은 csv를 읽어서 List<String> 를 리턴한다. */
	public Map<String,Object> uploadTextFile(HttpServletRequest req) {
		ServletFileUpload upload = new ServletFileUpload();
		upload.setFileSizeMax(fileSizeMax);
		upload.setHeaderEncoding(encode);
		//upload.setHeaderEncoding(encode);
		Map<String,Object> map = new HashMap<String,Object>();
		try {
			FileItemIterator iterator = upload.getItemIterator(req);
			while (iterator.hasNext()) {
				FileItemStream item = iterator.next();
				if(item.getContentType()==null){
					map.put(item.getFieldName(), IOUtils.toString(item.openStream(),"UTF-8")); //일반 텍스트 입력은 UTF-8로
				}else{
					InputStream stream = item.openStream();
					InputStreamReader t = new InputStreamReader(stream,encode); //CSV은 EUC-KR이다.
					BufferedReader br = new BufferedReader(t);
					List<String> textLines = new ArrayList<String>();
					String line = null;
					while((line = br.readLine()) != null){
						textLines.add(line);
					}
					map.put(item.getFieldName(),textLines);
				}
			}
		} catch (FileUploadException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return map;
	}

}
