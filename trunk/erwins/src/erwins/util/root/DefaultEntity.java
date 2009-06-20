package erwins.util.root;

import java.util.Calendar;

/**
 * Entity의 interface
 * @author     erwins(my.pojo@gmail.com)
 */
public interface DefaultEntity<ID> extends IdEntity<ID>{

    /**
     * @return
     * @uml.property  name="createDate"
     */
    public Calendar getCreateDate();
    /**
     * @param  date
     * @uml.property  name="createDate"
     */
    public void setCreateDate(Calendar date);
    /**
     * @return
     * @uml.property  name="updateDate"
     */
    public Calendar getUpdateDate();
    /**
     * @param  date
     * @uml.property  name="updateDate"
     */
    public void setUpdateDate(Calendar date);
    
    /**
     * 기본 설정값을 세팅한다.
     * genericDao같이 한곳에서만 적용하게 만들자.
     */    
    public void makeDefaultValue();
    
}