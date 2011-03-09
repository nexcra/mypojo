package erwins.util.exception;

import erwins.util.lib.StringUtil;

/**
 * Heap(캐시 및 임시 멤버필드 등)에서 자료를 찾을 수 없을때 던진다. 
 */
public class HeapNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public HeapNotFoundException() {
        super();
    }

    public HeapNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public HeapNotFoundException(String message) {
        super(message);
    }
    
    public HeapNotFoundException(String pattern,Object ... arguments) {
        super(StringUtil.format(pattern, arguments));
    }    

    public HeapNotFoundException(Throwable cause) {
        super(cause);
    }
    
}
