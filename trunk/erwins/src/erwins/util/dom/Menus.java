package erwins.util.dom;

import java.util.ArrayList;
import java.util.List;

import erwins.util.exception.runtime.HeapNotFoundException;
import erwins.util.lib.Sets;
import erwins.util.lib.Strings;

/**
 * 재귀 참조가 많아서 상속 해서 사용하는것이 거의 불가능하다. ㅠㅠ
 * isAccessibleRole = param1 이다.
 * @author erwins(quantum.object@gmail.com)
 **/
public class Menus{
    
    private static List<Dom> menus = new ArrayList<Dom>();      
    
    /**
     * 객체 복사를 수행하지 않는다. 변형시키면 안됨!!
     **/
    public static List<Dom> getMenu(){
        return menus;
    }
    
    public static void addMenu(Dom dom){
        menus.add(dom);
    }

    /**
     * 메뉴에 접속 가능한 권한들 반환.
     *  => 권한은 없어도 메뉴가 보이는 경우(로그인 한했을 경우 등)에 사용
     **/
    public static String getMenuAccessAbleRole(String menuId){
        Dom dom = Dom.getElementById(menus, menuId);
        if(dom==null) throw new HeapNotFoundException(menuId + " : Do Not find Menu.");
        return dom.getParam1();
    }
    
    /**
     * 플래쉬용 오더를 리턴합니다. 
     * XML로 구성된 가변적인 플래시의 현제 플래시상태를 위한 자바스크립트 파라메터로 사용
     **/
    public static int[] getOrderForFlash(String menuId){
        int[] orders = new int[]{1,1,1}; 
        Dom dom = Dom.getElementById(menus, menuId);
        
        if(dom == null) return orders;
        
        while(true){
             orders[dom.getLevel()-1] =  getOrder(dom);
             if(dom.isRoot()) break;
             dom = dom.getParent();
        }
        return orders;
    }
    
    /**
     * 메뉴의 오더를 구합니다.
     **/
    private static int getOrder(Dom nowMenu){
        int count = 0;
        for(Dom dom :menus){
            if(dom.getLevel() == nowMenu.getLevel() &&  nowMenu.getParent() ==  dom.getParent()){
                count++;
                if(nowMenu == dom ) break;                
            }
        }
        return count;        
    }    
    
    /**
     * 레이아웃용 메뉴 구조 트리를 리턴한다.
     */
    public static String getMenuTreeForLayout(String menuId) {
        Dom nowDom = Dom.getElementById(menus,menuId) ;
        if (nowDom == null) return "";
        List<String> strs = new ArrayList<String>();

        while (true) {
            strs.add(nowDom.getName());
            if (nowDom.isRoot()) break;
            nowDom = nowDom.getParent();
        }
        return Strings.joinTemp(Sets.inverse(strs), " > ");
    }    
    
    /**
     * 권한에 따른 이름, 링크 등이 담긴 죄측메뉴플래시용 xml을 생성한다.
     **/
    public static String getMenuTag(String role){
        
        Dom dom;        
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



}