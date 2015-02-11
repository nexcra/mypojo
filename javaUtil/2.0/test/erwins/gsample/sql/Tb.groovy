package erwins.gsample.sql




public class Tb{
	
	String TABLE_NAME
	Long NUM_ROWS
	Long COUNT
	String COMMENTS
	List<Col> COLUMNS
	
	public static class Col{
		String TABLE_NAME
		String COLUMN_NAME
		String COMMENTS
		String DATA_TYPE
		String P
		String C
		Long DATA_LENGTH
		Long DATA_PRECISION
		Long DATA_SCALE
	}
	
}
