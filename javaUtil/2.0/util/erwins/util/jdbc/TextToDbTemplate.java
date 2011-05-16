package erwins.util.jdbc;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import erwins.util.jdbc.TableInfos.TableInfo;
import erwins.util.lib.FileUtil;
import erwins.util.lib.StringUtil;

/** DB별로 데이터를 입력한다. 텍스트의 순서는 DB에서의 컬럼 순서와 동일하다. 
 * for 기상청프로젝트 */
public abstract class TextToDbTemplate{
    protected final TableInfos infos;
    public TextToDbTemplate(TableInfos generator){
    	this.infos = generator;
    }
    
    public void loadDirectory(File directory) throws SQLException {
    	Iterator<File> i = FileUtil.iterateFiles(directory);
    	while(i.hasNext()) load(i.next()); 
    }
    
    public void load(File file) throws SQLException {
    	TableInfo info = fileToTable(file);
    	if(info==null) return;
    	
    	StringBuilder sql = new StringBuilder("INSERT INTO ");
		sql.append(info.name);
		sql.append(" VALUES (");
		sql.append(StringUtil.iterateStr("?", ",",info.columns.size()));
		sql.append(")");
    	
		List<Object[]> parameters = new ArrayList<Object[]>();
		for(String each : FileUtil.readLines(file)){
			parameters.add(lineToParameter(info,each));
		}
		infos.jdbc.insert(sql.toString(), parameters);
    }
    
    abstract protected Object[] lineToParameter(TableInfo info,String line);
    /** 파일로 테이블을 가져온다. 파일 이름으로 테이블의 구분이 가능해야 한다. 이때 각종 데이터를 초기화할것 */
    abstract protected TableInfo fileToTable(File file);

}


