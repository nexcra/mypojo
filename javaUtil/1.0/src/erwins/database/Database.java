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

package erwins.database;

import java.io.*;
import java.net.URI;
import java.text.NumberFormat;
import java.util.*;
import java.util.Map.Entry;

import erwins.database.text.*;
import erwins.database.text.Scanner;
import erwins.util.exception.Throw;

/***
 * This class implements a small SQL-subset database. that provides a front end
 * to the Table classes. Find the grammar for the supported language below. The
 * remainder of the documentation of this class assumes that you know a little
 * SQL (see the <a href="http://www.holub.com/software/HolubSQL" >HolubSQL web
 * page</a> for a few SQL references).
 * <p>
 * My intent is to do simple things, only. None of the niceties of SQL (like
 * aliases, outer and inner joins, permissions, views, etc.) are supported. The
 * file src/com/holub/database/Database.test.sql in the original distribution
 * .jar file demonstrates the SQL subset that's supported.
 * <p>
 * A database is effectively a directory, and a table is effectively a file in
 * the directory. The argument to USE DATABASE specifies the full path to that
 * directory. A table name is a file name with the ".csv" extension added. Note
 * that a simple name (as in "USE DATABASE foo" will create a subdirectory
 * called "foo" in the current directory. Use a full path name to get something
 * else: "USE DATABASE c:/tmp/foo" See {@link CSVExporter} for a description of
 * the file format.
 * <p>
 * Because database names are path names, identifier names in general can
 * contain characters that would normally go in a path (/ \ : ~ _) but they
 * cannot contain a dot or dash (so your database name can't have a dot or dash
 * in it either). Identifiers can't contain spaces, and they cannot start with
 * digits.
 * <p>
 * SELECT statements support FROM and WHERE clauses, but nothing else.
 * (DISTINCT, ORDEREDBY, etc., aren't supported; neither are subqueries.) You
 * can join an arbitrary number of tables in a SELECT, but outer and inner joins
 * (ans subqueries) aren't supported. A few operators (BETWEEN, IN) aren't
 * supported---check the grammar, below. Any Java/Perl regular expression can be
 * used as an argument to LIKE, and for SQL compatibility, a % wild card is
 * automatically mapped to ".*". Selecting "into" another table works, but bear
 * in mind that the actual data is shared between tables. Since everything in
 * the table is a String, this strategy works fine <em>unless</em> you use the
 * {@link Table} object that's returned from {@link #execute} to modify the
 * table directly. Don't do that.
 * <p>
 * Though the following types are recognized by the parser (so you can use them
 * in the SQL), but they are ignored. Everything's stored in the underlying
 * database as a String. Strings that represent numbers (can be parsed
 * successfully by {@link java.text.NumberFormat}) can be used in arithmetic
 * expressions, however.
 * <table border=1 cellpadding=3 cellspacing=0>
 * <tr>
 * <td>integer(maxDigits)<br>
 * int(maxDigits)<br>
 * smallint(maxDigits)<br>
 * bigint(maxDigits)<br>
 * tinyint(size)</td>
 * <td>integers</td>
 * </tr>
 * <tr>
 * <td>decimal(l,r)<br>
 * real(l,r)<br>
 * double(l,r)<br>
 * numeric(l,r)</td>
 * <td>floating point, l and r specify the maximum number of digits to the left
 * and right of the decimal.</td>
 * </tr>
 * <tr>
 * <td>char(length)</td>
 * <td>Fixed length string.</td>
 * </tr>
 * <tr>
 * <td>varchar(maximum_length)</td>
 * <td>Variable length string.</td>
 * </tr>
 * <tr>
 * <td>date(format)</td>
 * <td>Date in the Gregorian calendar with optional format.</td>
 * </tr>
 * </table>
 * You may specify a "PRIMARY KEY(identifier)" in the list of columns, but it's
 * ignored, too.
 * <p>
 * Numbers in the input must begin with a digit (.10 doesn't work. 0.10 does),
 * and decimal fractions less than 1.0E-20 are assumed to be 0. (That is
 * 1.000000000000000000001 is rounded down to 1.0, and will be put into the
 * table as the integer 1.
 * <p>
 * You can't store a Boolean value as such, but if you decide on some string
 * like "true" and "false" as meaningful, and use it consistently, then
 * comparisons and assignments of boolean values will work fine. Null is
 * supported.
 * <p>
 * Simple transactions (in the sense of a group of SQL statements that execute
 * atomically, which can be rolled back) are supported. Initially, no
 * transaction is active, and all SQL requests are effectively committed
 * immediately on execution. This auto-commit mode is superceded once you issue
 * a BEGIN, but is reinstated as soon as the matching COMMIT or ROLLBACK is
 * encountered. All requests that occur between the BEGIN and COMMIT are treated
 * as a single unit. If you close (or DUMP) the database without a formal COMMIT
 * or ROLLBACK, then any open transactions are effectively committed. The
 * {@link #begin}, {@link #commit}, and {@link #rollback} methods have the same
 * effect as issuing the equivalent SQL requests.
 * <p>
 * Transactions affect only modifications of tables. Tables that are created or
 * dumped during a transaction are not destroyed (or put back in their original
 * state on the disk) if that transaction is rolled back.
 * <p>
 * An exception-toss that occurs when processing a SQL expression submitted to
 * {@link #execute} causes an automatic rollback before the exception is tossed
 * out to your code. This automatic-rollback behavior <u>is not implemented</u>
 * by the methods that mimic SQL statements ({@link #useDatabase
 * useDatabase(...)}, {@link #createDatabase createDatabase(...)},
 * {@link #createTable createTable(...)}, {@link #dropTable dropTable(...)}, and
 * {@link #dump dump(...)}). If you use these methods, you'll have to catch any
 * exceptions manually and call {@link #rollback} or {@link #commit} explicitly.
 * <p>
 * The modified database is not stored to disk until a DUMP is issued. (In the
 * JDBC wrapper, an automatic DUMP occurs when you close the Connection).
 * <p>
 * This class wraps various {@link Table} derivatives, but this class also
 * relies on the fact that the table is made up entirely of {@link String}
 * objects. You can use this class to access {@link Table} objects that were
 * created directly by yourself, but problems can arise if those manually
 * created tables have anything other than Strings in them. In particular,
 * {@link Object#toString} method is used to get the value of a cell, and if the
 * value is modified through an UPDATE, the new value is stored as a String,
 * without regard to the original field type.)
 * <p>
 * Here's the grammar I've implemented ("expr"=expression, "id"=identifier,
 * "opt"=optional, "e"=epsilon. "[...]" is an optional subproduction.
 * 
 * <PRE>
 * statement ::= INSERT INTO IDENTIFIER [LP
 * idList RP] VALUES LP exprList RP | CREATE DATABASE IDENTIFIER | CREATE TABLE
 * IDENTIFIER LP declarations RP | DROP TABLE IDENTIFIER | BEGIN
 * [WORK|TRAN[SACTION]] | COMMIT [WORK|TRAN[SACTION]] | ROLLBACK
 * [WORK|TRAN[SACTION]] | DUMP | USE DATABASE IDENTIFIER | UPDATE IDENTIFIER SET
 * IDENTIFIER EQUAL expr WHERE expr | DELETE FROM IDENTIFIER WHERE expr | SELECT
 * [INTO identifier] idList FROM idList [WHERE expr]
 * 
 * idList ::= IDENTIFIER idList' | STAR idList' ::= COMMA IDENTIFIER idList' | e
 * 
 * declarations ::= IDENTIFIER [type] [NOT [NULL]] declaration' declarations'
 * ::= COMMA IDENTIFIER [type] declarations' | COMMA PRIMARY KEY LP IDENTIFIER
 * RP | e
 * 
 * type ::= INTEGER [ LP expr RP ] | CHAR [ LP expr RP ] | NUMERIC [ LP expr
 * COMMA expr RP ] | DATE // format spec is part of token
 * 
 * exprList ::= expr exprList' exprList' ::= COMMA expr exprList' | e
 * 
 * expr ::= andExpr expr' expr' ::= OR andExpr expr' | e
 * 
 * andExpr ::= relationalExpr andExpr' andExpr' ::= AND relationalExpr andExpr'
 * | e
 * 
 * relationalExpr ::= additiveExpr relationalExpr' relationalExpr'::= RELOP
 * additiveExpr relationalExpr' | EQUAL additiveExpr relationalExpr' | LIKE
 * additiveExpr relationalExpr' | e
 * 
 * additiveExpr ::= multiplicativeExpr additiveExpr' additiveExpr' ::= ADDITIVE
 * multiplicativeExpr additiveExpr' | e
 * 
 * multiplicativeExpr ::= term multiplicativeExpr' multiplicativeExpr' ::= STAR
 * term multiplicativeExpr' | SLASH term multiplicativeExpr' | e
 * 
 * term ::= NOT factor | LP expr RP | factor
 * 
 * factor ::= compoundId | STRING | NUMBER | NULL
 * 
 * compoundId ::= IDENTIFIER compoundId' compoundId' ::= DOT IDENTIFIER | e
 * 
 * Most runtime errors (including inappropriate use of nulls) cause a exception
 * toss.
 * </PRE>
 * 
 * Most of the methods of this class throw a {@link ParseFailure} (a checked
 * {@link Exception}) if something goes wrong.
 * <p>
 * <b>Modifications Since Publication of Holub on Patterns:</b>
 * <table * border="1" cellspacing="0" cellpadding="3">
 * <tr>
 * <td valign="top">9/24/02</td>
 * <td>Added a few methods to the Cursor interface (and local implemenation) to
 * make it possible to get column-related metadata in the
 * {@link java.sql.ResultSetMetaData} class.</td>
 * </tr>
 * </table>
 * @include /etc/license.txt
 */

public final class Database { /*
                               * The directory that represents the database.
                               */
    private File location = new File(".");

    /**
     * The number of rows modified by the last INSERT, DELETE, or UPDATE
     * request.
     */
    private int affectedRows = 0;

    /**
     * This Map holds the tables that are currently active. I have to use be a
     * Map (as compared to a Set), because HashSet uses the equals() function to
     * resolve ambiguity. This requirement would force me to define "equals" on
     * a Table as "having the same name as another table," which I believe is
     * semantically incorrect. Equals should match both name and contents. I
     * avoid the problem entirely by using an external key, even if that key is
     * also an accessible attribute of the Table.
     * <p>
     * The table is actually a specialization of Map that requires a Table value
     * argument, and interacts with the transaction-processing system.
     */

    private final Map<String, Table> tables = new TableMap(new HashMap<String, Table>());

    /**
     * The current transaction-nesting level, incremented for a BEGIN and
     * decremented for a COMMIT or ROLLBACK.
     */
    private int transactionLevel = 0;

    /** Flex뷰어에 띄우기 위해 추가한 table정보. */
    public Map<String, Object> databaseInfo() {

        Map<String, Object> databaseInfo = new HashMap<String, Object>();
        databaseInfo.put("transactionLevel", transactionLevel);

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (Entry<String, Table> entry : tables.entrySet()) {
            Table table = entry.getValue();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("tableName", table.name());
            map.put("columnNames", table.columnNames());
            map.put("size", table.size());
            list.add(map);
        }
        databaseInfo.put("tableInfo", list);
        return databaseInfo;
    }
    
    public int  tableSize() {
    	return tables.size();
    }

    /**
     * 기존 데이터(*.csv)들을 전부 로딩한다. 이미 로딩된 테이블은 적용하지 않는다.
     **/
    public void loadPersistenceFromCsv() {
        for (File each : location.listFiles()) {
            String tableName = each.getName();
            if (!tableName.endsWith(".csv")) continue;
            tableName = tableName.replaceAll("\\.csv", "");
            if (!tables.containsKey(tableName)) tables.get(tableName);
        }
    }

    /**
     * A Map proxy that hanldes lazy instatiation of tables from the disk.
     */
    private final class TableMap implements Map<String, Table> {
        private final Map<String, Table> realMap;

        public TableMap(Map<String, Table> realMap) {
            this.realMap = realMap;
        }

        /**
         * If the requested table is already in memory, return it. Otherwise
         * load it from the disk.
         */
        public Table get(Object key) {
            String tableName = (String) key;
            try {
                Table desiredTable = realMap.get(tableName);
                if (desiredTable == null) {
                    desiredTable = TableFactory.load(tableName + ".csv", location);
                    put(tableName, desiredTable);
                }
                return desiredTable;
            }
            catch (IOException e) { // Can't use verify(...) or error(...) here because the
                // base-class "get" method doesn't throw any exceptions.
                // Kludge a runtime-exception toss. Call in.failure()
                // to get an exception object that calls out the
                // input file name and line number, then transmogrify
                // the ParseFailure to a RuntimeException.

                String message = "Table not created internally and couldn't be loaded." + "(" + e.getMessage() + ")\n";
                throw new RuntimeException(in.failure(message).getMessage());
            }
        }

        public Table put(String key, Table value) { // If transactions are active, put the new
            // table into the same transaction state
            // as the other tables.

            for (int i = 0; i < transactionLevel; ++i)
                value.begin();

            return realMap.put(key, value);
        }

        public void putAll(Map<? extends String, ? extends Table> m) {
            throw new UnsupportedOperationException();
        }

        public int size() {
            return realMap.size();
        }

        public boolean isEmpty() {
            return realMap.isEmpty();
        }

        public Table remove(Object k) {
            return realMap.remove(k);
        }

        public void clear() {
            realMap.clear();
        }

        public Set<String> keySet() {
            return realMap.keySet();
        }

        public Collection<Table> values() {
            return realMap.values();
        }

        public Set<Entry<String, Table>> entrySet() {
            return realMap.entrySet();
        }

        @Override
        public boolean equals(Object o) {
            return realMap.equals(o);
        }

        @Override
        public int hashCode() {
            return realMap.hashCode();
        }

        public boolean containsKey(Object k) {
            return realMap.containsKey(k);
        }

        public boolean containsValue(Object v) {
            return realMap.containsValue(v);
        }

    }

    //@token-start
    //--------------------------------------------------------------
    // The token set used by the parser. Tokens automatically
    // The Scanner object matches the specification against the
    // input in the order of creation. For example, it's important
    // that the NUMBER token is declared before the IDENTIFIER token
    // since the regular expression associated with IDENTIFIERS
    // will also recognize some legitimate numbers.

    private static final TokenSet tokens = new TokenSet();

    private static final Token COMMA = tokens.create("',"), //{=Database.firstToken}
            EQUAL = tokens.create("'="), LP = tokens.create("'("), RP = tokens.create("')"), DOT = tokens.create("'."), STAR = tokens
                    .create("'*"), SLASH = tokens.create("'/"), AND = tokens.create("'AND"),
            BEGIN = tokens.create("'BEGIN"),
            COMMIT = tokens.create("'COMMIT"), CREATE = tokens.create("'CREATE"), DATABASE = tokens.create("'DATABASE"), DELETE = tokens
                    .create("'DELETE"), DROP = tokens.create("'DROP"), DUMP = tokens.create("'DUMP"),
            FROM = tokens.create("'FROM"),
            INSERT = tokens.create("'INSERT"), INTO = tokens.create("'INTO"), KEY = tokens.create("'KEY"),
            LIKE = tokens.create("'LIKE"),
            NOT = tokens.create("'NOT"), NULL = tokens.create("'NULL"), OR = tokens.create("'OR"),
            PRIMARY = tokens.create("'PRIMARY"),
            ROLLBACK = tokens.create("'ROLLBACK"), SELECT = tokens.create("'SELECT"), SET = tokens.create("'SET"), TABLE = tokens
                    .create("'TABLE"), UPDATE = tokens.create("'UPDATE"), USE = tokens.create("'USE"),
            VALUES = tokens.create("'VALUES"),
            WHERE = tokens.create("'WHERE"),

            WORK = tokens.create("WORK|TRAN(SACTION)?"), ADDITIVE = tokens.create("\\+|-"),
            STRING = tokens.create("(\".*?\")|('.*?')"),
            RELOP = tokens.create("[<>][=>]?"), NUMBER = tokens.create("[0-9]+(\\.[0-9]+)?"),

            INTEGER = tokens.create("(small|tiny|big)?int(eger)?"), NUMERIC = tokens.create("decimal|numeric|real|double"), CHAR = tokens
                    .create("(var)?char"), DATE = tokens.create("date(\\s*\\(.*?\\))?"),

            IDENTIFIER = tokens.create("[a-zA-Z_0-9/\\\\:~]+"); //{=Database.lastToken}

    //private String expression; // SQL expression being parsed
    private Scanner in; // The current scanner.

    // Enums to identify operators not recognized at the token level
    // These are used by various inner classes, but must be declared
    // at the outer-class level because they're static.

    private static class RelationalOperator {
        private RelationalOperator() {}
    }

    private static final RelationalOperator EQ = new RelationalOperator();
    private static final RelationalOperator LT = new RelationalOperator();
    private static final RelationalOperator GT = new RelationalOperator();
    private static final RelationalOperator LE = new RelationalOperator();
    private static final RelationalOperator GE = new RelationalOperator();
    private static final RelationalOperator NE = new RelationalOperator();

    private static class MathOperator {
        private MathOperator() {}
    }

    private static final MathOperator PLUS = new MathOperator();
    private static final MathOperator MINUS = new MathOperator();
    private static final MathOperator TIMES = new MathOperator();
    private static final MathOperator DIVIDE = new MathOperator();

    //@declarations-end
    //--------------------------------------------------------------
    /**
     * Create a database object attached to the current directory. You can
     * specify a different directory after the object is created by calling
     * {@link #useDatabase}.
     */
    public Database() {}

    /** Use the indicated directory for the database */
    public Database(URI directory) throws IOException {
        useDatabase(new File(directory));
    }

    /** Use the indicated directory for the database */
    public Database(File path) throws IOException {
        useDatabase(path);
    }

    /** WAS에서 간이DB를 생성한다. */
    public Database(File path, boolean load) {
        try {
            if (load) {
                if (!path.exists()) path.mkdir();
                useDatabase(path);
                loadPersistenceFromCsv();
            } else useDatabase(path);
        }
        catch (Exception e) {
            Throw.wrap(e);
        }
    }

    /** Use the indicated directory for the database */
    public Database(String path) throws IOException {
        useDatabase(new File(path));
    }

    /**
     * Use this constructor to wrap one or more Table objects so that you can
     * access them using SQL. You may add tables to this database using SQL
     * "CREATE TABLE" statements, and you may safely extract a snapshot of a
     * table that you create in this way using:
     * 
     * <PRE>
     * Table t = execute(&quot;SELECT * from &quot; + tableName);
     * </PRE>
     * @param database an array of tables to use as the database.
     * @param path The default directory to search for tables, and the directory
     *            to which tables are dumped. Tables specified in the
     *            <code>database</code> argument are used in place of any table
     *            on the disk that has the same name.
     */
    public Database(File path, Table[] database) throws IOException {
        useDatabase(path);
        for (int i = 0; i < database.length; ++i)
            tables.put(database[i].name(), database[i]);
    }

    //--------------------------------------------------------------
    // Private parse-related workhorse functions.

    /**
     * Asks the scanner to throw a {@link ParseFailure} object that highlights
     * the current input position.
     */
    private void error(String message) throws ParseFailure {
        throw in.failure(message.toString());
    }

    /**
     * Like {@link #error}, but throws the exception only if the test fails.
     */
    private void verify(boolean test, String message) throws ParseFailure {
        if (!test) throw in.failure(message);
    }

    //--------------------------------------------------------------
    // Public methods that duplicate some SQL statements.
    // The SQL interpreter calls these methods to
    // do the actual work.

    /**
     * Use an existing "database." In the current implementation, a "database"
     * is a directory and tables are files within the directory. An active
     * database (opened by a constructor, a USE DATABASE directive, or a prior
     * call to the current method) is closed and committed before the new
     * database is opened.
     * @param path A {@link File} object that specifies directory that
     *            represents the database.
     * @throws IOException if the directory that represents the database can't
     *             be found.
     */
    public void useDatabase(File path) throws IOException {
        dump();
        tables.clear(); // close old database if there is one
        this.location = path;
    }

    /**
     * Create a database by opening the indicated directory. All tables must be
     * files in that directory. If you don't call this method (or issue a SQL
     * CREATE DATABASE directive), then the current directory is used.
     * @throws IOException if the named directory can't be opened.
     */
    public void createDatabase(String name) throws IOException {
        File location = new File(name);
        location.mkdir();
        this.location = location;
    }

    /**
     * Create a new table. If a table by this name exists, it's overwritten.
     */
    public void createTable(String name, List<String> columns) {
        String[] columnNames = new String[columns.size()];
        int i = 0;
        for (Iterator<String> names = columns.iterator(); names.hasNext();)
            columnNames[i++] = names.next();

        Table newTable = TableFactory.create(name, columnNames);
        tables.put(name, newTable);
    }

    /**
     * Destroy both internal and external (on the disk) versions of the
     * specified table. .cvs를 붙여야 할듯 하다.
     */
    public void dropTable(String name) {
        tables.remove(name); // ignore the error if there is one.

        File tableFile = new File(location, name + ".csv");
        if (!tableFile.exists()) throw new RuntimeException(tableFile.getName() + "is not exist");
        tableFile.delete();
    }

    /**
     * Flush to the persistent store (e.g. disk) all tables that are "dirty"
     * (which have been modified since the database was last committed). These
     * tables will not be flushed again unless they are modified after the
     * current dump() call. Nothing happens if no tables are dirty.
     * <p>
     * The present implemenation flushes to a .csv file whose name is the table
     * name with a ".csv" extension added.
     */
    public void dump() throws IOException {
        Collection<Table> values = tables.values();
        if (values != null) {
            for (Iterator<Table> i = values.iterator(); i.hasNext();) {
                Table current = i.next();
                if (current.isDirty()) {
                    Writer out = new FileWriter(new File(location, current.name() + ".csv"));
                    current.export(new CSVExporter(out));
                    out.close();
                }
            }
        }
    }

    /**
     * Return the number of rows that were affected by the most recent
     * {@link #execute} call. Zero is returned for all operations except for
     * INSERT, DELETE, or UPDATE.
     */
    public int affectedRows() {
        return affectedRows;
    }

    //@transactions-start
    //----------------------------------------------------------------------
    // Transaction processing.

    /**
     * Begin a transaction
     */
    public void begin() {
        ++transactionLevel;

        Collection<Table> currentTables = tables.values();
        for (Iterator<Table> i = currentTables.iterator(); i.hasNext();)
            i.next().begin();
    }

    /**
     * Commit transactions at the current level.
     * @throws NoSuchElementException if no <code>begin()</code> was issued.
     */
    public void commit() throws ParseFailure {
        assert transactionLevel > 0 : "No begin() for commit()";
        --transactionLevel;

        try {
            Collection<Table> currentTables = tables.values();
            for (Iterator<Table> i = currentTables.iterator(); i.hasNext();)
                i.next().commit(Table.THIS_LEVEL);
        }
        catch (NoSuchElementException e) {
            verify(false, "No BEGIN to match COMMIT");
        }
    }

    /**
     * Roll back transactions at the current level
     * @throws NoSuchElementException if no <code>begin()</code> was issued.
     */
    public void rollback() throws ParseFailure {
        assert transactionLevel > 0 : "No begin() for commit()";
        --transactionLevel;
        try {
            Collection<Table> currentTables = tables.values();

            for (Iterator<Table> i = currentTables.iterator(); i.hasNext();)
                i.next().rollback(Table.THIS_LEVEL);
        }
        catch (NoSuchElementException e) {
            verify(false, "No BEGIN to match ROLLBACK");
        }
    }

    //@transactions-end
    //@parser-start
    /*******************************************************************
     * Execute a SQL statement. If an exception is tossed and we are in the
     * middle of a transaction (a begin has been issued but no matching commit
     * has been seen), the transaction is rolled back.
     * @return a {@link Table} holding the result of a SELECT, or null for
     *         statements other than SELECT.
     * @param expression a String holding a single SQL statement. The complete
     *            statement must be present (you cannot break a long statement
     *            into multiple calls), and text following the SQL statement is
     *            ignored.
     * @throws erwins.database.text.ParseFailure if the SQL is corrupt.
     * @throws IOException Database files couldn't be accessed or created.
     * @see #affectedRows()
     */

    public Table execute(String expression) throws IOException, ParseFailure {
        try {
            in = new Scanner(tokens, expression);
            in.advance(); // advance to the first token.
            return statement();
        }
        catch (ParseFailure e) {
            if (transactionLevel > 0) rollback();
            throw e;
        }
        catch (IOException e) {
            if (transactionLevel > 0) rollback();
            throw e;
        }
    }

    /**
     * <PRE>
     * statement ::= CREATE DATABASE IDENTIFIER | CREATE TABLE IDENTIFIER
     * LP idList RP | DROP TABLE IDENTIFIER | USE DATABASE IDENTIFIER | BEGIN
     * [WORK|TRAN[SACTION]] | COMMIT [WORK|TRAN[SACTION]] | ROLLBACK
     * [WORK|TRAN[SACTION]] | DUMP
     * 
     * | INSERT INTO IDENTIFIER [LP idList RP] VALUES LP exprList RP | UPDATE
     * IDENTIFIER SET IDENTIFIER EQUAL expr [WHERE expr] | DELETE FROM
     * IDENTIFIER WHERE expr | SELECT idList [INTO table] FROM idList [WHERE
     * expr]
     * </PRE>
     * <p>
     * @return a Table holding the result of a SELECT, or null for other SQL
     *         requests. The result table is treated like a normal database
     *         table if the SELECT contains an INTO clause, otherwise it's a
     *         temporary table that's not put into the database.
     * @throws ParseFailure something's wrong with the SQL
     * @throws IOException a database or table couldn't be opened or accessed.
     * @see #createDatabase
     * @see #createTable
     * @see #dropTable
     * @see #useDatabase
     */
    private Table statement() throws ParseFailure, IOException {
        affectedRows = 0; // is modified by UPDATE, INSERT, DELETE

        // These productions map to public method calls:

        if (in.matchAdvance(CREATE) != null) {
            if (in.match(DATABASE)) {
                in.advance();
                createDatabase(in.required(IDENTIFIER));
            } else {
                in.required(TABLE);  // must be CREATE TABLE
                String tableName = in.required(IDENTIFIER);
                in.required(LP);
                createTable(tableName, declarations());
                in.required(RP);
            }
        } else if (in.matchAdvance(DROP) != null) {
            in.required(TABLE);
            dropTable(in.required(IDENTIFIER));
        } else if (in.matchAdvance(USE) != null) {
            in.required(DATABASE);
            useDatabase(new File(in.required(IDENTIFIER)));
        }

        else if (in.matchAdvance(BEGIN) != null) {
            in.matchAdvance(WORK); // ignore it if it's there
            begin();
        } else if (in.matchAdvance(ROLLBACK) != null) {
            in.matchAdvance(WORK); // ignore it if it's there
            rollback();
        } else if (in.matchAdvance(COMMIT) != null) {
            in.matchAdvance(WORK); // ignore it if it's there
            commit();
        } else if (in.matchAdvance(DUMP) != null) {
            dump();
        }

        // These productions must be handled via an
        // interpreter:

        else if (in.matchAdvance(INSERT) != null) {
            in.required(INTO);
            String tableName = in.required(IDENTIFIER);

            List<String> columns = null;
            List<Expression> values = null;

            if (in.matchAdvance(LP) != null) {
                columns = idList();
                in.required(RP);
            }
            if (in.required(VALUES) != null) {
                in.required(LP);
                values = exprList();
                in.required(RP);
            }
            affectedRows = doInsert(tableName, columns, values);
        } else if (in.matchAdvance(UPDATE) != null) { // First parse the expression
            String tableName = in.required(IDENTIFIER);
            in.required(SET);
            final String columnName = in.required(IDENTIFIER);
            in.required(EQUAL);
            final Expression value = expr();
            in.required(WHERE);
            affectedRows = doUpdate(tableName, columnName, value, expr());
        } else if (in.matchAdvance(DELETE) != null) {
            in.required(FROM);
            String tableName = in.required(IDENTIFIER);
            in.required(WHERE);
            affectedRows = doDelete(tableName, expr());
        } else if (in.matchAdvance(SELECT) != null) {
            List<String> columns = idList();

            String into = null;
            if (in.matchAdvance(INTO) != null) into = in.required(IDENTIFIER);

            in.required(FROM);
            List<String> requestedTableNames = idList();

            Expression where = (in.matchAdvance(WHERE) == null) ? null : expr();
            Table result = doSelect(columns, into, requestedTableNames, where);
            return result;
        } else {
            error("Expected insert, create, drop, use, " + "update, delete or select");
        }

        return null;
    }

    //----------------------------------------------------------------------
    // idList			::= IDENTIFIER idList' | STAR
    // idList'			::= COMMA IDENTIFIER idList'
    // 					|	e
    // Return a Collection holding the list of columns
    // or null if a * was found.

    private List<String> idList() throws ParseFailure {
        List<String> identifiers = null;
        if (in.matchAdvance(STAR) == null) {
            identifiers = new ArrayList<String>();
            String id;
            while ((id = in.required(IDENTIFIER)) != null) {
                identifiers.add(id);
                if (in.matchAdvance(COMMA) == null) break;
            }
        }
        return identifiers;
    }

    //----------------------------------------------------------------------
    // declarations  ::= IDENTIFIER [type] declaration'
    // declarations' ::= COMMA IDENTIFIER [type] [NOT [NULL]] declarations'
    //				 |   e
    //
    // type			 ::= INTEGER [ LP expr RP 				]
    //				 |	 CHAR 	 [ LP expr RP				]
    //				 |	 NUMERIC [ LP expr COMMA expr RP	]
    //				 |	 DATE			// format spec is part of token

    private List<String> declarations() throws ParseFailure {
        List<String> identifiers = new ArrayList<String>();

        String id;
        while (true) {
            if (in.matchAdvance(PRIMARY) != null) {
                in.required(KEY);
                in.required(LP);
                in.required(IDENTIFIER);
                in.required(RP);
            } else {
                id = in.required(IDENTIFIER);

                identifiers.add(id); // get the identifier

                // Skip past a type declaration if one's there

                if ((in.matchAdvance(INTEGER) != null) || (in.matchAdvance(CHAR) != null)) {
                    if (in.matchAdvance(LP) != null) {
                        expr();
                        in.required(RP);
                    }
                } else if (in.matchAdvance(NUMERIC) != null) {
                    if (in.matchAdvance(LP) != null) {
                        expr();
                        in.required(COMMA);
                        expr();
                        in.required(RP);
                    }
                } else if (in.matchAdvance(DATE) != null) {
                    ; // do nothing
                }

                in.matchAdvance(NOT);
                in.matchAdvance(NULL);
            }

            if (in.matchAdvance(COMMA) == null) // no more columns
            break;
        }

        return identifiers;
    }

    // exprList 		::= 	  expr exprList'
    // exprList'		::= COMMA expr exprList'
    // 					|	e

    private List<Expression> exprList() throws ParseFailure {
        List<Expression> expressions = new LinkedList<Expression>();

        expressions.add(expr());
        while (in.matchAdvance(COMMA) != null) {
            expressions.add(expr());
        }
        return expressions;
    }

    /**
     * Top-level expression production. Returns an Expression object which will
     * interpret the expression at runtime when you call it's evaluate() method.
     * 
     * <PRE>
     * expr ::= andExpr expr' expr' ::= OR andExpr expr' | e
     * </PRE>
     */

    private Expression expr() throws ParseFailure {
        Expression left = andExpr();
        while (in.matchAdvance(OR) != null)
            left = new LogicalExpression(left, OR, andExpr());
        return left;
    }

    // andExpr			::= 	relationalExpr andExpr'
    // andExpr'			::= AND relationalExpr andExpr'
    // 					|	e

    private Expression andExpr() throws ParseFailure {
        Expression left = relationalExpr();
        while (in.matchAdvance(AND) != null)
            left = new LogicalExpression(left, AND, relationalExpr());
        return left;
    }

    // relationalExpr ::=   		additiveExpr relationalExpr'
    // relationalExpr'::=	  RELOP additiveExpr relationalExpr'
    // 						| EQUAL additiveExpr relationalExpr'
    // 						| LIKE  additiveExpr relationalExpr'
    // 						| e

    private Expression relationalExpr() throws ParseFailure {
        Expression left = additiveExpr();
        while (true) {
            String lexeme;
            if ((lexeme = in.matchAdvance(RELOP)) != null) {
                RelationalOperator op;
                if (lexeme.length() == 1) op = lexeme.charAt(0) == '<' ? LT : GT;
                else {
                    if (lexeme.charAt(0) == '<' && lexeme.charAt(1) == '>') op = NE;
                    else op = lexeme.charAt(0) == '<' ? LE : GE;
                }
                left = new RelationalExpression(left, op, additiveExpr());
            } else if (in.matchAdvance(EQUAL) != null) {
                left = new RelationalExpression(left, EQ, additiveExpr());
            } else if (in.matchAdvance(LIKE) != null) {
                left = new LikeExpression(left, additiveExpr());
            } else break;
        }
        return left;
    }

    // additiveExpr	::= 			 multiplicativeExpr additiveExpr'
    // additiveExpr'	::= ADDITIVE multiplicativeExpr additiveExpr'
    // 					|	e

    private Expression additiveExpr() throws ParseFailure {
        String lexeme;
        Expression left = multiplicativeExpr();
        while ((lexeme = in.matchAdvance(ADDITIVE)) != null) {
            MathOperator op = lexeme.charAt(0) == '+' ? PLUS : MINUS;
            left = new ArithmeticExpression(left, multiplicativeExpr(), op);
        }
        return left;
    }

    // multiplicativeExpr	::=       term multiplicativeExpr'
    // multiplicativeExpr'	::= STAR  term multiplicativeExpr'
    // 						|	SLASH term multiplicativeExpr'
    // 						|	e

    private Expression multiplicativeExpr() throws ParseFailure {
        Expression left = term();
        while (true) {
            if (in.matchAdvance(STAR) != null) left = new ArithmeticExpression(left, term(), TIMES);
            else if (in.matchAdvance(SLASH) != null) left = new ArithmeticExpression(left, term(), DIVIDE);
            else break;
        }
        return left;
    }

    // term				::=	NOT expr
    // 					|	LP expr RP
    // 					|	factor

    private Expression term() throws ParseFailure {
        if (in.matchAdvance(NOT) != null) {
            return new NotExpression(expr());
        } else if (in.matchAdvance(LP) != null) {
            Expression toReturn = expr();
            in.required(RP);
            return toReturn;
        } else return factor();
    }

    // factor			::= compoundId | STRING | NUMBER | NULL
    // compoundId		::= IDENTIFIER compoundId'
    // compoundId'		::= DOT IDENTIFIER
    // 					|	e

    private Expression factor() throws ParseFailure {
        try {
            String lexeme;
            Value result;

            if ((lexeme = in.matchAdvance(STRING)) != null) result = new StringValue(lexeme);

            else if ((lexeme = in.matchAdvance(NUMBER)) != null) result = new NumericValue(lexeme);

            else if ((lexeme = in.matchAdvance(NULL)) != null) result = new NullValue();

            else {
                String columnName = in.required(IDENTIFIER);
                String tableName = null;

                if (in.matchAdvance(DOT) != null) {
                    tableName = columnName;
                    columnName = in.required(IDENTIFIER);
                }

                result = new IdValue(tableName, columnName);
            }

            return new AtomicExpression(result);
        }
        catch (java.text.ParseException e) { /* fall through */}

        error("Couldn't parse Number"); // Always throws a ParseFailure
        return null;
    }

    //@parser-end
    //@expression-start
    //======================================================================
    // The methods that parse the the productions rooted in expr work in
    // concert to build an Expression object that evaluates the expression.
    // This is an example of both the Interpreter and Composite pattern.
    // An expression is represented in memory as an abstract syntax tree
    // made up of instances of the following classes, each of which
    // references its subexpressions.

    private interface Expression { /*
                                    * Evaluate an expression using rows
                                    * identified by the two iterators passed as
                                    * arguments. <code>j</code> is null unless a
                                    * join is being processed.
                                    */

        Value evaluate(Cursor[] tables) throws ParseFailure;
    }

    //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    private class ArithmeticExpression implements Expression {
        private final MathOperator operator;
        private final Expression left, right;

        public ArithmeticExpression(Expression left, Expression right, MathOperator operator) {
            this.operator = operator;
            this.left = left;
            this.right = right;
        }

        public Value evaluate(Cursor[] tables) throws ParseFailure {
            Value leftValue = left.evaluate(tables);
            Value rightValue = right.evaluate(tables);

            verify(leftValue instanceof NumericValue && rightValue instanceof NumericValue, "Operands to < > <= >= = must be Boolean");

            double l = ((NumericValue) leftValue).value();
            double r = ((NumericValue) rightValue).value();

            return new NumericValue((operator == PLUS) ? (l + r) : (operator == MINUS) ? (l - r) : (operator == TIMES) ? (l * r) :
            /* operator == DIVIDE */(l / r));
        }
    }

    //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    private class LogicalExpression implements Expression {
        private final boolean isAnd;
        private final Expression left, right;

        public LogicalExpression(Expression left, Token op, Expression right) {
            assert op == AND || op == OR;
            this.isAnd = (op == AND);
            this.left = left;
            this.right = right;
        }

        public Value evaluate(Cursor[] tables) throws ParseFailure {
            Value leftValue = left.evaluate(tables);
            Value rightValue = right.evaluate(tables);
            verify(leftValue instanceof BooleanValue && rightValue instanceof BooleanValue,
                    "operands to AND and OR must be logical/relational");

            boolean l = ((BooleanValue) leftValue).value();
            boolean r = ((BooleanValue) rightValue).value();

            return new BooleanValue(isAnd ? (l && r) : (l || r));
        }
    }

    //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    private class NotExpression implements Expression {
        private final Expression operand;

        public NotExpression(Expression operand) {
            this.operand = operand;
        }

        public Value evaluate(Cursor[] tables) throws ParseFailure {
            Value value = operand.evaluate(tables);
            verify(value instanceof BooleanValue, "operands to NOT must be logical/relational");
            return new BooleanValue(!((BooleanValue) value).value());
        }
    }

    //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    private class RelationalExpression implements Expression {
        private final RelationalOperator operator;
        private final Expression left, right;

        public RelationalExpression(Expression left, RelationalOperator operator, Expression right) {
            this.operator = operator;
            this.left = left;
            this.right = right;
        }

        public Value evaluate(Cursor[] tables) throws ParseFailure {
            Value leftValue = left.evaluate(tables);
            Value rightValue = right.evaluate(tables);

            if ((leftValue instanceof StringValue) || (rightValue instanceof StringValue)) {
                verify(operator == EQ || operator == NE, "Can't use < <= > or >= with string");

                boolean isEqual = leftValue.toString().equals(rightValue.toString());

                return new BooleanValue(operator == EQ ? isEqual : !isEqual);
            }

            if (rightValue instanceof NullValue || leftValue instanceof NullValue) {
                verify(operator == EQ || operator == NE, "Can't use < <= > or >= with NULL");

                // Return true if both the left and right sides are instances
                // of NullValue.
                boolean isEqual = leftValue.getClass() == rightValue.getClass();

                return new BooleanValue(operator == EQ ? isEqual : !isEqual);
            }

            // Convert Boolean values to numbers so we can compare them.
            //
            if (leftValue instanceof BooleanValue) leftValue = new NumericValue(((BooleanValue) leftValue).value() ? 1 : 0);
            if (rightValue instanceof BooleanValue) rightValue = new NumericValue(((BooleanValue) rightValue).value() ? 1 : 0);

            verify(leftValue instanceof NumericValue && rightValue instanceof NumericValue, "Operands must be numbers");

            double l = ((NumericValue) leftValue).value();
            double r = ((NumericValue) rightValue).value();

            return new BooleanValue((operator == EQ) ? (l == r) : (operator == NE) ? (l != r) : (operator == LT) ? (l > r)
                    : (operator == GT) ? (l < r) : (operator == LE) ? (l <= r) :
                    /* operator == GE */(l >= r));
        }
    }

    //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    private class LikeExpression implements Expression {
        private final Expression left, right;

        public LikeExpression(Expression left, Expression right) {
            this.left = left;
            this.right = right;
        }

        public Value evaluate(Cursor[] tables) throws ParseFailure {
            Value leftValue = left.evaluate(tables);
            Value rightValue = right.evaluate(tables);
            verify(leftValue instanceof StringValue && rightValue instanceof StringValue, "Both operands to LIKE must be strings");

            String compareTo = ((StringValue) leftValue).value();
            String regex = ((StringValue) rightValue).value();
            regex = regex.replaceAll("%", ".*");

            return new BooleanValue(compareTo.matches(regex));
        }
    }

    //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    private class AtomicExpression implements Expression {
        private final Value atom;

        public AtomicExpression(Value atom) {
            this.atom = atom;
        }

        public Value evaluate(Cursor[] tables) {
            return atom instanceof IdValue ? ((IdValue) atom).value(tables) // lookup cell in table and
                    : atom // convert to appropriate type
            ;
        }
    }

    //@expression-end
    //@value-start
    //--------------------------------------------------------------
    // The expression classes pass values around as they evaluate
    // the expression.  // There  are four value subtypes that represent
    // the possible/ operands to an expression (null, numbers,
    // strings, table.column). The implementors of Value provide
    // convenience methods for using those operands.
    //
    private interface Value // tagging interface
    {}

    //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    private static class NullValue implements Value {
        @Override
        public String toString() {
            return null;
        }
    }

    //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    private static final class BooleanValue implements Value {
        boolean value;

        public BooleanValue(boolean value) {
            this.value = value;
        }

        public boolean value() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        };
    }

    //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    private static class StringValue implements Value {
        private String value;

        public StringValue(String lexeme) {
            value = lexeme.replaceAll("['\"](.*?)['\"]", "$1");
        }

        public String value() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    private final class NumericValue implements Value {
        private double value;

        public NumericValue(double value) // initialize from a double.
        {
            this.value = value;
        }

        public NumericValue(String s) throws java.text.ParseException {
            this.value = NumberFormat.getInstance().parse(s).doubleValue();
        }

        public double value() {
            return value;
        }

        @Override
        public String toString() // round down if the fraction is very small
        {
            if (Math.abs(value - Math.floor(value)) < 1.0E-20) return String.valueOf((long) value);
            return String.valueOf(value);
        }
    }

    //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    private final class IdValue implements Value {
        String tableName;
        String columnName;

        public IdValue(String tableName, String columnName) {
            this.tableName = tableName;
            this.columnName = columnName;
        }

        /**
         * Using the cursor, extract the referenced cell from the current Row
         * and return it's contents as a String.
         * @return the value as a String or null if the cell was null.
         */
        public String toString(Cursor[] participants) {
            String content = null;

            // If no name is to the left of the dot, then use
            // the (only) table.

            if (tableName == null) content = participants[0].column(columnName);
            else {
                Table container = tables.get(tableName);

                // Search for the table whose name matches
                // the one to the left of the dot, then extract
                // the desired column from that table.

                for (int i = 0; i < participants.length; ++i) {
                    if (participants[i].isTraversing(container)) {
                        content = participants[i].column(columnName);
                        break;
                    }
                }
            }

            // All table contents are converted to Strings, whatever
            // their original type. This conversion can cause
            // problems if the table was created manually.

            return (content == null) ? null : content;
        }

        /**
         * Using the cursor, extract the referenced cell from the current row of
         * the appropriate table, convert the contents to a {@link NullValue},
         * {@link NumericValue}, or {@link StringValue}, as appropriate, and
         * return that value object.
         */
        public Value value(Cursor[] participants) {
            String s = toString(participants);
            try {
                return (s == null) ? (Value) new NullValue() : (Value) new NumericValue(s);
            }
            catch (java.text.ParseException e) { // The NumericValue constructor failed, so it must be
                // a string. Fall through to the return-a-string case.
            }
            return new StringValue(s);
        }
    }

    //@value-end
    //@workhorse-start
    //======================================================================
    // Workhorse methods called from the parser.
    //
    private Table doSelect(List<String> columns, String into, List<String> requestedTableNames, final Expression where) throws ParseFailure {

        Iterator<String> tableNames = requestedTableNames.iterator();

        assert tableNames.hasNext() : "No tables to use in select!";

        // The primary table is the first one listed in the
        // FROM clause. The participantsInJoin are the other
        // tables listed in the FROM clause. We're passed in the
        // table names; use these names to get the actual Table
        // objects.

        Table primary = tables.get(tableNames.next());

        List<Table> participantsInJoin = new ArrayList<Table>();
        while (tableNames.hasNext()) {
            String participant = tableNames.next();
            participantsInJoin.add(tables.get(participant));
        }

        // Now do the select operation. First create a Strategy
        // object that picks the correct rows, then pass that
        // object through to the primary table's select() method.

        Selector selector = (where == null) ? Selector.ALL : //{=Database.selector}
                new Selector.Adapter() {
                    @Override
                    public boolean approve(Cursor[] tables) {
                        try {
                            Value result = where.evaluate(tables);

                            verify(result instanceof BooleanValue, "WHERE clause must yield boolean result");
                            return ((BooleanValue) result).value();
                        }
                        catch (ParseFailure e) {
                            throw new ThrowableContainer(e);
                        }
                    }
                };

        try {
            Table result = primary.select(selector, columns, participantsInJoin);

            // If this is a "SELECT INTO <table>" request, remove the 
            // returned table from the UnmodifiableTable wrapper, give
            // it a name, and put it into the tables Map.

            if (into != null) {
                result = ((UnmodifiableTable) result).extract();
                result.rename(into);
                tables.put(into, result);
            }
            return result;
        }
        catch (ThrowableContainer container) {
            throw (ParseFailure) container.contents();
        }
    }

    //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    private int doInsert(String tableName, List<String> columns, List<Expression> values) throws ParseFailure {
        List<String> processedValues = new LinkedList<String>();
        Table t = tables.get(tableName);

        for (Iterator<Expression> i = values.iterator(); i.hasNext();) {
            Expression current = i.next();
            processedValues.add(current.evaluate(null).toString());
        }

        // finally, put the values into the table.

        if (columns == null) return t.insert(processedValues);

        verify(columns.size() == values.size(), "There must be a value for every listed column");
        return t.insert(columns, processedValues);
    }

    //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    private int doUpdate(String tableName, final String columnName, final Expression value, final Expression where) throws ParseFailure {
        Table t = tables.get(tableName);
        try {
            return t.update(new Selector() {
                public boolean approve(Cursor[] tables) {
                    try {
                        Value result = where.evaluate(tables);

                        verify(result instanceof BooleanValue, "WHERE clause must yield boolean result");

                        return ((BooleanValue) result).value();
                    }
                    catch (ParseFailure e) {
                        throw new ThrowableContainer(e);
                    }
                }

                public void modify(Cursor current) {
                    try {
                        Value newValue = value.evaluate(new Cursor[] { current });
                        current.update(columnName, newValue.toString());
                    }
                    catch (ParseFailure e) {
                        throw new ThrowableContainer(e);
                    }
                }
            });
        }
        catch (ThrowableContainer container) {
            throw (ParseFailure) container.contents();
        }
    }

    //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    private int doDelete(String tableName, final Expression where) throws ParseFailure {
        Table t = tables.get(tableName);
        try {
            return t.delete(new Selector.Adapter() {
                @Override
                public boolean approve(Cursor[] tables) {
                    try {
                        Value result = where.evaluate(tables);
                        verify(result instanceof BooleanValue, "WHERE clause must yield boolean result");
                        return ((BooleanValue) result).value();
                    }
                    catch (ParseFailure e) {
                        throw new ThrowableContainer(e);
                    }
                }
            });
        }
        catch (ThrowableContainer container) {
            throw (ParseFailure) container.contents();
        }
    }
}
