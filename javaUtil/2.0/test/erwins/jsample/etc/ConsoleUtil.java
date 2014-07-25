package erwins.jsample.etc;

import java.util.Scanner;

import erwins.util.root.StringCallback;
import groovy.lang.Closure;

/**  ㅅㅂ 한글안되무니다 */
@Deprecated
public abstract class ConsoleUtil{
	
	public static void start(StringCallback callback){
		Scanner sc = new Scanner(System.in);
		while(!Thread.currentThread().isInterrupted()){
			String line = sc.next();
			callback.process(line);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static void start(String exit,Closure c){
		Scanner sc = new Scanner(System.in);
		while(!Thread.currentThread().isInterrupted()){
			String line = sc.next();
			System.out.println(line);
			if(exit.equals(line)) stop();
			else c.call(line);
			stop();
		}
	}
	
	public static void stop(){
		Thread.currentThread().interrupt();
	}
	

}
