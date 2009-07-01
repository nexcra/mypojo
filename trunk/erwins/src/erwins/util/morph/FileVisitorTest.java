
package erwins.util.morph;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import erwins.util.exception.runtime.Val;
import erwins.util.morph.FileVisitor.JsonFile;

public class FileVisitorTest {

    @Test
    public void notEmpty() throws IOException, InterruptedException {
        File test = new File("D:/기등록 통관자료");
        Val.isNotEmpty(JsonFile.get(test));
    }
}
