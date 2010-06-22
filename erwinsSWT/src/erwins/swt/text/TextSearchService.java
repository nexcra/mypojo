package erwins.swt.text;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import erwins.util.lib.Files;
import erwins.util.lib.Strings;
import erwins.util.lib.Files.IOFileFilter2;
import erwins.util.root.StringCallback;
import erwins.util.tools.TextFileReader;


@SuppressWarnings("serial")
public class TextSearchService implements Serializable{
	
	private final File root;
	private final String searchString;
	
	public static class TextSearchResult implements Iterable<String>{
		private final File file;
		private final List<String> line = new ArrayList<String>();
		public TextSearchResult(File file){
			this.file = file;
		}
		public File getFile() {
			return file;
		}
		public void addLine(String text) {
			line.add(text);
		}
		public int size() {
			return line.size();
		}
		@Override
		public Iterator<String> iterator() {
			return line.iterator();
		}
		
	}
	
	public TextSearchService(File root,String textForScan){
		this.root = root;
		this.searchString = textForScan;
	}

	public List<TextSearchResult> scan() {
		final List<TextSearchResult> result = new ArrayList<TextSearchResult>();
		Iterator<File> i = textFileIteraotor();
    	while(i.hasNext()){
    		final File each = i.next();
    		final TextSearchResult eachResult = new TextSearchResult(each); 
    		new TextFileReader().read(each, new StringCallback() {
				@Override
				public void process(String line) {
					if(!Strings.contains(line, searchString)) return;
					eachResult.addLine(line);
				}
			});
    		if(eachResult.size()!=0) result.add(eachResult);
    	}
    	return result;
	}
	
	private static final String[] ABLE_EXTS = new String[]{".JAVA",".TXT",".XML",".JSP",".JS",".AS",".MXML"};
	
	private Iterator<File> textFileIteraotor() {
		Iterator<File> i = Files.iterateFiles(root, new IOFileFilter2() {
			@Override
			public boolean accept(File file) {
				String upperFileName = file.getName().toUpperCase();
				for(String each : ABLE_EXTS) if(upperFileName.endsWith(each)) return true;
				return false;
			}
		});
		return i;
	}	
	

	public File getRoot() {
		return root;
	}

	public String getSearchString() {
		return searchString;
	}
	
}
