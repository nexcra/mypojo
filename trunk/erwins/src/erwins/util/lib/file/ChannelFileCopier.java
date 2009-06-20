package erwins.util.lib.file;
import java.io.*;
import java.nio.channels.FileChannel;
import java.text.MessageFormat;

/**
 * <p>이 클래스는 NIO Channel을 이용해서, 파일 복사 기능을 제공합니다. </p>
 * 
 * 
 * @author <a href="mailto:kangwoo@jarusoft.com">kangwoo</a>
 * @since 1.0
 */
public class ChannelFileCopier extends AbstractFileCopier {
    
    private boolean copyLastModified = false;
    
    
    public ChannelFileCopier() {
        super(false);
    }
    
    /**
     * 
     * @param copyLastModified <code>true</code>이면 파일의 최종변경일도 일치시킨다.
     */
    public ChannelFileCopier(boolean copyLastModified) {
        super(copyLastModified);
    }
    
    /**
     * <p>파일을 복사한다.</p>
     * <p>원본이 파일일 경우 해당 파일만 복사하고, 파일이 아닐 경우는 에러가 발생한다.</p>
     * 
     * @param srcFile
     * @param destFile
     * @param overwrite 덮어쓰기 여부
     * @return 복사한 파일 갯수
     * @throws IOException
     */
    public int copyFile(File srcFile, File destFile, boolean overwrite) throws IOException {
        if (!srcFile.isFile()) {
            
            throw new IOException(MessageFormat.format("'{0}' is not File.", srcFile.getAbsolutePath()));
        }
        int count = 0;
        if (overwrite || !destFile.exists()) {
            FileInputStream fis = null;
            FileOutputStream fos = null;
            FileChannel fic = null;
            FileChannel foc = null;
            try {
                fis = new FileInputStream(srcFile);
                fos = new FileOutputStream(destFile);
                fic = fis.getChannel();
                foc = fos.getChannel();
                
                fic.transferTo(0, fic.size(), foc);
            } finally {
                if (foc != null) { try { foc.close(); } catch (IOException e) {}}
                if (fos != null) { try { fos.close(); } catch (IOException e) {}}
                if (fic != null) { try { fic.close(); } catch (IOException e) {}}
                if (fis != null) { try { fis.close(); } catch (IOException e) {}}
            }
            if (copyLastModified) {
                destFile.setLastModified(srcFile.lastModified());
            }
            count++;
        }
        return count;
    }
}
