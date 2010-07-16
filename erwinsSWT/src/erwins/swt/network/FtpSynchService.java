package erwins.swt.network;

import java.io.Serializable;

import erwins.swtUtil.root.FailCallback;
import erwins.util.vender.apache.Net;
import erwins.util.vender.apache.Net.Synch;
import erwins.util.vender.apache.NetRoot.FtpLog;

@SuppressWarnings("serial")
public class FtpSynchService implements Serializable{
	
	private final String ip;
	private final int port;
	private final String id;
	private final String pass;
	
	private final String localDir;
	private final String remotDir;
	
	private boolean passive = false;
	
	public FtpSynchService(String ip,int port,String id,String pass,String localDir,String remotDir){
		this.ip = ip;
		this.port = port;
		this.id = id;
		this.pass = pass;
		this.localDir = localDir;
		this.remotDir = remotDir;
	}
	
	public static enum SynchType{
		COMMIT,UPDATE,COMMIT_LOG,UPDATE_LOG;
	}
	
	public static interface FTPcallback{
		public void run(FtpLog log);
	}

	public void synchronize(final SynchType type,final FTPcallback callback,final FailCallback failCallback){
		new Thread(new Runnable() {
			@Override
			public void run() {
				FtpLog log = null;
				Net m1 = new Net();
		        try {
		            m1.connect(ip,port,id,pass);
		            if(passive) m1.setPassive();
		            m1.setRoots(remotDir,localDir);
		            Synch synchType = null;
		            switch(type){
		            case COMMIT : synchType = m1.commit();  break;
		            case UPDATE : synchType = m1.update();  break;
		            case COMMIT_LOG : synchType = m1.commitLog();  break;
		            case UPDATE_LOG : synchType = m1.updateLog();  break;
		            }
		            m1.synchronize(synchType);
		            log = m1.getFtpLog();
		        }
		        catch (Exception e) {
		        	failCallback.exceptionHandle(e);
		        }
		        finally {
		            m1.disconnect();
		        }
				callback.run(log);
			}
		}).start();
	}

	public String getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	public String getLocalDir() {
		return localDir;
	}

	public String getRemotDir() {
		return remotDir;
	}

	public boolean isPassive() {
		return passive;
	}

	public void setPassive(boolean passive) {
		this.passive = passive;
	}

	public String getId() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((ip == null) ? 0 : ip.hashCode());
		result = prime * result + ((localDir == null) ? 0 : localDir.hashCode());
		result = prime * result + ((pass == null) ? 0 : pass.hashCode());
		result = prime * result + (passive ? 1231 : 1237);
		result = prime * result + port;
		result = prime * result + ((remotDir == null) ? 0 : remotDir.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		FtpSynchService other = (FtpSynchService) obj;
		if (id == null) {
			if (other.id != null) return false;
		} else if (!id.equals(other.id)) return false;
		if (ip == null) {
			if (other.ip != null) return false;
		} else if (!ip.equals(other.ip)) return false;
		if (localDir == null) {
			if (other.localDir != null) return false;
		} else if (!localDir.equals(other.localDir)) return false;
		if (pass == null) {
			if (other.pass != null) return false;
		} else if (!pass.equals(other.pass)) return false;
		if (passive != other.passive) return false;
		if (port != other.port) return false;
		if (remotDir == null) {
			if (other.remotDir != null) return false;
		} else if (!remotDir.equals(other.remotDir)) return false;
		return true;
	}
    
	
}
