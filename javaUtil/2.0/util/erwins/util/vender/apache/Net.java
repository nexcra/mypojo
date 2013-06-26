
package erwins.util.vender.apache;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.net.ftp.FTPFile;

import com.google.common.collect.Lists;

import erwins.util.lib.FileUtil;
import erwins.util.lib.CollectionUtil;

/**
 * 추후 페이징 로직 추가하자. 나중에~~ try catch를 없애는 commad패턴으로 수정하자.
 * 나중에 실제 사용하게 되면 고쳐보자
 */
@Deprecated
public class Net extends NetRoot {

    // ===========================================================================================
    //                                    down
    // ===========================================================================================

    /**
     * 작성 샘플이다.
     */
    public static void downloadSample(String serverIp, String id, String pass, String ftpRoot, String clientRoot) {
        Net net = new Net();
        try {
            net.connect(serverIp, id, pass);
            net.setRoots(ftpRoot, clientRoot).downloadAll();
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

    private void loopAndDownload(String ftpFullPath) throws IOException {
        FTPFile[] ftpFiles = ftpClient.listFiles(ftpFullPath);
        if (ftpFiles == null || ftpFiles.length == 0) {
            ftpLog.log(FtpAction.ERROR,"there are no file");//is?
            return;
        }
        for (FTPFile ftpFile : ftpFiles) {
            String newFtpFullPath = ftpFullPath + "/" + ftpFile.getName();
            if (ftpFile.isFile()) {
                if (!isAble(ftpFile.getName())) continue;
                download(newFtpFullPath);
            } else if (ftpFile.isDirectory()) {
                if (!isAbleFolder(ftpFile.getName())) continue;
                File dir = new File(getNameByFtpRoot(newFtpFullPath));
                dir.mkdir();
                loopAndDownload(newFtpFullPath);
            }
        }
    }

    // ===========================================================================================
    //                                    move
    // ===========================================================================================    

    public static void moveSample(String serverIp, String id, String pass, String ftpRoot, String ftpFolderName, String ftpToRoot) {
        Net m1 = new Net();
        try {
            m1.connect(serverIp, id, pass);
            m1.setRoots(ftpRoot, ftpToRoot).setAbleFolders(ftpFolderName).moveAll();
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

    private void loopAndMove(String ftpFullPath) throws IOException {
        FTPFile[] ftpFiles = ftpClient.listFiles(ftpFullPath);
        if (ftpFiles == null || ftpFiles.length == 0) {
            ftpLog.log(FtpAction.ERROR,"there are no file");
            return;
        }
        for (FTPFile ftpFile : ftpFiles) {
            String newFtpFullPath = ftpFullPath + "/" + ftpFile.getName();
            if (ftpFile.isFile()) {
                if (!isAble(ftpFile.getName())) continue;
                moveSingleFile(newFtpFullPath, getNameByFtpRoot(newFtpFullPath));
            } else if (ftpFile.isDirectory() && allDirectory) {
                if (!isAbleFolder(ftpFile.getName())) continue;
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
    public static void uploadSample(String serverIp, String id, String pass, String ftpRoot, String clientRoot) {
        Net net = null;
        try {
            net = new Net();
            net.connect(serverIp, id, pass);
            net.setRoots(ftpRoot, clientRoot).uploadAll();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            if (net != null) net.disconnect();
        }
    }

    /**
     * 정해진 조건에 따라 업로드 한다.
     */
    public void uploadAll() throws IOException {
        File root = new File(clientOrToRoot);
        if (!root.isDirectory()) throw new RuntimeException(root + " must be directory");
        loopAndUpload(root);
    }

    private void loopAndUpload(File clientFile) throws IOException {
        for (File file : clientFile.listFiles()) {
            if (file.isHidden()) continue;
            if (file.isFile()) {
                if (!isAble(file.getName())) continue;
                upload(file); // file.getName(), file.getSize() 등등..
            } else if (file.isDirectory()) {
                if (!isAbleFolder(file.getName())) continue;
                makeDir(getNameByClient(file));
                loopAndUpload(file);
            }
        }
    }

    // ===========================================================================================
    //                                    synch 
    // ===========================================================================================
    
    public Synch commit(){ return new Commit(); }
    public Synch update(){ return new Update(); }
    public Synch commitLog(){ return new CommitLog(); }
    public Synch updateLog(){ return new UpdateLog(); }

    public synchronized void synchronize(Synch type) throws IOException {
        File file = new File(clientOrToRoot);
        if (!file.isDirectory()) file.mkdirs();
        loopAndSynch(file, ftpRoot,type);
    }

    /**
     * 순회하면서 파일을 복사한다.
     */
    private void loopAndSynch(File localDir, String ftpFullPath,Synch synch) throws IOException {
        File[] localFiles = localDir.listFiles();
        /** 이게 매치가 안되면 로컬에는 있으나 FTP에는 없는 파일이다. 즉 업로드할것이다. */
        List<File> isNoMatch = Lists.newArrayList(localFiles);

        FTPFile[] ftpFiles = ftpClient.listFiles(ftpFullPath);
        for (int i = 0; i < ftpFiles.length; i++) {
            String newFtpFullPath = ftpFullPath + "/" + ftpFiles[i].getName();
            
            /** 서버의 파일과 로컬 파일이 일치하는지? */
            boolean match = false;
            for (File each : localFiles){
                if (each.getName().equals(ftpFiles[i].getName())){
                    isNoMatch.remove(each);
                    if(each.isDirectory()) loopAndSynch(each,newFtpFullPath,synch);
                    match =  true;
                    break; 
                }
            }
            if(match) continue;
            synch.noLocalFile(ftpFiles[i], ftpFullPath);
        }
        synch.noServerFile(isNoMatch);
    }
    
    /** 각 버전에 따라 동기화 한다. 로컬파일 기준으로 FTP서버와 동기화한다. 즉 로컬에서 삭제하면 FTP서버의 파일도 삭제된다. 이작업 도중 파일이 움직이면 안된다. */
    public interface Synch{
        /** 서버에는 파일이 있으나 로컬에는 없는 경우. */
        public void noLocalFile(FTPFile ftpFile,String root)  throws IOException ;
        /** 로컬에는 파일이 있으나 서버에는 없는경우.  */
        public void noServerFile(List<File> isNoMatch)  throws IOException ;
    }
    
    /** 로컬을 수정해서 서버로 반영할때. */
    private class Commit implements Synch{
        public void noLocalFile(FTPFile ftpFile,String root) throws IOException {
            if(ftpFile.getName().endsWith(".")) return;
            delete(root, ftpFile);
        }
        public void noServerFile(List<File> isNoMatch)  throws IOException {
            for (File each : isNoMatch){
                upload(each);
                if(each.isDirectory()){
                    String newFtpFullPath = getNameByClient(each);
                    loopAndSynch(each,newFtpFullPath,this);
                }
            }
        }
    }
    
    /** 로컬을 수정해서 서버로 반영할때. */
    private class CommitLog implements Synch{
    	public void noLocalFile(FTPFile ftpFile,String root) throws IOException {
    		if(ftpFile.getName().endsWith(".")) return;
    		ftpLog.log(FtpAction.FTP_FILE_DELETED, ftpFile.getName());
    	}
    	public void noServerFile(List<File> isNoMatch)  throws IOException {
    		for (File each : isNoMatch){
    			String uploadName = getNameByClient(each);
    			ftpLog.logWrite(FtpAction.UPLOADED,"{0} => {1}", each.getAbsolutePath(),uploadName);
    			if(each.isDirectory()){
    				String newFtpFullPath = getNameByClient(each);
    				loopAndSynch(each,newFtpFullPath,this);
    			}
    		}
    	}
    }
    
    /** 서버의 수정분을 로컬로 반영할때. */
    private class Update implements Synch{
        public void noLocalFile(FTPFile ftpFile,String root) throws IOException {
            if(ftpFile.getName().endsWith(".")) return;
            String newFtpFullPath = root + "/" + ftpFile.getName();
            if(ftpFile.isFile()) download(newFtpFullPath);
            else{
                File file = new File(getNameByFtpRoot(newFtpFullPath));
                file.mkdir();
                loopAndSynch(file,newFtpFullPath,this);
            }
        }
        public void noServerFile(List<File> isNoMatch)  throws IOException {
            for (File each : isNoMatch){
                if(FileUtil.deleteRecursively(each)) ftpLog.log(FtpAction.LOCAL_FILE_DELETED, each.getAbsolutePath());
                else ftpLog.log(FtpAction.ERROR, each.getAbsolutePath() + " : 로컬 파일/폴더 삭제 실패");
            }
        }
    }
    
    /** 서버의 수정분을 로컬로 반영할때 Log */
    private class UpdateLog implements Synch{
    	public void noLocalFile(FTPFile ftpFile,String root) throws IOException {
    		if(ftpFile.getName().endsWith(".")) return;
    		String newFtpFullPath = root + "/" + ftpFile.getName();
    		if(ftpFile.isFile()) {
    			String localFilePath = getNameByFtpRoot(newFtpFullPath);
    			ftpLog.logWrite(FtpAction.DOWNLOADED,"{0} => {1}", newFtpFullPath,localFilePath);
    		}
    		else{
    			File file = new File(getNameByFtpRoot(newFtpFullPath));
    			//file.mkdir();
    			loopAndSynch(file,newFtpFullPath,this);
    		}
    	}
    	public void noServerFile(List<File> isNoMatch)  throws IOException {
    		for (File each : isNoMatch) ftpLog.log(FtpAction.LOCAL_FILE_DELETED, each.getAbsolutePath());
    	}
    }

    // ===========================================================================================
    //                                    condition
    // ===========================================================================================

    /**
     * 모두 절대경로를 적어야 한다. 필수입력항목이다. Unix계열의 경우 맨앞에 /로 root를 표시 마지막이 /로 끝나면 /를
     * 삭제해준다.
     */
    public Net setRoots(String ftpRoot, String clientRoot) {
        this.ftpRoot = ftpRoot.endsWith("/") ? ftpRoot.substring(0, ftpRoot.length() - 1) : ftpRoot;
        this.clientOrToRoot = clientRoot.endsWith("/") ? clientRoot.substring(0, clientRoot.length() - 1) : clientRoot;
        return this;
    }

    /**
     * 해당하는 확장자만 다운로드 한다.
     */
    public Net setAbleExtentions(String... includeExtentions) {
        this.ableExtentions = includeExtentions;
        return this;
    }

    /**
     * like검색이다.
     */
    public Net setAbleNames(String... includFileNames) {
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
     * @uml.property name="overwrite"
     */
    public Net setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
        return this;
    }

    /**
     * 디폴트로 true이다. false시 디렉토리를 타고가지 않는다.
     * @uml.property name="allDirectory"
     */
    public Net setAllDirectory(boolean allDirectory) {
        this.allDirectory = allDirectory;
        return this;
    }

}
