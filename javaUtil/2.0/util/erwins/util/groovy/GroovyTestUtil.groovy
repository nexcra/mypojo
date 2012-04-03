package erwins.util.groovy

public class GroovyTestUtil {

	/** 얼마나 걸렸나? */	
	public long time(c){
		long startTime = System.nanoTime();
		c();
		long time = startTime - System.nanoTime();
	}

}
