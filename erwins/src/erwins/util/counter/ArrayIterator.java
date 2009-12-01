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

package erwins.util.counter;

import java.util.Iterator;
import java.util.NoSuchElementException;

/***
 * A simple implementation of java.util.Iterator that enumerates over arrays.
 * You can use this class to pass arrays to methods that normally accept
 * {@link Iterator} arguments. (It's an example of the Adapter design pattern in
 * that it makes an array appear to implement the <code>Iterator</code>
 * interface.)
 * 
 * @include /etc/license.txt
 */

public final class ArrayIterator<T> implements Iterator<T> {
    private int position = 0;
    private final T[] items;

    /**
     * Create and <code>ArrayIterator</code>.
     * 
     * @param items
     *            the array whose elements will be returned, in turn, by each
     *            {@link #next} call.
     */
    public ArrayIterator(T[] items) {
        this.items = items;
    }

    public boolean hasNext() {
        return (position < items.length);
    }

    public T next() {
        if (position >= items.length) throw new NoSuchElementException();
        return items[position++];
    }

    public void remove() {
        throw new UnsupportedOperationException("ArrayIterator.remove()");
    }

    /**
     * Not part of the Iterator interface, returns the data set in array form. A
     * clone of the wrapped array is actually returned, so modifying the
     * returned array will not affect the iteration at all.
     */
    public T[] toArray() {
        return items.clone();
    }
}
