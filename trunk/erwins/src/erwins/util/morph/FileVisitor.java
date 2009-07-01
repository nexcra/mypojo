
package erwins.util.morph;

import java.io.File;

import erwins.domain.file.LinkController;
import erwins.util.lib.Encoders;
import erwins.util.lib.file.Files;
import erwins.util.vender.etc.Flex;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * File에 visitor를 구현한다. File을 확장할 수 없어서 위임했다. FileToJsonTemplit에 비해 더 복잡하지만 이
 * 클래스를 수정하지 않고도 다른 구현이 가능하다.
 * 
 * @author erwins(my.pojo@gmail.com)
 */
public interface FileVisitor<T> {

    public void visit(File file, T obj);

    /** Element는 오직 Accept만을 가진다. */
    public interface FileVisitAcceptable<T> {
        void accept(FileVisitor<T> v, T result);
    }

    /**
     * 예제로 만들어 놓은 jsonFile
     */
    public abstract static class FileAcceptor<T> implements FileVisitAcceptable<T> {
        private File file;

        public FileAcceptor(File f) {
            this.file = f;
        }

        public void accept(FileVisitor<T> v, T result) {
            v.visit(file, result);
        }
    }

    /** 마음에 들지 않으면 FileAcceptor를 상속해서 새로 만들어 사용하도록 할것. */
    public static class JsonFile extends FileAcceptor<JSONArray> {
        public JsonFile(File f) {
            super(f);
        }
        
        /**
         * 기본타입 사용 
         */
        public static JSONArray get(File root) {
            return get(root,json);
        }

        /**
         * root를 무시하고 자식들만을 json배열로 리턴한다.
         * 이 방식이 트리나 list등에 자주 사용된다.
         * 이방식으로 하지 않고 Flex옵션에 Root무시를 체크해도 된다.  
         */
        public static JSONArray get(File root, FileVisitor<JSONArray> v) {
            JSONArray children = new JSONArray();
            for (File each : root.listFiles()) {
                JsonFile t = new JsonFile(each);
                t.accept(v, children);
            }
            return children;
        }
    }

    /**
     * 디폴트로 사용할 Flex에서 사용되는 기본 json타입.
     */
    public static final FileVisitor<JSONArray> json = new FileVisitor<JSONArray>() {
        public void visit(File file, JSONArray result) {
            if (file.isHidden()) return;
            JSONObject obj = new JSONObject();
            obj.put("label", file.getName());

            if (file.isFile()) {
                obj.put(Flex.IS_BRANCH, false);
                obj.put("length", Files.getMb(file));
                obj.put("link", LinkController.getLink(file));
                obj.put("stringPath", Files.escape(file));
                obj.put("absolutePath", Encoders.escapeUrl(file.getAbsolutePath()));
            } else {
                obj.put(Flex.IS_BRANCH, true);
                JSONArray children = new JSONArray();
                for (File each : file.listFiles()) {
                    JsonFile e = new JsonFile(each);
                    e.accept(this, children);
                }
                obj.put(Flex.CHILDREN, children);
            }
            result.add(obj);
        }
    };
}
