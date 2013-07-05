package erwins.util.spring;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.google.common.base.Preconditions;


/** 
 * 아파치 커먼스 업로드를 사용하는 org.springframework.web.multipart.commons.CommonsMultipartResolver 에 적용되는 도우미이다.
 * 최대용량 제한 : 이미 다 올리고 나서 적용되는듯.. 그러먼 사실 별필요가 없다
 * 임계용량 : 이 용량을 넘어가는 업로드는 임시디렉토리에 저장된다.  (인메모리든 로컬디스크든 스래드가 풀릴때 자동으로 삭제된다.)
 * 
 * 모든 행동(HTTP를 해석하고 컨트롤러를 타는 행위)은 업로드가 종료된 후 일어나게 된다
 * 즉 내부적으로 getItemIterator 가 아닌 parseRequest를 사용한다. ㅠㅠ
 * 
 * 스프링은 스트리밍 API를 지원하지 않느듯 하다. (올리는 도중 벨리데이션 처리해서, 요청을 스킵하는 등의 스트리밍 업로드를 사용하고싶으면 커먼스를 직접 사용하자)
 * 게다가 리졸버가 한번 등록되면 전역 적용이라 별도 구간에 스트리밍 업로드를 적용하기 힘들듯 하다.
 * */
public abstract class CommonsMultipartHelper {
	
    /** FileItem의 구현체가 DiskFileItem밖에 없다...
     * isInMemory 로 체크해서 파일이 없으면 DiskFileItem의  write()해주자 */
    public static DiskFileItem getDiskFileItem(HttpServletRequest req,String fieldName){
        Preconditions.checkState(req instanceof MultipartHttpServletRequest,"request가 Multipart를 구현하지 않습니다.  MultipartResolver가 설정되어있는지 확인하세요");
        MultipartHttpServletRequest multiReq = (MultipartHttpServletRequest)req;
        CommonsMultipartFile file = (CommonsMultipartFile) multiReq.getFile(fieldName);
        Preconditions.checkState(file != null,"fieldName " + fieldName +" 에 해당하는 파일이 존재하지 않습니다. HTML을 확인해 주세요");
        return (DiskFileItem)file.getFileItem();
    }
	
	
}
