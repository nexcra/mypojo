package erwins.util.vender.spring;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.web.servlet.View;

import com.google.common.base.Preconditions;

import erwins.util.lib.FileUtil;
import erwins.util.vender.apache.Poi;
import erwins.util.web.WebUtil;

public class FileDownloadView implements View{
	
	private String fileName;
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
		return WebUtil.CONTENT_TYPE_DOWNLOAD;
	}

	/** 구현이 정확하지 않다. 나중에 수정할것 */
	@Override
	public void render(Map<String, ?> arg0, HttpServletRequest arg1, HttpServletResponse resp) throws Exception {
		if(fileName==null) {
			Preconditions.checkArgument(file!=null,"file이 아니라면 fileName은 필수입력값입니다");
			fileName = file.getName();
		}
		WebUtil.setFileName(resp,fileName);
		if(file!=null){
			WebUtil.downloadFile(resp, file);
			if(fileDeleteAfterDownload) FileUtil.delete(file);
		}else if(poi!=null) poi.write(resp);
		else if(data!=null){
			resp.setCharacterEncoding(encoding);
	        IOUtils.write(data, resp.getWriter(),encoding);
		}else if(in!=null){
			try {
				IOUtils.copy(in, resp.getWriter());
			}finally{
				IOUtils.closeQuietly(in);	
			}
		}else throw new IllegalArgumentException("파라메터 입력 오류");
	}
}
