package erwins.database;

import java.io.*;
import java.util.Iterator;

import erwins.util.counter.ArrayIterator;

/***
 * Pass this importer to a {@link Table} constructor (such as {link
 * erwins.database.ConcreteTable#ConcreteTable(Table.Importer)} to initialize a
 * <code>Table</code> from a comma-sparated-value repressentation. For example:
 * <PRE> Reader in = new FileReader( "people.csv" ); people = new ConcreteTable(
 * new CSVImporter(in) ); in.close(); </PRE> The input file for a table called
 * "name" with columns "first," "last," and "addrId" would look like this: <PRE>
 * name first, last, addrId Fred, Flintstone, 1 Wilma, Flintstone, 1 Allen,
 * Holub, 0 </PRE> The first line is the table name, the second line identifies
 * the columns, and the subsequent lines define the rows.
 * 
 * @see Table
 * @see Table.Importer
 * @see CSVExporter
 */

public class CSVImporter implements Table.Importer {
    private BufferedReader in; // null once end-of-file reached
    private String[] columnNames;
    private String tableName;

    public CSVImporter(Reader in) {
        this.in = in instanceof BufferedReader ? (BufferedReader) in : new BufferedReader(in);
    }

    public void startTable() throws IOException {
        tableName = in.readLine().trim();
        columnNames = in.readLine().split("\\s*,\\s*");
    }

    public String loadTableName() throws IOException {
        return tableName;
    }

    public int loadWidth() throws IOException {
        return columnNames.length;
    }

    public Iterator<String> loadColumnNames() throws IOException {
        return new ArrayIterator<String>(columnNames); //{=CSVImporter.ArrayIteratorCall}
    }

    public Iterator<String> loadRow() throws IOException {
        Iterator<String> row = null;
        if (in != null) {
            String line = in.readLine();
            if (line == null) in = null;
            else row = new ArrayIterator<String>(line.split("\\s*,\\s*"));
        }
        return row;
    }

    public void endTable() throws IOException {}
}
