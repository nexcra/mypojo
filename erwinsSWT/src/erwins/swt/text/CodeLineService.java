package erwins.swt.text;

import java.io.File;
import java.util.Iterator;
import java.util.regex.Pattern;

import erwins.util.collections.ListMap;
import erwins.util.counter.AvgCounter;
import erwins.util.counter.Counter;
import erwins.util.counter.SimpleCounter;
import erwins.util.lib.Files;
import erwins.util.lib.RegEx;
import erwins.util.lib.Strings;
import erwins.util.lib.Files.IOFileFilter2;
import erwins.util.root.StringCallback;
import erwins.util.tools.TextFileReader;


public class CodeLineService{
	
	private final File root;
	
	public CodeLineService(File root){
		this.root = root;
	}
	
	private static final Pattern THIRD = Pattern.compile("\\w*.\\w*.\\w*");
	private static final Pattern SECOND = Pattern.compile("\\w*.\\w*");

	public ListMap<String> dependencyByJar() {
		final ListMap<String> map = ListMap.treeInstance();
		Iterator<File> i = javaFileIteraotor();
    	while(i.hasNext()){
    		final File each = i.next();
    		
    		new TextFileReader().read(each, new StringCallback() {
				@Override
				public void process(String line) {
					String source = line.trim();
					if(!source.startsWith("import ")) return;
					
					String className = source.substring(7);
					if(className.startsWith("static")) className = className.substring(7);
					
					//ignore
					if(Strings.isStartsWithAny(className, "java.","erwins.","org.apache.commons")) return;
					
					if(Strings.isStartsWithAny(className,"org.apache.","net.sf.")) className = RegEx.find(THIRD, className);
					else className = RegEx.find(SECOND, className);
					
					String path = Files.getrelativePath(each, root);
					map.addUnique(className, path);
				}
			});
    	}
    	return map;
	}

	private Iterator<File> javaFileIteraotor() {
		Iterator<File> i = Files.iterateFiles(root, new IOFileFilter2() {
			@Override
			public boolean accept(File file) {
				return file.getName().toUpperCase().endsWith(".JAVA");
			}
		});
		return i;
	}
	
	public AvgCounter getAvgCounter() {
		AvgCounter avgCounter = new AvgCounter();
		Iterator<File> i = javaFileIteraotor();
		
		while(i.hasNext()){
			File each = i.next();
			final Counter counter  = new SimpleCounter();
			new TextFileReader().read(each, new StringCallback() {
				@Override
				public void process(String line) {
					counter.next();
				}
			});
			if(counter.count() < 30) continue;
			avgCounter.add(counter.count());
		}
		return avgCounter;
	}

	public File getRoot() {
		return root;
	}
	
	
}
