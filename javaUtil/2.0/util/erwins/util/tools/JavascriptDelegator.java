package erwins.util.tools;

import java.io.File;
import java.io.IOException;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import lombok.experimental.Delegate;
import erwins.util.lib.FileUtil;




/**
 * 양쪽 다 Delegate 하는 스크립트 파싱 도우미~
 * http://docs.oracle.com/javase/6/docs/technotes/guides/scripting/programmer_guide/ 참고
 *  */
public class JavascriptDelegator {
	
	private String encoding = "UTF-8";
	@Delegate
	private ScriptEngine engine;
	@Delegate
	private Invocable inv;
	
	public JavascriptDelegator(){
		ScriptEngineManager factory = new ScriptEngineManager();
        engine = factory.getEngineByName("JavaScript");
        inv = (Invocable) engine;
	}
	
	/** 파일 내용을 통째로  eval 한다 */
	public Object eval(File file) throws ScriptException, IOException{
		return engine.eval(FileUtil.readFileToString(file,encoding));
	}
	

}
