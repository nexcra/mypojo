package erwins.jsample;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.math.BigDecimal;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.*;

import javax.swing.*;

import netscape.javascript.JSObject;

/**
 * 1. 파일의 멀티 셀렉트
 * 2. 얼티 업로드
 * 3. 진행상황 체크 가능 
 * 주의 ! : 세션이 유지되지 않는다... 유짜지?
 */
@SuppressWarnings("unchecked")
public class AppletUploader extends Applet {
    
    private static final long serialVersionUID = 5519898546114212820L;
    
    private JProgressBar uploadProgressive;
    protected JProgressBar totalUploadProgressive;        

    protected File[] files;
    
    private Vector fileList;
    private JTable uploadFileTable;
    
    @Override
    public void init() {}
    
    /**
     * 1. 전체 패널을 완성한다.
     */    
    @Override
    public void start() {
        setLayout(new BorderLayout());
        
        JPanel buttonPanel = new JPanel();
        add("South", buttonPanel);
        
        JPanel progressivePanel = new JPanel(new BorderLayout());
        add("North", progressivePanel);
        
        button_fileOpen(buttonPanel);
        button_upload(buttonPanel);        
        
        progressiveBar(progressivePanel);       
        
        buildUploadFileTable();
    }
    
    private void button_fileOpen(JPanel buttonPanel) {
        JButton fileOpen = new JButton("파일 열기");
        buttonPanel.add(fileOpen);
        fileOpen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        JFileChooser fileChooser = new JFileChooser(".");
                        fileChooser.setMultiSelectionEnabled(true);
                        fileChooser.showOpenDialog(null);
                        files = fileChooser.getSelectedFiles();
                        onFileSelected();
                    }
                });
            }
        });
    }    
    
    protected void onFileSelected() {
        fileList.clear();
        for(int i=files.length-1;i>-1; --i) {
            Vector v = new Vector();
            v.add(files[i].getName());
            v.add(Long.valueOf(files[i].length()));
            fileList.add(v);
        }
        uploadFileTable.paintImmediately(uploadFileTable.getBounds());
        uploadFileTable.addNotify();
    }    

    protected void progressiveBar(JPanel progressivePanel) {
        
        this.uploadProgressive = new JProgressBar();
        uploadProgressive.setStringPainted(true);
        progressivePanel.add("North", uploadProgressive);
        
        this.totalUploadProgressive = new JProgressBar(); 
        progressivePanel.add("South", totalUploadProgressive);
        totalUploadProgressive.setStringPainted(true);
        totalUploadProgressive.setDoubleBuffered(true);
        totalUploadProgressive.setString("0Bps");       
    }
    
    
    /**
     * 브라우저의 자바스크립트를 호출한다.
     **/
    public void jsAlert(String str){
        JSObject window = JSObject.getWindow(this);
        String as1[] = {str};
        window.call("alert", as1);
    }
    /**
     * 브라우저의 자바스크립트를 호출한다.
     **/
    public void jsInvoke(String method,String ... args){
        JSObject window = JSObject.getWindow(this);
        window.call(method, args);
    }
    
    private void buildUploadFileTable() {
        Vector cols = new Vector(5);
        cols.add("이름(Name)");
        cols.add("크기(Size)");
        fileList = new Vector();
        uploadFileTable = new JTable(fileList, cols);
        uploadFileTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        JScrollPane scrollPane = new JScrollPane(uploadFileTable);
        uploadFileTable.setPreferredScrollableViewportSize(new Dimension(300, 70));
        uploadFileTable.setFillsViewportHeight(true);
        add("Center", scrollPane);
    }
    
    
    /**
     * 백그라운드 업로드를 위한 스래드  락 현상을 제거하기 위해 SwingWorker를 사용한다.
     */
    private class UploadRun implements Runnable{
        
        /**
         * @uml.property  name="totalFileLength"
         */
        private BigDecimal totalFileLength;
        protected SocketChannel out;
        protected Selector sel;
        /**
         * @uml.property  name="param"
         * @uml.associationEnd  
         */
        protected Parameter param;
        
        private long prosessedLength;
        private long prevCurrentTimeMillis;
        private long prevProsessedLength;
        private String bps;
        
        private static final String BOUNDARY = "-----------------javawide.com-LIMEUNCHEON";
        private static final String LINE_SEPARATOR = "\r\n";

        SwingWorker worker = new SwingWorker() {
            @Override
            protected Object doInBackground(){
                try {
                    getParameters();
                    if(!hasSelectedFiles() || isOverflowLimitFileSize()) return null;
                    prosessedLength = 0;                    
                    buildChannel();
                    buildUploadCommand();
                    buildHeaders();
                    buildBody();
                    buildLastBoundary();
                    getResponseMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    closeChannel();
                }
                //jsInvoke("Btn.LIST");
                alert("파일이 private폴더에 모두 업로드 되었습니다.");
                return null;
            }
        };
        
        /**
         * 파라메터 묶음. 
         */
        private class Parameter{
            public BigDecimal LIMIT_FILE_SIZE;
            public String host;
            public int port;
            public String uploadURL;
        }
        
        /**
         * 최초 입력 파라메터를 설정해준다.
         * 여기에서 초기값도 설정.
         */
        protected void getParameters() {
            param = new Parameter();
            param.host = getParameter("HOST");
            if(null == param.host) {
                param.host = getCodeBase().getHost();
                if(null == param.host) {
                    param.host = "localhost";
                }
            }
            String port = getParameter("PORT");
            String uploadURL = getParameter("UPLOAD_URL");
            String limitFileSize = getParameter("LIMIT_FILE_SIZE");
            param.port = (null == port) ? 80 : Integer.parseInt(port);
            param.uploadURL = (null == uploadURL) ? "/framework/jsp/fileUpload/jspFileUploader.jsp" : uploadURL;
            param.LIMIT_FILE_SIZE = (null == limitFileSize) ? new BigDecimal(2000000000) : new BigDecimal(limitFileSize); //2기가?
        }
        private boolean hasSelectedFiles() {
            boolean hasFiles = (null != files);
            if(!hasFiles) alert("파일을 선택해 주세요");
            return hasFiles;
        }
        
        private boolean isOverflowLimitFileSize() {
            totalFileLength = getTotalFileLength();
            boolean isOverflow = totalFileLength.compareTo(param.LIMIT_FILE_SIZE) > 0;
            if(isOverflow) alert(String.format("파일 용량이 제한 크기인 %s bytes를 넘겼습니다.", param.LIMIT_FILE_SIZE));
            return isOverflow;
        }        
        protected void buildChannel() throws IOException {
            //out = new FileOutputStream(new File("C:/HTTPPosted.txt")).getChannel();
            SocketAddress addr = new InetSocketAddress(InetAddress.getByName(param.host), param.port);
            out = SocketChannel.open(addr);
            out.configureBlocking(false);
            sel = Selector.open();
            out.register(sel, SelectionKey.OP_READ);
        }           
        private void buildUploadCommand() throws IOException {
            out.write(ByteBuffer.wrap(("POST " + param.uploadURL  + " HTTP/1.0" + LINE_SEPARATOR).getBytes()));
        }
        
        private void buildHeaders() throws IOException{
            Map headers = new LinkedHashMap();
            headers.put("Accept", "image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, */*");
            headers.put("Accept-Language", "ko");  //ko
            headers.put("Content-Type", "multipart/form-data; boundary="+BOUNDARY);
            headers.put("UA-CPU", "x86");
            headers.put("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; .NET CLR 2.0.50727; .NET CLR 1.1.4322; .NET CLR 3.0.04506.30)");
            headers.put("Host", param.host);
            headers.put("Content-Length", getContentLength());
            headers.put("Connection", "Keep-Alive");
            headers.put("Pragma", "no-cache");
            writeHttpHeaders(headers);
        }       
        private void writeHttpHeaders(Map headers) throws IOException {
            Iterator iter = headers.keySet().iterator();
            Object keyObj= null, valueObj = null;
            String aHeader = null;
            while(iter.hasNext()) {
                keyObj = iter.next();
                valueObj = headers.get(keyObj);
                aHeader = keyObj.toString() + ": " + valueObj.toString() + LINE_SEPARATOR;
                ByteBuffer buffer = ByteBuffer.wrap(aHeader.getBytes());
                out.write(buffer);
            }
            out.write(ByteBuffer.wrap(LINE_SEPARATOR.getBytes()));
        }        
        private void buildBody() throws IOException {
            for (int i = files.length - 1; i > -1; --i) {
                out.write(ByteBuffer.wrap(getFormDataHeader(i)));
                writeReadedFile(files[i]);
            }
        }
        private void writeReadedFile(File file) {
            uploadProgressive.setMaximum(100);
            FileChannel in = null;
            long total = file.length();
            long written = 0;
            long currentWritten = 0;
            try {
                in = new FileInputStream(file).getChannel();
                while(written < total) {
                    currentWritten = in.transferTo(written, 1024, out);
                    written += currentWritten;
                    onWritten(currentWritten);
                    uploadProgressive.setValue((int)((double)written/(double)total * 100));
                }           
                in.close();
                in = null;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if(null != in)
                        in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }   
        protected final void onWritten(long written) {
            prosessedLength += written;
            //totalUploadProgressive.setValue((int)((double)prosessedLength /(double)totalFileLength.longValue() * 100));
            totalUploadProgressive.setValue((int)((double)prosessedLength /(double)totalFileLength.longValue() * 100));
            totalUploadProgressive.setString(getBPS());     
        }        
        private String getBPS() {
            long currentTimeMillis = System.currentTimeMillis();
            long timeDiff =  currentTimeMillis - this.prevCurrentTimeMillis;
            if(timeDiff < 500) return this.bps;
            double bits = (this.prosessedLength - this.prevProsessedLength) * 8;
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
            this.prevProsessedLength = this.prosessedLength;
            this.prevCurrentTimeMillis = currentTimeMillis;
            this.bps = bps;
            return bps;
        }            
        private byte[] getFormDataHeader(int index) {
            String contentType = URLConnection.guessContentTypeFromName(files[index].getName());
            contentType = (null == contentType) ? "application/octet-stream" : contentType;
            return String.format(
                    "%s--%s%sContent-Disposition: form-data; name=\"fileToUpload%d\"; filename=\"%s\"%s" +
                    "Content-Type: %s%s%s", LINE_SEPARATOR, BOUNDARY, LINE_SEPARATOR, index,
                    files[index].getAbsolutePath(), LINE_SEPARATOR, contentType, LINE_SEPARATOR, LINE_SEPARATOR).getBytes();
    /*      try{
                return String.format(
                        "%s--%s%sContent-Disposition: form-data; name=\"fileToUpload%d\"; filename=\"%s\"%s" +
                        "Content-Type: %s%s%s", LINE_SEPARATOR, BOUNDARY, LINE_SEPARATOR, index,
                        new String(files[index].getAbsolutePath().getBytes("UTF-8"),"8859_1"), LINE_SEPARATOR, contentType, LINE_SEPARATOR, LINE_SEPARATOR).getBytes();
            }catch(UnsupportedEncodingException e){
                return null;
            }*/
        }        
        private void buildLastBoundary() throws IOException {
            out.write(ByteBuffer.wrap(String.format("%s--%s--%s",
                    LINE_SEPARATOR, BOUNDARY, LINE_SEPARATOR).getBytes()));
        }    
        private void getResponseMessage() throws IOException {
            System.out.println("-----------------------Response Message-----------------------");
            ByteBuffer buf = ByteBuffer.allocate(1024);
            sel.select();
            Iterator iter = sel.selectedKeys().iterator();
            while(iter.hasNext()) {
                SelectionKey key = (SelectionKey)iter.next();
                SocketChannel sc = (SocketChannel)key.channel();
                sc.read(buf);
                buf.flip();
                System.out.println(Charset.forName("UTF-8").decode(buf).toString());
                buf.clear();
            }
            System.out.println("-----------------------Response Message-----------------------");
        }        
        private void closeChannel() {
            try {
                if(null == out) return;
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }   
        public void run() {
            worker.execute();
        }    
        
        
        
        private BigDecimal getContentLength() {
            return getTotalFileLength().add(getFormDataHeaderLength()).add(getLastBoundaryLength());
        }
        private BigDecimal getLastBoundaryLength() {
            String lastBoundary = LINE_SEPARATOR + "--" + BOUNDARY + "--" + LINE_SEPARATOR;
            return new BigDecimal(lastBoundary.length());
        }
        private BigDecimal getFormDataHeaderLength() {
            BigDecimal formDataLength = BigDecimal.ZERO;
            for (int i = files.length - 1; i > -1; --i) {
                formDataLength = formDataLength.add(new BigDecimal(getFormDataHeader(i).length));
            }
            return formDataLength;
        }
        /**
         * @return
         * @uml.property  name="totalFileLength"
         */
        protected BigDecimal getTotalFileLength() {
            BigDecimal length = BigDecimal.ZERO;
            for (int i = files.length - 1; i > -1; --i) {
                length = length.add(new BigDecimal(files[i].length()));
            }
            return length;
        }        
    }
    
    private void alert(String str){        
        JOptionPane.showMessageDialog(this, str);        
    }

    private void button_upload(JPanel buttonPanel) {
        JButton upload = new JButton("업로드");
        buttonPanel.add(upload);
        upload.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                SwingUtilities.invokeLater(new UploadRun());
            }
        });
    }
    
}