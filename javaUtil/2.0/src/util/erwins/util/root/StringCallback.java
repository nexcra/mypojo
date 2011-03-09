package erwins.util.root;



/**
 * 한줄씩 작업을 처리하자. 
 */
public interface StringCallback {
    public void process(String line);
    
    public static interface StringsCallback {
        public void process(String ... lines);
    }
    
}