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
import java.util.Properties;

/***
 * @include /etc/license.txt
 */

@SuppressWarnings({"unused","unchecked"})
public class ConnectionAdapter implements java.sql.Connection {
    public ConnectionAdapter() throws SQLException {} // Not an error if this one is called.

    public ConnectionAdapter(java.sql.Driver driver, String url, java.util.Properties info) throws SQLException {
        throw new SQLException("ConnectionAdapter constructor unsupported");
    }

    public void setHoldability(int h) throws SQLException {
        throw new SQLException("Connection.setHoldability(int h) unsupported");
    }

    public int getHoldability() throws SQLException {
        throw new SQLException("Connection.getHoldability() unsupported");
    }

    //...
    //@middle
    public Savepoint setSavepoint() throws SQLException {
        throw new SQLException("Connection.setSavepoint() unsupported");
    }

    public Savepoint setSavepoint(String name) throws SQLException {
        throw new SQLException("Connection.setSavepoint(String name) unsupported");
    }

    public Statement createStatement() throws SQLException {
        throw new SQLException("Connection.createStatement() unsupported");
    }

    public Statement createStatement(int a, int b, int c) throws SQLException {
        throw new SQLException("Connection.createStatement(int a, int b, int c) unsupported");
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        throw new SQLException("Connection.createStatement(int resultSetType, int resultSetConcurrency) unsupported");
    }

    public DatabaseMetaData getMetaData() throws SQLException {
        throw new SQLException("Connection.getMetaData() unsupported");
    }

    public void close() throws SQLException {
        throw new SQLException("Connection.close() unsupported");
    }

    public boolean isClosed() throws SQLException {
        throw new SQLException("Connection.isClosed() unsupported");
    }

    public boolean isReadOnly() throws SQLException {
        throw new SQLException("Connection.isReadOnly() unsupported");
    }

    public void clearWarnings() throws SQLException {
        throw new SQLException("Connection.clearWarnings() unsupported");
    }

    public void commit() throws SQLException {
        throw new SQLException("Connection.commit() unsupported");
    }

    public boolean getAutoCommit() throws SQLException {
        throw new SQLException("Connection.getAutoCommit() unsupported");
    }

    public String getCatalog() throws SQLException {
        throw new SQLException("Connection.getCatalog() unsupported");
    }

    public int getTransactionIsolation() throws SQLException {
        throw new SQLException("Connection.getTransactionIsolation() unsupported");
    }

    public SQLWarning getWarnings() throws SQLException {
        throw new SQLException("Connection.getWarnings() unsupported");
    }

    public String nativeSQL(String sql) throws SQLException {
        throw new SQLException("Connection.nativeSQL(String sql) unsupported");
    }

    public CallableStatement prepareCall(String sql) throws SQLException {
        throw new SQLException("Connection.prepareCall(String sql) unsupported");
    }

    public CallableStatement prepareCall(String sql, int a, int b, int c) throws SQLException {
        throw new SQLException("Connection.prepareCall(String sql, int a, int b, int c) unsupported");
    }

    public CallableStatement prepareCall(String sql, int x, int y) throws SQLException {
        throw new SQLException("Connection.prepareCall(String sql, int x, int y) unsupported");
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        throw new SQLException("Connection.prepareStatement(String sql) unsupported");
    }

    public PreparedStatement prepareStatement(String sql, int x, int y) throws SQLException {
        throw new SQLException("Connection.prepareStatement(String sql, int x, int y) unsupported");
    }

    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        throw new SQLException("Connection.prepareStatement(String sql,String[] columnNames) unsupported");
    }

    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        throw new SQLException("Connection.prepareStatement(String sql,int[] columnIndexes) unsupported");
    }

    public PreparedStatement prepareStatement(String sql, int a, int b, int c) throws SQLException {
        throw new SQLException("Connection.prepareStatement(String sql,int a, int b, int c) unsupported");
    }

    public PreparedStatement prepareStatement(String sql, int a) throws SQLException {
        throw new SQLException("Connection.prepareStatement(String sql,int a) unsupported");
    }

    public void releaseSavepoint(Savepoint s) throws SQLException {
        throw new SQLException("Connection.releaseSavepoint( Savepoint s ) unsupported");
    }

    public void rollback() throws SQLException {
        throw new SQLException("Connection.rollback() unsupported");
    }

    public void rollback(Savepoint s) throws SQLException {
        throw new SQLException("Connection.rollback(Savepoint s) unsupported");
    }

    public void setAutoCommit(boolean autoCommit) throws SQLException {
        throw new SQLException("Connection.setAutoCommit(boolean autoCommit) unsupported");
    }

    public void setCatalog(String catalog) throws SQLException {
        throw new SQLException("Connection.setCatalog(String catalog) unsupported");
    }

    public void setReadOnly(boolean readOnly) throws SQLException {
        throw new SQLException("Connection.setReadOnly(boolean readOnly) unsupported");
    }

    public void setTransactionIsolation(int level) throws SQLException {
        throw new SQLException("Connection.setTransactionIsolation(int level) unsupported");
    }

    public java.util.Map getTypeMap() throws SQLException {
        throw new SQLException("unsupported");
    }

    public void setTypeMap(java.util.Map map) throws SQLException {
        throw new SQLException("Connection.setTypeMap(java.util.Map map) unsupported");
    }

    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return null;
    }

    public Blob createBlob() throws SQLException {
        return null;
    }

    public Clob createClob() throws SQLException {
        return null;
    }

    public NClob createNClob() throws SQLException {
        return null;
    }

    public SQLXML createSQLXML() throws SQLException {
        return null;
    }

    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return null;
    }

    public Properties getClientInfo() throws SQLException {
        return null;
    }

    public String getClientInfo(String name) throws SQLException {
        return null;
    }

    public boolean isValid(int timeout) throws SQLException {
        return false;
    }

    public void setClientInfo(Properties properties) throws SQLClientInfoException {

    }

    public void setClientInfo(String name, String value) throws SQLClientInfoException {

    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }
}
