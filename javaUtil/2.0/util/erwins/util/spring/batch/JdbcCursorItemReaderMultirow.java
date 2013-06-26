package erwins.util.spring.batch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.database.ExtendedConnectionDataSourceProxy;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.jdbc.SQLWarningException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.jdbc.support.SQLStateSQLExceptionTranslator;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * DB의 멀티로우을 하나의 객체(T)로 받는 스트리밍 리더
 * 3.1 기준으로 JdbcCursorItemReader를 복사/붙여넣기 함
 * RowMapper에서 null을 리턴하면 그 라인은 데이터가 완전히 채워지지 않은것으로 간주하고 다음 로우를 읽는다.
 * 스프링에서 왜 멀티라인 커서를 지원하지 않는지 모르겠다. 있는데 못찾는것 일수도 있다.
 */
public class JdbcCursorItemReaderMultirow<T> extends AbstractItemCountingItemStreamItemReaderNotFinal<T> implements InitializingBean{
    
    private static Log log = LogFactory.getLog(JdbcCursorItemReader.class);

    public static final int VALUE_NOT_SET = -1;

    private Connection con;

    private PreparedStatement preparedStatement;

    private PreparedStatementSetter preparedStatementSetter;

    protected ResultSet rs;

    private DataSource dataSource;

    private String sql;

    private int fetchSize = VALUE_NOT_SET;

    private int maxRows = VALUE_NOT_SET;

    private int queryTimeout = VALUE_NOT_SET;

    private boolean ignoreWarnings = true;

    private boolean verifyCursorPosition = true;

    private SQLExceptionTranslator exceptionTranslator;

    private RowMapper<T> rowMapper;

    private boolean initialized = false;

    private boolean driverSupportsAbsolute = false;

    private boolean useSharedExtendedConnection = false;

    
    public JdbcCursorItemReaderMultirow() {
        setName(ClassUtils.getShortName(JdbcCursorItemReaderMultirow.class));
    }

    /**
     * Assert that mandatory properties are set.
     * 
     * @throws IllegalArgumentException if either data source or sql properties
     * not set.
     */
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(dataSource, "DataSource must be provided");
        //Assert.notNull(sql, "The SQL query must be provided"); //SQL검사는 제외한다. (로직에따라 변경 가능하게 수정)
        Assert.notNull(rowMapper, "RowMapper must be provided");
    }

    /**
     * Public setter for the data source for injection purposes.
     * 
     * @param dataSource
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Executes the provided SQL query. 
     */
    private void executeQuery() {

        Assert.state(dataSource != null, "DataSource must not be null.");

        try {
            if (useSharedExtendedConnection) {
                if (!(dataSource instanceof ExtendedConnectionDataSourceProxy)) {
                    throw new InvalidDataAccessApiUsageException(
                            "You must use a ExtendedConnectionDataSourceProxy for the dataSource when " +
                            "useSharedExtendedConnection is set to true.");
                }
                this.con = DataSourceUtils.getConnection(dataSource);
                ((ExtendedConnectionDataSourceProxy)dataSource).startCloseSuppression(this.con);
                preparedStatement = this.con.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY,
                        ResultSet.HOLD_CURSORS_OVER_COMMIT);
            }
            else {
                this.con = dataSource.getConnection();
                preparedStatement = this.con.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            }
            applyStatementSettings(preparedStatement);
            if (this.preparedStatementSetter != null) {
                preparedStatementSetter.setValues(preparedStatement);
            }
            this.rs = preparedStatement.executeQuery();
            handleWarnings(preparedStatement);
        }
        catch (SQLException se) {
            close();
            throw getExceptionTranslator().translate("Executing query", sql, se);
        }

    }

    /**
     * Prepare the given JDBC Statement (or PreparedStatement or
     * CallableStatement), applying statement settings such as fetch size, max
     * rows, and query timeout. @param stmt the JDBC Statement to prepare
     * @throws SQLException
     * 
     * @see #setFetchSize
     * @see #setMaxRows
     * @see #setQueryTimeout
     */
    private void applyStatementSettings(PreparedStatement stmt) throws SQLException {
        if (fetchSize != VALUE_NOT_SET) {
            stmt.setFetchSize(fetchSize);
            stmt.setFetchDirection(ResultSet.FETCH_FORWARD);
        }
        if (maxRows != VALUE_NOT_SET) {
            stmt.setMaxRows(maxRows);
        }
        if (queryTimeout != VALUE_NOT_SET) {
            stmt.setQueryTimeout(queryTimeout);
        }
    }

    /**
     * Return the exception translator for this instance.
     * 
     * Creates a default SQLErrorCodeSQLExceptionTranslator for the specified
     * DataSource if none is set.
     */
    protected SQLExceptionTranslator getExceptionTranslator() {
        if (exceptionTranslator == null) {
            if (dataSource != null) {
                exceptionTranslator = new SQLErrorCodeSQLExceptionTranslator(dataSource);
            }
            else {
                exceptionTranslator = new SQLStateSQLExceptionTranslator();
            }
        }
        return exceptionTranslator;
    }

    /**
     * Throw a SQLWarningException if we're not ignoring warnings, else log the
     * warnings (at debug level).
     * 
     * @param warnings the warnings object from the current statement. May be
     * <code>null</code>, in which case this method does nothing.
     * @throws SQLException
     * 
     * @see org.springframework.jdbc.SQLWarningException
     */
    private void handleWarnings(PreparedStatement pstmt) throws SQLWarningException, SQLException {
        if (ignoreWarnings) {
            if (log.isDebugEnabled()) {
                SQLWarning warningToLog = pstmt.getWarnings();
                while (warningToLog != null) {
                    log.debug("SQLWarning ignored: SQL state '" + warningToLog.getSQLState() + "', error code '"
                            + warningToLog.getErrorCode() + "', message [" + warningToLog.getMessage() + "]");
                    warningToLog = warningToLog.getNextWarning();
                }
            }
        }
        else {
            SQLWarning warnings = pstmt.getWarnings();
            if (warnings != null) {
                throw new SQLWarningException("Warning not ignored", warnings);
            }
        }
    }

    /**
     * Moves the cursor in the ResultSet to the position specified by the row
     * parameter by traversing the ResultSet.
     * @param row
     */
    private void moveCursorToRow(int row) {
        try {
            int count = 0;
            while (row != count && rs.next()) {
                count++;
            }
        }
        catch (SQLException se) {
            throw getExceptionTranslator().translate("Attempted to move ResultSet to last committed row", sql, se);
        }
    }

    /**
     * Gives the JDBC driver a hint as to the number of rows that should be
     * fetched from the database when more rows are needed for this
     * <code>ResultSet</code> object. If the fetch size specified is zero, the
     * JDBC driver ignores the value.
     * 
     * @param fetchSize the number of rows to fetch
     * @see ResultSet#setFetchSize(int)
     */
    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    /**
     * Sets the limit for the maximum number of rows that any
     * <code>ResultSet</code> object can contain to the given number.
     * 
     * @param maxRows the new max rows limit; zero means there is no limit
     * @see Statement#setMaxRows(int)
     */
    public void setMaxRows(int maxRows) {
        this.maxRows = maxRows;
    }

    /**
     * Sets the number of seconds the driver will wait for a
     * <code>Statement</code> object to execute to the given number of seconds.
     * If the limit is exceeded, an <code>SQLException</code> is thrown.
     * 
     * @param queryTimeout seconds the new query timeout limit in seconds; zero
     * means there is no limit
     * @see Statement#setQueryTimeout(int)
     */
    public void setQueryTimeout(int queryTimeout) {
        this.queryTimeout = queryTimeout;
    }

    /**
     * Set whether SQLWarnings should be ignored (only logged) or exception
     * should be thrown.
     * 
     * @param ignoreWarnings if TRUE, warnings are ignored
     */
    public void setIgnoreWarnings(boolean ignoreWarnings) {
        this.ignoreWarnings = ignoreWarnings;
    }

    /**
     * Allow verification of cursor position after current row is processed by
     * RowMapper or RowCallbackHandler. Default value is TRUE.
     * 
     * @param verifyCursorPosition if true, cursor position is verified
     */
    public void setVerifyCursorPosition(boolean verifyCursorPosition) {
        this.verifyCursorPosition = verifyCursorPosition;
    }

    /**
     * Set the RowMapper to be used for all calls to read().
     * 
     * @param rowMapper
     */
    public void setRowMapper(RowMapper<T> rowMapper) {
        this.rowMapper = rowMapper;
    }

    /**
     * Set the SQL statement to be used when creating the cursor. This statement
     * should be a complete and valid SQL statement, as it will be run directly
     * without any modification.
     * 
     * @param sql
     */
    public void setSql(String sql) {
        this.sql = sql;
    }

    /**
     * Set the PreparedStatementSetter to use if any parameter values that need
     * to be set in the supplied query.
     * 
     * @param preparedStatementSetter
     */
    public void setPreparedStatementSetter(PreparedStatementSetter preparedStatementSetter) {
        this.preparedStatementSetter = preparedStatementSetter;
    }

    /**
     * Indicate whether the JDBC driver supports setting the absolute row on a
     * {@link ResultSet}. It is recommended that this is set to
     * <code>true</code> for JDBC drivers that supports ResultSet.absolute() as
     * it may improve performance, especially if a step fails while working with
     * a large data set.
     * 
     * @see ResultSet#absolute(int)
     * 
     * @param driverSupportsAbsolute <code>false</code> by default
     */
    public void setDriverSupportsAbsolute(boolean driverSupportsAbsolute) {
        this.driverSupportsAbsolute = driverSupportsAbsolute;
    }

    /**
     * Indicate whether the connection used for the cursor should be used by all other processing
     * thus sharing the same transaction. If this is set to false, which is the default, then the 
     * cursor will be opened using in its connection and will not participate in any transactions
     * started for the rest of the step processing. If you set this flag to true then you must 
     * wrap the DataSource in a {@link ExtendedConnectionDataSourceProxy} to prevent the
     * connection from being closed and released after each commit.
     * 
     * When you set this option to <code>true</code> then the statement used to open the cursor 
     * will be created with both 'READ_ONLY' and 'HOLD_CUSORS_OVER_COMMIT' options. This allows 
     * holding the cursor open over transaction start and commits performed in the step processing. 
     * To use this feature you need a database that supports this and a JDBC driver supporting 
     * JDBC 3.0 or later.
     *
     * @param useSharedExtendedConnection <code>false</code> by default
     */
    public void setUseSharedExtendedConnection(boolean useSharedExtendedConnection) {
        this.useSharedExtendedConnection = useSharedExtendedConnection;
    }

    /**
     * Check the result set is in synch with the currentRow attribute. This is
     * important to ensure that the user hasn't modified the current row.
     */
    private void verifyCursorPosition(long expectedCurrentRow) throws SQLException {
        if (verifyCursorPosition) {
            if (expectedCurrentRow != this.rs.getRow()) {
                throw new InvalidDataAccessResourceUsageException("Unexpected cursor position change.");
            }
        }
    }

    /**
     * Close the cursor and database connection.
     */
    protected void doClose() throws Exception {
        initialized = false;
        JdbcUtils.closeResultSet(this.rs);
        rs = null;
        JdbcUtils.closeStatement(this.preparedStatement);
        if (useSharedExtendedConnection && dataSource instanceof ExtendedConnectionDataSourceProxy) {
            ((ExtendedConnectionDataSourceProxy)dataSource).stopCloseSuppression(this.con);
            if (!TransactionSynchronizationManager.isActualTransactionActive()) {
                DataSourceUtils.releaseConnection(con, dataSource);
            }
        }
        else {
            JdbcUtils.closeConnection(this.con);
        }
    }

    /**
     * Execute the {@link #setSql(String)} query.
     */
    protected void doOpen() throws Exception {
        Assert.state(!initialized, "Stream is already initialized.  Close before re-opening.");
        Assert.isNull(rs, "ResultSet still open!  Close before re-opening.");
        executeQuery();
        initialized = true;

    }

    /**
     * Read next row and map it to item, verify cursor position if
     * {@link #setVerifyCursorPosition(boolean)} is true.
     * item이 널이면 재귀호출
     */
    @SuppressWarnings("cast")
    protected T doRead() throws Exception {
        try {
            if (!rs.next()) {
                return null;
            }
            int currentRow = getCurrentItemCount();
            T item = (T) rowMapper.mapRow(rs, currentRow);
            verifyCursorPosition(currentRow);
            if(item==null){
                currentItemCount++;
                return doRead();
            }
            return item;
        }
        catch (SQLException se) {
            throw getExceptionTranslator().translate("Attempt to process next row failed", sql, se);
        }
    }

    /**
     * Use {@link ResultSet#absolute(int)} if possible, otherwise scroll by
     * calling {@link ResultSet#next()}.
     */
    protected void jumpToItem(int itemIndex) throws Exception {
        if (driverSupportsAbsolute) {
            try {
                rs.absolute(itemIndex);
            }
            catch (SQLException e) {
                // Driver does not support rs.absolute(int) revert to
                // traversing ResultSet
                log.warn("The JDBC driver does not appear to support ResultSet.absolute(). Consider"
                        + " reverting to the default behavior setting the driverSupportsAbsolute to false", e);

                moveCursorToRow(itemIndex);
            }
        }
        else {
            moveCursorToRow(itemIndex);
        }
    }
    
}
