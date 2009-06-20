package erwins.util.lib.file;

import java.io.File;
import java.io.IOException;


/**
 * <p>이 추상 클래스는 파일 복사 기능을 제공하도록 구현해야한다.</p>
 */
public abstract class AbstractFileCopier implements IFileCopier {
    
     protected boolean copyLastModified = false;
     
     public AbstractFileCopier() {
         this(false);
     }

     /**
      * 
      * @param copyLastModified <code>true</code>이면 파일의 최종변경일도 일치시킨다.
      */
     public AbstractFileCopier(boolean copyLastModified) {
        this.copyLastModified = copyLastModified;
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
    public abstract int copyFile(File srcFile, File destFile, boolean overwrite) throws IOException;
    
    /**
     * <p>디렉토리를 복사한다.</p>
     * <p>원본이 디렉토리일 경우만 복사하고, 디렉토리가 아닐 경우는 에러가 발생한다.</p>
     * 
     * @param srcDir 원본 디렉토리
     * @param destDir 대상 디렉토리
     * @param overwrite 덮어쓰기 여부
     * @param copySubdirectory 하위 디렉토리 복사 여부
     * @return 복사한 디렉토리와 파일 갯수
     * @throws IOException
     */
    public int copyDirectory(File srcDir, File destDir, boolean overwrite, boolean copySubdirectory) throws IOException {
        if (!srcDir.isDirectory()) {
            throw new IOException("");
        }
        int count = 0;
        if (overwrite || !destDir.exists()) {
            Files.mkdir(destDir); //혹시 없으면 만든다.
            File[] files = srcDir.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    File src = files[i];
                    File dest = new File(destDir, files[i].getPath().substring(srcDir.getPath().length()));
                    if (src.isFile()) {
                        count += copyFile(src, dest, overwrite);    
                    } else if (copySubdirectory && src.isDirectory()) {
                        count += copyDirectory(src, dest, overwrite, copySubdirectory); 
                    }
                }
            }
            if (copyLastModified) {
                destDir.setLastModified(srcDir.lastModified());
            }
            count++;
        }
        return count;
    }

    /**
     * <p>파일이나 디렉토리를 복사한다.</p>
     * <p>원본이 디렉토리일 경우 해당 디렉토리내의 파일(하위 디렉토리 제외)만을 복사한다.</p>
     * 
     * @param source 원본 파일 또는 디렉토리
     * @param dest 대상 파일 또는 디렉토리
     * @param overwrite 덮어쓰기 여부
     * @return 복사한 디렉토리와 파일 갯수
     * @throws IOException
     */
    public int copy(File source, File dest, boolean overwrite) throws IOException {
        return copy(source, dest, overwrite, false);
    }
    
    /**
     * <p>파일이나 디렉토리를 복사한다.</p>
     * 
     * @param source 원본 파일 또는 디렉토리
     * @param dest 대상 파일 또는 디렉토리
     * @param overwrite 덮어쓰기 여부
     * @param copySubdirectory 하위 디렉토리 복사 여부
     * 
     * @return 복사한 디렉토리와 파일 갯수
     * @throws IOException
     */
    public int copy(File source, File dest, boolean overwrite, boolean copySubdirectory) throws IOException {
        int count = 0;
        if (source.isFile()) {
            count += copyFile(source, dest, overwrite);
        } else if (source.isDirectory()){
            count += copyDirectory(source, dest, overwrite, copySubdirectory);
        }
        return count;
    }
}
