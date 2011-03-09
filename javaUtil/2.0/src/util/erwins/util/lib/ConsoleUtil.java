package erwins.util.lib;

import java.util.Scanner;

import erwins.util.root.StringCallback;


public abstract class ConsoleUtil{
	
	public static void start(StringCallback callback){
		Scanner sc = new Scanner(System.in);
		while(!Thread.currentThread().isInterrupted()){
			String line = sc.next();
			callback.process(line);
		}
	}
	
	public static void stop(){
		Thread.currentThread().interrupt();
	}
	
	
	
	

}
