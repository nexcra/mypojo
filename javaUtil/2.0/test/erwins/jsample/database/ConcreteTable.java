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

package erwins.jsample.database;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.apache.commons.collections.iterators.ArrayIterator;

class ConcreteTable implements Table {
    // Supporting clone() complicates the following declarations. In
    // particular, the fields can't be final because they're modified
    // in the clone() method. Also, the rows field has to be declared
    // as a Linked list (rather than a List) because Cloneable is made
    // public at the LinkedList level. If you declare it as a list,
    // you'll get an error message because clone()---for reasons that
    // are mysterious to me---is declared protected in Object.
    //
    // Be sure to change the clone() method if you modify anything about
    // any of these fields.

    private LinkedList<String[]> rowSet = new LinkedList<String[]>();
    private String[] columnNames;
    private String tableName;

    private transient boolean isDirty = false;
    private transient LinkedList<LinkedList<Undo>> transactionStack = new LinkedList<LinkedList<Undo>>();

    public String[] columnNames() {
        return columnNames;
    }

    public int size() {
        return rowSet.size();
    }

    /**********************************************************************
     * Create a table with the given name and columns.
     * @param tableName the name of the table.
     * @param an array of Strings that specify the column names.
     */
    public ConcreteTable(String tableName, String[] columnNames) {
        this.tableName = tableName;
        this.columnNames = columnNames.clone();
    }

    /**********************************************************************
     * Return the index of the named column. Throw an IndexOutOfBoundsException
     * if the column doesn't exist.
     */
    private int indexOf(String columnName) {
        for (int i = 0; i < columnNames.length; ++i)
            if (columnNames[i].equals(columnName)) return i;

        throw new IndexOutOfBoundsException("Column (" + columnName + ") doesn't exist in " + tableName);
    }

    //@simple-construction-end
    //
    /**********************************************************************
     * Create a table using an importer. See {@link CSVImporter} for an example.
     */
    public ConcreteTable(Table.Importer importer) throws IOException {
        importer.startTable();

        tableName = importer.loadTableName();
        int width = importer.loadWidth();
        Iterator<String> columns = importer.loadColumnNames();

        this.columnNames = new String[width];
        for (int i = 0; columns.hasNext();)
            columnNames[i++] = columns.next();

        while ((columns = importer.loadRow()) != null) {
            String[] current = new String[width];
            for (int i = 0; columns.hasNext();)
                current[i++] = columns.next();
            this.insert(current);
        }
        importer.endTable();
    }

    //----------------------------------------------------------------------
    public void export(Table.Exporter exporter) throws IOException {
        exporter.startTable();
        exporter.storeMetadata(tableName, columnNames.length, rowSet.size(), new ArrayIterator(columnNames));

        for (Iterator<String[]> i = rowSet.iterator(); i.hasNext();)
            exporter.storeRow(new ArrayIterator(i.next()));

        exporter.endTable();
        isDirty = false;
    }

    //@import-export-end
    //----------------------------------------------------------------------
    // Inserting
    //
    public int insert(String[] intoTheseColumns, String[] values) {
        assert (intoTheseColumns.length == values.length) : "There must be exactly one value for " + "each specified column";

        String[] newRow = new String[width()];

        for (int i = 0; i < intoTheseColumns.length; ++i)
            newRow[indexOf(intoTheseColumns[i])] = values[i];

        doInsert(newRow);
        return 1;
    }

    //----------------------------------------------------------------------
    public int insert(Collection<String> intoTheseColumns, Collection<String> values) {
        assert (intoTheseColumns.size() == values.size()) : "There must be exactly one value for " + "each specified column";

        String[] newRow = new String[width()];

        Iterator<String> v = values.iterator();
        Iterator<String> c = intoTheseColumns.iterator();
        while (c.hasNext() && v.hasNext())
            newRow[indexOf(c.next())] = v.next();

        doInsert(newRow);
        return 1;
    }

    //----------------------------------------------------------------------
    public int insert(Map<String, String> row) { // A map is considered to be "ordered,"  with the order defined
        // as the order in which an iterator across a "view" returns
        // values. My reading of this statement is that the iterator
        // across the keySet() visits keys in the same order as the
        // iterator across the values() visits the values.

        return insert(row.keySet(), row.values());
    }

    //----------------------------------------------------------------------
    public int insert(String[] values) {
        assert values.length == width() : "Values-array length (" + values.length + ") " + "is not the same as table width (" + width()
                + ")";

        doInsert(values.clone());
        return 1;
    }

    //----------------------------------------------------------------------
    public int insert(Collection<String> values) {
        return insert(values.toArray(new String[values.size()]));
    }

    //----------------------------------------------------------------------
    private void doInsert(String[] newRow) {
        rowSet.add(newRow);
        registerInsert(newRow);
        isDirty = true;
    }

    //@insert-end
    //----------------------------------------------------------------------
    // Traversing and cursor-based Updating and Deleting
    //
    public Cursor rows() {
        return new Results();
    }

    //----------------------------------------------------------------------
    private final class Results implements Cursor {
        private final Iterator<String[]> rowIterator = rowSet.iterator();
        private String[] row = null;

        public String tableName() {
            return ConcreteTable.this.tableName;
        }

        public boolean advance() {
            if (rowIterator.hasNext()) {
                //제너릭 이전의 임시로직
                String[] temp = rowIterator.next();
                row = new String[temp.length];
                for (int i = 0; i < temp.length; i++) {
                    row[i] = temp[i];
                }
                return true;
            }
            return false;
        }

        public int columnCount() {
            return columnNames.length;
        }

        public String columnName(int index) {
            return columnNames[index];
        }

        public String column(String columnName) {
            return row[indexOf(columnName)];
        }

        public Iterator<String> columns() {
            return new ArrayIterator(row);
        }

        public String[] columnsArray() {
            return row;
        }

        public boolean isTraversing(Table t) {
            return t == ConcreteTable.this;
        }

        // This method is for use by the outer class only, and is not part
        // of the Cursor interface.
        private String[] cloneRow() {
            return row.clone();
        }

        public String update(String columnName, String newValue) {
            int index = indexOf(columnName);

            // The following test is required for undo to work correctly.
            if (row[index] == newValue) throw new IllegalArgumentException("May not replace object with itself");

            String oldValue = row[index];
            row[index] = newValue;
            isDirty = true;

            registerUpdate(row, index, oldValue);
            return oldValue;
        }

        public void delete() {
            String[] oldRow = row;
            rowIterator.remove();
            isDirty = true;

            registerDelete(oldRow);
        }
    }

    //@cursor-end
    //----------------------------------------------------------------------
    // Undo subsystem.
    //
    private interface Undo {
        void execute();
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    private class UndoInsert implements Undo {
        private final String[] insertedRow;

        public UndoInsert(String[] insertedRow) {
            this.insertedRow = insertedRow;
        }

        public void execute() {
            rowSet.remove(insertedRow);
        }
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    private class UndoDelete implements Undo {
        private final String[] deletedRow;

        public UndoDelete(String[] deletedRow) {
            this.deletedRow = deletedRow;
        }

        public void execute() {
            rowSet.add(deletedRow);
        }
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    private class UndoUpdate implements Undo {
        private String[] row;
        private int cell;
        private String oldContents;

        public UndoUpdate(String[] row, int cell, String oldContents) {
            this.row = row;
            this.cell = cell;
            this.oldContents = oldContents;
        }

        public void execute() {
            row[cell] = oldContents;
        }
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public void begin() {
        transactionStack.addLast(new LinkedList<Undo>());
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    private void register(Undo op) {
        transactionStack.getLast().addLast(op);
    }

    private void registerUpdate(String[] row, int cell, String oldContents) {
        if (!transactionStack.isEmpty()) register(new UndoUpdate(row, cell, oldContents));
    }

    private void registerDelete(String[] oldRow) {
        if (!transactionStack.isEmpty()) register(new UndoDelete(oldRow));
    }

    private void registerInsert(String[] newRow) {
        if (!transactionStack.isEmpty()) register(new UndoInsert(newRow));
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public void commit(boolean all) throws IllegalStateException {
        if (transactionStack.isEmpty()) throw new IllegalStateException("No BEGIN for COMMIT");
        do {
            LinkedList<Undo> currentLevel = transactionStack.removeLast();
            if (!transactionStack.isEmpty()) transactionStack.getLast().addAll(currentLevel);

        } while (all && !transactionStack.isEmpty());
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public void rollback(boolean all) throws IllegalStateException {
        if (transactionStack.isEmpty()) throw new IllegalStateException("No BEGIN for ROLLBACK");
        do {
            LinkedList<Undo> currentLevel = transactionStack.removeLast();

            while (!currentLevel.isEmpty())
                currentLevel.removeLast().execute();

        } while (all && !transactionStack.isEmpty());
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    //@undo-end
    //-----------------------------------------------------------------
    public int update(Selector where) {
        Results currentRow = (Results) rows();
        Cursor[] envelope = new Cursor[] { currentRow };
        int updated = 0;

        while (currentRow.advance()) {
            if (where.approve(envelope)) {
                where.modify(currentRow);
                ++updated;
            }
        }

        return updated;
    }

    //----------------------------------------------------------------------
    public int delete(Selector where) {
        int deleted = 0;

        Results currentRow = (Results) rows();
        Cursor[] envelope = new Cursor[] { currentRow };

        while (currentRow.advance()) {
            if (where.approve(envelope)) {
                currentRow.delete();
                ++deleted;
            }
        }
        return deleted;
    }

    //@select-start
    //----------------------------------------------------------------------
    public Table select(Selector where) {
        Table resultTable = new ConcreteTable(null, columnNames.clone());

        Results currentRow = (Results) rows();
        Cursor[] envelope = new Cursor[] { currentRow };

        while (currentRow.advance()) {
            if (where.approve(envelope)) resultTable.insert(currentRow.cloneRow());
        }
        return new UnmodifiableTable(resultTable);
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public Table select(Selector where, String[] requestedColumns) {
        if (requestedColumns == null) return select(where);

        Table resultTable = new ConcreteTable(null, requestedColumns.clone());

        Results currentRow = (Results) rows();
        Cursor[] envelope = new Cursor[] { currentRow };

        while (currentRow.advance()) {
            if (where.approve(envelope)) {
                String[] newRow = new String[requestedColumns.length];
                for (int column = 0; column < requestedColumns.length; ++column) {
                    newRow[column] = currentRow.column(requestedColumns[column]);
                }
                resultTable.insert(newRow);
            }
        }
        return new UnmodifiableTable(resultTable);
    }

    /**
     * This version of select does a join
     */
    public Table select(Selector where, String[] requestedColumns, Table[] otherTables) {
        // If we're not doing a join, use the more efficient version
        // of select().

        if (otherTables == null || otherTables.length == 0) return select(where, requestedColumns);

        // Make the current table not be a special case by effectively
        // prefixing it to the otherTables array.

        Table[] allTables = new Table[otherTables.length + 1];
        allTables[0] = this;
        System.arraycopy(otherTables, 0, allTables, 1, otherTables.length);

        // Create places to hold the result of the join and to hold
        // iterators for each table involved in the join.

        Table resultTable = new ConcreteTable(null, requestedColumns);
        Cursor[] envelope = new Cursor[allTables.length];

        // Recursively compute the Cartesian product, adding to the
        // resultTable all rows that the Selector approves

        selectFromCartesianProduct(0, where, requestedColumns, allTables, envelope, resultTable);

        return new UnmodifiableTable(resultTable);
    }

    /**
     * Think of the Cartesian product as a kind of tree. That is given one table
     * with rows A and B, and another table with rows C and D, you can look at
     * the product like this: root ______|______ | | A B ____|____ ____|____ | |
     * | | C D C D The tree is as deep as the number of tables we're joining.
     * Every possible path from the root to a leaf represents one row in the
     * Cartesian product. The current method effectively traverses this tree
     * recursively without building an actual tree. It assembles an array of
     * iterators (one for each table) positioned at the current place in the set
     * of rows as it recurses to a leaf, and then asks the selector whether or
     * not to approve that row. It then goes up a notch, advances the correct
     * iterator, and recurses back down.
     */
    private static void selectFromCartesianProduct(int level, Selector where, String[] requestedColumns, Table[] allTables,
            Cursor[] allIterators, Table resultTable) {
        allIterators[level] = allTables[level].rows();

        while (allIterators[level].advance()) { // If we haven't reached the tips of the branches yet,
            // go down one more level.

            if (level < allIterators.length - 1) selectFromCartesianProduct(level + 1, where, requestedColumns, allTables, allIterators,
                    resultTable);

            // If we are at the leaf level, then get approval for
            // the fully-assembled row, and add the row to the table
            // if it's approved.

            if (level == allIterators.length - 1) {
                if (where.approve(allIterators))
                    insertApprovedRows(resultTable, requestedColumns, allIterators);
            }
        }
    }

    /**
     * Insert an approved row into the result table:
     * 
     * <PRE>
     * for( every requested
     * column ) for( every table in the join ) if the requested column is in the
     * current table add the associated value to the result table
     * 
     * </PRE>
     * 
     * Only one column with a given name is added, even if that column appears
     * in multiple tables. Columns in tables at the beginning of the list take
     * precedence over identically named columns that occur later in the list.
     */
    private static void insertApprovedRows(Table resultTable, String[] requestedColumns, Cursor[] allTables) {

        String[] resultRow = new String[requestedColumns.length];

        for (int i = 0; i < requestedColumns.length; ++i) {
            for (int table = 0; table < allTables.length; ++table) {
                try {
                    resultRow[i] = allTables[table].column(requestedColumns[i]);
                    break; // if the assignment worked, do the next column
                }
                catch (Exception e) { // otherwise, try the next table
                }
            }
        }
        resultTable.insert( /* requestedColumns, */resultRow);
    }

    /**
     * A collection variant on the array version. Just converts the collection
     * to an array and then chains to the other version (
     * {@linkplain #select(Selector,String[],Table[]) see}).
     * @param requestedColumns the value returned from the {@link #toString}
     *            method of the elements of this collection are used as the
     *            column names.
     * @param other Collection of tables to join to the current one,
     *            <code>null</code>if none.
     * @throws ClassCastException if any elements of the <code>other</code>
     *             collection do not implement the {@link Table} interface.
     */
    public Table select(Selector where, Collection<String> requestedColumns, Collection<Table> other) {
        String[] columnNames = null;
        Table[] otherTables = null;

        if (requestedColumns != null) // SELECT *
        {
            // Can't cast an Object[] to a String[], so make a copy to ensure
            // type safety.

            columnNames = new String[requestedColumns.size()];
            int i = 0;
            Iterator<String> column = requestedColumns.iterator();

            while (column.hasNext())
                columnNames[i++] = column.next();
        }

        if (other != null) otherTables = other.toArray(new Table[other.size()]);

        return select(where, columnNames, otherTables);
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public Table select(Selector where, Collection<String> requestedColumns) {
        return select(where, requestedColumns, null);
    }

    //@select-end
    //----------------------------------------------------------------------
    // Housekeeping stuff
    //
    public String name() {
        return tableName;
    }

    public void rename(String s) {
        tableName = s;
    }

    public boolean isDirty() {
        return isDirty;
    }

    private int width() {
        return columnNames.length;
    }

    //----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    @Override
    public Object clone() throws CloneNotSupportedException {
        ConcreteTable copy = (ConcreteTable) super.clone();
        copy.rowSet = (LinkedList<String[]>) rowSet.clone();
        copy.columnNames = columnNames.clone();
        copy.tableName = tableName;
        return copy;
    }

    //----------------------------------------------------------------------
    @Override
    public String toString() {
        StringBuffer out = new StringBuffer();

        out.append(tableName == null ? "<anonymous>" : tableName);
        out.append("\n");

        for (int i = 0; i < columnNames.length; ++i)
            out.append(columnNames[i] + "\t");
        out.append("\n----------------------------------------\n");

        for (Cursor i = rows(); i.advance();) {
            Iterator<String> columns = i.columns();
            while (columns.hasNext()) {
                Object next = columns.next();
                if (next == null) out.append("null\t");
                else out.append(next + "\t");
            }
            out.append('\n');
        }
        return out.toString();
    }

    //----------------------------------------------------------------------

}
