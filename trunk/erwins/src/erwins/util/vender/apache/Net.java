
package erwins.util.vender.apache;

import java.io.File;
import java.io.IOException;

import org.apache.commons.net.ftp.FTPFile;

import erwins.util.lib.Strings;

/**
 * 추후 페이징 로직 추가하자.
 * @author  erwins(my.pojo@gmail.com)
 */
public class Net extends NetRoot {
    
    
    // ===========================================================================================
    //                                    down
    // ===========================================================================================

    /** 
     * 작성 샘플이다.
     */
    public static void downloadSample(String serverIp, String id, String pass,String ftpRoot, String clientRoot) {
        Net net = new Net();
        try {
            net.connect(serverIp,id,pass);
            net.setRoots(ftpRoot,clientRoot).downloadAll();
            net.logging();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            net.disconnect();
        }
    }    
    
    /**
     * 정해진 조건에 따라 다운로드 한다. 
     */
    public void downloadAll() throws IOException {
        File file = new File(clientOrToRoot);
        if (!file.isDirectory()) file.mkdirs();
        loopAndDownload(ftpRoot);
    }

    /**
     * 순회하면서 파일을 복사한다.
     */
    private void loopAndDownload(String ftpFullPath) throws IOException {
        FTPFile[] ftpFiles = ftpClient.listFiles(ftpFullPath);
        if (ftpFiles == null || ftpFiles.length==0){
            log.debug("there are no file");
            return;
        }
        for (FTPFile ftpFile : ftpFiles) {
            String newFtpFullPath = ftpFullPath + "/" + ftpFile.getName();
            if (ftpFile.isFile()) {
                if (!isAble(ftpFile.getName())) continue;
                download(newFtpFullPath,getNameByFtpRoot(newFtpFullPath));
            } else if (ftpFile.isDirectory()) {
                if(!isAbleFolder(ftpFile.getName())) continue;
                File dir = new File(getNameByFtpRoot(newFtpFullPath));
                dir.mkdir();
                loopAndDownload(newFtpFullPath);
            }
        }
    }

    
    // ===========================================================================================
    //                                    move
    // ===========================================================================================    
    
    public static void moveSample(String serverIp, String id, String pass,String ftpRoot,String ftpFolderName, String ftpToRoot) {
        Net m1 = new Net();
        try {
            m1.connect(serverIp,id,pass);
            m1.setRoots(ftpRoot, ftpToRoot).setAbleFolders(ftpFolderName).moveAll();
            m1.logging();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            m1.disconnect();
        }
    }
    
    /**
     * 정해진 조건에 따라 원격지의 파일을 이동시킨다. 
     */
    public void moveAll() throws IOException {
        File file = new File(clientOrToRoot);
        if (!file.isDirectory()) file.mkdirs();
        loopAndMove(ftpRoot);
    }

    /**
     * 순회하면서 파일을 복사한다.
     */
    private void loopAndMove(String ftpFullPath) throws IOException {
        FTPFile[] ftpFiles = ftpClient.listFiles(ftpFullPath);
        if (ftpFiles == null || ftpFiles.length==0){
            log.debug("there are no file");
            return;
        }
        for (FTPFile ftpFile : ftpFiles) {
            String newFtpFullPath = ftpFullPath + "/" + ftpFile.getName();
            if (ftpFile.isFile()) {
                if (!isAble(ftpFile.getName())) continue;
                moveSingleFile(newFtpFullPath,getNameByFtpRoot(newFtpFullPath));
            } else if (ftpFile.isDirectory()) {
                if(!isAbleFolder(ftpFile.getName())) continue;
                makeDir(getNameByFtpRoot(newFtpFullPath));
                loopAndMove(newFtpFullPath);
                deleteDir(newFtpFullPath);
            }
        }
    }
    
    
    // ===========================================================================================
    //                                    upload
    // ===========================================================================================
    
    /**
     * 작성 샘플이다/.
     */
    public static void uploadSample(String serverIp, String id, String pass,String ftpRoot, String clientRoot) {
        Net net = null;
        try {
            net = new Net();
            net.connect(serverIp,id,pass);
            net.setRoots(ftpRoot,clientRoot).uploadAll();
            net.logging();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            net.disconnect();
        }
    }
    
    /**
     * 정해진 조건에 따라 업로드 한다. 
     */
    public void uploadAll() throws IOException {
        File root = new File(clientOrToRoot);
        if(!root.isDirectory()) throw new RuntimeException(root + " must be directory");
        loopAndUpload(root);
    }

    /**
     * 순회하면서 파일을 복사한다.
     */
    private void loopAndUpload(File clientFile) throws IOException {
        
        for(File file : clientFile.listFiles()){
            if(file.isHidden()) continue;
            if(file.isFile()){
                if (!isAble(file.getName())) continue;
                upload(file); // file.getName(), file.getSize() 등등..
            }else if(file.isDirectory()){
                if(!isAbleFolder(file.getName())) continue;
                makeDir(getNameByClient(file.getAbsolutePath()));
                loopAndUpload(file);
            }
        }
    }
    
    // ===========================================================================================
    //                                    condition
    // ===========================================================================================
    
    /**
     * 모두 절대경로를 적어야 한다. 필수입력항목이다. 
     * Unix계열의 경우 맨앞에 /로 root를 표시 
     * 마지막이 /로 끝나면 /를 삭제해준다.
     */
    public Net setRoots(String ftpRoot, String clientRoot) {
        this.ftpRoot =  Strings.endsWith(ftpRoot, "/") ? ftpRoot.substring(0,ftpRoot.length()-1) : ftpRoot;
        this.clientOrToRoot = Strings.endsWith(clientRoot, "/") ? clientRoot.substring(0,clientRoot.length()-1) : clientRoot;
        return this;
    }

    /**
     * @param includeExtentions
     * @return
     * @uml.property  name="ableExtentions"
     */
    public Net setAbleExtentions(String... includeExtentions) {
        this.ableExtentions = includeExtentions;
        return this;
    }
    
    /**
     * like검색이다.
     * @uml.property  name="ableNames"
     */
    public Net setAbleNames(String ... includFileNames) {
        this.ableNames = includFileNames;
        return this;
    }
    
    /**
     * and검색이다.
     */
    public Net setAbleFolders(String ableFolder) {
        this.ableFolder = ableFolder;
        return this;
    }

    /**
     * 디폴트로 true이다.
     * @uml.property  name="overwrite"
     */
    public Net setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
        return this;
    }    
    
    /**
     * 디폴트로 true이다. false시 디렉토리를 타고가지 않는다.
     * @uml.property  name="allDirectory"
     */    
    public Net setAllDirectory(boolean allDirectory) {
        this.allDirectory = allDirectory;
        return this;
    }    
    
}
