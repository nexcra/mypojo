package erwins.util.lib;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;


public abstract class PageUtil {
    
	/** 
	 * 인메모리 페이징 처리.  
	 * 소팅 같은 옵션은 나중에 추가하자. 
	 * */
	public static <T> Page<T> page(Pageable pageable,List<T> totalList) {
		int offset = pageable.getOffset();
		List<T> sub = totalList.subList(offset, offset + pageable.getPageSize());
		Page<T> page = new PageImpl<T>(sub, pageable, totalList.size());
		return page;
	}
    


}
