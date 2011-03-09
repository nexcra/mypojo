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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.sql.Statement;

import erwins.database.Database;
import erwins.database.jdbc.adapters.ConnectionAdapter;
import erwins.database.text.ParseFailure;

/** A limited version of the Connection class. All methods
 *  undocumented base-class overrides throw a
 *  {@link SQLException} if called.
 *  <p>
 *  Note that you can't
 *  mix non-autocommit behavior with explicit
 *  SQL begin/commit statements. For example, if you
 *  turn off autocommit mode (which causes a SQL begin
 *  to be issued), and then execute a SQL begin manually,
 *  a call to `commit` will commit the inner transaction,
 *  but not the outer one. In effect, you can't do
 *  nested transactions using the JDBC {@link #commit} or
 *  {@link #rollback}  methods.
 *
 * @include /etc/license.txt
 */

public class JDBCConnection extends ConnectionAdapter
{
	private Database database;

	// Establish a connection to the indicated database.
	//
	public JDBCConnection(String uri) throws SQLException,
											 URISyntaxException,
											 IOException
	{	this( new URI(uri) );
	}

	public JDBCConnection(URI uri) throws	SQLException,
											IOException
	{	database = new Database( uri );
	}

	/** Close a database connection. A commit is issued
	 *  automatically if auto-commit mode is disabled.
	 *  @see #setAutoCommit
	 */
	@Override
    public void close() throws SQLException
	{	try
		{	
			autoCommitState.close();

			database.dump();
			database=null;	// make the memory reclaimable and
							// also force a nullPointerException
							// if anybody tries to use the
							// connection after it's closed.
		}
		catch(IOException e)
		{	throw new SQLException( e.getMessage() );
		}
	}
	@Override
	public Statement createStatement() throws SQLException
	{	return new JDBCStatement(database);
	}

	/** Terminate the current transactions and start a new
	 *  one. Does nothing if auto-commit mode is on.
	 *  @see #setAutoCommit
	 */
	@Override
    public void commit() throws SQLException
	{	autoCommitState.commit();
	}

	/** Roll back the current transactions and start a new
	 *  one. Does nothing if auto-commit mode is on.
	 *  @see #setAutoCommit
	 */
	@Override
	public void rollback() throws SQLException
	{	autoCommitState.rollback();
	}

	/** 
	 * Once set true, all SQL statements form a stand-alone
	 * transaction. A begin is issued automatically when
	 * auto-commit mode is disabled so that the {@link #commit}
	 * and {@link #rollback} methods will work correctly.
	 * Similarly, a commit is issued automatically when
	 * auto-commit mode is enabled.
	 * <p>
	 * Auto-commit mode is on by default.
	 */
	@Override
	public void setAutoCommit( boolean enable ) throws SQLException
	{	autoCommitState.setAutoCommit(enable);
	}

	@Override
	/** Return true if auto-commit mode is enabled */
	public boolean getAutoCommit() throws SQLException
	{	return autoCommitState == enabled;
	}

	//----------------------------------------------------------------------
	private interface AutoCommitBehavior
	{	void close() throws SQLException;
		void commit() throws SQLException;
		void rollback() throws SQLException;
		void setAutoCommit( boolean enable ) throws SQLException;
	}

	private AutoCommitBehavior enabled =
		new AutoCommitBehavior()
		{	public void close() throws SQLException {/* nothing to do */}
			public void commit() 					{/* nothing to do */}
			public void rollback() 				 	{/* nothing to do */}
			public void setAutoCommit( boolean enable )
			{	if( enable == false )
				{	database.begin();
					autoCommitState = disabled;
				}
			}
		};

	private AutoCommitBehavior disabled = 
		new AutoCommitBehavior()
		{	public void close() throws SQLException
			{	try
				{	database.commit();
				}
				catch( ParseFailure e )
				{	throw new SQLException( e.getMessage() );
				}
			}
			public void commit() throws SQLException
			{	try
				{	database.commit();
					database.begin();
				}
				catch( ParseFailure e )
				{	throw new SQLException( e.getMessage() );
				}
			}
			public void rollback() throws SQLException
			{	try
				{	database.rollback();
					database.begin();
				}
				catch( ParseFailure e )
				{	throw new SQLException( e.getMessage() );
				}
			}
			public void setAutoCommit( boolean enable ) throws SQLException
			{	try
				{	if( enable == true )
					{	database.commit();
						autoCommitState = enabled;
					}
				}
				catch( ParseFailure e )
				{	throw new SQLException( e.getMessage() );
				}
			}
		};

	private AutoCommitBehavior autoCommitState = enabled;
}
