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
import java.util.Properties;

/**
 * A JDBC driver for a small in-memory database that wraps the
 * {@link erwins.jsample.database.Database} class. See that class for a discussion of
 * the supported SQL.
 * @include /etc/license.txt
 * @see erwins.jsample.database.Database
 */

@SuppressWarnings("unused")
public class JDBCDriver implements java.sql.Driver {

    private JDBCConnection connection;
    static //{=JDBCDriver.staticInitializer}
    {
        try {
            java.sql.DriverManager.registerDriver(new JDBCDriver());
        }
        catch (SQLException e) {
            System.err.println(e);
        }
    }

    public boolean acceptsURL(String url) throws SQLException {
        return url.startsWith("file:/");
    }

    public Connection connect(String uri, Properties info) throws SQLException {
        try {
            return connection = new JDBCConnection(uri);
        }
        catch (Exception e) {
            throw new SQLException(e.getMessage());
        }
    }

    public int getMajorVersion() {
        return 1;
    }

    public int getMinorVersion() {
        return 0;
    }

    public boolean jdbcCompliant() {
        return false;
    }

    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return new DriverPropertyInfo[0];
    }
}
