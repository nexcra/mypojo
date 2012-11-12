
package erwins.util.vender.etc;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.Collection;

import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

import biz.source_code.miniTemplator.MiniTemplator;
import biz.source_code.miniTemplator.MiniTemplator.TemplateSpecification;
import biz.source_code.miniTemplator.MiniTemplator.TemplateSyntaxException;

/**
 * 간이 템플릿 작성기.
 * @author sin
 */
public class TemplateBuilder {

    private String charset = "UTF-8";
    
    /** 템플릿을 치환해서 텍스트로 바꿔준다
     * 템플릿 해석에 오류가 날 확율이 있다. 조심 */
    public String toText(String templateFile, Object values){
        TemplateSpecification sp = new TemplateSpecification();
        sp.charset = Charset.forName(charset);
        sp.templateFileName = templateFile;
        MiniTemplator mt;
        try {
            mt = new MiniTemplator(sp);
        } catch (TemplateSyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        applyParam(mt,values);
        return  mt.generateOutput();
    }
    
    /** 재귀호출하면서 각 파라메터를 세팅한다.
     * null값은 입력하지 않는다 */
    private void applyParam(final MiniTemplator mt,final Object param){
        ReflectionUtils.doWithFields(param.getClass(), new FieldCallback(){
            @SuppressWarnings("rawtypes")
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                field.setAccessible(true);
                Object value = field.get(param);
                if(value==null) return;
                if(value instanceof Collection){
                    Collection coll = (Collection)value;
                    for(Object each : coll){
                        applyParam(mt,each);
                        mt.addBlock(field.getName());
                    }
                }else mt.setVariable(field.getName(), value.toString());
            }
        });
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }
    
    

}
