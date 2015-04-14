package erwins.jsample.validation;

import lombok.Data;
import erwins.util.dateTime.DateTimeVo;
import erwins.util.dateTime.DateVo;
import erwins.util.validation.constraints.vo.CompositeVo;
import erwins.util.validation.constraints.vo.RangeVo;

//@ScriptAssert(lang = "javascript", script = "_this.startDate > _this.endDate")
@Data
public class TestVoBean{
	
	@RangeVo
	@CompositeVo
	private DateTimeVo date01;
	
	//@CompositeVo
	@RangeVo
	private DateVo date02;
	
	

}
