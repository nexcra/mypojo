package erwins.util.vender.etc;

import java.util.Date;

/** Lock확인용 VO */
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
    
    public Long getSid() {
        return sid;
    }
    public void setSid(Long sid) {
        this.sid = sid;
    }
    public String getCommand() {
        return command;
    }
    public void setCommand(String command) {
        this.command = command;
    }
    public String getMachine() {
        return machine;
    }
    public void setMachine(String machine) {
        this.machine = machine;
    }
    public String getTerminal() {
        return terminal;
    }
    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }
    public Object getUsername() {
        return username;
    }
    public void setUsername(Object username) {
        this.username = username;
    }
    public String getProcess() {
        return process;
    }
    public void setProcess(String process) {
        this.process = process;
    }
    public String getObjectName() {
        return objectName;
    }
    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }
    public String getLmode() {
        return lmode;
    }
    public void setLmode(String lmode) {
        this.lmode = lmode;
    }
    public Object getLockwait() {
        return lockwait;
    }
    public void setLockwait(Object lockwait) {
        this.lockwait = lockwait;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getProgram() {
        return program;
    }
    public void setProgram(String program) {
        this.program = program;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public Date getLogonTime() {
        return logonTime;
    }
    public void setLogonTime(Date logonTime) {
        this.logonTime = logonTime;
    }
    public String getObjectType() {
        return objectType;
    }
    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }
    public String getSqlAddress() {
        return sqlAddress;
    }
    public void setSqlAddress(String sqlAddress) {
        this.sqlAddress = sqlAddress;
    }
    
    
    
}
