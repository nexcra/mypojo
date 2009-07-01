
package erwins.util.tools;

import java.io.*;

public class FileReader2 {

    public interface LineCallback {
        public void process(String line) throws Exception;
    }

    public void read(String path, LineCallback callback) {
        BufferedReader reader = null;
        try {

            reader = new BufferedReader(new FileReader(path));
            String line = null;

            while ((line = reader.readLine()) != null) {
                callback.process(line);
            }
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException ex) {
                    throw new RuntimeException(ex);        
                }
            }
        }
    }
}