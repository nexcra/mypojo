
package erwins.util.vender.apache;

import java.io.*;
import java.util.*;

import sun.net.TelnetInputStream;
import sun.net.TelnetOutputStream;
import sun.net.ftp.FtpClient;

/**
 * FTP Client sun package를 이용한 FTP Client sun JDK1.2.2 linux 에서는 정상작동하지 않음 windows에서는 정상작동 되었음. linux 플랫폼에서는 IBM JDK1.3.0에서 정상작동됨음 확인
 * @since  2001.2.7
 * @author  Jeonghun Kim
 * @version  1.0
 */
public class Net2 extends FtpClient {
    /**
     * @uml.property  name="localPath"
     */
    private String localPath; //LocalHost의 경로
    private String logFileName = ""; //LogFile 이름
    private String rHost; //원격지 IP
    private int rPort; //원격지 접속 Port
    private boolean flag = true;
    private boolean logFlag;

    /**
     * 생성자
     * 
     * @param host
     *            접속할 호스트의 IPAddress
     * @param port
     *            접속할 호스트 FTP 서비스 포트번호
     * @param logFileDir
     *            localhost의 로그파일 절대경로- 로그파일에 대한 기록을 해야한다.
     */
    public Net2(String host, int port, String logFileDir) throws Exception {
        super(host, port);
        logFileName = logFileDir; // 로그 파일이 기록될 절대 경로를 저장한다.
        rHost = host;
        rPort = port;

    } //constructor

    /**
     * 생성자
     * 
     * @param host
     *            접속할 호스트의 IPAddress
     * @param port
     *            접속할 호스트 FTP 서비스 포트번호
     */
    public Net2(String host, int port) throws Exception {
        super(host, port);
        //logFileName = logFileDir;        // 로그 파일이 기록될 절대 경로를 저장한다.
        rHost = host;
        rPort = port;

    } //constructor

    private void writeLog(String msg) {

        if (logFlag) {
            try {

                BufferedWriter bw = new BufferedWriter(new FileWriter(logFileName, true));

                bw.write(msg, 0, msg.length());
                bw.close();

            }
            catch (Exception e) {
                System.out.println(e.toString());
            } //try
        }//end if

    } //writeLog

    /**
     * 로컬서버의 경로 설정
     * @param path  로컬서버의 경로
     * @uml.property  name="localPath"
     */
    public void setLocalPath(String path) {
        localPath = path;
        System.out.println("localPath : " + localPath);
    } //setLocalPath

    /**
     * 로컬서버의 경로 설정 현재 디렉토리로 설정
     */
    public void setLocalPath() {

        try {
            File path = new File(".");

            localPath = path.getAbsolutePath();
            System.out.println(localPath);

        }
        catch (Exception e) {
            System.out.println("로컬의 절대경로를 얻는데 실패했습니다 ");
            writeLog(e.toString());

        } //try

    } //setLocalPath

    /**
     * @return
     * @uml.property  name="localPath"
     */
    public String getLocalPath() {
        return localPath;
    }

    /**
     * FTP Server에 접속하는 과정
     * 
     * @param logFileName
     *            로그파일명
     */
    public boolean connect(String id, String pass, String logFileName) {
        try {
            login(id, pass);
            this.logFileName += logFileName;

            writeLog(rHost + "  Port : " + rPort + "로 접속했습니다. \n현재 시각은 " + new Date().toString() + "입니다 \n\n");
            return true;

        }
        catch (Exception e) {
            System.out.println("Login ERROR : " + e.toString());
            writeLog("login에 실패했습니다\n");
            return false;
        } //try

    } //connect

    /**
     * FTP Server에 접속하는 과정 로그 파일을 생성하지 않을시 사용
     */
    public boolean connect(String id, String pass) {
        try {
            login(id, pass);
            return true;

        }
        catch (Exception e) {
            System.out.println("Login ERROR : " + e.toString());
            writeLog("login에 실패했습니다\n");
            return false;
        } //try

    } //connect

    /**
     * FTP Server에 접속을 해제. finally 구문에 반드시 사용
     */
    public void disconnect() {
        try {
            closeServer();
            writeLog("접속을 해제합니다. \n현재 시각은 " + new Date().toString() + "입니다 \n");
        }
        catch (Exception e) {
            System.out.println("disConnection Error : " + e.toString());
        } //try

    } //disconnect

    //공백 제거..
    private String convStr(String str) throws Exception {
        if (str == null) return "";

        StringBuffer sb = new StringBuffer(str);
        int index = sb.toString().indexOf("  ");

        while (index != -1) {
            sb.replace(index, index + 2, " ");
            index = sb.toString().indexOf("  ");
        } //end while

        return sb.toString();
    } //end convStr

    /**
     * ls한 결과를 리턴한다.
     */
    public String getLs() throws Exception {

        String result = "";
        StringBuffer sb = new StringBuffer();

        TelnetInputStream ls = list();
        BufferedReader br = new BufferedReader(new InputStreamReader(ls));

        while ((result = br.readLine()) != null) {
            sb.append(result);
            sb.append("\n");
        } //end while

        ls.close();
        br.close();

        return sb.toString();
    } //end getLs

    /**
     * cd를 실행한다
     * 
     * @param dir
     *            이동할 디렉토리
     * @return 성공시 true 리턴
     */
    public boolean doCd(String dir) {

        try {
            cd(dir);
            return true;

        }
        catch (FileNotFoundException e1) {
            System.out.println("파일이나 디렉토리를 찾을 수 없습니다 ");
            System.out.println(e1.toString());

            writeLog("파일이나 디렉토리를 찾을 수 없습니다 \n");

            return false;

        }
        catch (Exception e2) {
            System.out.println("Cd Error : " + e2.toString());
            writeLog("doCd Error: " + e2.toString() + "\n");
            //연결을 끊는다.
            disconnect();

            return false;
        } //try-catch

    } //end doCd

    /**
     * put을 실행 현재 디렉토리를 기준으로 파일을 찾는다.
     * 
     * @param fileName
     *            Upload할 File Name
     */
    public boolean doPut(String fileName) {

        try {
            doCheck(fileName);

            File file_in = new File(fileName);
            FileInputStream is = new FileInputStream(file_in);

            //int len = (int)file_in.length();
            byte[] bytes = new byte[1024];
            int c = 0;
            //int total_bytes = 0;

            TelnetOutputStream tos = put(fileName);

            while ((c = is.read(bytes)) != -1) {
                //total_bytes += c;
                //is.read(bytes,0,c);

                tos.write(bytes, 0, c);

            } //end while

            is.close();
            tos.flush();
            tos.close();

            Date date = new Date();
            writeLog("서버에 " + fileName + "파일을 업로드했습니다 " + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds() + "\n");
        }
        catch (Exception e) {
            System.out.println(e.toString());
            writeLog("서버에 " + fileName + "파일을 업로드하는 도중 에러발생");

            return false;
        } //try

        return true;
    } //end put

    /**
     * put을 실행 절대경로를 기준으로 파일을 찾는다.
     * 
     * @param dir
     *            입력할 파일의 절대경로 /usr/local/apache/htdocs
     * @param fileName
     *            Upload할 File Name
     */
    public void doPut(String dir, String fileName) throws IOException {

        File file_in = new File(dir + "/" + fileName);
        FileInputStream is = new FileInputStream(file_in);

        int len = (int) file_in.length();
        byte[] bytes = new byte[len];
        int c = 0;
        int total_bytes = 0;

        while ((c = is.read(bytes)) != -1) {
            total_bytes += c;
            is.read(bytes, 0, c);
        } //end while

        TelnetOutputStream tos = put(fileName);
        tos.write(bytes, 0, total_bytes);

        tos.flush();
        is.close();
        tos.close();

        Date date = new Date();
        writeLog("서버에 " + fileName + "파일을 업로드했습니다 " + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds() + "\n");

    } //end put

    /**
     * put을 실행 byte[]로 데이터를 넘겨받아 바로 올린다.
     * 
     * @param fileName
     *            Upload할 File Name
     * @param bytes
     *            Upload할 데이터
     */
    public void doPut(String fileName, byte[] bytes) throws IOException {
        TelnetOutputStream tos = put(fileName);
        tos.write(bytes, 0, bytes.length);

        tos.flush();
        tos.close();

        Date date = new Date();
        writeLog("서버에 " + fileName + "파일을 업로드했습니다 " + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds() + "\n");

    } //end put

    /**
     * mput 실행 디렉토리를 넘겨받아 디렉토리안에 있는 파일및 하위 디렉토리 파일까지 업로드한다.
     * 
     * @param dirName
     *            디렉토리명
     */
    public boolean doMput(String dirName) {

        flag = true;

        String tmpLocalPath = localPath;
        doMput_run(tmpLocalPath, dirName);

        return flag;

    } //end doMput

    private void doMput_run(String tmpLocalPath, String dirName) {

        Vector dirVec = new Vector();
        Vector fileVec = new Vector();
        File source = null;
        File[] tmpFileObj = null;

        try {
            //Remote Directory 생성
            doMkDir(dirName);

            //Step  디렉토리 이동
            doCd(dirName);

            source = new File(tmpLocalPath, dirName);
            tmpFileObj = source.listFiles();
            tmpLocalPath += "/" + dirName;

            //디렉토리와 파일을 분류한다.
            for (int i = 0; i < tmpFileObj.length; i++) {
                if (tmpFileObj[i].isDirectory()) {
                    dirVec.addElement(tmpFileObj[i].getName());
                } else {
                    fileVec.addElement(tmpFileObj[i].getName());
                } //end if
            } //end for

            for (int i = 0; i < dirVec.size(); i++) {

                doMput_run(tmpLocalPath, dirVec.get(i).toString());
            } //end for

            for (int i = 0; i < fileVec.size(); i++) {

                doCheck(fileVec.get(i).toString());
                doPut(tmpLocalPath, fileVec.get(i).toString());

            } //end for

            //상위디렉토리로 이동
            doCd("..");

        }
        catch (Exception e) {
            System.out.println("Here Mput : " + e.toString());
            writeLog("doMput_run Error: " + e.toString() + "\n");

            flag = false;
            disconnect();
        } //try-catch

    } //doMput_run

    /**
     * get을 실행
     * 
     * @param fileName
     *            get할 파일명
     * 
     */
    public boolean doGet(String fileName) {

        try {

            doCheck(fileName);

            TelnetInputStream tis = get(fileName);
            BufferedInputStream dataInput = new BufferedInputStream(tis);
            FileOutputStream outfile = new FileOutputStream(localPath + "/" + fileName);

            byte[] b = new byte[1024];
            int c = 0;

            while ((c = dataInput.read(b)) != -1) {

                outfile.write(b, 0, c);

            } //end while

            outfile.close();

            Date date = new Date();
            writeLog("서버에서 " + fileName + "파일을 다운로드했습니다 " + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds() + "\n");

            return true;
        }
        catch (Exception e) {
            System.out.println("during doGet Error : " + e.toString());
            writeLog("서버에서 " + fileName + "파일을 다운로드중 에러가 발생했습니다.");
            return false;
        } //try

    } //end doGet

    //doGet overloading
    /**
     * get을 실행
     * 
     * @param fileName
     *            get할 파일명
     * @parma type ascii- true, binary-false
     * @deprecated 내부적으로 binary모드로 동작
     */
    public boolean doGet(String fileName, boolean type) {

        try {

            /*
             * if(type == true){ ascii(); }else{ binary(); }//end if
             */

            binary();

            TelnetInputStream tis = get(fileName);
            BufferedInputStream dataInput = new BufferedInputStream(tis);
            FileOutputStream outfile = new FileOutputStream(localPath + "/" + fileName);

            byte[] b = new byte[1024];
            int n = 0;

            while ((n = dataInput.read(b)) != -1) {

                outfile.write(b, 0, b.length);
            } //end while

            /*
             * int n =0;
             * 
             * while((n = dataInput.read()) != -1){
             * 
             * outfile.write(n); }//end while
             */

            outfile.close();

            Date date = new Date();
            writeLog("서버에서 " + fileName + "파일을 다운로드했습니다 " + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds() + "\n");

            return true;
        }
        catch (Exception e) {
            System.out.println("during doGet Error : " + e.toString());
            writeLog("서버에서 " + fileName + "파일을 다운로드중 에러가 발생했습니다.");
            return false;
        } //try

    } //end doGet Overloading

    /**
     * get 실행 디렉토리 경로와 파일명을 입력받아 디폴트 로컬 경로에 저장한다.
     */
    public void doGet(String dir, String fileName) throws IOException {

        TelnetInputStream tis = get(fileName);
        BufferedInputStream dataInput = new BufferedInputStream(tis);
        FileOutputStream outfile = new FileOutputStream(dir + "/" + fileName);

        byte[] b = new byte[1024];
        int n = 0;

        while ((n = dataInput.read(b)) != -1) {

            outfile.write(b, 0, b.length);
        } //end while

        outfile.close();

        Date date = new Date();
        writeLog("서버에서 " + fileName + "파일을 다운로드했습니다 " + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds() + "\n");

    } //end doGet

    /**
     * mget 실행 디렉토리명을 입력받는다.
     */
    public boolean doMget(String dirName) {

        flag = true;

        String tmpLocalPath = localPath;
        doMget_run(tmpLocalPath, dirName);

        return flag;

        //시작 디렉토리로 이동
        //doCd("~/");

    } //end doMget

    private void doMget_run(String tmpLocalPath, String dirName) {

        Vector dirVec = new Vector();
        Vector fileVec = new Vector();
        Vector tmpVec = null;

        StringTokenizer token = null;

        String ls = "";
        String tmpLine = "";

        int index2 = 0;

        try {

            //Step 1 디렉토리 이동
            doCd(dirName);

            //Step 2 로컬경로에 디렉토리 생성
            File localFile = new File(tmpLocalPath, dirName);
            localFile.mkdir();
            tmpLocalPath += "/" + dirName;

            //Step 3 파일과 디렉토리 분류
            ls = convStr(getLs());

            index2 = ls.indexOf("\n");

            while (index2 != -1) {
                tmpVec = new Vector(9);

                tmpLine = ls.substring(0, index2);
                ls = ls.substring(index2 + 1);

                index2 = ls.indexOf("\n");

                token = new StringTokenizer(tmpLine.trim(), " ");
                while (token.hasMoreTokens()) {
                    tmpVec.addElement(token.nextToken());
                } //end while

                if (tmpVec.size() == 9) {
                    tmpLine = tmpVec.get(0).toString();

                    //directory 여부 판단
                    if (tmpLine.indexOf("d") == 0) {
                        dirVec.addElement(tmpVec.get(8).toString());
                    } else {
                        fileVec.addElement(tmpVec.get(8).toString());
                    } //end if
                } //end if

            } //end while

            //Step 4 하위디렉토리가 없으면 파일을 순차적으로 가져온다. 아니면 재귀호출
            for (int i = 0; i < dirVec.size(); i++) {
                doMget_run(tmpLocalPath, dirVec.get(i).toString());
            } //end for

            for (int i = 0; i < fileVec.size(); i++) {

                doCheck(fileVec.get(i).toString());
                doGet(tmpLocalPath, fileVec.get(i).toString());

            } //end for

            //상위 디렉토리로...
            doCd("..");

        }
        catch (Exception e) {
            System.out.println("Here : " + e.toString());
            writeLog("doMget_run Error: " + e.toString() + "\n");

            flag = false;
            disconnect();
        } //try

    } //doMget_run

    /**
     * 원격지의 디렉토리를 삭제하는 명령
     * 
     * @param dirName
     *            원격지의 디렉토리명
     */
    public boolean doRmd(String dirName) {

        try {
            issueCommand("RMD " + dirName);
            writeLog("디렉토리를 삭제했습니다 : " + dirName + "\n");

            return true;
        }
        catch (Exception e) {
            System.out.println("Rmdir Error : " + e.toString());
            writeLog("디렉토리를 지우는 중 에러가 발생했습니다 : " + e.toString() + "\n");

            return false;
        } //try

    } //end doRmd

    /**
     * 원격지의 파일을 지운다.
     */
    public boolean deleteFile(String fileName) {

        try {
            issueCommand("DELE " + fileName);
            writeLog("파일을 삭제했습니다 : " + fileName + "\n");

            return true;
        }
        catch (Exception e) {
            System.out.println("Delete File Error : " + e.toString());
            writeLog("파일을 삭제하는 중 에러가 발생했습니다.: " + e.toString() + "\n");

            return false;
        } //try
    } //end deleteFile

    /**
     * 원격지의 하위디렉토리와 파일을 다 지운다.
     */
    public boolean doRmDir(String dirName) {

        flag = true;

        //String tmpLocalPath = localPath;
        doRmDir_run(dirName);

        //Directory를 지운다.
        doRmd(dirName);

        return flag;
        //시작 디렉토리로 이동
        //doCd("~/");

    } //end doRmDir

    private void doRmDir_run(String dirName) {

        Vector dirVec = new Vector();
        Vector fileVec = new Vector();
        Vector tmpVec = null;

        StringTokenizer token = null;

        String ls = "";
        String tmpLine = "";

        int index2 = 0;

        try {

            //Step 1 디렉토리 이동
            doCd(dirName);

            //Step 3 파일과 디렉토리 분류
            ls = convStr(getLs());

            index2 = ls.indexOf("\n");

            while (index2 != -1) {
                tmpVec = new Vector(9);

                tmpLine = ls.substring(0, index2);
                ls = ls.substring(index2 + 1);

                index2 = ls.indexOf("\n");

                token = new StringTokenizer(tmpLine.trim(), " ");
                while (token.hasMoreTokens()) {
                    tmpVec.addElement(token.nextToken());
                } //end while

                if (tmpVec.size() == 9) {
                    tmpLine = tmpVec.get(0).toString();

                    //directory 여부 판단
                    if (tmpLine.indexOf("d") == 0) {
                        dirVec.addElement(tmpVec.get(8).toString());
                    } else {
                        fileVec.addElement(tmpVec.get(8).toString());
                    } //end if
                } //end if

            } //end while

            //Step 4
            for (int i = 0; i < dirVec.size(); i++) {
                //재귀호출
                doRmDir_run(dirVec.get(i).toString());

                //Directory를 지운다.
                doRmd(dirVec.get(i).toString());
            } //end for

            for (int i = 0; i < fileVec.size(); i++) {

                //File을 지운다.
                deleteFile(fileVec.get(i).toString());

            } //end for

            //상위 디렉토리로...
            doCd("..");

        }
        catch (Exception e) {
            writeLog("하위 디렉토리와 파일을 찾아서 지우는 도중 에러가 발생했습니다 : " + e.toString() + "\n");
            flag = false;

            disconnect();
        } //try

    } //doRmDir_run

    /**
     * 원격지의 디렉토리를 생성한다
     */
    public boolean doMkDir(String dirName) {

        try {
            issueCommand("MKD " + dirName);
            writeLog("디렉토리를 생성했습니다 : " + dirName + "\n");

            return true;
        }
        catch (Exception e) {
            System.out.println("MKdir Error : " + e.toString());
            writeLog("디렉토리를 생성하는 중 에러가 발생했습니다 : " + e.toString() + "\n");

            return false;
        } //try

    } //doMkDir

    private void doCheck(String fileName) {

        fileName = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

        try {
            if (fileName.equals("txt") || fileName.equals("html") || fileName.equals("htm") || fileName.equals("asp")
                    || fileName.equals("jsp") || fileName.equals("cms") || fileName.equals("xml")) {

                ascii();
            } else {
                binary();
            } //end if
        }
        catch (Exception e) {
            System.out.println(e.toString());
            writeLog(e.toString());

            disconnect();
        } //try

    } //doCheck

}//class

