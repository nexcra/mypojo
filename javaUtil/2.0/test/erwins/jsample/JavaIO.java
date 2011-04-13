
package erwins.jsample;

import java.io.*;

import org.junit.Test;

class JavaIO{

    /** 키보드로부터 한줄씩 입력받아서 출력. */
    @Test
    public void scripting() throws IOException {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        while(true){
            String line = br.readLine();
            System.out.println(line);    
        }
    }
}
