package erwins.util.vender.apache;

import java.io.IOException;
import java.util.Date;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Layout;

import erwins.util.lib.ReflectionUtil.Fields;
import erwins.util.lib.ReflectionUtil.Methods;

/**  야메로 만들었다. 이거보다 다음 버전 어펜더가 있다니 그걸 사용하자. */
public class CustomRollingFileAppender extends DailyRollingFileAppender{
	
	public CustomRollingFileAppender() {
		super();
	}

	public CustomRollingFileAppender(Layout layout, String filename, String datePattern) throws IOException {
		super(layout, filename, datePattern);
	}
	
	private Fields now = new Fields(DailyRollingFileAppender.class,"now");
	private Methods rollOver = new Methods(DailyRollingFileAppender.class,"rollOver");

	/** 강제로 다음 파일을 진행한다.
	 * 3일 데이터가 쌓여있고, 4일로 넘어갔지만, 4일에 로그가 하나도 쌓이지 않으면 3일 데이터가 리네임 되지 않는다. 
	 * 이를 리네임 되도록 강제로 세팅해준다. */
	public void rollOverForce() throws IOException {
		Date date = (Date) now.get(this);
		date.setTime(System.currentTimeMillis());
		rollOver.invoke(this);
	}
    
}
