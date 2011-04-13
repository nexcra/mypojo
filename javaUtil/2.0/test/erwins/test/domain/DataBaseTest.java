
package erwins.test.domain;

import org.junit.Test;

import erwins.database.Database;
import erwins.database.Table;
import erwins.database.text.ParseFailure;
import erwins.util.exception.Check;
import erwins.util.exception.Check.ExceptionRunnable;
import erwins.util.lib.StringUtil;

public class DataBaseTest{

    private static final String sqls[] = new String[]{
        "create database Dbase",
        "create table address (addrId int, street varchar, city varchar, state char(2), zip int, primary key(addrId))",
        "create table name(first varchar(10), last varchar(10), addrId integer)",
        "insert into address values( 0,'12 MyStreet','Berkeley','CA','99999')",
        "insert into address values( 1, '34 Quarry Ln.', 'Bedrock' , 'XX', '00000')",
        "insert into name VALUES ('Fred',  'Flintstone', '1')",
        "insert into name VALUES ('Wilma', 'Flintstone', '1')",
        "insert into name (last,first,addrId) VALUES('Holub','Allen',(10-10*1))"
    };
    
    @Test
    public void run() throws Exception {
        
        final Database theDatabase = new Database();

        for(String sql : sqls) theDatabase.execute(sql);
        
        Table s = theDatabase.execute("select * from name");
        Check.isTrue( StringUtil.isMatch(s.toString(), "Holub","Allen"));
        
        Check.isThrowException(new ExceptionRunnable() {
			@Override
			public void run() throws Exception {
				theDatabase.execute("insert garbage SQL");
			}
		},ParseFailure.class);

    }
    



}