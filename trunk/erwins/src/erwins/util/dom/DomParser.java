package erwins.util.dom;

import java.util.List;

import org.apache.ecs.html.*;


/**
 * @author  erwins(my.pojo@gmail.com)
 */
public class DomParser{
    
    private List<Dom> baseDoms;
    /**
     * @uml.property  name="domSkin"
     * @uml.associationEnd  
     */
    private DomSkin domSkin = DomSkin.TREE;
    
    public DomParser(List<Dom> doms){
        baseDoms = doms;
    }

    /**
     * 자식 객체를 구해서 UL로 담고 각 자식마다. 재귀호출 한다.
     **/
    private UL getUl(Dom thisDom,LI thisLi){
        List<Dom> thisDoms = Dom.getChild(baseDoms, thisDom);
        if(thisDoms.size()==0) return null;
        UL ul = new UL();
        for(Dom dom : thisDoms){
            LI li = new LI();
            li.setNeedClosingTag(true); //필요한가?
            String href = dom.getHref(); 
            if(href!=null) li.addElement(new A(dom.getHref()).setTagText(dom.getName()));
            li.setClass(domSkin.getSecondCss());
            getUl(dom,li);
            ul.addElement(li);
        }
        if(thisDom==null){
            ul.setID(domSkin.getId());
            ul.setClass(domSkin.getFirstCss());
            return ul;
        }
        else{
            thisLi.addElement(ul);
            return null;
        }
    }
    
    /**
     * DOM을 해석하여 tree구조의 Apache UL을 리턴한다.
     **/
    public String get(){
        return getUl(null,null).toString();
    }

    /**
     * @param domSkin
     * @return
     * @uml.property  name="domSkin"
     */
    public DomParser setDomSkin(DomSkin domSkin) {
        this.domSkin = domSkin;
        return this;
    }
    
    
    // ===========================================================================================
    //                            예전소스        
    // ===========================================================================================
    
    /**
     * <ul>, <li>로 구성된 특수형 노드를 완성한다.
     * DHTML_APPLICATION의 경우에는 하위 li에 시퀀스한 id가 있어야 한다.
     * FOLDER_TREE의  경우에는 cssClass가 있어야 한다.
     */
    @Deprecated
    public String getSequanceNode(){
        
        StringBuffer applicationForm = new StringBuffer();
        Integer nodeCount = 0;
        Dom dom;
        
        if(domSkin == DomSkin.HEADER ) applicationForm.append("<ul id='' >");
        else applicationForm.append("<ul id=\""+domSkin.getId()+"\" class='"+domSkin.getFirstCss()+"'>");
        
        for(int i=0,j=baseDoms.size();i<j;i++){
            dom = baseDoms.get(i);
            //센터 추가.
            if(domSkin == DomSkin.HEADER ) applicationForm.append("<li><a href='"+dom.getHref()+"'>"+dom.getName()+"</a>");
            else applicationForm.append("<li "+getSubCss(dom)+" ><a href='"+dom.getHref()+"' >"+dom.getName()+"</a>");
            //푸터 추가.
            applicationForm.append(getUlFromApplicationLevel(baseDoms,i,nodeCount));
        }
        applicationForm.delete(applicationForm.length()-5, applicationForm.length()); //마지막 </li>제거
        return applicationForm.toString();
    }


    private String getSubCss(Dom dom) {
        if(domSkin == DomSkin.HEADER) return "";
        return (dom.getHref().equals("")) ? "" : "class='dhtmlgoodies_sheet.gif'" ; 
    }

    /**
     * 2. footer를 추가해 준다. 수정 해주기.
     */     
    private String getUlFromApplicationLevel(List<Dom> doms,int i,Integer nodeCount){
        Dom dom = doms.get(i);
        String ul="</li>";
        int gap = (i==doms.size()-1) ? -dom.getLevel() : doms.get(i+1).getLevel()-dom.getLevel();
        while(gap!=0){
            if(gap>0){
                ul = "<ul>";
                nodeCount++;
                gap--;
            }else{
                gap++;
                ul += "</ul></li>";
            }
        };
        return ul;
    }
    
    
    
}

