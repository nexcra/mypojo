package erwins.util.tools;

import org.apache.commons.io.IOUtils;

public class StringBuilder2{

    private final StringBuilder the = new StringBuilder();

    @Override
	public String toString() {
		return the.toString();
	}
    
    public StringBuilder2 append(Object append) {
    	the.append(append);
    	return this;
    }

    public StringBuilder2 appendLine(Object append) {
    	the.append(append);
    	the.append(IOUtils.LINE_SEPARATOR);
        return this;
    }
    
    public StringBuilder2 appendWord(Object append) {
    	the.append(append);
    	the.append(" ");
    	return this;
    }
    public StringBuilder2 appendComma(Object append) {
    	the.append(append);
    	the.append(",");
    	return this;
    }
}