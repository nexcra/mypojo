package erwins.util.tools;

import java.util.ArrayList;
import java.util.List;

/** DB용 무결성 체크기. SQL이 긴 등록은 Groovy활용~ */
public abstract class DBValidator {

	protected abstract List<Object> executeSql(String sql);

	private final List<DBValidateCondition> conditions = new ArrayList<DBValidator.DBValidateCondition>();

	public static class DBValidateCondition {
		public final String name;
		public final String description;
		public final String sql;
		public final DBValidateType type;
		private List<Object> result;
		public DBValidateCondition(String name, String description, String sql, DBValidateType type) {
			this.name = name;
			this.description = description;
			this.sql = sql;
			this.type = type;
		}
		public List<Object> getResult() {
			return result;
		}
	}

	/** 더 있을게 있나? */
	public static enum DBValidateType {
		Unique, Empty
	}

	public Object validate() {
		List<DBValidateCondition> violate = new ArrayList<DBValidator.DBValidateCondition>();
		for (DBValidateCondition each : conditions) {
			List<Object> result = executeSql(each.sql);
			each.result = result;
			switch (each.type) {
			case Unique:
				if(result.size()!=1) violate.add(each);
				break;
			case Empty:
				if(result.size()!=0) violate.add(each);
				break;
			}
		}
		return violate;
	}

}
