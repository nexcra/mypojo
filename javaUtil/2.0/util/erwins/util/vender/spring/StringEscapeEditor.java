package erwins.util.vender.spring;

import java.beans.PropertyEditorSupport;

import erwins.util.lib.StringUtil;

/** @컨트롤러에서 넘어오는 파라메터를 리플레이스 한다.? 그닥 쓸모는 없어보인다. 일단 저장
 * ex) @InitBinder("taxBillAutoPubChangeHisCommonVo")
    public void initBinder(ServletRequestDataBinder binder) throws Exception { 
   */
public class StringEscapeEditor extends PropertyEditorSupport{
    
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if(StringUtil.isEmpty(text)) setValue("");
        else setValue(text.replaceAll("\\.|-|_",""));
    }
    
    public static PropertyEditorSupport instance(final String regEx){
        return new PropertyEditorSupport(){
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                if(StringUtil.isEmpty(text)) setValue("");
                else setValue(text.replaceAll(regEx,""));
            }
        };
    }
    
}
