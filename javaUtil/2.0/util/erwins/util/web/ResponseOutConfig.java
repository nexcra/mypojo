
package erwins.util.web;


/** HttpServletResponse 등에 관한 종합 쓰기 객체. 한번 설정후 spring-bean등의 설정으로 재사용 하자. */
public class ResponseOutConfig{
	
	public static enum ContentType{
		XML("text/xml"),
		HTML("text/html"),
		JSON("application/json");
		private final String value;
		private ContentType(String value){
			this.value = value;
		}
		public String getValue() {
			return value;
		}
	}
	
	private ContentType contentType = ContentType.JSON;
	private String encode = "UTF-8";
	private boolean cache = true;
	
	private String isSuccessKey = "isSuccess";
	private String messageKey = "message";
	private boolean xmlEscape = false;
	/** Google서비스 등에서 한글지원이 안될때 사용한다. */
	private boolean javascriptEscape = false;
	
	public void setIsSuccessKey(String isSuccessKey) {
		this.isSuccessKey = isSuccessKey;
	}
	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}
	public boolean isXmlEscape() {
		return xmlEscape;
	}
	public void setXmlEscape(boolean xmlEscape) {
		this.xmlEscape = xmlEscape;
	}
	public String getIsSuccessKey() {
		return isSuccessKey;
	}
	public String getMessageKey() {
		return messageKey;
	}
	public ContentType getContentType() {
		return contentType;
	}
	public String getContentTypeString() {
		return contentType.getValue()+"; charset=" + encode;
	}
	public void setContentType(ContentType contentType) {
		this.contentType = contentType;
	}
	public String getEncode() {
		return encode;
	}
	public void setEncode(String encode) {
		this.encode = encode;
	}
	public boolean isCache() {
		return cache;
	}
	public void setCache(boolean cache) {
		this.cache = cache;
	}
	public boolean isJavascriptEscape() {
		return javascriptEscape;
	}
	public void setJavascriptEscape(boolean javascriptEscape) {
		this.javascriptEscape = javascriptEscape;
	}

}
