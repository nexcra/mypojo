
package erwins.util.vender.hibernate;

import org.hibernate.cfg.ImprovedNamingStrategy;

import erwins.util.lib.StringUtil;


@SuppressWarnings("serial")
public class DefaultNamingRule  extends ImprovedNamingStrategy{

	/** ManyToOne을 매핑할 경우 호출되는듯.  */
	@Override
	public String logicalCollectionColumnName(String columnName, String propertyName, String referencedColumn) {
		return super.logicalCollectionColumnName(columnName, propertyName, referencedColumn);
	}

	@Override
	public String logicalCollectionTableName(String tableName, String ownerEntityTable, String associatedEntityTable,
			String propertyName) {
		return super.logicalCollectionTableName(tableName, ownerEntityTable, associatedEntityTable, propertyName);
	}

	/** 일반적인 사용법. 값이 없을때만 적용해준다. ID같은데는 적용하면 안된다. */
	@Override
	public String logicalColumnName(String columnName, String propertyName) {
		if(StringUtil.isEmpty(columnName)) return StringUtil.getUnderscore(propertyName); 
		return columnName;
	}

	/** 컬럼 이름을 생략했을때만 호출된다. 이게 호출된 후 logicalColumnName()이 실행된다. */
	@Override
	public String propertyToColumnName(String propertyName) {
		return super.propertyToColumnName(propertyName);
	}

	/** 컬럼 이름을 명시적으로 적어주었을때만  호출된다. 사용할일 없을듯 */
	@Override
	public String columnName(String columnName) {
		return super.columnName(columnName);
	}

	/** 테이블 이름 바꿀 일은 거의 없다. */
	@Override
	public String tableName(String tableName) {
		return super.tableName(tableName);
	}
	
	
}