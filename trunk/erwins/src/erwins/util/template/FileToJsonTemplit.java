package erwins.util.template;

import java.io.File;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import erwins.util.vender.etc.Flex;

/**
 * File을 Menus로 변환한다.
 * 클로저와 templitMethod를 동시에 사용한다. 확장하거나 혹은 command를 입력하거나.
 * visitor패터을 쓰도록 하자.
 * @author erwins(my.pojo@gmail.com)
 **/
@Deprecated
public abstract class FileToJsonTemplit{
    
    private JSONArray root = new JSONArray();
    private FileToJsonCommand command;
    
    /**
     * 폴더 안의 파일들을 Menus로 바꾼다.
     */ 
    public JSONArray get(File rootFolder){
        if(!rootFolder.isDirectory()) throw new RuntimeException(rootFolder.getAbsolutePath() + " directory only plz..");
        File[] rootFiles = rootFolder.listFiles();
        scanByRootFiles(root,rootFiles);
        return root;
    }
    
    /**
     * 트리구조를 적용하기 위해 디렉토리까지 합쳐서 검색한다. 상위 폴더를 제외한 현재  폴더 레벨별로 검색된다.
     */
    protected  void scanByRootFiles(JSONArray parent, File[] rootFiles){
        for(File file : rootFiles){
            if(file.isHidden()) continue;
            JSONObject obj = null;
            
            if(command==null) obj = fileToJson(file);
            else obj = command.fileToJson(file);
            
            if(file.isDirectory()){
                File searchFile = new File(file.getPath());       
                File[] searchFiles = searchFile.listFiles();
                //if(searchFiles.length==0) continue;
                JSONArray children = new JSONArray();
                scanByRootFiles(children,searchFiles);
                obj.put(Flex.CHILDREN, children);
            }
            parent.add(obj);
        }
    }
    
    /**
     * 트리 구조로 변형할 list에 들어갈 데이터를 입력한다.
     * 다운로드할 대상 클릭시 파라메터는  JSON으로 넘겨준다.
     * 디폴트로 적용되며 command입력시 command가 우선된다.
     **/
    abstract protected JSONObject fileToJson(File file);
    
    /**
     * 클로저.
     */
    public interface FileToJsonCommand{
        public JSONObject fileToJson(File file);
    }
    
    public void setCommand(FileToJsonCommand command){
        this.command = command;
    }

}



