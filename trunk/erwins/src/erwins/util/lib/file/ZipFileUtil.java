
package erwins.util.lib.file;

import java.io.*;
import java.util.*;
import java.util.zip.ZipException;

import net.sf.jazzlib.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 미완성.. 
 */
public class ZipFileUtil {

    public static int unZip(String orgFileName, String orgDirName) {
        return ZipFileUtil.unZip(new File(orgDirName + File.separator + orgFileName));
    }

    @SuppressWarnings("unchecked")
    public static int unZip(File fileName) {
        /*
        Enumeration entries;
        ZipFile zipFile = null;

        try {
            //zipFile = new ZipFile(fileName, "EUC-KR"); // 요거 한방이면 끝인데.ㅠㅠ;
            zipFile = new ZipFile(fileName); // 요거 한방이면 끝인데.ㅠㅠ;

            entries = zipFile.getEntries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                String orgDirName = fileName.getParent() + File.separator;
                String entryFileName = entry.getName();
                if (entry.isDirectory()) {
                    System.err.println("Extracting directory: " + entryFileName);
                    (new File(orgDirName + entryFileName)).mkdir();
                    continue;
                } else {
                    String[] tmpSplit = entryFileName.split(File.separator);
                    if (tmpSplit.length > 1) {
                        String tmpDir = "";
                        for (int i = 0; i < tmpSplit.length - 1; i++)
                            tmpDir += (tmpSplit[i] + File.separator);
                        tmpDir = orgDirName + tmpDir;
                        File tmpFile = new File(tmpDir);
                        if (!tmpFile.exists()) tmpFile.mkdir();
                    }
                }
                System.out.println("Extracting File: " + entryFileName);

                FileUtil.copyInputStream(zipFile.getInputStream(entry), new BufferedOutputStream(new FileOutputStream(orgDirName
                        + entryFileName)));
            }

        }
        catch (ZipException ze) {
            ze.printStackTrace();
            return 0;
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
            return 0;
        }
        finally {
            try {
                if (zipFile != null) zipFile.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }*/
        return 1;
    }

    public static class FileUtil {

        /**
         * 파일을 stream으로 읽어들여 대상 outputStream에 복사하는 메소드
         * 
         * @param in
         * @param out
         * @throws IOException
         */
        public static final void copyInputStream(InputStream in, OutputStream out) throws IOException {
            byte[] buffer = new byte[1024];
            int len;

            while ((len = in.read(buffer)) >= 0)
                out.write(buffer, 0, len);

            in.close();
            out.close();
        }
    }
    

    static Log logger = LogFactory.getLog(ZipFileUtil.class);
    static final int COMPRESSION_LEVEL = 8;
    //static final int BUFFER_SIZE = 64 * 1024;
    static final int BUFFER_SIZE = 1024;

    public static void zip(String fullPath) throws IOException {
        // 압축할 폴더를 설정한다.
        String targetDir = fullPath;
        int cnt;
        byte[] buffer = new byte[BUFFER_SIZE];
        FileInputStream finput = null;
        FileOutputStream foutput;

        ZipOutputStream zoutput;

        /*
         * ******************************************* 압축할 폴더명을 얻는다. : 절대 경로가
         * 넘어올 경우 --> 상대경로로 변경한다...*******************************************
         */
        targetDir.replace('\\', File.separatorChar);
        targetDir.replace('/', File.separatorChar);
        
        String dirNm = targetDir.substring(targetDir.lastIndexOf(File.separatorChar) + 1, targetDir.length());

        // 압축할 폴더를 파일 객체로 생성한다.
        File file = new File(targetDir);
        String filePath = file.getAbsolutePath();
        logger.debug("File Path : " + file.getAbsolutePath());

        /*
         * **********************************************************************
         * *** 폴더인지 파일인지 확인한다... 만약 넘겨받은 인자가 파일이면 그 파일의 상위 디렉토리를 타겟으로 하여 압축한다.
         * ***
         * *******************************************************************
         * *****
         */
        if (file.isDirectory()) {
            logger.debug("Directory.........");
        } else {
            file = new File(file.getParent());
        }

        // 폴더 안에 있는 파일들을 파일 배열 객체로 가져온다.
        File[] fileArray = file.listFiles();

        /*
         * **************************************************************** 압축할
         * 파일 이름을 정한다. 압축할 파일 명이 존재한다면 다른 이름으로 파일명을 생성한다.
         * ***************************************************************
         */
        String zfileNm = filePath + ".zip";
        int num = 1;
        while (new File(zfileNm).exists()) {
            zfileNm = filePath + "_" + num++ + ".zip";
        }

        logger.debug("Zip File Path and Name : " + zfileNm);

        // Zip 파일을 만든다.
        File zfile = new File(zfileNm);
        // Zip 파일 객체를 출력 스트림에 넣는다.
        foutput = new FileOutputStream(zfile);

        // 집출력 스트림에 집파일을 넣는다.
        zoutput = new ZipOutputStream((OutputStream) foutput);

        ZipEntry zentry = null;

        try {
            for (int i = 0; i < fileArray.length; i++) {
                // 압축할 파일 배열 중 하나를 꺼내서 입력 스트림에 넣는다.
                finput = new FileInputStream(fileArray[i]);

                // ze = new net.sf.jazzlib.ZipEntry ( inFile[i].getName());
                zentry = new ZipEntry(fileArray[i].getName());

                logger.debug("Target File Name for Compression : "

                + fileArray[i].getName()

                + ", File Size : "

                + finput.available());

                zoutput.putNextEntry(zentry);

                /*
                 * **************************************************************
                 * * 압축 레벨을 정하는것인데 9는 가장 높은 압축률을 나타냅니다. 그 대신 속도는 젤 느립니다. 디폴트는
                 * 8입니다.
                 * *********************************************************
                 * ******
                 */
                zoutput.setLevel(COMPRESSION_LEVEL);

                cnt = 0;
                while ((cnt = finput.read(buffer)) != -1) {
                    zoutput.write(buffer, 0, cnt);
                }

                finput.close();
                zoutput.closeEntry();
            }
            zoutput.close();
            foutput.close();
        }
        catch (Exception e) {
            logger.fatal("Compression Error : " + e.toString());
            /*
             * ********************************************* 압축이 실패했을 경우 압축 파일을
             * 삭제한다.*********************************************
             */
            logger.error(zfile.toString() + " : 압축이 실패하여 파일을 삭제합니다...");
            if (!zfile.delete()) {
                logger.error(zfile.toString() + " : 파일 삭제가 실패하여 다시 삭제합니다...");
                while (!zfile.delete()) {
                    logger.error(zfile.toString() + " : 삭제가 실패하여 다시 삭제합니다....");
                }
            }
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        finally {
            if (finput != null) {
                finput.close();
            }
            if (zoutput != null) {
                zoutput.close();
            }
            if (foutput != null) {
                foutput.close();
            }
        }

    }
    
    public void service() {

        List filenames = new ArrayList();
        List filepathes = new ArrayList();


        filenames.add("07@.hwp");
        //filenames.add("군인정경_.jpg");
        //filenames.add("군인정경1111.jpg");

        filepathes.add("D:/_temp_data/07년.hwp");
        //filepathes.add("D:/DEV/tomatoWCMS/web/upload/2006/10/30/1162174102687_군인정경_.jpg");
        //filepathes.add("D:/DEV/tomatoWCMS/web/upload/2006/11/17/1163729915562_군인정경1111.jpg");

        createZipFile(filenames, filepathes, "D:/doc.zip");
}

/**
     * @param filenames 압출할 파일명
     * @param filepathes 압축할 파일경로
     * @param outFileFullPath 압축파일명
     *
     * by issuettl
     */
    public void createZipFile(List filenames, List filepathes, String outFileFullPath){

        // Create a buffer for reading the files
        byte[] buf = new byte[1024];

        try {
            // Create the ZIP file
            String outFilename = outFileFullPath;
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outFilename));

            if(filenames == null || filepathes == null)
                throw new NullPointerException();

            if(filenames.size() != filepathes.size())
                throw new Exception();

            // Compress the files
            for (int i=0; i<filenames.size(); i++) {
                Object objFilename = filenames.get(i);
                Object objFilepath = filepathes.get(i);

                if (objFilename instanceof String &&
                        objFilepath instanceof String) {
                    String filename = (String) objFilename;
                    String filepath = (String) objFilepath;

                    FileInputStream in = new FileInputStream(filepath);

                    // Add ZIP entry to output stream.
                    out.putNextEntry(new ZipEntry(filename));

                    // Transfer bytes from the file to the ZIP file
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }

                    // Complete the entry
                    out.closeEntry();
                    in.close();
                }
            }

            // Complete the ZIP file
            out.close();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }      

}
