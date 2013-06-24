package erwins.util.vender.springBatch;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import erwins.util.lib.StringUtil;
import erwins.util.vender.springBatch.ToStringArrayRowMapper.ColumnMetaData;

/** 
 * 보통 CSV가 더 좋기때문에 잘 사용되지 않는다.
 * DB의ResultSet을 단순 문자열로 변경한다.
 * 메타 정보를 추가할 수 있지만 일단 단순 백업용 문자열로 사용.  */
@Deprecated
public class ToStringRowMapper implements RowMapper<String>,Serializable{

    private static final long serialVersionUID = 2002087137176208746L;
    
    private String columnSeparator = "\t";
    private ToStringArrayRowMapper mapper = new ToStringArrayRowMapper();
    
    /** 첫 라이터가 호출되기 전에 메타데이터가 저장된다.
     * 헤더를 원하면 첫 라인에 추가하자 */
    @Override
    public String mapRow(ResultSet rs, int rowNum) throws SQLException {
        String[] result = mapper.mapRow(rs, rowNum);
        return StringUtil.join(result, columnSeparator);
    }

    public String getColumnSeparator() {
        return columnSeparator;
    }


    public void setColumnSeparator(String columnSeparator) {
        this.columnSeparator = columnSeparator;
    }

	public void setColumnMetaDatas(List<ColumnMetaData> columnMetaDatas) {
		mapper.setColumnMetaDatas(columnMetaDatas);
	}
    

}
