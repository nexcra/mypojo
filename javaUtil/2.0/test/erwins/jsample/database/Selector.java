
package erwins.jsample.database;

/**
 * {@link Table#select} 가 특정 로우를 포함할지 안할지를 결정하는데 사용하는 stragegy객체이다. 인자로 전달된
 * Cursor는 정확한 위치에 있으며 억지로 진행시키려 하면 실패할것이다.
 */

interface Selector {

    /**
     * This method is passed rows from the tables being joined and returns true
     * if the aggregate row is approved for the current operation. In a select,
     * for example, "aproval" means that the aggregate row should be included in
     * the result-set Table.
     * @param rows An array of iterators, one for the current row in each table
     *            to be examined (The array will have only one element unless a
     *            you're approving rows in a join.) These iterators are already
     *            positioned at the correct row. Attempts to advance the
     *            iterator result in an exception toss (
     *            {@link java.lang.IllegalStateException}).
     * @return true if the aggregate row should has been approved for the
     *         current operation.
     */
    boolean approve(Cursor[] rows);

    /**
     * This method is called only when an update request for a row is approved
     * by {@link #approve approve(...)}. It should replace the required cell
     * with a new value. You must do the replacement using the iterator's
     * {@link Cursor#update} method. A typical implementation takes this form:
     * 
     * <PRE>
     * public Object modify(Cursor current) {
     *     return current.update(&quot;columnName&quot;, &quot;new-value&quot;);
     * }
     * </PRE>
     * @param current Iterator positioned at the row to modify
     */
    void modify(Cursor current);

    /**
     * An implementation of {@link Selector} whose approve method approves
     * everything, and whose replace() method throws an
     * {@link UnsupportedOperationException} if called. Useful for creating
     * selectors on the fly with anonymous inner classes.
     */
    public static class Adapter implements Selector {
        public boolean approve(Cursor[] tables) {
            return true;
        }

        public void modify(Cursor current) {
            throw new UnsupportedOperationException("Can't use a Selector.Adapter in an update");
        }
    }

    /**
     * An instance of {@link Selector.Adapter), pass Selector.ALL to the {
     * @link Table}'s {@link Table#select select(...)} or {@link Table#delete
     * delete(...)} methods to select all rows of the table. May not be used in
     * an update operation.
     */
    public static final Selector ALL = new Selector.Adapter();

}
