package erwins.util.spring;

import lombok.Data;

/** 지금은 스프링 변환에만 사용하지만, 나중에 범용으로 수정하자  */
@Data
public class LineMetadata implements Comparable<LineMetadata>{
	private final String name;
	/** EL 표현식을 입력한다. ex) mediaCodes[1].mediaCodeName  */
	private final String fieldName;
	private final Integer index;
	public LineMetadata(Integer index,String fieldName,String name) {
		this.name = name;
		this.index = index;
		this.fieldName = fieldName;
	}
	@Override
	public int compareTo(LineMetadata o) {
		return index.compareTo(o.index);
	}
}