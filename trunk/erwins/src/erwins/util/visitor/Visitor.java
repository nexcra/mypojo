
package erwins.util.visitor;

import java.io.File;

/**
 * 클래스를 수정하지 않고도 다른 구현이 가능한 visitor를 구현한다.
 * @author erwins(my.pojo@gmail.com)
 */
public interface Visitor<T>{

    public void visit(T target);

    /** Element는 오직 Accept만을 가진다. */
    public interface Acceptor<T> {
        void accept(Visitor<T> v);
    }

    /**
     * 예제로 만들어 놓은 FileAcceptor
     */
    public static class FileAcceptor implements Acceptor<File> {
        protected final File file;

        public FileAcceptor(File f) {
            this.file = f;
        }

        public void accept(Visitor<File> v) {
            v.visit(file);
        }
        
        /**
         * root를 무시하고 자식들만을 visit한다.
         * 이 방식이 트리나 list등에 자주 사용된다.
         * Flex를 사용한다면 이방식으로 하지 않고 Flex옵션에 Root무시를 체크해도 된다.  
         */
        public void acceptIgnoreRoot(Visitor<File> v) {
            for (File each : file.listFiles()) {
                FileAcceptor t = new FileAcceptor(each);
                t.accept(v);
            }
        }
    }
    

}
