package erwins.util.exception;

/**
 * 다운로드의 경우 서버에서는 Ajax호출로 처리되나 클라이언트에서는 jsp요청으로 다룬다.
 * 즉 예외 발생시 처리하기가 매우 까다롭다. 따라서 서버에서 Ajax로 취급해서 처리중 오류가 발생하면 다음 예외를 던진다. 
 */
public class DownloadException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DownloadException() {
        super();
    }

    public DownloadException(String message, Throwable cause) {
        super(message, cause);
    }

    public DownloadException(String message) {
        super(message);
    }

    public DownloadException(Throwable cause) {
        super(cause);
    }
    
}
