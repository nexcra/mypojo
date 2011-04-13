
package erwins.util.tools;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import erwins.util.collections.MapForList;
import erwins.util.collections.MapType;
import erwins.util.lib.FileUtil;
import erwins.util.lib.TextFileUtil;
import erwins.util.lib.security.MD5;
import erwins.util.vender.apache._Log;
import erwins.util.vender.apache._LogFactory;

/** 파일 이름에 관계없이 동일파일을 Hash기준으로 알려준다. */
public class DuplicatedFileFilter{
	
	private MapForList<File> map = new MapForList<File>(MapType.Hash);
	
	protected _Log log = _LogFactory.instance(this.getClass());
	
	public DuplicatedFileFilter add(String directory){
		Iterator<File> i = FileUtil.iterateFiles(directory,FileUtil.ALL_FILES);
		while(i.hasNext()){
			File each = i.next();
			if(each.getAbsolutePath().endsWith(".svn-base")) continue; //SVN에 복제된 파일 무시.
			String hash = MD5.getHashHexString(each);
			map.add(hash, each);
		}
		return this;
	}
	
	public void log(){
		for(Entry<String,List<File>> each : map){
			if(each.getValue().size() <= 1) continue;
			log.debug("Duplicated : {0} times",each.getValue().size());
			for(File eachFile : each.getValue()){
				log.debug("Path : {0}", eachFile.getAbsolutePath());
			}
		}
	}
	
	public void log(File file){
		StringBuilder2 b = new StringBuilder2();
		for(Entry<String,List<File>> each : map){
			if(each.getValue().size() <= 1) continue;
			b.append("Duplicated : ");
			b.append(each.getValue().size());
			b.append(" times");
			b.appendLine(" times");
			for(File eachFile : each.getValue()){
				b.append("Path : ");
				b.appendLine(eachFile.getAbsolutePath());
			}
		}
		TextFileUtil.write(file, b.toString(), "UTF-8");
	}
	
	/** 중복된거 삭제. 처음게 남고 나중에 들어온게 전부 삭제된다. */
	public void remove(){
		for(Entry<String,List<File>> each : map){
			if(each.getValue().size() <= 1) continue;
			int count = 0;
			for(File eachFile : each.getValue()){
				if(count++ == 0) continue;
				FileUtil.delete(eachFile);
				log.debug("removed : {0}", eachFile.getAbsolutePath());
			}
		}		
	}
    
    
}