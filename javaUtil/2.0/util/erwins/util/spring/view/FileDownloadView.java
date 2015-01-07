package erwins.util.spring.view;

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.web.servlet.View;

import com.google.common.base.Preconditions;

import erwins.util.lib.FileUtil;
import erwins.util.text.StringUtil;
import erwins.util.vender.apache.Poi;
import erwins.util.web.WebUtil;

/** static한 로컬파일 or 인메모리 파일 다운로드일때 사용.
 * DB등의 스트리밍 다운로드에 사용되지는 않는다 */
public class FileDownloadView implements View{
	
	private String fileName;
	private boolean image = false;
	private String contentType = WebUtil.CONTENT_TYPE_DOWNLOAD;
	private String encoding = "UTF-8";
	/** 임시파일이라서 다운로드 후 삭제가 필요할때 (zip파일 등) */
	private boolean fileDeleteAfterDownload =  false;
	
	private File file;
	private Poi poi; //사실 out으로 대체되어야함
	private byte[] data;
	private InputStream in;
	
	public static FileDownloadView create(File file){
		FileDownloadView view = new FileDownloadView();
		view.file = file;
		return view;
	}
	public static FileDownloadView create(Poi poi){
		FileDownloadView view = new FileDownloadView();
		view.poi = poi;
		return view;
	}
	public static FileDownloadView create(byte[] data){
		FileDownloadView view = new FileDownloadView();
		view.data = data;
		return view;
	}
	public static FileDownloadView create(InputStream in){
		FileDownloadView view = new FileDownloadView();
		view.in = in;
		return view;
	}
	
	public FileDownloadView setFileName(String fileName) {
		this.fileName = fileName;
		return this;
	}
	public FileDownloadView setFileDeleteAfterDownload(boolean fileDeleteAfterDownload) {
		this.fileDeleteAfterDownload = fileDeleteAfterDownload;
		return this;
	}
	
	@Override
	public String getContentType() {
		return contentType;
	}

	/** 구현이 정확하지 않다. 나중에 수정할것 */
	@Override
	public void render(Map<String, ?> arg0, HttpServletRequest arg1, HttpServletResponse resp) throws Exception {
		if(fileName==null) {
			Preconditions.checkArgument(file!=null,"file이 아니라면 fileName은 필수입력값입니다");
			fileName = file.getName();
		}
		
		//=== 컨텐츠 타입 결정 ===  네이버 섬네일 서버 등에는 contentType과 컨텐츠길이가 없다면 리젝트 된다.
		if(image){
			//디폴트 컨텐츠 타입이라면 수정해준다.
			if(contentType.equals(WebUtil.CONTENT_TYPE_DOWNLOAD)){
				String ext = StringUtil.getExtention(fileName).toLowerCase();
				if(ext.equals("jpg")) ext = "jpeg"; //IE에서는 정확히 써줘야 열린다. 크롬은 jpg,jpeg 둘다 됨
				contentType = "image/" + ext;
				resp.setContentType(getContentType()); //파일 이름을 지정하지 않는다.
			}
		}else WebUtil.setFileName(resp,fileName,getContentType()); //브라우저에서 파일 형태로 다운로드 된다.
		
		
		if(file!=null){
			WebUtil.downloadFile(resp, file);
			if(fileDeleteAfterDownload) FileUtil.delete(file);
		}else if(poi!=null){
			poi.write(resp);
		}else if(data!=null){
			resp.setCharacterEncoding(encoding);
			PrintWriter writer = resp.getWriter();
	        IOUtils.write(data, writer,encoding);
	        writer.flush();
		}else if(in!=null){
			try {
				PrintWriter writer = resp.getWriter();
				IOUtils.copy(in, writer);
				writer.flush();
			}finally{
				IOUtils.closeQuietly(in);
			}
		}else throw new IllegalArgumentException("파라메터 입력 오류");
	}
	
	/** image/gif 등등으로 변경 */
	public FileDownloadView setContentType(String contentType) {
		this.contentType = contentType;
		return this;
	}
	public FileDownloadView setImage(boolean image) {
		this.image = image;
		return this;
	}
	
}
