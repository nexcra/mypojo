package erwins.util.groovy


import java.sql.Timestamp
import java.util.List

import org.apache.poi.hssf.record.formula.functions.T

import erwins.util.collections.MapForList
import erwins.util.exception.BusinessException
import erwins.util.lib.FormatUtil
import erwins.util.lib.StringUtil
import erwins.util.tools.TextFile
import erwins.util.valueObject.ShowTime
import erwins.util.vender.apache.Poi
import erwins.util.vender.apache.PoiReaderFactory
import erwins.util.vender.etc.OpenCsv
import groovy.sql.Sql


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
