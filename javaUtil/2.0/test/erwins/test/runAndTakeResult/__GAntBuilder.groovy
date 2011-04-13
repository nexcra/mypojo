
package erwins.test.runAndTakeResult

import org.junit.Test
import groovy.util.AntBuilder
/** 2개의 jar파일이 필요하다. 정말로 멋지다!! */
public class __GAntBuilders{

    @Test
    public void build(){
        def ant = new AntBuilder();
        ant.mkdir(dir:'D:/qwe');
        def file = new  File('D:/qwe');
        println file.exists()
        ant.delete(dir:'D:/qwe');
        
        /*
        if ( ! System.properties.'java.version'.contains('1.4')) {
            ant.fail 'This build script requires JDK 1.4.x'
        } */
   }
   
}
