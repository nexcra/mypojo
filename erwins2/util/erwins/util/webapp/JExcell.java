
package erwins.util.webapp;

import java.io.File;

import javax.servlet.http.HttpServletResponse;


/** POI를 사용할 수 없는 환경에서 유용하다. ( ex) Google App Engine ) */
public class JExcell extends JExcellRoot{
	
	public JExcell(File file){
		super(file);
	}
	
	public JExcell(HttpServletResponse resp){
		super(resp);
	}
	
	
}
