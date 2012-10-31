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

import java.io.*;
import java.text.MessageFormat;

/***
 * @include /etc/license.txt
 */

public class TableFactory {
    /**
     * Create an empty table with the specified columns.
     * @param name the table name
     * @param columns names of all the columns
     * @return the table
     */
    public static Table create(String name, String[] columns) {
        return new ConcreteTable(name, columns);
    }

    /**
     * Create a table from information provided by a {@link Table.Importer}
     * object.
     */
    public static Table create(Table.Importer importer) throws IOException {
        return new ConcreteTable(importer);
    }

    /**
     * This convenience method is equivalent to
     * <code>load(name, new File(".") );</code>
     * @see #load(String,File)
     */
    public static Table load(String name) throws IOException {
        return load(name, new File("."));
    }

    /**
     * This convenience method is equivalent to
     * <code>load(name, new File(location) );</code>
     * @see #load(String,File)
     */
    public static Table load(String name, String location) throws IOException {
        return load(name, new File(location));
    }

    /*
     * Create a table from some form stored on the disk. <p> At present, the
     * filename extension is used to determine the data format, and only a
     * comma-separated-value file is recognized. (The file name must end in
     * .csv). Eventually, other extensions (like .xml) will be recognized.
     * @param the file name. The table name is the string to the left of the
     * extension. For example, if the file is "foo.csv," then the table name is
     * "foo."
     * @param the directory within which the file is found.
     * @throws java.io.IOException if the filename extension is not recognized.
     */
    public static Table load(String name, File directory) throws IOException {
        if (!(name.endsWith(".csv") || name.endsWith(".CSV"))) throw new java.io.IOException("Filename (" + name + ") does not end in "
                + "supported extension (.csv)");

        String tableName = name.substring(0, name.length() - 4);
        Reader in = new FileReader(new File(directory, name));
        Table loaded = new ConcreteTable(new CSVImporter(in));
        if (!tableName.equals(loaded.name())) throw new IOException(MessageFormat.format("fileName {0} is not equal table name {1}",
                tableName, loaded.name()));
        in.close();
        return loaded;
    }
}
