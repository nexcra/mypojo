package erwins.jsample;

import org.apache.commons.fileupload.ProgressListener;

public class UploadProgressListener implements ProgressListener {
	private double bytesRead;
	private double contentLength;
	private long currentTimeMillis;
	private String bps = "";
	private int processed;
	private double prevBytesRead = 0;
	private long prevCurrentTimeMillis = 0;
	public String message;
	private StringBuffer buffer;
	
	public void update(long bytesRead, long contentLength, int fileNumber) {
		this.bytesRead = bytesRead;
		this.contentLength = contentLength;
		this.currentTimeMillis = System.currentTimeMillis();
		this.bps = this.getBPS();
		this.processed = (int)(this.bytesRead / this.contentLength * 100);
		buffer = new StringBuffer(20).append("<updateInfo>")
			.append(String.format("<bps>%s</bps>", getBPS()))
			.append(String.format("<processed>%s</processed>", processed))
			.append(String.format("<bytesRead>%s</bytesRead>", bytesRead))
			.append(String.format("<contentLength>%s</contentLength>", contentLength))
			.append(String.format("<fileNumber>%s</fileNumber>", fileNumber))
			.append("</updateInfo>");
		message = buffer.toString();
	}
	private String getBPS() {
		long timeDiff = currentTimeMillis - prevCurrentTimeMillis;
		if(0 == timeDiff) return this.bps;
		double bits = (this.bytesRead - this.prevBytesRead) * 8;
		bits = bits * 1000 / timeDiff;
		String bps = "";
		if(bits > 1073741824) {
			bps = String.format("%10.2f", bits / 1073741824) + "GBps";
		} else if(bits > 1048576) {
			bps = String.format("%5.2f", bits / 1048576) + "MBps";
		} else if(bits > 1024) {
			bps = String.format("%5.2f", bits / 1024) + "KBps";
		} else if(bits <= 1024) {
			bps = bits + "Bps";
		}
		this.prevBytesRead = this.bytesRead;
		this.prevCurrentTimeMillis = this.currentTimeMillis;
		return bps;
	}
}
