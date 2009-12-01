
package erwins.util.lib;

import java.io.*;
import java.text.MessageFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import erwins.util.exception.*;

/**
 * <p>
 * 이 클래스는 파일 관련 기능을 제공
 * </p>
 */
public abstract class Files extends FileUtils {
    
    public static final int BUFFER_SIZE = 4096;

    /** 출처 : Spring  */
    public static int copy(InputStream in, OutputStream out){
        Val.isNotEmpty(in,"stream is null!");
        Val.isNotEmpty(out,"stream is null!");
        int byteCount = 0;
        try {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = -1;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                byteCount += bytesRead;
            }
            out.flush();
        }catch (IOException ex) {
            Throw.wrap(ex);
        }finally {
            try {
                in.close();
            }
            catch (IOException ex) {
            }
            try {
                out.close();
            }
            catch (IOException ex) {
            }
        }
        return byteCount;
    }
    
    /** 출처 : Spring  */
    public static int copy(File in, File out) throws IOException {
        Val.isNotEmpty(in,"in file is null!");
        Val.isNotEmpty(out,"out file is null!");
        return copy(new BufferedInputStream(new FileInputStream(in)),
            new BufferedOutputStream(new FileOutputStream(out)));
    }
    
    /** 출처 : Spring */
    public static boolean deleteRecursively(File root) {
        if (root.exists()) {
            if (root.isDirectory()) {
                File[] children = root.listFiles();
                for (int i = 0; i < children.length; i++) {
                    deleteRecursively(children[i]);
                }
            }
            return root.delete();
        }
        return false;
    }

    /** 출처 : Spring */
    public static void copyRecursively(File src, File dest) throws IOException {
        dest.mkdir();
        File[] entries = src.listFiles();
        for (int i = 0; i < entries.length; i++) {
            File file = entries[i];
            File newFile = new File(dest, file.getName());
            if (file.isFile()) {
                newFile.createNewFile();
                copy(file, newFile);
            }
            else {
                copyRecursively(file, newFile);
            }
        }
    }

    public static void delete(File file) {
        if (file == null || !file.exists()) throw new MalformedException("file is not exist");
        if (!file.delete()) throw new MalformedException("file is not deleted");;
    }

    /** InputStream으로 File에 기록한다. */
    public static void write(InputStream in, File file) {

        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file);
            byte[] bytes = new byte[1024];
            int c;
            while ((c = in.read(bytes)) != -1) {
                os.write(bytes, 0, c);
            }
        }
        catch (FileNotFoundException e) {
            Throw.wrap(e);
        }
        catch (IOException e) {
            Throw.wrap(e);
        }
        finally {
            try {
                if (os != null) os.close();
            }
            catch (IOException e) {
                Throw.wrap(e);
            }
        }

    }

    /**
     * 해당 폴더에서 유일한 파일 이름을 리턴한다. 즉 동일한 값이 있으면 [A => A(1) => A(2) => ..]으로 바꾸어 준다.
     * 파일 업로드 등에 사용한다.
     */
    public static File uniqueFileName(File orgFile) {
        File result = orgFile;
        File parent = result.getParentFile();
        while (result.isFile() || result.isDirectory()) {
            String fileName = result.getName();
            if (RegEx.isMatch("\\(((\\d))\\)\\.", fileName)) {
                String[] temp = Strings.substringsBetween(fileName, "(", ")");
                Integer i = Strings.plusForInteger(Sets.getLast(temp), 1);
                fileName = fileName.replaceAll("\\(((\\d))\\)\\.", "(" + i + ").");
                result = new File(parent, fileName);
            } else {
                String[] temp = Strings.getExtentions(fileName);
                result = new File(parent, temp[0] + "(1)." + temp[1]);
            }
        }
        return result;
    }

    /** 자바 기본패키지로 압축한다. 한글은 안됨. */
    public static void zip(File zip, File... files) {

        byte[] buf = new byte[1024];

        try {
            ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zip));

            for (File each : files) {

                if (!each.isFile()) continue;

                FileInputStream in = new FileInputStream(each);

                zipOut.putNextEntry(new ZipEntry(each.getName()));

                int len;
                while ((len = in.read(buf)) > 0) {
                    zipOut.write(buf, 0, len);
                }

                zipOut.closeEntry();
                in.close();
            }
            zipOut.close();
        }
        catch (IOException e) {
            Throw.wrap(e);
        }

    }

    /**
     * 객체를 파일로 저장한다.
     */
    public static void setObject(File file, Serializable obj) {
        //if(!file.exists()) throw new RuntimeException(file+" there are no file");
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = new FileOutputStream(file);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(obj);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                if (fos != null) fos.close();
                if (oos != null) oos.close();
            }
            catch (IOException e) {
                //무시한다.
            }
        }
    }

    /**
     * 객체로저장되었던 파일을 읽어온다. 파일이 없으면 null을 리턴한다.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getObject(File file) {
        T obj;
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            if (!file.exists()) return null;
            fis = new FileInputStream(file);
            ois = new ObjectInputStream(fis);
            obj = (T) ois.readObject();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                if (fis != null) fis.close();
                if (ois != null) ois.close();
            }
            catch (IOException e) {
                //무시한다.
            }
        }
        return obj;
    }

    /**  파일 사이즈(MB)를 String으로 나타낸다. */
    public static String getMb(File file) {
        return Formats.DOUBLE1.get(file.length() / FileUtils.ONE_KB / 1000.0) + "Mb";
    }
    
    /** String문자열을 파일로 입력한다. 웬만하면 사용하지 말자. */
    public static void writeStr(String str, File writeFile) {
        writeStr(str, writeFile, CharSets.UTF_8);
    }

    /** String문자열을 파일로 입력한다. 웬만하면 사용하지 말자. */
    public static void writeStr(String str, File writeFile, String writeCharset) {
        OutputStreamWriter osw = null;
        try {
            osw = new OutputStreamWriter(new FileOutputStream(writeFile), writeCharset);
            osw.write(str);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                if (osw != null) osw.close();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
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
     * response에 OS상에 존재하는 file을 담아서 출력한다. 기본적으로 application/octet-stream로 되어있다.
     * 필요하면 바꾸자. 덤으로 인코딩 문제도 해결~ 얼쑤
     */
    public static void download(HttpServletResponse response, File file) {

        if (!file.exists()) file = new File(CharSets.getEucKr(file.getAbsolutePath()));
        if (!file.exists()) throw new RuntimeException(file.getAbsolutePath() + " : file not found!");

        OutputStream out = null;
        FileInputStream fis = null;

        response.setContentType("application/octet-stream");
        response.setContentLength((int) file.length());

        try {
            //MS익스플러어가  기본적으로 8859_1를 인식하기때문에 변환을 해주어야 한다.             
            response.setHeader("Content-Disposition", "attachment; fileName=\"" + new String(file.getName().getBytes("EUC_KR"), "8859_1")
                    + "\";");
            response.setHeader("Content-Transfer-Encoding", "binary");

            out = response.getOutputStream();
            fis = new FileInputStream(file);
            IOUtils.copy(fis, out);
            out.flush();
        }
        catch (IOException e) {
            //if(!e.getClass().getName().equals("org.apache.catalina.connector.ClientAbortException"))
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            if (fis != null) try {
                fis.close();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /** null safe한 mkdir  */
    public static boolean mkdir(File file) {
        if (file == null) return false;
        return file.mkdirs();
    }
    
    // ===========================================================================================
    //                                  convert  
    // =========================================================================================== 
    

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
            throw new RuntimeException(e);
        }
        finally {
            try {
                if (isr != null) isr.close();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                if (osw != null) osw.close();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}