package erwins.util.vender.springBatch;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.jdbc.core.RowMapper;

import erwins.util.lib.StringUtil;

/** DB의ResultSet을 단순 문자열로 변경한다.
 * 메타 정보를 추가할 수 있지만 일단 단순 백업용 문자열로 사용  */
public class ToStringRowMapper implements RowMapper<String>,FlatFileHeaderCallback,Serializable{

    private static final long serialVersionUID = 2002087137176208746L;
    
    private String columnSeparator = "\t";
    private Integer columnCount = null;
    private String[] columnLabels;
    private String[] columnClassNames;
    private String[] columnTypeNames;
    
    
    /** 첫 라이터가 호출되기 전에 메타데이터가 저장된다.
     * 헤더를 원하면 첫 라인에 추가하자 */
    @Override
    public String mapRow(ResultSet rs, int rowNum) throws SQLException {
        if(rs.isFirst()) {
            ResultSetMetaData md = rs.getMetaData();
            columnCount = md.getColumnCount();
            columnLabels = new String[columnCount];
            columnClassNames = new String[columnCount];
            columnTypeNames = new String[columnCount];
            for(int column=0;column<columnCount;column++){
                columnLabels[column] = md.getColumnLabel(column+1); 
                columnClassNames[column] = md.getColumnClassName(column+1); 
                columnTypeNames[column] = md.getColumnTypeName(column+1); 
            }
        }
        String[] result = new String[columnCount];
        for(int column=0;column<columnCount;column++){
            result[column] =  rs.getString(column+1);
        }
        return StringUtil.join(result, columnSeparator);
    }


    public String getColumnSeparator() {
        return columnSeparator;
    }


    public void setColumnSeparator(String columnSeparator) {
        this.columnSeparator = columnSeparator;
    }

    /** 작동안됨~ 연습용 */
    @Override
    public void writeHeader(Writer writer) throws IOException {
        writer.write(StringUtil.join(columnLabels, columnSeparator));
    }
    
    
    

}
