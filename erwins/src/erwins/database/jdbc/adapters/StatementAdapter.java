/*  (c) 2004 Allen I. Holub. All rights reserved.
 *
 *  This code may be used freely by yourself with the following
 *  restrictions:
 *
 *  o Your splash screen, about box, or equivalent, must include
 *    Allen Holub's name, copyright, and URL. For example:
 *
 *      This program contains Allen Holub's SQL package.<br>
 *      (c) 2005 Allen I. Holub. All Rights Reserved.<br>
 *              http://www.holub.com<br>
 *
 *    If your program does not run interactively, then the foregoing
 *    notice must appear in your documentation.
 *
 *  o You may not redistribute (or mirror) the source code.
 *
 *  o You must report any bugs that you find to me. Use the form at
 *    http://www.holub.com/company/contact.html or send email to
 *    allen@Holub.com.
 *
 *  o The software is supplied <em>as is</em>. Neither Allen Holub nor
 *    Holub Associates are responsible for any bugs (or any problems
 *    caused by bugs, including lost productivity or data)
 *    in any of this code.
 */
package erwins.database.jdbc.adapters;
import java.sql.*;
/***
 * @include /etc/license.txt
 */
public class StatementAdapter implements java.sql.Statement
{
public StatementAdapter() {}

public void setFetchSize(int fetchSize) throws SQLException {throw new SQLException("Statement.setFetchSize(int fetchSize) not supported");}
public int getFetchSize() throws SQLException {throw new SQLException("Statement.getFetchSize() not supported");}
public int getMaxRows() throws SQLException {throw new SQLException("Statement.getMaxRows() not supported");}
public void setMaxRows(int max) throws SQLException {throw new SQLException("Statement.setMaxRows(int max) not supported");}
public void setFetchDirection(int fetchDirection) throws SQLException {throw new SQLException("Statement.setFetchDirection(int fetchDirection) not supported");}
public int getFetchDirection() throws SQLException {throw new SQLException("Statement.getFetchDirection() not supported");}
public int getResultSetConcurrency() throws SQLException {throw new SQLException("Statement.getResultSetConcurrency() not supported");}
public int getResultSetHoldability() throws SQLException {throw new SQLException("Statement.getResultSetHoldability() not supported");}
public int getResultSetType() throws SQLException {throw new SQLException("Statement.getResultSetType() not supported");}
public void setQueryTimeout(int seconds) throws SQLException {throw new SQLException("Statement.setQueryTimeout(int seconds) not supported");}
public int getQueryTimeout() throws SQLException {throw new SQLException("Statement.getQueryTimeout() not supported");}
public ResultSet getResultSet() throws SQLException {throw new SQLException("Statement.getResultSet() not supported");}
public ResultSet executeQuery(String sql) throws SQLException {		throw new SQLException("Statement.executeQuery(String sql) not supported");}
public int executeUpdate(String sql, int i) throws SQLException {throw new SQLException("Statement.executeUpdate(String sql, int i) not supported");}  
public int executeUpdate(String sql, String[] cols) throws SQLException {throw new SQLException("Statement.executeUpdate(String sql, String[] cols) not supported");}  
public boolean execute(String sql) throws SQLException {throw new SQLException("Statement.execute(String sql) not supported");}
public boolean execute(String sql, String[] cols) throws SQLException {throw new SQLException("Statement.execute(String sql, String[] cols) not supported");}
public boolean execute(String sql, int i) throws SQLException {throw new SQLException("Statement.execute(String sql, int i) not supported");}
public boolean execute(String sql, int[] cols) throws SQLException {throw new SQLException("Statement.execute(String sql, int[] cols) not supported");}
public void cancel() throws SQLException {throw new SQLException("Statement.cancel() not supported");}
public void clearWarnings() throws SQLException {throw new SQLException("Statement.clearWarnings() not supported");}
public Connection getConnection() throws SQLException {throw new SQLException("Statement.getConnection() not supported");}
public ResultSet getGeneratedKeys() throws SQLException {throw new SQLException("Statement.getGeneratedKeys() not supported");}
public void addBatch(String sql) throws SQLException {throw new SQLException("Statement.addBatch(String sql) not supported");}
public int[] executeBatch() throws SQLException {throw new SQLException("not supported");}
public void clearBatch() throws SQLException {throw new SQLException("Statement.clearBatch() not supported");}
public void close() throws SQLException {throw new SQLException("Statement.close() not supported");}
public int executeUpdate(String sql, int[] i) throws SQLException {throw new SQLException("Statement.executeUpdate(String sql, int[] i) not supported");}
public int executeUpdate(String sql) throws SQLException {throw new SQLException("Statement.executeUpdate(String sql) not supported");}
public int getMaxFieldSize() throws SQLException {throw new SQLException("Statement.getMaxFieldSize() not supported");}
public boolean getMoreResults() throws SQLException {throw new SQLException("Statement.getMoreResults() not supported");}
public boolean getMoreResults(int i) throws SQLException {throw new SQLException("Statement.getMoreResults(int i) not supported");}
public int getUpdateCount() throws SQLException {throw new SQLException("Statement.getUpdateCount() not supported");}
public SQLWarning getWarnings() throws SQLException {throw new SQLException("Statement.getWarnings() not supported");}
public void setCursorName(String name) throws SQLException {throw new SQLException("Statement.setCursorName(String name) not supported");}
public void setEscapeProcessing(boolean enable) throws SQLException {throw new SQLException("Statement.setEscapeProcessing(boolean enable) not supported");}
public void setMaxFieldSize(int max) throws SQLException {throw new SQLException("Statement.setMaxFieldSize(int max) not supported");}
public void checkClosed() throws SQLException {throw new SQLException("Statement.checkClosed() not supported");}

public boolean isClosed() throws SQLException {
    // TODO Auto-generated method stub
    return false;
}

public boolean isPoolable() throws SQLException {
    // TODO Auto-generated method stub
    return false;
}

public void setPoolable(boolean poolable) throws SQLException {
    // TODO Auto-generated method stub
    
}

public boolean isWrapperFor(Class<?> iface) throws SQLException {
    // TODO Auto-generated method stub
    return false;
}

public <T> T unwrap(Class<T> iface) throws SQLException {
    // TODO Auto-generated method stub
    return null;
}	
}
