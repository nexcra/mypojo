package erwins.util.vender.mybatis;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.ResultContext;

import erwins.util.web.WebUtil;

/** 
 * 간단한 리포트 등을 다운로드할때 사용한다.  VO가 아닌 Map으로 받아서 바로 내려준다. 
 * 파라메터에 resultType="org.apache.commons.collections.map.ListOrderedMap" fetchSize="1000" 을 사용할것을 권장
 *  */
public class OpenCsvMybatisMapResultHandler extends OpenCsvMybatisResultHandler{
	
	private String[] header;
	
	public OpenCsvMybatisMapResultHandler(HttpServletResponse resp,String fileName) throws IOException{
		open(resp.getOutputStream());
		WebUtil.setFileName(resp,fileName );
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String[] resultContexttoCsv(ResultContext arg0) {
		Map<String,Object> map =  (Map<String, Object>) arg0.getResultObject();
		if(arg0.getResultCount()==1){
			header = map.keySet().toArray(new String[map.size()]);
			writeNext(header);
		}
		String[] values = new String[header.length];
		for(int i=0;i<header.length;i++){
			Object value = map.get(header[i]);
			values[i] = value==null ? "" :  value.toString();
		}
		return values;
	}
    
}
