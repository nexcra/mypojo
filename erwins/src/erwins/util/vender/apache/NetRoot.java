
package erwins.util.vender.apache;

import java.io.*;
import java.text.MessageFormat;

import org.apache.commons.net.ftp.*;
import org.apache.log4j.Logger;

import erwins.util.lib.Strings;

/**
 * Ftp 등등을 래핑한다. 필요할때마다 로직을 추가하자. 
 * ex)1 .. ftpClient.changeWorkingDirectory("/public"); boolean result = ftpClient.makeDirectory("oops"); // /public/oops 절대경로를 적지 않아도 된다
 * @since  2009-05-15 테스트 완료
 * http://www.takeone.pe.kr/94 참고.
 * @author  erwins(my.pojo@gmail.com)
 */
public abstract class NetRoot {
    
    /** 완료되는 요구된 파일 활동 okay. */
    public static final int SUCCESS = 250;
    /** 요구된 파일 활동 & 팬딩. 즉 다음 커맨드를 주어야 정상 실행 */ 
    public static final int ACCEPT_AND_WAIT = 350;

    protected Logger log = Logger.getLogger(this.getClass());
    
    protected FTPClient ftpClient = null;

    /** ftp상의 root */
    protected String ftpRoot = "/";
    /** 
     * 1. 로컬 PC상의 업로드/다운로드할 경로
     * 2. ftp상의 파일을 이동시킬 Root경로 move메소드에서 사용된다. 
     * */
    protected String clientOrToRoot = "/";
    
    protected boolean overwrite = true;
    protected boolean allDirectory = true;
    protected String[] ableNames;
    protected String[] ableExtentions;
    protected String ableFolder;
    
    private int downloaded;
    private int uploaded;
    private int moved;
    private int deleted;
    private int directoryMaked;
    private int directorydeleted;
    /** Synch할때만 사용된다. */
    protected int localFiledeleted;

    /**
     * FileType : 이 값을 설정하지 않으면 디폴트는 ASCII 이다 
     * ( FTP.BINARY_FILE_TYPE, FTP.ASCII_FILE_TYPE, FTP.EBCDIC_FILE_TYPE, FTP.IMAGE_FILE_TYPE ,FTP.LOCAL_FILE_TYPE ) 
     * FileTransferMode : 이값을 설정하지 않으면 디폴트는 FTP.STREAM_TRANSFER_MODE 이다
     * ( FTP.BLOCK_TRANSFER_MODE,FTP.COMPRESSED_TRANSFER_MODE)
     */
    public NetRoot() {
        ftpClient = new FTPClient();
        ftpClient.setControlEncoding("euc-kr"); // 한글파일명 때문에 디폴트 인코딩을 euc-kr로 합니다
    }

    /**
     * connect 후 login한다. 
     */
    public void connect(String serverIp, String id, String pass) throws IOException {
        ftpClient.connect(serverIp);
        log.debug("connect to "+ serverIp);
        int reply = ftpClient.getReplyCode(); // 응답코드가 비정상이면 종료합니다
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftpClient.disconnect();
            throw new IOException("FTP server refused connection.");
        }
        log.debug("connectedMessage : "+ftpClient.getReplyString());
        ftpClient.setSoTimeout(10000); // 현재 커넥션 timeout을 millisecond 값으로 입력합니다
        if(!ftpClient.login(id, pass)) throw new IOException("login fail"); 
            
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        ftpClient.setFileTransferMode(FTP.STREAM_TRANSFER_MODE);
    }

    /**
     * 연결을 종료한다. logout은 가능하다면 한다.
     */
    public void disconnect(){
        if (ftpClient == null) return;
        try {
            ftpClient.logout();
        }
        catch (Exception e){
            //login이 안되어있을수도 있다. 아무것도 하지 않는다.
        }finally{
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                    log.debug("disconnected");
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }
    
    // ===========================================================================================
    //                                  able  
    // ===========================================================================================

    /** 
     * 조건이 없으면 필터하지 않는다.
     * 최초 폴더만 사용 가능하다.
     **/
    protected boolean isAbleFolder(String fileName) {
        if(!allDirectory) return false;
        if (fileName.equals(".") || fileName.equals("..")) return false;
        if (ableFolder == null) return true;
        boolean retult = Strings.isEquals(fileName, ableFolder);
        if(retult) ableFolder = null;
        return retult;
    }
    
    /**
     * 이들중 하나라도 해당하지 않으면 false를 리턴한다.
     * 즉 and조건이다. 
     */
    protected boolean isAble(String fimeName) {
        if(!isAbleExt(fimeName)) return false;
        if(!isAbleName(fimeName)) return false;
        return true;
    }

    /** 조건이 없으면 필터하지 않는다. */
    protected boolean isAbleExt(String fimeName) {
        if (ableExtentions == null) return true;
        return Strings.isEquals(Strings.getExtention(fimeName), ableExtentions);
    }
    
    /** 조건이 없으면 필터하지 않는다. */
    protected boolean isAbleName(String fimeName) {
        if (ableNames == null) return true;
        return Strings.isMatch(fimeName, ableNames);
    }    
    
    // ===========================================================================================
    //                                    common
    // ===========================================================================================
    
    /** 전부다 지운다. 이것만 사용하도록 하자. */
    public void delete(String root,FTPFile ftpfile) throws IOException {
        if (ftpfile.getName().equals(".") || ftpfile.getName().equals("..")) return;
        String ftpFileName = root + "/" + ftpfile.getName();
        if(ftpfile.isFile()) delete(ftpFileName);
        else if(ftpfile.isDirectory()){
            FTPFile[] ftpfiles = ftpClient.listFiles(ftpFileName);
            for(FTPFile each : ftpfiles) delete(ftpFileName,each);
            deleteDir(ftpFileName);
        }
    }
    
    protected void delete(String fullName) throws IOException {
        boolean result = ftpClient.deleteFile(fullName);
        if(!result) throw new IOException(fullName +" 를 지우는데 실패했습니다.");
        deleted++;
    }
    
    protected void deleteDir(String fullName) throws IOException {
        boolean result = ftpClient.removeDirectory(fullName);
        if(!result) throw new IOException(fullName +" 를 지우는데 실패했습니다.");
        directorydeleted++;
    }
    
    /**
     * 원격지의 파일 1개를 이동시킨다.
     */
    public void moveSingleFile(String fromFile,String toFile) throws IOException {
        int exist = ftpClient.rnfr(fromFile);
        //550이면 실패
        if(exist != ACCEPT_AND_WAIT) throw new IOException(fromFile+" 해당 파일이 존재하지 않습니다. "+ exist); //350 
        int move = ftpClient.rnto(toFile);
        //503이면 실패
        if(move != SUCCESS) throw new IOException(toFile+" 해당 파일의 이동에 실패했습니다. " + move); //250 503
        moved++;
    }
    
    /**
     * 이미 디렉토리가 있으면 실패할 수 있다. 
     */
    public void makeDir(String file) throws IOException {
        boolean result = ftpClient.makeDirectory(file);
        if(result) directoryMaked++;
        //else throw new RuntimeException(file+" FTP의 directory생성에 실패했습니다.");
    }  
    
    /**
     * overwrite가 false이면 있는 파일은 무시한다.
     */
    public void download(String ftpFileName,String localFileName) throws IOException {
        File localFile = new File(localFileName);
        if (localFile.exists() && !overwrite) return;
        OutputStream outputStream = new FileOutputStream(localFile);
        boolean result = ftpClient.retrieveFile(ftpFileName, outputStream);
        outputStream.close();
        if(result) downloaded++;
        else  log.debug(ftpFileName+" 를 "+localFileName+"로 다운로드하는데 실패하였습니다.");
    }
    
    /** ftpFileName으로 Local의 파일이름을 Root기준으로 구한다. */
    public void download(String ftpFileName) throws IOException {
        download(ftpFileName,getNameByFtpRoot(ftpFileName));
    }
    
    /**
     * overwrite가 false이면 있는 파일은 무시한다.
     * appendFile의 경우 있으면 flase를 리턴?
     */
    public void upload(File clientFile) throws IOException {
        String uploadName = getNameByClient(clientFile);
        if(clientFile.isDirectory()) makeDir(uploadName);
        else{
            FileInputStream inputStream = new FileInputStream(clientFile);
            boolean result;
            if(overwrite) result = ftpClient.storeFile(uploadName, inputStream); //덮어쓰기
            else result =  ftpClient.appendFile(uploadName, inputStream);
            inputStream.close();
            if(result) uploaded++;
            else log.debug(clientFile.getAbsolutePath()+" 의 업로드에 실패하였습니다.");    
        }
    }
    
    // ===========================================================================================
    //                                    etc..
    // ===========================================================================================
    
    
    /**
     * fullPath에서 ftpRoot부분을 잘라낸뒤 clientRoot를 더해서 리턴한다.
     */
    protected String getNameByFtpRoot(String name) {
        name = name.substring(ftpRoot.length(),name.length());
        return  clientOrToRoot + name;
    }
    
    /**
     * fullPath에서 clientOrToRoot부분을 잘라낸뒤 ftpRoot를 더해서 리턴한다.
     */    
    protected String getNameByClient(File file) {
        String name = file.getAbsolutePath();
        name = name.substring(clientOrToRoot.length(),name.length());
        return ftpRoot + name;
    }
    
    /**
     * 로깅한다.
     */
    public void logging(){
        if(directoryMaked!=0) log.debug(MessageFormat.format("{0}개의 디렉토리가 생성 되었습니다.", directoryMaked));
        if(directorydeleted!=0) log.debug(MessageFormat.format("{0}개의 디렉토리가 삭제 되었습니다.", directorydeleted));
        if(deleted!=0) log.debug(MessageFormat.format("{0}개의 파일이 삭제 되었습니다.", deleted));
        if(moved!=0) log.debug(MessageFormat.format("{0}개의 파일이 이동되었습니다.", moved));
        if(uploaded!=0)log.debug(MessageFormat.format("{0}개의 파일이 업로드 되었습니다.", uploaded));
        if(downloaded!=0) log.debug(MessageFormat.format("{0}개의 파일이 다운로드 되었습니다.", downloaded));
        if(localFiledeleted!=0) log.debug(MessageFormat.format("{0}개의 로컬 파일/디렉토리가 삭제 되었습니다.", localFiledeleted));
    }
    
    // ===========================================================================================
    //                                  etc..  
    // ===========================================================================================
    @Deprecated
    public void findAll(String root) throws IOException {
        fileLoopAndPrint(root);
    }

    @Deprecated
    protected void fileLoopAndPrint(String root) throws IOException {
        FTPFile[] ftpfiles = ftpClient.listFiles(root); // public 폴더의 모든 파일을 list 합니다
        if(ftpfiles==null)  return;
        for (int i = 0; i < ftpfiles.length; i++) {
            FTPFile file = ftpfiles[i];
            if (file.isFile()) file.toString(); // file.getName(), file.getSize() 등등..
            else if(file.isDirectory()) {
                String fileName = file.getName();
                if(fileName.equals(".") || fileName.equals("..") ) continue;
                fileLoopAndPrint(root +"/"+ file.getName());
            }
        }
    }
    
    /**
     * 변경전 파일명과 변경할 파일명을 파라미터로 준다
     *//*
    @Deprecated
    public void rename() throws IOException {
        boolean result = ftpClient.rename("/public/바꾸기전파일.txt", "/public/바꾼후파일.txt");
    }
    
    @Deprecated
    public void up() throws IOException {
        File put_file = new File("C:\\Test\\보내자.txt");
        FileInputStream inputStream = new FileInputStream(put_file);
        boolean result = ftpClient.storeFile("/public/보내자.txt", inputStream); //덮어쓰기
        //appendFile  //있으면 false
        inputStream.close();
    }    


    @Deprecated
    public void makeDir() throws IOException {
        boolean result = ftpClient.makeDirectory("/public/oops");
        //ftpClient.changeWorkingDirectory("/public");
        //boolean result = ftpClient.makeDirectory("oops"); // /public/oops 절대경로를 적지 않아도 된다
    }

    @Deprecated
    public void findPaging() throws IOException {
        int page = 1;
        FTPListParseEngine engine = ftpClient.initiateListParsing("/open"); // 목록을 나타낼 디렉토리
        while (engine.hasNext()) {
            FTPFile[] ftpfiles2 = engine.getNext(10); // 10개 단위로 끊어서 가져온다
            if (ftpfiles2 != null) {
                for (int i = 0; i < ftpfiles2.length; i++) {
                    FTPFile file = ftpfiles2[i];
                }
            }
        }
    }*/
    
}
