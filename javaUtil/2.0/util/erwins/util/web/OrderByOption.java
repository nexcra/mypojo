package erwins.util.web;

import java.util.ArrayList;
import java.util.List;

import erwins.util.lib.StringUtil;

/** HTML 테이블에 오더바이 옵션을 준다.
 * order by  order by  <isNotEmpty property="orderBy" > $orderBy$ </isNotEmpty> */
public class OrderByOption{
	
	//private static final String ASC = "asc";
	private static final String DESC = "desc";
	
	private List<String> columns = new ArrayList<String>();
	private int defaultIndex = 0;
	private String defaultOption = "asc";
	
	public int getDefaultIndex() {
		return defaultIndex;
	}

	public void setDefaultIndex(int defaultIndex) {
		this.defaultIndex = defaultIndex;
	}

	public String getDefaultOption() {
		return defaultOption;
	}

	public void setDefaultOption(String defaultOption) {
		this.defaultOption = defaultOption;
	}

	public OrderByOption(String ... cilumns){
		for (String string : cilumns) columns.add(string);
	}
	
	/** 디폴트로 0번째꺼~ */
	public void apply(OrderAble able){
		String orderIndex = able.getOrderIndex();
		int index = defaultIndex;
		String option = defaultOption;
		if(!StringUtil.isEmpty(orderIndex)){
			index = Integer.parseInt(orderIndex);
			option = able.getOrderOption();
		}
		String result = columns.get(index);
		if(DESC.equals(option)) result+= " " + DESC;
		able.setOrderBy(result);
	}
	
	public static interface OrderAble{
		public String getOrderIndex();
		public void setOrderIndex(String orderIndex);
		public String getOrderBy();
		public void setOrderBy(String orderBy);
		public String getOrderOption();
		public void setOrderOption(String orderOption);
	}
	
	
	
    
}