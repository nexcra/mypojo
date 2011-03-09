package erwins.domain.talk;

/** 각 메세지는 메시지 구분자로 원자화 된다. 요청이 동시에 몰릴경우 한번의 버퍼에 여러 데이터가 같이 들어올 수 있다.
 * 이 경우 메세지 구분자로 명령 단위를 나눈다.
 * 메시지는  헤더 + 바디로 구분된다. 바디는 텍스트구분 + 라인 구분으로 테이블 형태를 가질 수 있다. */
public abstract class Protocol{
	
	private static class SEPARATOR{
		private static final String MESSAGE = "@@@";
		/** 정규식 치환자. */
		private static final String HEADER_REG = "\\|\\|";
		private static final String HEADER = "||";
		private static final String TEXT = "#@#";
		@SuppressWarnings("unused")
		private static final String LINE = "@#@";
	}
	
	public static String[] splitHeader(String body){
		return body.split(SEPARATOR.HEADER_REG);
	}
	public static String[] splitMessage(String body){
		if(!body.endsWith(SEPARATOR.MESSAGE)) throw new RuntimeException(body + " 메시지 구분자 누락.");
		return body.split(SEPARATOR.MESSAGE);
	}
	public static String[] splitText(String body){
		return body.split(SEPARATOR.TEXT);
	}
	
	
	public static String mergeText(String ... text){
		if(text.length==1) return text[0];
		boolean first = true;
		StringBuilder buff = new StringBuilder();
		for(String each : text){
			if(first) first = false;
			else buff.append(SEPARATOR.TEXT); 
			buff.append(each);
		}
		return buff.toString();
	}
	
	public static String newMessage(String header,String ... texts){
		String text = mergeText(texts);
		return header + SEPARATOR.HEADER + text + SEPARATOR.MESSAGE;
	}
	
	public static final String MESSAGE = "message";
	public static final String LOGIN = "login";
	public static final String LOGIN_INFO = "loginInfo";
	public static final String EXIT = "exit";
	public static final String LOGOUT = "logout";
	public static final String ERROR = "error";
	public static final String ERROR_LOGIN_EXIST = "errorLoginExist";
	
}