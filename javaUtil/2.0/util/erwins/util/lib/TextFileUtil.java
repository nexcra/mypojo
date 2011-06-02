
package erwins.util.lib;

import java.io.*;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;

import erwins.util.reflexive.FolderIterator;
import erwins.util.root.StringCallback;
import erwins.util.tools.TextFileReader;

/**
 * 텍스트 파일을 간단히 문자열로 바꿔준다.
 * 성능에 무관한 곳에만 사용해야 한다. 
 */
public class TextFileUtil{
    
    public static void change(String rootDirectory,TextFileChanger changer) {
        Iterator<File> root = new FolderIterator(rootDirectory);
        while(root.hasNext()){
            File each = root.next();
            if(!changer.isTarget(each)) continue;
            CharSequence result = changer.change(TextFileUtil.read(each));
            File file2 = new File(each.getName()+"1");
            TextFileUtil.write(file2, result);
            each.delete();
            file2.renameTo(each);
        }
    }
    
    public static interface TextFileChanger{
        /** 대상이 아니라면 false를 리턴할것. */
        public boolean isTarget(File file);
        /** 변경될 완전한 문자열을 리턴한다. */
        public CharSequence change(CharSequence str);
    }
    
    public static StringBuilder read(File file) {
        return read(file,CharEncodeUtil.UTF_8);    
    }
    
    public static StringBuilder read(File file,String encoding) {
        StringBuilder builder = new StringBuilder();
        InputStreamReader isr = null;
        try {
            isr = new InputStreamReader(new FileInputStream(file), encoding);
            BufferedReader br = new BufferedReader(isr);
            String s = null;
            while ((s = br.readLine()) != null) {
                builder.append(s);
                builder.append(IOUtils.LINE_SEPARATOR);
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
        }
        return builder;
    }
    
    public static void write(File file,CharSequence str) {
        write(file,str,CharEncodeUtil.UTF_8);
    }
    
    public static void write(File file,CharSequence str,String encoding) {
        OutputStreamWriter osw = null;
        try {
            osw = new OutputStreamWriter(new FileOutputStream(file), encoding);
            osw.write(str.toString());
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

	/** directory내의 텍스트 내용물을 합친다. */
	public static void gather(File directory,File out,String encode){
		Iterator<File> i = FileUtil.iterateFiles(directory);
	    TextFileUtil.gather(i,out,encode);
	}

	public static void gather(Iterator<File> i,File out,String encode){
		final StringBuilder b = new StringBuilder(); 
		while(i.hasNext()){
			File each = i.next();
			new TextFileReader().setEncoding(encode).read(each,new StringCallback() {
				@Override
				public void process(String line) {
					b.append(line);
					b.append("\r\n");
				}
			});
		}
		FileUtil.writeStr(b,out);
	}
    
    
    
}