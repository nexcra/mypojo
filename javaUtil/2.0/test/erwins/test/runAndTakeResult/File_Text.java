
package erwins.test.runAndTakeResult;

import java.io.File;
import java.util.Iterator;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import erwins.util.lib.CharEncodeUtil;
import erwins.util.lib.FileUtil;
import erwins.util.lib.RegEx;
import erwins.util.lib.StringUtil;
import erwins.util.lib.TextFileUtil;
import erwins.util.lib.TextFileUtil.TextFileChanger;
import erwins.util.reflexive.FolderIterator;

public class File_Text{
	
	/** SQL 텍스트 내용물을 합친다. */
    public void gather() throws Exception {
    	File root = new File("D:/멀티캠퍼스_0222");
    	TextFileUtil.gather(root,new File(root,"oracle.txt"),CharEncodeUtil.EUC_KR);
    }
    
	/** 라인 세퍼레이터를 전부 삭제한다. 추가로 ; 이후의 데이터에는 라인 세퍼레이터를 추가한다.  */
    @Test
	public void noLine() throws Exception {
    	File root = new File("D:/멀티캠퍼스_0222");
        Iterator<File> i = FileUtil.iterateFiles(root);
        while(i.hasNext()){
            File each = i.next();
            StringBuilder b = TextFileUtil.read(each,CharEncodeUtil.EUC_KR);
            String result = b.toString().replaceAll(IOUtils.LINE_SEPARATOR_WINDOWS, "");
            result = RegEx.replace("\\);", result, "\\);\r\n");
            File temp = new File(root,"1"+each.getName());
            TextFileUtil.write(temp, result);
            each.delete();
            temp.renameTo(each);
        }
        System.out.println("complite");
    }	
    
    /** 텍스트파일의 내용물을 교체한다. Good!! */
    //@Test
    public void chnage() throws Exception {
        Iterator<File> root = new FolderIterator("D:/src/Groovy-in-Action-source-code/publist");
        while(root.hasNext()){
            File each = root.next();
            if(!StringUtil.getExtention(each.getName()).equals("groovy")) continue;
            StringBuilder b = TextFileUtil.read(each);
            String result = RegEx.replace(Pattern.compile("//#.*"), b,"");
            result = RegEx.LINE_END_BLANK.replace( result,"");
            File file2 = new File(each.getName()+"1");
            TextFileUtil.write(file2, result);
            each.delete();
            file2.renameTo(each);
        }
        System.out.println("complite");
    }
    
    /** MS 코딩스타일을 Java식으로 바꾼다. */
    //@Test
    public void chnage2() throws Exception {
        TextFileUtil.change("D:/src/FlexViewer/src/com/esri/solutions/flexviewer",new TextFileChanger(){
            public CharSequence change(CharSequence str) {
                return RegEx.MS_CODE_TYPE.replace(str,"");
            }
            public boolean isTarget(File file) {
                String ext = StringUtil.getExtention(file.getName());
                if(StringUtil.isEqualsIgnoreCase(ext,"as","mxml")) return true;
                return false;
            }
        });
        System.out.println("complite");
    }
    
    
}