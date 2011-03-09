
package erwins.util.template;

import java.io.Serializable;
import java.util.Collection;

import org.apache.ecs.html.Input;

import erwins.util.exception.LoginRequiredException;
import erwins.util.exception.RoleNotFoundException;
import erwins.util.lib.Encoders;
import erwins.util.lib.Sets;
import erwins.util.morph.JDissolver;
import erwins.util.root.*;

/**
 * Jsp용 버튼.. 커스텀 태그를 사용하지 않는 이유는 객체를 파라메터로 받기 위해서이다. 
 * Ajax등 외부에서 id를 이용해 이벤트를 할당하면 seq등의 파라메터를 넘길 수 없다. 
 * 이것은 client-Script단에서 조정하는 것으로 악의적인 사용자라면 마음대로 변경 가능하다. 
 * 고로 서버에서 Session과 비교해 실제적인 체크가 필요하다.
 * @author  erwins(my.pojo@gmail.com)
 */
public abstract class ButtonTag<ID extends Serializable,ROLE> {
    
    public static interface EntityUser<ROLE,ID extends Serializable> extends EntityId<ID>{
        public Collection<ROLE> getRoles();
    }

    protected EntityUser<ROLE,ID> user; //asd
    protected ROLE[] roles = null;
    protected EntityOwnerValidator<ID> pkEntity;
    protected EntityOwnerValidator<ID> jsonEntity;
    protected String id;
    protected String script;
    protected String title;
    protected boolean active = true;

    // ===========================================================================================
    //                                    method
    // ===========================================================================================

    /** Pair가 없다면 id는 직접 등록해주어야 한다. */
    public String build(Pair mode) {
        id = mode.getValue();
        return build(mode.getName());
    }
    
    /**
     * JSP에서 버튼을 만듭니다.
     **/
    public String build(String btnName) {
        try {
            return buildButton(btnName);
        }
        catch (LoginRequiredException e) {
            return "";
        }
        catch (RoleNotFoundException e) {
            return "";
        }
        catch (Exception e) {
            Encoders.stackTrace(e);
            return "Button Tag Error";
        }
        finally { //버튼의 호출 뒤 초기값을 복원해준다.
            pkEntity = null;
            jsonEntity = null;
            btnName = null;
            id = null;
            active = true;
            roles = null;
            script = null;
            title = null;
        }
    }


    /**
     * MODE에 관한 버튼을 HTML을 조합하여 리턴한다.
     */
    private String buildButton(String btnName) {

        if(!active) return "";
        roleCheck();
        ownerCheck();

        Input btn = new Input("button");
        btn.setValue(btnName);
        if(id!=null) btn.setID(id);
        if(script!=null) btn.setOnClick(script);
        btn.setStyle("cursor:pointer");
        btn.setTitle(title == null ? btnName : title);
        if (pkEntity != null) {
            ID pk = pkEntity.getId();
            if(pk!=null) btn.addAttribute("pk", pk);
        }
        if (jsonEntity != null){
            //추후 부하 문제가 있으면 add로 하게 만들기.
            btn.addAttribute("json",JDissolver.instance().build(jsonEntity));
        }
        
        return btn.toString();
    }

    /**
     * 해당하는 객체의 작성자가 세션의 사용자와 일치하지 않으면 예외처리.
     * entity를 정의했을 경우에만 적용.
     * subEntity를 우선 적용한다.
     **/
    private void ownerCheck() {
        if (jsonEntity != null) jsonEntity.validateOwner(); 
        else if (pkEntity != null) pkEntity.validateOwner();
    }
    
    /**
     * 사용자의 Role에 따라 버튼의 존재 여부를 결정한다. 버튼의 제약조건(role)null이면 패스이다.
     **/
    private void roleCheck() {
        if (roles == null) return;
        if(user==null) throw new RoleNotFoundException("role fail");
        if(!Sets.isEqualsAny(user.getRoles(),roles))  throw new RoleNotFoundException("role fail");
    }

}