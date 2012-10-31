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

package erwins.jsample.database.jdbc;

import java.sql.*;
import java.text.*;

import erwins.jsample.database.*;
import erwins.jsample.database.jdbc.adapters.*;

/**
 * A limited version of the result-set class. All methods not shown throw a
 * {@link SQLException} if called. Note the the underlying table actually holds
 * nothing but strings, so the numeric accessors (e.g. {@link #getDouble}) are
 * doing string-to-number and number-to-string conversions. These conversions
 * might fail if the underlying String doesn't represent a number.
 * 
 * @include /etc/license.txt
 */

@SuppressWarnings("unused")
public class JDBCResultSet extends ResultSetAdapter {
    private final Cursor cursor;
    private static final NumberFormat format = NumberFormat.getInstance();

    /**
     * Wrap a result set around a Cursor. The cursor should never have been
     * advanced; just pass this constructor the return value from
     * {@link Table#rows}.
     */
    
    public JDBCResultSet(Cursor cursor) throws SQLException {
        this.cursor = cursor;
    }

    @Override
    public boolean next() {
        return cursor.advance();
    }

    @Override
    public String getString(String columnName) throws SQLException {
        try {
            Object contents = cursor.column(columnName);
            return (contents == null) ? null : contents.toString();
        }
        catch (IndexOutOfBoundsException e) {
            throw new SQLException("column " + columnName + " doesn't exist");
        }
    }

    @Override
    public double getDouble(String columnName) throws SQLException {
        try {
            String contents = getString(columnName);
            return (contents == null) ? 0.0 : format.parse(contents).doubleValue();
        }
        catch (ParseException e) {
            throw new SQLException("field doesn't contain a number");
        }
    }

    @Override
    public int getInt(String columnName) throws SQLException {
        try {
            String contents = getString(columnName);
            return (contents == null) ? 0 : format.parse(contents).intValue();
        }
        catch (ParseException e) {
            throw new SQLException("field doesn't contain a number");
        }
    }

    @Override
    public long getLong(String columnName) throws SQLException {
        try {
            String contents = getString(columnName);
            return (contents == null) ? 0L : format.parse(contents).longValue();
        }
        catch (ParseException e) {
            throw new SQLException("field doesn't contain a number");
        }
    }

    @Override
    public void updateNull(String columnName) {
        cursor.update(columnName, null);
    }

    @Override
    public void updateDouble(String columnName, double value) {
        cursor.update(columnName, format.format(value));
    }

    public void updateInt(String columnName, long value) {
        cursor.update(columnName, format.format(value));
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return new JDBCResultSetMetaData(cursor);
    }
}
