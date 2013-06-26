package erwins.jsample.etc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * 미검증~
 **/
public class RuntimeUtil{
	
    private String path;
    private String exec;
    private List<String> arguments = new ArrayList<String>();
    
    
    public RuntimeUtil(String path,String exec){
        this.path = path; 
        this.exec = exec; 
    }
    
    public void addArg(String arg){
        arguments.add(arg);
    }
    
    public void excc(){
        try {
            Runtime.getRuntime().exec(path+exec, arguments.toArray(new String[arguments.size()]));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    

}