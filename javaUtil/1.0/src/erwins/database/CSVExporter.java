
package erwins.database;

import java.io.*;
import java.util.*;

/***
 * Pass this exporter to a {@link Table#export} implementation to create a
 * comma-sparated-value version of a {@link Table}. For example:
 * 
 * <PRE>
 * Table people  = TableFactory.create( ... );
 * //...
 * Writer out = new FileWriter( &quot;people.csv&quot; );
 * people.export( new CSVExporter(out) );
 * out.close();
 * </PRE>
 * 
 * The output file for a table called "name" with columns "first," "last," and
 * "addrId" would look like this:
 * 
 * <PRE>
 * name
 * first,	last,	addrId
 * Fred,	Flintstone,	1
 * Wilma,	Flintstone,	1
 * Allen,	Holub,	0
 * </PRE>
 * 
 * The first line is the table name, the second line identifies the columns, and
 * the subsequent lines define the rows.
 * @include /etc/license.txt
 * @see Table
 * @see Table.Exporter
 * @see CSVImporter
 */

public class CSVExporter implements Table.Exporter {
    private final Writer out;
    private int width;

    public CSVExporter(Writer out) {
        this.out = out;
    }

    public void storeMetadata(String tableName, int width, int height, Iterator<String> columnNames) throws IOException {
        this.width = width;
        out.write(tableName == null ? "<anonymous>" : tableName);
        out.write("\n");
        storeRow(columnNames); // comma separated list of columns ids
    }

    public void storeRow(Iterator<String> data) throws IOException {
        int i = width;
        while (data.hasNext()) {
            String datum = data.next();

            // Null columns are represented by an empty field
            // (two commas in a row). There's nothing to write
            // if the column data is null.
            if (datum != null) out.write(datum);

            if (--i > 0) out.write(",\t");
        }
        out.write("\n");
    }

    public void startTable() throws IOException {/* nothing to do */}

    public void endTable() throws IOException {/* nothing to do */}
}
