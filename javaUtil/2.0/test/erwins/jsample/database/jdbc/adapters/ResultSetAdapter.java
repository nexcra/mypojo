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
package erwins.jsample.database.jdbc.adapters;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.*;

@SuppressWarnings({"unchecked"})
public class ResultSetAdapter implements java.sql.ResultSet {public ResultSetAdapter() {}
	
public boolean next() throws SQLException {throw new SQLException("ResultSet.next() unsupported");}

public int findColumn(String columnName)throws SQLException {throw new SQLException("ResultSet.findColumn(String columnName) unsupported");}

public int getRow() throws SQLException {throw new SQLException("ResultSet.getRow() unsupported");}
public boolean previous() throws SQLException {throw new SQLException("ResultSet.previous() unsupported");}
public boolean absolute(int row) throws SQLException {throw new SQLException("ResultSet.absolute(int row) unsupported");}
public boolean relative(int row) throws SQLException {throw new SQLException("ResultSet.relative(int row) unsupported");}
public void setFetchDirection(int dir) throws SQLException {throw new SQLException("ResultSet.setFetchDirection(int dir) unsupported");}
public int getFetchDirection() throws SQLException {throw new SQLException("ResultSet.getFetchDirection() unsupported");}
public void setFetchSize(int fsize) throws SQLException {throw new SQLException("ResultSet.setFetchSize(int fsize) unsupported");}
public int getFetchSize() throws SQLException {throw new SQLException("ResultSet.getFetchSize() unsupported");}
public String getString(int columnIndex) throws SQLException {throw new SQLException("ResultSet.getString(int columnIndex) unsupported");}
public String getString(String columnName) throws SQLException {throw new SQLException("ResultSet.getString(String columnName) unsupported");}
public int getInt(int columnIndex) throws SQLException {throw new SQLException("ResultSet.getInt(int columnIndex) unsupported");}
public int getInt(String columnName) throws SQLException {throw new SQLException("ResultSet.getInt(String columnName) unsupported");}
public boolean getBoolean(int columnIndex) throws SQLException {throw new SQLException("ResultSet.getBoolean(int columnIndex) unsupported");}
public boolean getBoolean(String columnName) throws SQLException {throw new SQLException("ResultSet.getBoolean(String columnName) unsupported");}
public ResultSetMetaData getMetaData() throws SQLException {throw new SQLException("ResultSet.getMetaData() unsupported");}
public short getShort(String columnName) throws SQLException{throw new SQLException("ResultSet.getShort(String columnName) unsupported");}
public short getShort(int columnIndex) throws SQLException{throw new SQLException("ResultSet.getShort(int columnIndex) unsupported");}
public java.sql.Time getTime(int columnIndex) throws SQLException{throw new SQLException("ResultSet.getTime(int columnIndex) unsupported");}
public java.sql.Time getTime(String columnName) throws SQLException{throw new SQLException("ResultSet.getTime(String columnName) unsupported");}
public java.sql.Time getTime(int columnIndex, java.util.Calendar cal) throws SQLException{throw new SQLException("ResultSet.getTime(int columnIndex, java.util.Calendar cal) unsupported");}
public java.sql.Time getTime(String columnName, java.util.Calendar cal) throws SQLException{throw new SQLException("ResultSet.getTime(String columnName, java.util.Calendar cal) unsupported");}
public java.sql.Timestamp getTimestamp(String columnName) throws SQLException{throw new SQLException("ResultSet.getTimestamp(String columnName) unsupported");}
public java.sql.Timestamp getTimestamp(int columnIndex) throws SQLException{throw new SQLException("ResultSet.getTimestamp(int columnIndex) unsupported");}
public java.sql.Timestamp getTimestamp(String columnName, java.util.Calendar cal) throws SQLException{throw new SQLException("ResultSet.getTimestamp(String columnName, java.util.Calendar cal) unsupported");}
public java.sql.Timestamp getTimestamp(int columnIndex, java.util.Calendar cal) throws SQLException{throw new SQLException("ResultSet.getTimestamp(int columnIndex, java.util.Calendar cal) unsupported");}
public java.sql.Date getDate(int columnIndex) throws SQLException{throw new SQLException("ResultSet.getDate(int columnIndex) unsupported");}
public java.sql.Date getDate(int columnIndex, java.util.Calendar cal) throws SQLException{throw new SQLException("ResultSet.getDate(int columnIndex, java.util.Calendar cal) unsupported");}
public java.sql.Date getDate(String columnIndex, java.util.Calendar cal) throws SQLException{throw new SQLException("ResultSet.getDate(String columnIndex, java.util.Calendar cal) unsupported");}
public java.sql.Date getDate(String columnName) throws SQLException{throw new SQLException("ResultSet.getDate(String columnName) unsupported");}
public double getDouble(String columnName) throws SQLException{throw new SQLException("ResultSet.getDouble(String columnName) unsupported");}
public double getDouble(int columnIndex) throws SQLException{throw new SQLException("ResultSet.getDouble(int columnIndex) unsupported");}
public float getFloat(String columnName) throws SQLException{throw new SQLException("ResultSet.getFloat(String columnName) unsupported");}
public float getFloat(int columnIndex) throws SQLException{throw new SQLException("ResultSet.getFloat(int columnIndex) unsupported");}
public long getLong(String columnName) throws SQLException{throw new SQLException("ResultSet.getLong(String columnName) unsupported");}
public long getLong(int columnIndex) throws SQLException{throw new SQLException("ResultSet.getLong(int columnIndex) unsupported");}
public java.io.InputStream getUnicodeStream(int columnIndex) throws SQLException{throw new SQLException("ResultSet.getUnicodeStream(int columnIndex) unsupported");}
public java.io.InputStream getUnicodeStream(String columnName) throws SQLException{throw new SQLException("ResultSet.getUnicodeStream(String columnName) unsupported");}
public java.io.InputStream getAsciiStream(String columnName) throws SQLException{throw new SQLException("ResultSet.getAsciiStream(String columnName) unsupported");}
public java.io.InputStream getAsciiStream(int columnIndex) throws SQLException{throw new SQLException("ResultSet.getAsciiStream(int columnIndex) unsupported");}
public BigDecimal getBigDecimal(String columnName) throws SQLException{throw new SQLException("ResultSet.getBigDecimal(String columnName) unsupported");}
public BigDecimal getBigDecimal(String columnName, int scale) throws SQLException{throw new SQLException("ResultSet.getBigDecimal(String columnName, int scale) unsupported");}
public BigDecimal getBigDecimal(int columnIndex) throws SQLException{throw new SQLException("ResultSet.getBigDecimal(int columnIndex) unsupported");}
public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException{throw new SQLException("ResultSet.getBigDecimal(int columnIndex, int scale) unsupported");}
public java.io.InputStream getBinaryStream(int columnIndex) throws SQLException{throw new SQLException("ResultSet.getBinaryStream(int columnIndex) unsupported");}
public java.io.InputStream getBinaryStream(String columnName) throws SQLException{throw new SQLException("ResultSet.getBinaryStream(String columnName) unsupported");}
public byte getByte(int columnIndex) throws SQLException{throw new SQLException("ResultSet.getByte(int columnIndex) unsupported");}
public byte getByte(String columnName) throws SQLException{throw new SQLException("ResultSet.getByte(String columnName) unsupported");}
public byte[] getBytes(int columnIndex) throws SQLException{throw new SQLException("unsupported");}
public byte[] getBytes(String columnName) throws SQLException{throw new SQLException("unsupported");}
public String getCursorName() throws SQLException{return null;}
public Object getObject(String columnName) throws SQLException{throw new SQLException("ResultSet.getObject(String columnName) unsupported");}
public Object getObject(int columnIndex) throws SQLException{throw new SQLException("ResultSet.getObject(int columnIndex) unsupported");}
public Object getObject(int columnIndex, java.util.Map map) throws SQLException{throw new SQLException("ResultSet.getObject(int columnIndex, java.util.Map map) unsupported");}
public Object getObject(String columnIndex, java.util.Map map) throws SQLException{throw new SQLException("ResultSet.getObject(String columnIndex, java.util.Map map) unsupported");}
public java.sql.Ref getRef(int columnIndex) throws SQLException{throw new SQLException("ResultSet.getRef(int columnIndex) unsupported");}
public java.sql.Ref getRef(String columnIndex) throws SQLException{throw new SQLException("ResultSet.getRef(String columnIndex) unsupported");}
public java.sql.Blob getBlob(int columnIndex) throws SQLException{throw new SQLException("ResultSet.getBlob(int columnIndex) unsupported");}
public java.sql.Blob getBlob(String columnIndex) throws SQLException{throw new SQLException("ResultSet.getBlob(String columnIndex) unsupported");}
public java.sql.Clob getClob(int columnIndex) throws SQLException{throw new SQLException("ResultSet.getClob(int columnIndex) unsupported");}
public java.sql.Clob getClob(String columnIndex) throws SQLException{throw new SQLException("ResultSet.getClob(String columnIndex) unsupported");}
public java.sql.Array getArray(int columnIndex) throws SQLException{throw new SQLException("ResultSet.getArray(int columnIndex) unsupported");}
public java.sql.Array getArray(String columnIndex) throws SQLException{throw new SQLException("ResultSet.getArray(String columnIndex) unsupported");}
public java.io.Reader getCharacterStream(int columnIndex) throws SQLException{throw new SQLException("ResultSet.getCharacterStream(int columnIndex) unsupported");}
public java.io.Reader getCharacterStream(String columnName) throws SQLException{throw new SQLException("ResultSet.getCharacterStream(String columnName) unsupported");}
public SQLWarning getWarnings() throws SQLException {throw new SQLException("ResultSet.getWarnings() unsupported");}
public boolean wasNull() throws SQLException {throw new SQLException("ResultSet.wasNull() unsupported");}
public void clearWarnings() throws SQLException {throw new SQLException("ResultSet.clearWarnings() unsupported");}	
public boolean isFirst() throws SQLException {throw new SQLException("ResultSet.isFirst() unsupported");}
public boolean isBeforeFirst() throws SQLException {throw new SQLException("ResultSet.isBeforeFirst() unsupported");}
public void beforeFirst() throws SQLException {throw new SQLException("ResultSet.beforeFirst() unsupported");}
public boolean first() throws SQLException {throw new SQLException("ResultSet.first() unsupported");}
public java.net.URL getURL(String s) throws SQLException {throw new SQLException("ResultSet.getURL(String s) unsupported");}
public java.net.URL getURL(int i) throws SQLException {throw new SQLException("ResultSet.getURL(int i) unsupported");}
public boolean isAfterLast() throws SQLException {throw new SQLException("ResultSet.isAfterLast() unsupported");}
public void afterLast() throws SQLException {throw new SQLException("ResultSet.afterLast() unsupported");}
public boolean isLast() throws SQLException {throw new SQLException("ResultSet.isLast() unsupported");}
public boolean last() throws SQLException {throw new SQLException("ResultSet.last() unsupported");}
public int getType() throws SQLException {throw new SQLException("ResultSet.getType() unsupported");}
public int getConcurrency() throws SQLException {throw new SQLException("ResultSet.getConcurrency() unsupported");}
public boolean rowUpdated() throws SQLException {throw new SQLException("ResultSet.rowUpdated() unsupported");}
public boolean rowInserted() throws SQLException {throw new SQLException("ResultSet.rowInserted() unsupported");}
public boolean rowDeleted() throws SQLException {throw new SQLException("ResultSet.rowDeleted() unsupported");}
public void updateRef(String s, Ref r) throws SQLException {throw new SQLException("ResultSet.updateRef(String s, Ref r) unsupported");}
public void updateRef(int s, Ref r) throws SQLException {throw new SQLException("ResultSet.updateRef(int s, Ref r) unsupported");}
public void updateClob(String s, Clob c) throws SQLException {throw new SQLException("ResultSet.updateClob(String s, Clob c) unsupported");}
public void updateClob(int i, Clob c) throws SQLException {throw new SQLException("ResultSet.updateClob(int i, Clob c) unsupported");}
public void updateClob(Clob c) throws SQLException {throw new SQLException("ResultSet.updateClob(Clob c) unsupported");}
public void updateBlob(int i, Blob c) throws SQLException {throw new SQLException("ResultSet.updateBlob(int i, Blob c) unsupported");}
public void updateBlob(String s, Blob c) throws SQLException {throw new SQLException("ResultSet.updateBlob(String s, Blob c) unsupported");} 
public void updateArray(int i, Array c) throws SQLException {throw new SQLException("ResultSet.updateArray(int i, Array c) unsupported");}
public void updateArray(String s, Array c) throws SQLException {throw new SQLException("ResultSet.updateArray(String s, Array c) unsupported");}
public void insertRow() throws SQLException {throw new SQLException("ResultSet.insertRow() unsupported");}
public void updateRow() throws SQLException {throw new SQLException("ResultSet.updateRow() unsupported");}
public void deleteRow() throws SQLException {throw new SQLException("ResultSet.deleteRow() unsupported");}
public void refreshRow() throws SQLException {throw new SQLException("ResultSet.refreshRow() unsupported");}
public void cancelRowUpdates() throws SQLException {throw new SQLException("ResultSet.cancelRowUpdates() unsupported");}
public void moveToInsertRow() throws SQLException {throw new SQLException("ResultSet.moveToInsertRow() unsupported");}
public void moveToCurrentRow() throws SQLException {throw new SQLException("ResultSet.moveToCurrentRow() unsupported");}
public void updateNull(int colIndex) throws SQLException {throw new SQLException("ResultSet.updateNull(int colIndex) unsupported");}
public void updateBoolean(int colIndex, boolean b) throws SQLException {throw new SQLException("ResultSet.updateBoolean(int colIndex, boolean b) unsupported");}
public void updateByte(int colIndex, byte b) throws SQLException {throw new SQLException("ResultSet.updateByte(int colIndex, byte b) unsupported");}
public void updateShort(int colIndex, short b) throws SQLException {throw new SQLException("ResultSet.updateShort(int colIndex, short b) unsupported");}
public void updateInt(int colIndex, int b) throws SQLException {throw new SQLException("ResultSet.updateInt(int colIndex, int b) unsupported");}
public void updateLong(int colIndex, long b) throws SQLException {throw new SQLException("ResultSet.updateLong(int colIndex, long b) unsupported");}
public void updateFloat(int colIndex, float f) throws SQLException {throw new SQLException("ResultSet.updateFloat(int colIndex, float f) unsupported");}
public void updateDouble(int colIndex, double f) throws SQLException {throw new SQLException("ResultSet.updateDouble(int colIndex, double f) unsupported");}
public void updateBigDecimal(int colIndex, BigDecimal f) throws SQLException {throw new SQLException("ResultSet.updateBigDecimal(int colIndex, BigDecimal f) unsupported");}
public void updateString(int colIndex, String s) throws SQLException {throw new SQLException("ResultSet.updateString(int colIndex, String s) unsupported");}
public void updateBytes(int colIndex, byte[] s) throws SQLException {throw new SQLException("ResultSet.updateBytes(int colIndex, byte[] s) unsupported");}
public void updateDate(int colIndex, java.sql.Date d) throws SQLException {throw new SQLException("ResultSet.updateDate(int colIndex, java.sql.Date d) unsupported");}
public void updateTime(int colIndex, java.sql.Time t) throws SQLException {throw new SQLException("ResultSet.updateTime(int colIndex, java.sql.Time t) unsupported");}
public void updateTimestamp(int colIndex, java.sql.Timestamp t) throws SQLException {throw new SQLException("ResultSet.updateTimestamp(int colIndex, java.sql.Timestamp t) unsupported");}
public void updateAsciiStream(int colIndex, java.io.InputStream in, int s) throws SQLException {throw new SQLException("ResultSet.updateAsciiStream(int colIndex, java.io.InputStream in, int s) unsupported");}
public void updateBinaryStream(int colIndex, java.io.InputStream in, int s) throws SQLException {throw new SQLException("ResultSet.updateBinaryStream(int colIndex, java.io.InputStream in, int s) unsupported");}
public void updateCharacterStream(int colIndex, java.io.Reader in, int s) throws SQLException {throw new SQLException("ResultSet.updateCharacterStream(int colIndex, java.io.Reader in, int s) unsupported");}
public void updateObject(int colIndex, Object obj) throws SQLException {throw new SQLException("ResultSet.updateObject(int colIndex, Object obj) unsupported");}
public void updateObject(int colIndex, Object obj, int s) throws SQLException {throw new SQLException("ResultSet.updateObject(int colIndex, Object obj, int s) unsupported");}
public void updateNull(String colIndex) throws SQLException {throw new SQLException("ResultSet.updateNull(String colIndex) unsupported");}
public void updateBoolean(String colIndex, boolean b) throws SQLException {throw new SQLException("ResultSet.updateBoolean(String colIndex, boolean b) unsupported");}
public void updateByte(String colIndex, byte b) throws SQLException {throw new SQLException("ResultSet.updateByte(String colIndex, byte b) unsupported");}
public void updateShort(String colIndex, short b) throws SQLException {throw new SQLException("ResultSet.updateShort(String colIndex, short b) unsupported");}
public void updateInt(String colIndex, int b) throws SQLException {throw new SQLException("ResultSet.updateInt(String colIndex, int b) unsupported");}
public void updateLong(String colIndex, long b) throws SQLException {throw new SQLException("ResultSet.updateLong(String colIndex, long b) unsupported");}
public void updateFloat(String colIndex, float f) throws SQLException {throw new SQLException("ResultSet.updateFloat(String colIndex, float f) unsupported");}
public void updateDouble(String colIndex, double f) throws SQLException {throw new SQLException("ResultSet.updateDouble(String colIndex, double f) unsupported");}
public void updateBigDecimal(String colIndex, BigDecimal f) throws SQLException {throw new SQLException("ResultSet.updateBigDecimal(String colIndex, BigDecimal f) unsupported");}
public void updateString(String colIndex, String s) throws SQLException {throw new SQLException("ResultSet.updateString(String colIndex, String s) unsupported");}
public void updateBytes(String colIndex, byte[] s) throws SQLException {throw new SQLException("ResultSet.updateBytes(String colIndex, byte[] s) unsupported");}
public void updateDate(String colIndex, java.sql.Date d) throws SQLException {throw new SQLException("ResultSet.updateDate(String colIndex, java.sql.Date d) unsupported");}
public void updateTime(String colIndex, java.sql.Time t) throws SQLException {throw new SQLException("ResultSet.updateTime(String colIndex, java.sql.Time t) unsupported");}
public void updateTimestamp(String colIndex, java.sql.Timestamp t) throws SQLException {throw new SQLException("ResultSet.updateTimestamp(String colIndex, java.sql.Timestamp t) unsupported");}
public void updateAsciiStream(String colIndex, java.io.InputStream in, int s) throws SQLException {throw new SQLException("ResultSet.updateAsciiStream(String colIndex, java.io.InputStream in, int s) unsupported");}
public void updateBinaryStream(String colIndex, java.io.InputStream in, int s) throws SQLException {throw new SQLException("ResultSet.updateBinaryStream(String colIndex, java.io.InputStream in, int s) unsupported");}
public void updateCharacterStream(String colIndex, java.io.Reader in, int s) throws SQLException {throw new SQLException("ResultSet.updateCharacterStream(String colIndex, java.io.Reader in, int s) unsupported");}
public void updateObject(String colIndex, Object obj) throws SQLException {throw new SQLException("ResultSet.updateObject(String colIndex, Object obj) unsupported");}
public void updateObject(String colIndex, Object obj, int s) throws SQLException {throw new SQLException("ResultSet.updateObject(String colIndex, Object obj, int s) unsupported");}
public Statement getStatement() throws SQLException {throw new SQLException("ResultSet.getStatement() unsupported");}
public void close() throws SQLException {throw new SQLException("ResultSet.close() unsupported");}
public void checkClosed() throws SQLException {throw new SQLException("ResultSet.checkClosed() unsupported");}

public int getHoldability() throws SQLException {
    return 0;
}

public Reader getNCharacterStream(int columnIndex) throws SQLException {
    return null;
}

public Reader getNCharacterStream(String columnLabel) throws SQLException {
    return null;
}

public NClob getNClob(int columnIndex) throws SQLException {
    return null;
}

public NClob getNClob(String columnLabel) throws SQLException {
    return null;
}

public String getNString(int columnIndex) throws SQLException {
    return null;
}

public String getNString(String columnLabel) throws SQLException {
    return null;
}



public RowId getRowId(int columnIndex) throws SQLException {
    return null;
}

public RowId getRowId(String columnLabel) throws SQLException {
    return null;
}

public SQLXML getSQLXML(int columnIndex) throws SQLException {
    return null;
}

public SQLXML getSQLXML(String columnLabel) throws SQLException {
    return null;
}

public boolean isClosed() throws SQLException {
    return false;
}

public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
    
}

public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
    
}

public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
    
}

public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
    
}

public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
    
}

public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
    
}

public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
    
}

public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
    
    
}

public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
    
    
}

public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
    
    
}

public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
    
    
}

public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
    
    
}

public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
    
    
}

public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
    
    
}

public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
    
    
}

public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
    
    
}

public void updateClob(int columnIndex, Reader reader) throws SQLException {
    
    
}

public void updateClob(String columnLabel, Reader reader) throws SQLException {
    
    
}

public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
    
    
}

public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
    
    
}

public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
    
    
}

public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
    
    
}

public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
    
    
}

public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
    
    
}

public void updateNClob(int columnIndex, NClob clob) throws SQLException {
    
    
}

public void updateNClob(String columnLabel, NClob clob) throws SQLException {
    
    
}

public void updateNClob(int columnIndex, Reader reader) throws SQLException {
    
    
}

public void updateNClob(String columnLabel, Reader reader) throws SQLException {
    
    
}

public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
    
    
}

public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
    
    
}

public void updateNString(int columnIndex, String string) throws SQLException {
    
    
}

public void updateNString(String columnLabel, String string) throws SQLException {
    
    
}

public void updateRowId(int columnIndex, RowId x) throws SQLException {
    
    
}

public void updateRowId(String columnLabel, RowId x) throws SQLException {
    
    
}

public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
    
    
}

public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
    
    
}

public boolean isWrapperFor(Class<?> iface) throws SQLException {
    
    return false;
}

public <T> T unwrap(Class<T> iface) throws SQLException {
    
    return null;
}	
}
