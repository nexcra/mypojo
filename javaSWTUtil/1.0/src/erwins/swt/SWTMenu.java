
package erwins.swt;

import java.util.ArrayList;
import java.util.List;

import erwins.swt.network.DownloadByUrl;
import erwins.swt.network.FtpSynch;
import erwins.swt.network.TalkClientActivator;
import erwins.swt.text.CodeLine;
import erwins.swt.text.TextSearch;
import erwins.util.reflexive.Connectable;
import erwins.util.reflexive.Connector;
import erwins.util.reflexive.Visitor;


public enum SWTMenu implements Connectable<String,SWTMenu>{
    
	root("루트","이건 아무 의미 없음~",null,null),
	
	text("텍스트 파싱","",null,root),
	codeLine("코드 평균라인/의존관계","코드별 합계/평균/최대 라인수 , 각 JAR/패키지별 의존관계 파악",new CodeLine(),text),
	textSearch("텍스트 검색","각 텍스트파일에서 특정 문자열 검색/치환/통합하기.",new TextSearch(),text),
	
    database("데이터베이스 관련","",null,root),
    excell("엑셀 업/다운 로드","",null,database),
    sqlGenerator("SQL생성기","",null,database),
    
    file("파일 다루기","",null,root),
    fileHash("해시를 이용한 중복파일 검색 / 삭제","",null,file),
    fileFocus("jpg등의 각 파일을 특정 디렉토리로 분산 저장","",null,file),
    fileSearch("용량 등의 조건으로 파일 검색 / 삭제","",null,file),
    
    network("네트워크","",null,root),
    TalkClient("간단 채팅","간단히 만든 소켓 채팅 프로그램",new TalkClientActivator(),network),
    urlTest("URL테스트","개발 등 할때 URL을 테스트 한다.",null,network),
    ftp("FTP 유틸","",null,network),
    ftpDownload("FTP Download","로컬 디스크에 원격지의 FTP상 특정 파일을 다운로드 한다.",null,ftp),
    ftpSynch("FTP Synch","로컬 디스크의 디렉토리와 원격지의 FTP디렉토리를 동기화 한다.",new FtpSynch(),ftp),
    downloadByURL("URL download","해당 웹페이지 내의 리소스(jpg등)를 모두 다운받는다.",new DownloadByUrl(),network),
    googleToNateon("Google To Nateon","구글 주소록을 네이트온 형식으로 바꿔준다.",null,network),
    ;

    private final String id;
    private final String name;
    private final String uri;
    private final String description;
    private final SWTBuildable swt;
    private SWTMenu parent;
    private List<SWTMenu> children = new ArrayList<SWTMenu>();
    
    private SWTMenu(String name,String description,SWTBuildable swt,SWTMenu parent){
        this.name = name;
        this.description = description;
        this.parent = parent;
        if(parent!=null && parent.id!=null) this.uri = parent.uri + "." + this.name(); 
        else this.uri = this.name();
        this.id = this.name();
        this.swt = swt;
    }
    
    public void addChildren(SWTMenu child) {
    	children.add(child);
    }
    
    public List<SWTMenu> getChildren() {
        return children;
    }
    
    public boolean isLeaf() {
        return children.size()==0 ? true : false; 
    }
    
    public SWTMenu getRoot() {
        return connector.getRoot(this);
    }
    
    public void setId(String id) {
        throw new UnsupportedOperationException();
    }
    
    // ===========================================================================================
    //                                    static
    // ===========================================================================================
    public static final Connector<String,SWTMenu> connector = new Connector<String,SWTMenu>();
    static{
        connector.setChildren(SWTMenu.values());
        connector.orderSiblings();
    }
    
    
    // ===========================================================================================
    //                                    getter
    // ===========================================================================================
    public String getValue() {
        return getId();
    }
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public SWTMenu getParent() {
        return parent;
    }
    public void setParent(SWTMenu parent) {
        this.parent = parent;
    }    
    public void accept(Visitor<SWTMenu> v) {
        v.visit(this);
    }

	public String getUri() {
		return uri;
	}

	public SWTBuildable getSwt() {
		return swt;
	}
	
    
    
}
