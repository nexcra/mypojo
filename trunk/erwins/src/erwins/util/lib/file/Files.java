
package erwins.util.lib.file;

import java.io.*;
import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import erwins.util.exception.runtime.MalformedException;
import erwins.util.lib.*;

/**
 * <p>이 클래스는 파일 관련 기능을 제공합니다. </p>
 * @since  1.0
 */
public abstract class Files {

    /**
     * @uml.property  name="fileCopier"
     * @uml.associationEnd  
     */
    private static final IFileCopier fileCopier = new ChannelFileCopier(false);
    
    public static final String ESCAPE = "*@*";
    
    public static  void delete(File file){
        if(file==null || !file.exists()) throw new MalformedException("file is not exist");
        if(!file.delete()) throw new MalformedException("file is not deleted");;
    }
    
    /**
     * 객체를 파일로 저장한다.  
     */
    public static  void setObject(File file,Serializable obj){
        //if(!file.exists()) throw new RuntimeException(file+" there are no file");
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = new FileOutputStream(file);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(obj);
        }
        catch (IOException e) {
            throw new RuntimeException(e.getMessage(),e);
        }finally{
            try {
                fos.close();
                oos.close();
            }
            catch (IOException e) {
                //무시한다.
            }
        }
    }
    
    /**
     * 파일 경로의 특수문자를 일반 TEXT로 치환한다.
     * FLEX등에서 지원 안해주기때문에 만들었다. 
     */
    public static String escape(File file){
        return file.getAbsolutePath().replaceAll("\\\\", ESCAPE);
    }

    /**
     * 객체로저장되었던 파일을 읽어온다. 
     * 파일이 없으면 null을 리턴한다.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getObject(File file){
        T obj;
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            if(!file.exists()) return null;
            fis = new FileInputStream(file);
            ois = new ObjectInputStream(fis);
            obj = (T)ois.readObject();
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage(),e);
        }finally{
            try {
                fis.close();
                ois.close();
            }
            catch (IOException e) {
                //무시한다.
            }
        }
        return obj;
    }

    /**
     * 파일 사이즈(MB)를 String으로 나타낸다.
     */
    public static String getMb(File file) {
        return Formats.DOUBLE1.get(file.length() / FileUtils.ONE_KB / 1000.0) + "Mb";
    }

    /**
     * 텍스트 파일을 UTF_8로 변환한다.
     */
    public static void txtAnsiToUtf8(File file, File toFile) {
        convert(file, toFile, CharSets.EUC_KR, CharSets.UTF_8);
    }

    public static void txtAnsiToUtf8(File file) {
        File temp = new File(file.getAbsolutePath() + "temp");
        convert(file, temp, CharSets.EUC_KR, CharSets.UTF_8);
        changeFile(file, temp);
    }

    /**
     * 텍스트 파일을 EUC_KR로 변환한다.
     */
    public static void txtUtf8ToAnsi(File file, File toFile) {
        convert(file, toFile, CharSets.UTF_8, CharSets.EUC_KR);
    }

    public static void txtUtf8ToAnsi(File file) {
        File temp = new File(file.getAbsolutePath() + "temp");
        convert(file, temp, CharSets.UTF_8, CharSets.EUC_KR);
        changeFile(file, temp);
    }

    /**
     * temp를 file로 변경한다.
     */
    private static void changeFile(File file, File temp) {
        if (file.isFile()) file.delete();
        if (!temp.renameTo(file)) throw new RuntimeException(MessageFormat.format("do not change file {0}", file.getAbsolutePath()));
    }

    /**
     * txt파일을 해당 인코딩으로 변경한다. ISO-8859-1, ISO-8859-15, US-ASCII ,UTF-16,
     * UTF-16BE, UTF-16LE, UTF-8 , windows-1252
     */
    private static void convert(File readFile, File writeFile, String readCharset, String writeCharset) {
        //    Charset cset = Charset.forName("US-ASCII");
        OutputStreamWriter osw = null;
        InputStreamReader isr = null;
        try {
            osw = new OutputStreamWriter(new FileOutputStream(writeFile), writeCharset);
            isr = new InputStreamReader(new FileInputStream(readFile), readCharset);
            BufferedReader br = new BufferedReader(isr);
            String s = null;
            while ((s = br.readLine()) != null) {
                osw.write(s + IOUtils.LINE_SEPARATOR);
            }
            br.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (isr != null) isr.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (osw != null) osw.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /** String문자열을 파일로 입력한다. 웬만하면 사용하지 말자. */
    public static void writeStr(String str, File writeFile) {
        writeStr(str,writeFile,CharSets.UTF_8);
    }
    
    /** String문자열을 파일로 입력한다. 웬만하면 사용하지 말자. */
    public static void writeStr(String str, File writeFile, String writeCharset) {
        OutputStreamWriter osw = null;
        try {
            osw = new OutputStreamWriter(new FileOutputStream(writeFile), writeCharset);
            osw.write(str);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (osw != null) osw.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }    

    /**
     * 멀티파트 리퀘스트인지 검사
     */
    public static boolean isMultipartFormRequest(HttpServletRequest req) {
        return (Strings.nvl(req.getContentType()).toLowerCase().startsWith("multipart/form-data")) ? true : false;
    }

    /**
     * response에 OS상에 존재하는 file을 담아서 출력한다. 기본적으로 application/octet-stream로 되어있다. 필요하면 바꾸자.
     * 덤으로 인코딩 문제도 해결~ 얼쑤
     */
    public static void download(HttpServletResponse response, File file) {
        
        if(!file.exists()) file = new File(CharSets.getEucKr(file.getAbsolutePath()));
        if(!file.exists()) throw new RuntimeException(file.getAbsolutePath() + " : file not found!");        

        OutputStream out = null;
        FileInputStream fis = null;

        response.setContentType("application/octet-stream");
        response.setContentLength((int) file.length());

        try {
            //MS익스플러어가  기본적으로 8859_1를 인식하기때문에 변환을 해주어야 한다.             
            response.setHeader("Content-Disposition", "attachment; fileName=\"" + new String(file.getName().getBytes("EUC_KR"), "8859_1") + "\";");
            response.setHeader("Content-Transfer-Encoding", "binary");

            out = response.getOutputStream();
            fis = new FileInputStream(file);
            IOUtils.copy(fis, out);
            out.flush();
        }catch (IOException e) {
            //if(!e.getClass().getName().equals("org.apache.catalina.connector.ClientAbortException"))
        }catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("fail",e);
        }
        finally {
            if (fis != null) try {
                fis.close();
            }
            catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("fail",e);
            }
        }
    }
    
    /**
     * <p>대상 디렉토리와 하위에 있는 모든 디렉토리와 파일을 한번에 삭제한다.</p>
     * 
     * @param dir
     *            삭제할 디렉토리
     * @return 삭제를 성공했을 경우 <code>true</code>, 실패했을 경우 <code>false</code>
     */
    public static boolean rmdir(File dir) {
        if (dir == null) { return false; }
        if (dir.exists()) {
            if (dir.isDirectory()) {
                File[] files = dir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isFile()) {
                            if (!file.delete()) { return false; }
                        } else if (file.isDirectory()) {
                            if (!rmdir(file)) { return false; }
                        }
                    }
                }
            }
        }
        return dir.delete();
    }

    /**
     * <p>파일이나 디렉토리를 복사한다.</p> <p>디렉토리일 경우 하위 디렉토리까지 같이 복사한다.</p> dest
     * 
     * @param src
     *            원본
     * @param dest
     *            대상
     * @param overwrite
     *            덮어쓰기 여부
     * @return 복사한 파일 또는 디렉토리의 갯수
     * @throws IOException
     */
    public static int copy(File src, File dest, boolean overwrite) throws IOException {
        return fileCopier.copy(src, dest, overwrite, true);
    }

    public static int copy(String src, String dest, boolean overwrite) throws IOException {
        return copy(new File(src), new File(dest), overwrite);
    }

    /**
     * <p>파일이나 디렉토리를 이동한다.</p>
     * 파일이면 걍 이동이지만 폴더라면 파일을 복사 후 삭제한다. 
     * 즉 반쪽짜리 메서드임 ㅠㅠ. 트렌잭션 따윈 없는거다. 
     */
    public static int move(File src, File dest, boolean overwrite){
        int count = 0;
        if (src.isFile()) {
            if (overwrite) {
                if (dest.exists()) {
                    dest.delete();
                }
            }
            if (src.renameTo(dest)) {
                count++;
            } else {
                throw new RuntimeException("do not move file : " + src.getAbsolutePath());
                //count += fileCopier.copy(src, dest, overwrite, true);
                // 복사 후 원본 삭제를 실패한 경우
                //if (!src.delete()) {  }
            }
        } else if (src.isDirectory()) {
            try {
                count += fileCopier.copy(src, dest, overwrite, true);
            }
            catch (IOException e) {
                Encoders.stackTrace(e);
                throw new RuntimeException(e.getMessage(),e);
            }
            // 복사후 대상 디렉토리 삭제를 실패한 경우
            if (rmdir(src)) { throw new RuntimeException("copy success But delete fail"); }
        }
        return count;
    }

    /**
     * null safe한 mkdir
     */
    public static boolean mkdir(File file) {
        if (file == null) return false;
        return file.mkdirs();
        //return file.mkdir();
    }

}
