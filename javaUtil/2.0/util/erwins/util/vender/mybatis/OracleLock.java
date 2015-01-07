package erwins.util.vender.mybatis;

import java.util.Date;

import lombok.Data;

/**
 * Lock확인용 VO
 * @author sin
 */
@Data
public class OracleLock{
    
    private Long sid;
    private String command;
    private String machine;
    private String terminal;
    private Object username;
    private String process;
    private String objectName;
    private String lmode;
    private Object lockwait;
    private String status;
    private String program;
    private String type;
    private Date logonTime;
    private String objectType;
    private String sqlAddress;
    
}
