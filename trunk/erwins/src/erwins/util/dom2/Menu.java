package erwins.util.dom2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.persistence.*;

import erwins.util.lib.Sets;
import erwins.util.lib.Strings;


/**
 * 메뉴를 나타낸다.
 */
@javax.persistence.Entity
@javax.persistence.Table(name="T_MENU")
@AttributeOverrides({
    @AttributeOverride(name="id"  , column = @Column(name="ID",length=20)), //20이면 바차40이다.
    @AttributeOverride(name="upperId", column = @Column(name="UPPER_ID",length=20,nullable=false)),
    @AttributeOverride(name="name", column = @Column(name="NAME",length=500,nullable=false)),
    @AttributeOverride(name="sort", column = @Column(name="SORT",length=10)) //최소가 number10이다.
})
public class Menu extends Dom<Menu>{

    private static final long serialVersionUID = 1L;
    
    /**
     * @uml.property  name="accessAbleRoles"
     */
    private String accessAbleRoles;
    /**
     * @uml.property  name="description"
     */
    private String description;
    /**
     * @uml.property  name="use"
     */
    private boolean use = true;
    
    /**
     * 플래쉬용 오더를 리턴합니다. 
     * XML로 구성된 가변적인 플래시의 현제 플래시상태를 위한 자바스크립트 파라메터로 사용
     **/
    @Transient  public int[] getOrderForFlash(String menuId){
        int[] orders = new int[]{1,1,1}; 
        Menu menu = this;        
        if(menu == null) return orders;        
        while(true){
             orders[menu.getLevel()-1] =  menu.getOrder();
             if(menu.isRoot()) break;
             menu = menu.getParent();
        }
        return orders;
    }
    
    /**
     * 메뉴의 오더를 구합니다.
     **/
    @Transient  private int getOrder(){
        int count = 0;
        for(Menu menu :menus){
            if(menu.getLevel() == this.getLevel() &&  this.getParent() ==  menu.getParent()){
                count++;
                if(this.equals(menu)) break;                
            }
        }
        return count;        
    }    
    
    /**
     * 레이아웃용 메뉴 구조 트리를 리턴한다.
     */
    @Transient public String getMenuTreeForLayout() {
        Menu nowDom = this ;
        if (nowDom == null) return "";
        List<String> strs = new ArrayList<String>();

        while (true) {
            strs.add(nowDom.getName());
            if (nowDom.isRoot()) break;
            nowDom = nowDom.getParent();
        }
        return Strings.joinTemp(Sets.inverse(strs), " > ");
    }       
    
    
    // ===========================================================================================
    //                                    Cache
    // ===========================================================================================
    
    //private static List<Menu> menus = new ArrayList<Menu>();
    private static List<Menu> menus = new CopyOnWriteArrayList<Menu>();
    
    /**
     * 객체 복사를 수행하지 않는다. 변형시키면 안됨!!
     **/
    public static List<Menu> getMenu(){
        return menus;
    }
    
    /**
     * 자료를 추가한다. 이 자료는 명시적으로 삭제하지 않는 한 메모리에 남아있게 된다. 
     */
    public static void addMenu(Menu menu){
        menus.add(menu);
    }
    public static void setMenu(List<Menu> menu){
        menus = menu;
    }
    
    public static Menu getElementById(String menuId){
        return Menu.getElementById(menus, menuId);
    }
    
    
    // ===========================================================================================
    //                                     static
    // ===========================================================================================    
    
    /**
     * 권한에 따른 이름, 링크 등이 담긴 죄측메뉴플래시용 xml을 생성한다.
     **/
    public static String getMenuTag(String role){
        
        Menu dom;        
        StringBuffer stringBuffer = new StringBuffer();
        
        for(int i=0,j=menus.size();i<j;i++){
            dom = menus.get(i);
            
            //하위 노드가 있을 경우 클릭하면 하위 노드를 대신해서 보여준다.
            if(dom.isEnd() || dom.getLevel() >= dom.getNextDom().getLevel()) 
                stringBuffer.append(getStartTag(dom.getLevel()) + " name=\""+dom.getName()+"\" url=\"/"+dom.getId()+".do\" >");
            else 
                stringBuffer.append(getStartTag(dom.getLevel()) + " name=\""+dom.getName()+"\" url=\"/"+dom.getNextDom().getId()+".do\" >");
            
            stringBuffer.append("\n");
            
            //종료태그
            if(dom.isEnd()) for(int k=0;k<dom.getLevel();k++) stringBuffer.append(getEndTag(dom.getLevel()-k));    
            //else if(dom.getLevel() < dom.getNextDom().getLevel()) continue;
            //else if(dom.getLevel() == dom.getNextDom().getLevel()) stringBuffer.append(getEndTag(dom.getLevel()));
            else if(dom.getLevel() > dom.getNextDom().getLevel()){
                for(int k=0;k<dom.getLevel()-dom.getNextDom().getLevel()+1;k++){
                    stringBuffer.append(getEndTag(dom.getLevel()-k));                    
                }
            }
        }
        return stringBuffer.toString();
    }
    
    private static String getStartTag(int level){
        if(level == 1) return "<menu ";
        else return "<list ";
    }
    
    private static String getEndTag(int level){
        if(level == 1) return "</menu> \n";
        else return "</list> \n";
    }
    
    // ===========================================================================================
    //                                     getter / setter
    // ===========================================================================================

    /**
     * 메뉴에 접속 가능한 권한들 반환. => 권한은 없어도 메뉴가 보이는 경우(로그인 한했을 경우 등)에 사용
     * @uml.property  name="accessAbleRoles"
     */    
    @Column(name="ACCESS_ABLE_ROLES",length=500)
    public String getAccessAbleRoles() {
        return accessAbleRoles;
    }
    /**
     * @param accessAbleRoles
     * @uml.property  name="accessAbleRoles"
     */
    public void setAccessAbleRoles(String accessAbleRoles) {
        this.accessAbleRoles = accessAbleRoles;
    }
    /**
     * @return
     * @uml.property  name="description"
     */
    @Column(name="DESCRIPTION",length=2000)
    public String getDescription() {
        return description;
    }
    /**
     * @param description
     * @uml.property  name="description"
     */
    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * @return
     * @uml.property  name="use"
     */
    @Column(name="IS_USER",length=1,nullable=false)
    public boolean isUse() {
        return use;
    }
    /**
     * @param isUse
     * @uml.property  name="use"
     */
    public void setUse(boolean isUse) {
        this.use = isUse;
    }
    

}