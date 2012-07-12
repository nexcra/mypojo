package erwins.gsample.dsl


public class SqlBuilder extends FactoryBuilderSupport { {
		registerFactory('select', new SelectFactory())
		registerFactory('join', new JoinFactory())
		registerFactory('using', new UsingFactory())
		registerFactory('where', new WhereFactory())
		registerFactory('groupBy', new GroupAndOrderFactory())
		registerFactory('orderBy', new GroupAndOrderFactory())
	}
}

/**
 * Select class encapsulates the select clause node of the SQL query
 * and acts as a parent node to all select queries.
 */
class Select {
	
	def table,        // Name of the main select table
	tables  = [], // All tables join tables and select table
	joins   = [], // Collection of all joins
	where,        // Where clause
	groupBy = [], // Collection of group by columns
	orderBy = []  // Collection of order by columns
	/**
	 * Builds and returns the SQL query based on the DSL expressions
	 */
	String getSql() {
		def sql = "SELECT * FROM $table"
		joins.each {Join join ->
			sql += " ${join.type} JOIN ${join.table} ON"
			join.using.eachWithIndex {Using using, idx ->
				if (idx > 0) sql += " AND"
				sql += " ${using.lhs} ${using.op} ${using.rhs}"
			}
		}
		where.eachWithIndex {Where where, i ->
			if (i == 0) sql += " WHERE ";
			sql += "${where.clause}"
		}
		if (groupBy) sql += " GROUP BY ${groupBy.join(', ')}";
		if (orderBy) sql += " ORDER BY ${orderBy.join(', ')}";
		return sql
	}
}

///////// Private classes to support the builder functionality /////////////

class Join {
	def table
	def type = "INNER"
	def using = []
}

class Using {
	def lhs, rhs, op = "="
	
	def methodMissing(String name, args) {
		println "Looking for ${name}"
	}
}

class Where {
	def clause
}

///////// Factories that construct the appropriate objects based on the DSL expressions //////////////

public class SelectFactory extends AbstractFactory {
	
	public Object newInstance(FactoryBuilderSupport factoryBuilderSupport, name, value, Map map) {
		return new Select(table: value, tables: [value])
	}
	
	public void setChild(FactoryBuilderSupport factoryBuilderSupport, Object parent, Object child) {
		//println "Child ${child}"
		if (child instanceof Join) {
			//println "Adding a join"
			parent.joins << child
		}
		if (child instanceof Where) {
			//println "Adding a where"
			parent.where = child
		}
	}
}

public class JoinFactory extends AbstractFactory {
	
	public Object newInstance(FactoryBuilderSupport factoryBuilderSupport, name, value, Map map) {
		Join join = new Join(table: value)
		return join
	}
	
	public void setChild(FactoryBuilderSupport factoryBuilderSupport, Object parent, Object child) {
		super.setChild(factoryBuilderSupport, parent, child)
		if (child instanceof Using) {
			parent.using << child
		}
	}
	
	public void setParent(FactoryBuilderSupport factoryBuilderSupport, Object parent, Object child) {
		super.setParent(factoryBuilderSupport, parent, child);
		parent.tables << child.table
	}
}

public class UsingFactory extends AbstractFactory {
	
	public Object newInstance(FactoryBuilderSupport factoryBuilderSupport, name, op, Map map) {
		Using using = new Using()
		if (op) using.op = op
		map.each {k, v ->
			if (!using.lhs) {
				using.lhs = "${k}.${v}"
			}
			else if (!using.rhs) {
				using.rhs = "${k}.${v}"
			}
		}
		map.clear()
		return using
	}
	
	public boolean onHandleNodeAttributes(FactoryBuilderSupport factoryBuilderSupport, Object o, Map map) {
		return true;
	}
	
	public boolean isLeaf() {
		return true;
	}
}

public class WhereFactory extends AbstractFactory {
	
	public Object newInstance(FactoryBuilderSupport factoryBuilderSupport, name, value, Map map) {
		return new Where(clause: value)
	}
	
	public boolean isLeaf() {
		return true;
	}
}


public class GroupAndOrderFactory extends AbstractFactory {
	
	public Object newInstance(FactoryBuilderSupport factoryBuilderSupport, name, value, Map map) {
		def fqCols = [];
		map.each {k, v ->
			fqCols << "${k}.${v}"
		}
		return [name: name, cols: fqCols];
	}
	
	public void setParent(FactoryBuilderSupport factoryBuilderSupport, Object parent, Object child) {
		super.setParent(factoryBuilderSupport, parent, child);
		if (child.name == "groupBy") {
			parent.groupBy += child.cols
		}
		if (child.name == "orderBy") {
			parent.orderBy += child.cols
		}
	}
	
	public boolean onHandleNodeAttributes(FactoryBuilderSupport factoryBuilderSupport, Object o, Map map) {
		return true;
	}
	
	public boolean isLeaf() {
		return true;
	}
}
