package erwins.util.dom2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;

import erwins.util.lib.file.Files;

/**
 * File을 Menus로 변환한다.
 * @author erwins(my.pojo@gmail.com)
 **/
@Deprecated
public class FileToMenus{
    
    protected List<Menu> doms = new ArrayList<Menu>();
    
    /**
     * 폴더 안의 파일들을 Menus로 바꾼다.
     */ 
    public List<Menu> getMenus(File rootFolder){
        File[] rootFiles = rootFolder.listFiles();
        scanByRootFiles(rootFiles);
        return doms;
    }
    
	/**
     * 트리구조를 적용하기 위해 디렉토리까지 합쳐서 검색한다. 상위 폴더를 제외한 현재  폴더 레벨별로 검색된다.
     */
    protected  void scanByRootFiles(File[] rootFiles){        
        for(int i=0,j=rootFiles.length;i<j;i++){
            File file = rootFiles[i];
            addDoms(file);
            if(file.isDirectory()){                                
                File searchFile = new File(file.getPath());       
                File[] searchFiles = searchFile.listFiles();
                scanByRootFiles(searchFiles);
            }
        }
    }
    
    /**
     * 트리 구조로 변형할 list에 들어갈 데이터를 입력한다.
     * 다운로드할 대상 클릭시 파라메터는  JSON으로 넘겨준다.
     **/
    protected void addDoms(File file) {
        
        Menu menu = new Menu();
        
        menu.setId(file.getAbsolutePath());
        String name = file.getName();
        if(file.isFile()){
            if(file.length() > FileUtils.ONE_MB)
                name+= "  [" + Files.getMb(file)+"]";
        }
        menu.setName(name);
        menu.setUpperId(file.getParent());
        
        JSONObject obj = new JSONObject();
        //obj.put("link", Encoders.escapeJavaScript(LinkController.getLink(file)));
        //obj.put("link", LinkController.getLink(file));  //hmm..
        //obj.put("absolutePath",Encoders.escapeJavaScript(file.getAbsolutePath()));        
        obj.put("absolutePath",file.getAbsolutePath());
        menu.setData((file.isDirectory()) ? "" : "javascript:selectDownloadFile("+ obj.toString()+");");
        doms.add(menu);
    }

}



