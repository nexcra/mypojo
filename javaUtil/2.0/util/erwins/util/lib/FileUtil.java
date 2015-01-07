package erwins.util.lib;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import erwins.util.root.StringCallback;
import erwins.util.root.exception.IORuntimeException;
import erwins.util.root.exception.PropagatedRuntimeException;
import erwins.util.text.CharEncodeUtil;
import erwins.util.text.FormatUtil;
import erwins.util.text.RegEx;
import erwins.util.text.StringUtil;

/**
 * 이 클래스는 파일 관련 기능을 제공
 * readLine 등을 사용할때 한글인코딩일 경우 EUC-KR 보다 MS949를 사용하자
 */
public abstract class FileUtil extends FileUtils {

	public static final int BUFFER_SIZE = 4096;
	public static final int COMPRESSION_LEVEL = 8;
	
	public static final FileFilter DIRECTORY_ONLY = new FileFilter(){
		@Override
		public boolean accept(File pathname) {
			return pathname.isDirectory();
		}
	};
	
	public static final FileFilter FILE_ONLY = new FileFilter(){
		@Override
		public boolean accept(File pathname) {
			return pathname.isFile();
		}
	};
	
	
	/** commons에는 기본 메소드가 없다. 그것을 대체한다. */
	public static final IOFileFilter ALL = new IOFileFilter(){
		@Override
		public boolean accept(File file) {
			return true;
		}
		@Override
		public boolean accept(File dir, String name) {
			return true;
		}
	};
	
	/** commons에는 기본 메소드가 없다. 그것을 대체한다. */
	public static final IOFileFilter ALL_FILES = new IOFileFilter(){
		@Override
		public boolean accept(File file) {
			return file.isFile();
		}
		@Override
		public boolean accept(File dir, String name) {
			return true;
		}
	};
	
	/** 2개나 재정의하는게 귀찮아서 만든 간이 클래스~ */
	public static abstract class IOFileFilter2 implements IOFileFilter{
		@Override
		public boolean accept(File dir, String name) {
			//디렉토리를 읽지 않으니 어차피 안쓴다.
			return false;
		}
	}

	
	@SuppressWarnings("unchecked")
	public static Iterator<File> iterateFiles(File directory){
		return iterateFiles(directory, ALL, ALL);
	}
	@SuppressWarnings("unchecked")
	public static Iterator<File> iterateFiles(String directoryName){
		return iterateFiles(new File(directoryName), ALL, ALL);
	}
	/** Files.ALL_FILES가 아니라면 디렉토리도 같이 포함되어 들어올것이다. */
	@SuppressWarnings("unchecked")
	public static Iterator<File> iterateFiles(String directoryName,IOFileFilter filter){
		return iterateFiles(new File(directoryName), filter, ALL);
	}
	
	/** 하나만 재정의해도 된다. */
	@SuppressWarnings("unchecked")
	public static Iterator<File> iterateFiles(File directory,IOFileFilter2 filter){
		return iterateFiles(directory, filter, ALL);
	}
	
	/** 기존 메소드의 []를 ...로 대체시킨것이다. */
	@SuppressWarnings("unchecked")
	public static Iterator<File> iterateFiles(File directory,boolean recursive,String ... ext){
		return iterateFiles(directory, ext, recursive);
	}

	/** 출처 : Spring */
	public static int copy(InputStream in, OutputStream out) {
		Preconditions.checkNotNull(in, "stream is null!");
		Preconditions.checkNotNull(out, "stream is null!");
		int byteCount = 0;
		try {
			byte[] buffer = new byte[BUFFER_SIZE];
			int bytesRead = -1;
			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
				byteCount += bytesRead;
			}
			out.flush();
		} catch (IOException ex) {
			throw new IORuntimeException(ex);
		} finally {
			try {
				in.close();
			} catch (IOException ex) {
			}
			try {
				out.close();
			} catch (IOException ex) {
			}
		}
		return byteCount;
	}

	/** 출처 : Spring. IOException을 던지지 않게만 수정 */
	public static int copy(File in, File out){
		Preconditions.checkNotNull(in, "in file is null!");
		Preconditions.checkNotNull(out, "out file is null!");
		try {
			return copy(new BufferedInputStream(new FileInputStream(in)), new BufferedOutputStream( new FileOutputStream(out)));
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}
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
			} else {
				copyRecursively(file, newFile);
			}
		}
	}
	
	/** child가  parent에 속해있거나 같으면 true를 리턴한다. */
	public static boolean isParent(File parent,File child) {
		while(true){
			if(child.equals(parent)) return true;
			child = child.getParentFile();
			if(child==null) return false;
		}
	}
	
	/** root는 반드시 file의  parent(몇단계가 되었던 간에)이어야 한다.
	 * ex) file이 D:/A/B/C  이고 루트가 D:/A 이라면 D:/A/B 를 리턴한다.  */
	public static File getFirstByRoot(File file,File root) {
		//ExceptionUtil.throwIfNotExist(file,root);
		File parent;
		while(true){
			parent = file.getParentFile();
			
			if(parent==null) throw new IllegalArgumentException(file.getAbsolutePath()+" file's parent must be root");
			if(parent.equals(root)) return file;
			file = parent;
		}
	}
	
	/** root에 기준하는 상대경로를 리턴한다.
	 * ex) file이 D:/A/B/C  이고 루트가 D:/A 이라면 B/C 를 리턴한다.  */
	public static String getrelativePath(File file,File root) {
		//ExceptionUtil.throwIfNotExist(file,root);
		String rootName = getPath(root);
		String fileName = getPath(file);
		return fileName.substring(rootName.length());
	}

	/** 파일을 하위 폴더로 이동시킨다. */
	public static void renameToParentDirectory(File file) {
		File renamed = new File(file.getParentFile().getParentFile(),file.getName());
		renameTo(file, renamed); 
	}

	/** 자동으로 유니크한 이름으로 변경 후 리네임한다 */
	public static void renameToUniqueName(File file, File renamed) {
		renamed = FileUtil.uniqueFileName(renamed);
		boolean success = file.renameTo(renamed);
		if(!success) throw new IllegalStateException(file.getAbsolutePath() + " : file move fail");
	}
	
	/** renameTo와 동일하나, 벨리데이션 체크랄 해준다. */
	public static void renameTo(File file, File renamed) {
		Preconditions.checkState(file.exists(), "파일이 존재하지 않습니다 " + file.getAbsolutePath());
		Preconditions.checkState(!renamed.exists(), "renamed할 파일이 이미 존재합니다 " + renamed.getAbsolutePath());
		if(!renamed.getParentFile().exists()) renamed.getParentFile().mkdirs();
    	boolean renameCheck = file.renameTo(renamed);
    	Preconditions.checkState(renameCheck, "파일이 정상적으로 리네임 되지 못했습니다 " + file.getAbsolutePath() + " -> " + renamed.getAbsolutePath());
	}
	
	/** 파일을 디렉토리로 이동시킨다. */
	public static void renameToDirectory(File file, File toDir) {
		File renamed = new File(toDir,file.getName()); 
		renamed = FileUtil.uniqueFileName(renamed);
		renameTo(file,renamed);
	}
	
	/** 파일이 정상적으로 지워지지 않으면 예외를 던진다. */
	public static void delete(File file) {
		if (!file.exists()) throw new IllegalStateException(file.getAbsolutePath() + " : file is not exist");
		if (!file.delete()) throw new IllegalStateException(file.getAbsolutePath() + " : file deleted fail");
	}

	/** RMI등에 사용. 용량이 큰거 전송시 주의!! heap size 여유있게 해야한다. */
	public static byte[] toByteArray(File input) {
		return toByteArray(input,null);
	}
	
	/** RMI등에 사용. 용량 제한이 있다. */
	public static byte[] toByteArray(File input,Integer limitMb) {
		if(limitMb!=null && input.length() > (limitMb * 1024 * 1024)) 
			throw new IllegalArgumentException(input.getName()+" is too large. (limit is " + limitMb + ")");
		InputStream in = null;
		try {
			in = new FileInputStream(input);
			return IOUtils.toByteArray(new BufferedInputStream(in));
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}finally{
			IOUtils.closeQuietly(in);
		}
	}

	/**
	 * 해당 폴더에서 유일한 파일 이름을 리턴한다. 즉 동일한 값이 있으면 [A => A(1) => A(2) => ..]으로 바꾸어 준다.
	 * 파일 업로드 등에 사용한다.
	 */
	public static File uniqueFileName(File orgFile) {
		if(!orgFile.exists()) return orgFile;
		File result = orgFile;
		File parent = result.getParentFile();
		while (result.exists()) {
			String fileName = result.getName();
			if (orgFile.isFile() && RegEx.isMatch("\\(((\\d+))\\)\\.", fileName)) {
				String[] temp = StringUtil.substringsBetween(fileName, "(", ")");
				String numString = CollectionUtil.getLast(temp);
				Integer i = StringUtil.plusForInteger(numString, 1);
				String changedNumString = StringUtil.leftPad(i.toString(), numString.length(), '0');
				fileName = fileName.replaceAll("\\(((\\d+))\\)\\.", "(" + changedNumString + ").");
				result = new File(parent, fileName);
			}else if (orgFile.isDirectory() && RegEx.isMatch("\\(((\\d+))\\)", fileName)) { //확장자가 없는경우(디렉토리)
				String[] temp = StringUtil.substringsBetween(fileName, "(", ")");
				String numString = CollectionUtil.getLast(temp);
				Integer i = StringUtil.plusForInteger(numString, 1);
				String changedNumString = StringUtil.leftPad(i.toString(), numString.length(), '0');
				fileName = fileName.replaceAll("\\(((\\d+))\\)", "(" + changedNumString + ")");
				result = new File(parent, fileName);
			} else {
				String[] temp = StringUtil.getExtentions(fileName);
				if(temp==null) result = new File(parent, fileName+ "(01)"); //확장자가 없는 경우(디렉토리)
				else result = new File(parent, temp[0] + "(01)." + temp[1]); //확장자가 있는경우
			}
		}
		return result;
	}
	
	/** 디렉토리를 통째로 압축. 한글안되~~~~~~~~~~ ㅅㅂ ㅅㅂ ㅅㅂ ㅅㅂ  */
	public static void zip(File dir) {
		if(!dir.isDirectory()) throw new IllegalArgumentException("input is not directory");
		File newZip = new File(dir.getParentFile(),dir.getName()+".zip");
		zip(newZip,dir.listFiles());
	}

	/** 자바 기본패키지로 압축한다. 폴더 인식 안된다. 한글은 안됨. '.'으로 시작하는거 패스. */
	public static void zip(File zip, File... files) {
		byte[] buf = new byte[BUFFER_SIZE];
		try {
			ZipOutputStream zipStream = new ZipOutputStream(new FileOutputStream(zip));
			for (File each : files) {
				if (!each.isFile())
					continue;
				if (each.getName().startsWith("."))
					continue;
				FileInputStream in = new FileInputStream(each);
				zipStream.putNextEntry(new ZipEntry(each.getName()));
				int len;
				while ((len = in.read(buf)) > 0) {
					zipStream.write(buf, 0, len);
				}
				zipStream.closeEntry();
				in.close();
			}
			zipStream.close();
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}
	}

	/** 현재폴더에 압축을 푼다. */
	public static void unzip(File zip) {
		unzip(zip, zip.getParentFile());
	}

	/** zip파일과 동일한 이름의 폴더를 만들어 그 안에 압축을 푼다. */
	public static void unzipUp(File zip) {
		unzip(zip, new File(zip.getParentFile(), fileName(zip)));
	}

	/** 확장자를 포함하지 않는 파일 이름을 리턴한다. */
	public static String fileName(File file) {
		String name = file.getName();
		String[] exts = StringUtil.getExtentions(name);
		if (exts == null)
			return name;
		return exts[0];
	}

	/**
	 * 자바 기본패키지로 압축해제한다. 이름 중복시 덮어쓰지 않고 이름을 변경하여 저장한다.
	 * */
	public static void unzip(File zip, File dir) {
		if (!dir.exists())
			dir.mkdirs();
		if (!dir.isDirectory())
			throw new IllegalArgumentException(dir + "is not directory");
		byte[] buffer = new byte[BUFFER_SIZE];
		InputStream fileIn = null;
		try {
			fileIn = new FileInputStream(zip);
			ZipInputStream zipStream = new ZipInputStream(fileIn);
			ZipEntry entry;
			while ((entry = zipStream.getNextEntry()) != null) {
				String outputFileNm = entry.getName();
				File entryFile = new File(dir, outputFileNm);

				// 압축을 폴더 안에다 한 경우 폴더를 생성해 준다.
				File parent = entryFile.getParentFile();
				if (!parent.exists())
					parent.mkdirs();

				// 동일한 이름이 있을 경우 이름을 바꿔 준다.
				if (entryFile.exists())
					entryFile = FileUtil.uniqueFileName(entryFile);

				FileOutputStream fileOut = new FileOutputStream(entryFile);
				int length;
				while ((length = zipStream.read(buffer)) != -1) {
					fileOut.write(buffer, 0, length);
				}
				fileOut.flush();
				fileOut.close();
			}
			zipStream.close();
		} catch (IOException e) {
			throw new IORuntimeException(e);
		} finally {
			try {
				if (fileIn != null)
					fileIn.close();
			} catch (IOException e) {
				throw new IORuntimeException(e);
			}
		}
	}

	/**
	 * 객체를 파일로 저장한다.
	 */
	public static <T extends Serializable> void setObject(File file, T obj) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = new FileOutputStream(file);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(obj);
		} catch (IOException e) {
			throw new IORuntimeException(e);
		} finally {
			try {
				if (fos != null)
					fos.close();
				if (oos != null)
					oos.close();
			} catch (IOException e) {
				// 무시한다.
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
			if (!file.exists())
				return null;
			fis = new FileInputStream(file);
			ois = new ObjectInputStream(fis);
			obj = (T) ois.readObject();
		} catch (IOException e) {
			throw new IORuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new PropagatedRuntimeException(e);
		} finally {
			try {
				if (fis != null)
					fis.close();
				if (ois != null)
					ois.close();
			} catch (IOException e) {
				// 무시한다.
			}
		}
		return obj;
	}

	/** 윈도우의 디렉토리 세퍼레이터는 \이다 이때문에 문자열 취급시 오류가 발생할 수 있음으로 치환해 준다.  */
	public static String getPath(File file) {
		return file.getAbsolutePath().replaceAll("\\\\","/");
	}
	
	/** 파일 사이즈(MB)를 String으로 나타낸다. */
	public static String getMb(File file) {
		return FormatUtil.DOUBLE1.get(file.length() / FileUtils.ONE_KB / 1000.0) + "Mb";
	}

	/** String문자열을 파일로 입력한다. 웬만하면 사용하지 말자. */
	public static void writeStr(CharSequence str, File writeFile) {
		writeStr(str, writeFile, CharEncodeUtil.UTF_8);
	}

	/** String문자열을 파일로 입력한다. 웬만하면 사용하지 말자. */
	public static void writeStr(CharSequence str, File writeFile, String writeCharset) {
		OutputStreamWriter osw = null;
		try {
			osw = new OutputStreamWriter(new FileOutputStream(writeFile), writeCharset);
			osw.write(str.toString());
		} catch (IOException e) {
			throw new IORuntimeException(e);
		} finally {
			try {
				if (osw != null)
					osw.close();
			} catch (IOException e) {
				throw new IORuntimeException(e);
			}
		}
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
		} catch (IOException e) {
			throw new IORuntimeException(e);
		} finally {
			try {
				if (os != null)
					os.close();
			} catch (IOException e) {
				throw new IORuntimeException(e);
			}
		}
	}	

	/** null safe한 mkdir */
	public static boolean mkdir(File file) {
		if (file == null)
			return false;
		return file.mkdirs();
	}
	
	public static void mkdirOrThrowException(File file) {
		if(file.isDirectory()) return;
		boolean success = file.mkdirs();
		if(!success) throw new IllegalStateException("can not make dir : " + file.getAbsolutePath());
	}
	
	public static boolean canReadAndWrite(File file) {
		return file.canRead() || file.canWrite();
	}

	// ===========================================================================================
	// convert
	// ===========================================================================================

	/**
	 * 텍스트 파일을 UTF_8로 변환한다.
	 */
	public static void txtAnsiToUtf8(File file, File toFile) {
		convert(file, toFile, CharEncodeUtil.EUC_KR, CharEncodeUtil.UTF_8);
	}

	public static void txtAnsiToUtf8(File file) {
		File temp = new File(file.getAbsolutePath() + "temp");
		convert(file, temp, CharEncodeUtil.EUC_KR, CharEncodeUtil.UTF_8);
		changeFile(file, temp);
	}

	/**
	 * 텍스트 파일을 EUC_KR로 변환한다.
	 */
	public static void txtUtf8ToAnsi(File file, File toFile) {
		convert(file, toFile, CharEncodeUtil.UTF_8, CharEncodeUtil.EUC_KR);
	}

	public static void txtUtf8ToAnsi(File file) {
		File temp = new File(file.getAbsolutePath() + "temp");
		convert(file, temp, CharEncodeUtil.UTF_8, CharEncodeUtil.EUC_KR);
		changeFile(file, temp);
	}

	/**
	 * temp를 file로 변경한다.
	 */
	private static void changeFile(File file, File temp) {
		if (file.isFile()) delete(file);
		if (!temp.renameTo(file))
			throw new IllegalStateException(MessageFormat.format("do not change file {0}", file.getAbsolutePath()));
	}

	/**
	 * txt파일을 해당 인코딩으로 변경한다. ISO-8859-1, ISO-8859-15, US-ASCII ,UTF-16,
	 * UTF-16BE, UTF-16LE, UTF-8 , windows-1252
	 */
	private static void convert(File readFile, File writeFile, String readCharset, String writeCharset) {
		// Charset cset = Charset.forName("US-ASCII");
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
		} catch (IOException e) {
			throw new IORuntimeException(e);
		} finally {
			try {
				if (isr != null)
					isr.close();
			} catch (IOException e) {
				throw new IORuntimeException(e);
			}
			try {
				if (osw != null)
					osw.close();
			} catch (IOException e) {
				throw new IORuntimeException(e);
			}
		}
	}
	
	/** 걍 래핑. -> 인메모리시 이걸쓰자. */
	@SuppressWarnings("unchecked")
	public static List<String> readLines(File file,String encoding){
		try {
			return FileUtils.readLines(file, encoding);
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}
	}
	public static List<String> readLines(File file){
		return readLines(file,"UTF-8");
	}
	
	/** 한줄씩 라인 단위로 스트리밍해서 읽는다
	 * ex) CharEncodeUtil.C_EUC_KR  */
	public static long readLines(File file,Charset encoding,StringCallback calback){
        try {
			return readLines(new FileInputStream(file), encoding,calback);
		} catch (FileNotFoundException e) {
			throw new IORuntimeException(e);
		}
	}
	
	/** 스트림을 한줄씩 읽는다 */
	public static long readLines(InputStream stream,Charset encoding,StringCallback calback){
		InputStreamReader isr = null;
		BufferedReader br = null;
		long skipIndex = 0;
        try {
            isr = new InputStreamReader(stream,encoding);
            br = new BufferedReader(isr,BUFFER_SIZE);
            String s = null;
            while ((s = br.readLine()) != null) {
            	calback.process(s);
				skipIndex++;
            }
        } catch (IOException e) {
        	throw new IORuntimeException(e);
        } catch (ReadSkipException e) {
            //즉시 종료한다
        } finally {
        	IOUtils.closeQuietly(br);
        	IOUtils.closeQuietly(isr); //자동으로 닫기지만 혹시나 해서
        }
        return skipIndex;
	}
	
	/** 읽기를 중단할때 던진다 */
	@SuppressWarnings("serial")
	public static class ReadSkipException extends RuntimeException{
	}
	
    /** 로그 등 일자료 롤링되는 파일중 가장 최신파일을 가져온다.  */
    public static File getLastFile(File dir,final String prefix) {
        File[] logs = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File arg0, String arg1) {
                return arg1.startsWith(prefix);
            }
        });
        TreeMap<String,File> logMap = new TreeMap<String, File>();
        for(File each : logs) logMap.put(each.getName(), each);
        File log = logMap.lastEntry().getValue();
        return log;
    }
    
    /** ins들을 해당 파일에 합쳐서 적재한다. (outFile에 append가 아니다)
     * \n 으로 구분된 txt파일에도 잘된다.  */
    public static long writeToFile(File outFile,int bufferSize,File[] ins) {
    	long byteCount = 0;
        OutputStream out = null;
        try {
            out = new FileOutputStream(outFile);
            for(File each : ins){
                InputStream in = null;
                try {
                    in = new FileInputStream(each);
                    byteCount += writeStreamWithNoClose(in,out,bufferSize);
                }finally{
                    IOUtils.closeQuietly(in);
                }
            }
        } catch (IOException e) {
        	throw new IORuntimeException(e);
        }finally{
            IOUtils.closeQuietly(out);
        }
        return byteCount;
    }
    
    /** stream을 닫지 않는다
     * BufferedInputStream + byte[] buffer 할것  
     * @throws IOException */ 
	public static long writeStreamWithNoClose(InputStream in,OutputStream out,int bufferSize) throws IOException {
        Preconditions.checkNotNull(in, "stream is null!");
        Preconditions.checkNotNull(out, "stream is null!");
        if(! (in instanceof BufferedInputStream)) in = new BufferedInputStream(in, bufferSize);
        if(! (out instanceof BufferedOutputStream)) out = new BufferedOutputStream(out, bufferSize);
        
        long byteCount = 0;
        byte[] buffer = new byte[bufferSize];
        int bytesRead = -1;
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
            byteCount += bytesRead;
        }
        out.flush();
        return byteCount;
    }
    
    /** 확장자로 매칭하는 FilenameFilter를 리턴한다 */
    public static FilenameFilter filenameFilterByExtention(final String ext){
    	return new FilenameFilter(){
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(ext);
			}
    	};
    }
    
    /** 딱히 넣을데가 없어서 여기 넣음. */
    public static void closeQuietly(Closeable closeable){
    	if(closeable!=null){
    		try {
				closeable.close();
			} catch (IOException e) {
				throw new IORuntimeException(e);
			}
    	}
    }
    
	/** 파일에 간단한 라인을 추가할때 사용한다. */
	public static void appendLines(File file,Charset charset,String ... lines) {
		BufferedWriter bw = null;
		try {
			FileOutputStream out = new FileOutputStream(file,true);
			OutputStreamWriter ww = new OutputStreamWriter(out,charset);
			bw = new BufferedWriter(ww); //append mode
			for(String line : lines){
				bw.write(line);
				bw.newLine();	
			}
			bw.flush();
		} catch (IOException e) {
			throw new IORuntimeException(e);
		} finally {
			IOUtils.closeQuietly(bw);
		}
	}
	

	/** 
	 * readLine 기준으로 뒤에서부터 읽는다. 
	 * 주로 로그의 마지막 부분을 읽을때 사용된다. (List<String>를 리턴하지 않고 text를 리턴한다.)
	 * 바이트 단위로 seek함으로, 특정 인코딩 지정은 못하고, 8859_1를 사용한다.
	 * readOffset의 단위는 kb이다
	 *  */
	public static String readLineAsLength(File file,int readOffset,Charset charset){
		Preconditions.checkArgument(readOffset > 0);
		Preconditions.checkArgument(readOffset < 1024*1024*50,"메모리 아웃을 염려해서.. 50MB로 제한했다.");
		List<String> reads = Lists.newArrayList();
		
		RandomAccessFile fo = null;
		try {
			fo = new RandomAccessFile(file, "r");
			long size = fo.length();
			long seek = size - 1024 * readOffset; 
			if(seek > 0) fo.seek(seek);
			
			String read = null;
			while((read = fo.readLine())!=null){
				reads.add(new String(read.getBytes(CharEncodeUtil.C_8859_1),charset));
			}
			if(reads.size() > 0) reads.remove(0); //첫 read는 버린다.
			return Joiner.on('\n').join(reads);
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}finally{
			try {
				if(fo!=null) fo.close();
			} catch (IOException e) {
				throw new IORuntimeException(e);
			}
		}
	}
		

}
