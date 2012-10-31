package erwins.jsample;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.FileAppender;
import org.apache.log4j.spi.LoggingEvent;

/**
 * log4j 시간별 폴더링   - 퍼온건데.. 쓸일이 있을지 의문
 */
public class WizDirFileAppender extends FileAppender {
	final private String DEFALUT_DIRECTORY = "logs";
	final private String DEFALUT_PREFIX = "default_log";
	final private String DEFALUT_SUFFIX = ".log";
	final private String DEFALUT_DIRECTORY_PATTERN = "yyyy/MM";
	final private String DEFALUT_FILENAME_PATTERN = "yyyy-MM-dd";
	final int TIME_YEAR = 0;
	final int TIME_MONTH = 1;
	final int TIME_DAY = 2;
	final int TIME_HOUR = 3;
	final int TIME_MINUTE = 4;
	final int TIME_SECOND = 5;
	
	int iDirectoryPatternMode = TIME_DAY;
	int iFileNamePatternMode = TIME_HOUR;
	
	String strDirectory;
	String strPrefix;
	String strSuffix;
	String strDirectoryPattern;
	String strFileNamePattern;
	
	File fileLog;
	Calendar calendar;
	SimpleDateFormat sdf4Path;
	SimpleDateFormat sdf4File;	
	
	String strLogDirName;
	String strLogFileName;		
	
	/**
	 * 
	 *
	 */
	public WizDirFileAppender() {
		strDirectory = DEFALUT_DIRECTORY;
		strPrefix = DEFALUT_PREFIX;
		strSuffix = DEFALUT_SUFFIX;
		strDirectoryPattern = DEFALUT_DIRECTORY_PATTERN;
		fileLog = null;
	}
	
	/**
	 *  
	 * @param _strDirectory
	 * @param _strPrefix
	 * @param _strSuffix
	 */
	public WizDirFileAppender(String _strDirectory, String _strPrefix, String _strSuffix, String _strDirectoryPattern) {		
		strDirectory = _strDirectory;
		strPrefix = _strPrefix;
		strSuffix = _strSuffix;
		strDirectoryPattern = _strDirectoryPattern;
		
		activateOptions();
	}
	
	/**
	 * 
	 * @param _strDirectory
	 */
	public void setDirectory(String _strDirectory) {
		strDirectory = _strDirectory;
	}

	/**
	 * 
	 * @return
	 */
	public String getDirectory() {
		return strDirectory;
	}
	
	/**
	 * 
	 * @param _strPrefix
	 */
	public void setPrefix(String _strPrefix) {
		strPrefix = _strPrefix;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getPrefix() {
		return strPrefix;
	}
	
	/**
	 * 
	 * @param _strSuffix
	 */
	public void setSuffix(String _strSuffix) {
		strSuffix = _strSuffix;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getSuffix() {
		return strSuffix;
	}
	
	/**
	 * 
	 * @param _strDirectoryPattern
	 */
	public void setDirectoryPattern(String _strDirectoryPattern) {
		strDirectoryPattern = _strDirectoryPattern;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getDirectoryPattern() {
		return strDirectoryPattern;
	}
	
	/**
	 * 
	 * @param _strFileNamePattern
	 */
	public void setFileNamePattern(String _strFileNamePattern) {
		strFileNamePattern = _strFileNamePattern;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getFileNamePattern() {
		return strFileNamePattern;
	}
	
	/**
	 * 
	 */
	public void activateOptions() {		
		if( strDirectory == null ) {
			strDirectory = DEFALUT_DIRECTORY;
		}
		if( strPrefix == null ) {
			strPrefix = DEFALUT_PREFIX;
		}
		if( strSuffix == null ) {
			strSuffix = DEFALUT_SUFFIX;
		}
		if( strDirectoryPattern == null ) {
			strDirectoryPattern = DEFALUT_DIRECTORY_PATTERN;
		}
		
		mappingDirPattern(strDirectoryPattern);
		mappingFileNamePattern(strFileNamePattern);
		
		setCurrentDatePath();
		setCurrentDateFileName();
		makeLogDirectory();
		makeLogFile();
	}
	
	/**
	 * write log in file
	 * if pattern data is same, append data present file
	 * else make new directory and file
	 */
	public void append(LoggingEvent loggingevent) {		
		// variable name is FileAppender's class variable.
		if( layout == null) {
            errorHandler.error("[DailyDirFileAppender] append() : No layout set for the appender named [" + name + "].");
            return;
        }
        if( calendar == null) {
            errorHandler.error("[DailyDirFileAppender] append() : Improper initialization for the appender named [" + name + "].");
            return;
        }
        
        if( isNextFile() ) {
        	makeLogDirectory();
        	makeLogFile();
        }
        
        // QW(QuietWriter)is WriterAppender's class variable.
        if( qw == null ) {
            errorHandler.error("[DailyDirFileAppender] append() : No output stream or file set for the appender named [" + name + "].");
            return;
        }else {
            subAppend(loggingevent);
            return;
        }
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isNextFile() {
		int[] arrBeforeTimeValues = getTimeValues();
		calendar.setTime(new Date());
		int iYear = calendar.get(Calendar.YEAR);
		int iMonth = calendar.get(Calendar.MONTH);
		int iDate = calendar.get(Calendar.DATE);
		int iHour = calendar.get(Calendar.HOUR_OF_DAY);
		int iMinute = calendar.get(Calendar.MINUTE);
		int iSecond = calendar.get(Calendar.SECOND);
		calendar.clear();
		calendar.set(iYear, iMonth, iDate, iHour, iMinute, iSecond);
		
		int[] arrNowTimeValues = getTimeValues();
		if( arrBeforeTimeValues[iDirectoryPatternMode] != arrNowTimeValues[iDirectoryPatternMode] ) {
			// change directory..
			setCurrentDatePath();
			// change file's name..
			setCurrentDateFileName();
			return true;
		}
		if( arrBeforeTimeValues[iFileNamePatternMode] != arrNowTimeValues[iFileNamePatternMode] ) {
			// change file's name..
			setCurrentDateFileName();
			return true;
		}
		return false;
	}
	
	/**
	 * path for creating log's directories..
	 *
	 */
	public void setCurrentDatePath() {
		if( sdf4Path == null ) {
			sdf4Path = new SimpleDateFormat(strDirectoryPattern);
		}
		if( calendar == null ) {
			calendar = Calendar.getInstance();
		}
		StringBuffer sbDatePath = new StringBuffer();
		sbDatePath.append(strDirectory);
		sbDatePath.append("/");
		sbDatePath.append(sdf4Path.format(calendar.getTime()));
		strLogDirName = sbDatePath.toString();
		sbDatePath = null;
	}
	
	/**
	 * create log file's name..
	 *
	 */
	public void setCurrentDateFileName() {
		if( sdf4File == null ) {
			sdf4File = new SimpleDateFormat(strFileNamePattern);
		}
		if( calendar == null ) {
			calendar = Calendar.getInstance();
		}
		strLogFileName = sdf4File.format(calendar.getTime());
	}
	
	/**
	 * 
	 * @param _strDirectory
	 * @param _strFileName
	 */
	public void makeLogFile(String _strDirectory, String _strFileName) {
		StringBuffer sbLogFileFullPath = new StringBuffer();
		sbLogFileFullPath.append(_strDirectory);
		sbLogFileFullPath.append("/");
		sbLogFileFullPath.append(strPrefix);
		sbLogFileFullPath.append(_strFileName);
		sbLogFileFullPath.append(strSuffix);
		
		fileLog = new File(sbLogFileFullPath.toString());
		fileName = fileLog.getAbsolutePath();
        super.activateOptions();
	}
	
	/**
	 * 
	 *
	 */
	public void makeLogFile() {
		makeLogFile(strLogDirName, strLogFileName);
	}
	
	/**
	 * 
	 *
	 */
	public void makeLogDirectory() {
		setCurrentDatePath();
		fileLog = new File(strLogDirName);
		
		if( !fileLog.isAbsolute() ) {
			String strPath = System.getProperty("user.dir");
			if( strPath != null ) {
				fileLog = new File(strPath, strDirectory);
			}
		}
		
		if( !fileLog.exists() ) {
			System.out.println("[DailyDirFileAppender] makeLogDirectory() : "+ fileLog.getAbsolutePath());
			if( !fileLog.mkdirs() ) {
				System.out.println("[DailyDirFileAppender] makeLogDirectory() : Failed make directorys("+ fileLog.getAbsolutePath() +")");
			}
		}
		fileLog = null;
	}
	
	/**
	 * Year, Month, Day,Hour, Min, Second
	 * @return
	 */
	public int[] getTimeValues() {
		int[] arrTimeValues = 
		{
				calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DATE), calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND)
		};		
		return arrTimeValues;
	}
	
	/**
	 * Using properties's DirectoryPattern, setting pattern
	 * @param _strDirectoryPattern
	 */
	public void mappingDirPattern(String _strDirectoryPattern) {
		if( _strDirectoryPattern == null || _strDirectoryPattern.trim().length() < 4 ) {
			mappingDirPattern(DEFALUT_DIRECTORY_PATTERN);
			return;
		}
		String[] arrPatterns = _strDirectoryPattern.split("/");
		if( arrPatterns == null ) {
			mappingDirPattern(DEFALUT_DIRECTORY_PATTERN);
			return;
		}
		strDirectoryPattern = _strDirectoryPattern;
		iDirectoryPatternMode = arrPatterns.length - 1;
	}
	
	/**
	 * Using properties's FileNamePattern, setting pattern
	 * @param _strFileNamePattern
	 */
	public void mappingFileNamePattern(String _strFileNamePattern) {
		if( _strFileNamePattern == null || _strFileNamePattern.trim().length() < 1 ) {
			mappingFileNamePattern(DEFALUT_FILENAME_PATTERN);
			return;
		}
		String[] arrPatterns = _strFileNamePattern.split("-");
		if( arrPatterns == null ) {
			mappingFileNamePattern(DEFALUT_FILENAME_PATTERN);
			return;
		}
		strFileNamePattern = _strFileNamePattern;
		iFileNamePatternMode = arrPatterns.length - 1;
	}
}