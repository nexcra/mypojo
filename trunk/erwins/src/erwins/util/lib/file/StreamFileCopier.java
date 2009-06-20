package erwins.util.lib.file;


import java.io.*;
import java.text.MessageFormat;

/**
 * <p>이 클래스는 Stream을 이용하여, 파일 복사 기능을 제공합니다. </p>
 * 
 * 
 * @author <a href="mailto:kangwoo@jarusoft.com">kangwoo</a>
 * @since 1.0
 */
public class StreamFileCopier extends AbstractFileCopier {
    

    public StreamFileCopier() {
        super(false);
    }
    
    /**
     * 
     * @param copyLastModified <code>true</code>이면 파일의 최종변경일도 일치시킨다.
     */
    public StreamFileCopier(boolean copyLastModified) {
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
            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;
            try {
                bis = new BufferedInputStream(new FileInputStream(srcFile));
                bos = new BufferedOutputStream(new FileOutputStream(destFile));
                byte buffer[] = new byte[1024];
                for (int cnt; (cnt = bis.read(buffer)) != -1;) {
                    bos.write(buffer, 0, cnt);
                }
            } finally {
                if (bos != null) {
                    try {
                        bos.close();
                    } catch (IOException ioex) {
                    }
                }
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException ioex) {
                    }
                }
            }
            if (copyLastModified) {
                destFile.setLastModified(srcFile.lastModified());
            }
            count++;
        }
        return count;
    }
}

