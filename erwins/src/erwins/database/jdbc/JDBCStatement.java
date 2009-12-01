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

package erwins.database.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import erwins.database.Database;
import erwins.database.Table;
import erwins.database.jdbc.adapters.StatementAdapter;

/***
 * @include /etc/license.txt
 */

public class JDBCStatement extends StatementAdapter {
    private Database database;

    public JDBCStatement(Database database) {
        this.database = database;
    }

    @Override
    public int executeUpdate(String sqlString) throws SQLException {
        try {
            database.execute(sqlString);
            return database.affectedRows();
        }
        catch (Exception e) {
            throw new SQLException(e.getMessage());
        }
    }
    @Override
    public ResultSet executeQuery(String sqlQuery) throws SQLException {
        try {
            Table result = database.execute(sqlQuery);
            return new JDBCResultSet(result.rows());
        }
        catch (Exception e) {
            throw new SQLException(e.getMessage());
        }
    }
    @Override
    public void close() throws SQLException { // does nothing.
    }
}
