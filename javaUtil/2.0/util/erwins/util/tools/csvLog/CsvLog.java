package erwins.util.tools.csvLog;

import lombok.Data;

/** CSV 한줄. 매번  객체를 생성하는 비용은... 무시 */
@Data
class CsvLog {

	private final String name;
	private final String[] data;
	/** 강제 플러싱 */
	private boolean flush = false;

}
