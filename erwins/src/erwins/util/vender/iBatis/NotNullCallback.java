package erwins.util.vender.iBatis;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;

import com.ibatis.sqlmap.client.extensions.*;

/**
 * DB 입출력시 null을 제거해주자. ==> 작동 안할듯.. 책보고 만들자.
 * @author erwins(my.pojo@gmail.com)
 */
@Deprecated
public class NotNullCallback implements TypeHandlerCallback {

    public void setParameter(ParameterSetter setter, Object obj)throws SQLException {
        if(obj instanceof String){
            setter.setString(obj==null ? "" : (String)obj);
        }else if(obj instanceof BigDecimal){
            setter.setBigDecimal((obj==null) ? BigDecimal.ZERO : (BigDecimal)obj);
        }
    	//setter.setString((obj==null) ? "" : (String)obj);
    }

    /**
     * 핸들러 콜백으로 Calendar를 Date로 자동매핑할려고 했으나 실패함 080814 
     * Calendar 자동 매핑이 아에 안된다.. 여기서 변환도 불가능하며 바로 오류가 난다.
     **/
    public Object getResult(ResultGetter getter) throws SQLException {
        Object obj = getter.getObject();
        
        if(obj instanceof String){
            return (obj==null) ? "" : obj;
        }else if(obj instanceof BigDecimal){
            return (obj==null) ? BigDecimal.ZERO : obj;
        }else if(obj instanceof Date){
            //System.out.println("======  DATE" + obj.toString());
        }        
        return (obj==null) ? "" : obj;
    }

    public Object valueOf(String s) {        
        return s;
    }

  

}