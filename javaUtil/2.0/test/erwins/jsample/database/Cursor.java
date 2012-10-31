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

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 트랜잭션 지원 update 가능 join 가능
 */
public interface Cursor {

    /** select operation.에서 수행된 경우 null을 리턴한다. */
    String tableName();

    /**
     * @return true if the iterator is positioned at a valid row after the
     *         advance.
     */
    boolean advance() throws NoSuchElementException;

    /**
     * Return the number of columns in the table that we're traversing.
     */
    int columnCount();

    /**
     * Return the name of the column at the indicated index. Note that this is a
     * zero-referenced index---the leftmost column is columnName(0); The JDBC
     * ResultSet class is 1 indexed, so don't get confused.
     */
    String columnName(int index);

    /**
     * 스래드에 안전할려면 read-only로 사용해야 한다. 테이블을 수정할려면 {@link Table#update}를 사용하라.
     */
    String column(String columnName);

    /**
     * Return a java.util.Iterator across all the columns in the current row.
     */
    Iterator<String> columns();
    
    /** 귀찮아서 배열로 추가.. 왜 구지 Iterator를 쓸려고 하는지.. */
    String[] columnsArray();

    /**
     * 카서가 인자로 넘어온 테이블을 순회하고 있다면 true를 리턴한다. 언제 사용되는지?
     */
    boolean isTraversing(Table t);

    /**
     * Replace the value of the indicated column of the current row with the
     * indicated new value.
     * @throws IllegalArgumentException if the newValue is the same as the
     *             object that's being updated.
     * @return the former contents of the now-modified cell.
     */
    String update(String columnName, String newValue);

    /**
     * Delete the row at the current cursor position.
     */
    void delete();
}
