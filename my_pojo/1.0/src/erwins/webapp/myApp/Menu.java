
package erwins.webapp.myApp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import erwins.util.exception.LoginRequiredException;
import erwins.util.exception.RoleNotFoundException;
import erwins.util.lib.CollectionUtil;
import erwins.util.lib.StringUtil;
import erwins.util.reflexive.Connectable;
import erwins.util.reflexive.Connector;
import erwins.util.reflexive.Visitor;
import erwins.util.reflexive.Visitor.Acceptor;
import erwins.webapp.myApp.user.GoogleUser;


/**
 * 메뉴이다. jsp에만 해당된다.
 * 내부적으로만 사용함으로 방어복사하지 않는다.
 * @author erwins(my.pojo@gmail.com)
 */
public enum Menu implements Connectable<String,Menu>,Acceptor<Menu>{
    
	rest("ROOT","",null),
	index("대문","대문",rest),
	none("NONE","로그인없이 사용가능기능모음",rest),
	
	//여기부터 실제 메뉴
	mtgo("매직더게더링","매직 더 개더링 온라인",rest),
	translator("간단변환기","MD5, 암호화 등 각종 간단변환",rest),
	mapLabel("맵라벨","지도에 간단한 표식 가능",rest),
	user("사용자 관리","구글로 들어온 사용자의 보기",rest,GoogleUser.ROLE_USER),
	admin("관리자","관리자용 메뉴",rest,GoogleUser.ROLE_ADMIN);
    

    private final String id;
    private final String name;
    private final String link;
    private final String description;
    private String[] accessableRoles;
    private Menu parent;
    private List<Menu> childs = new ArrayList<Menu>();
    
    private Menu(String name,String description,Menu parent,String ... accessables){
        this.name = name;
        this.description = description;
        this.parent = parent;
        if(parent!=null && parent.id!=null) this.id = parent.id + "/" + this.name(); 
        else this.id = this.name();
        this.accessableRoles = accessables;
        if(!description.equals("")) link = "/" + id + ".do";                
        else link = "#";
    }

    public void addChildren(Menu child) {
        childs.add(child);
    }
    
    public List<Menu> getChildren() {
        return childs;
    }
    
    public boolean isLeaf() {
        return childs.size()==0 ? true : false; 
    }
    
    public Menu getRoot() {
        return connector.getRoot(this);
    }
    
    public void setId(String id) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * 권한중 메뉴의 role이 포함되어있는지 확인한다.
     * roles이 없으면 무시한다.
     * Menu가 없으면 뭔가 잘못된것이다.
     **/
    public void validate(GoogleUser user){
        
    	String[] roles = this.getAccessableRoles();
        if(roles.length==0) return;
        if(user==null) throw new LoginRequiredException();
        Collection<String> userRoles = user.getRoles();
        if(userRoles==null) throw new LoginRequiredException();
        
        if(!CollectionUtil.isEqualsAny(roles,userRoles))
        	throw new RoleNotFoundException("{0} 에 접근할 권한이 없습니다.  {1}권한이 필요합니다."
        			,this.getName(),StringUtil.toString(roles));
    }
    
    // ===========================================================================================
    //                                    static
    // ===========================================================================================
    
    public static Menu getMenuByStartWith(String menuId){
    	if(menuId.startsWith("/")) menuId = menuId.substring(1);
        for(Menu each : Menu.values()){
            if(each.isLeaf()) if(menuId.startsWith(each.id)) return each;
        }
        return null;
    }
    
    public static final Connector<String,Menu> connector = new Connector<String,Menu>();
    static{
        connector.setChildren(Menu.values());
        connector.orderSiblings();
    }
    
    
    // ===========================================================================================
    //                                    getter
    // ===========================================================================================
    public String getValue() {
        return getLink();
    }
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public String[] getAccessableRoles() {
        return accessableRoles;
    }
    public Menu getParent() {
        return parent;
    }
    public void setParent(Menu parent) {
        this.parent = parent;
    }    
    public String getLink() {
        return link;
    }
    public List<Menu> getChilds() {
        return childs;
    }

    public void accept(Visitor<Menu> v) {
        v.visit(this);
    }
    
}
