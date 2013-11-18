package erwins.util.vender.mybatis;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;


public abstract class MybatisUtil{
    
    /** 등록된 SQL모음집을 리턴한다. 정확하지는 않다! */ 
    public static Map<String,String> getSqlMap(SqlSession session){
        Configuration config = session.getConfiguration();
        Iterator<MappedStatement> i = config.getMappedStatements().iterator();
        Map<String,String> result = new HashMap<String,String>();
        while(i.hasNext()){
            try {
                MappedStatement now = i.next();
                SqlSource source =  now.getSqlSource();
                BoundSql boundSql = source.getBoundSql(null); //sql에 입력될 파라메터. 일단 null입력
                result.put(now.getId(), boundSql.getSql());
            } catch (ClassCastException e) {
                //내부에서만 사용되는 객체.. 무시한다.
            } 
        }
        return result;
    }
    
    /** mybatis에서 개별 SQL을 구할때 사용된다.
     * 기존 #{aa} 들은 ?로 나타난다 */
    public static String getSqlString(SqlSession session,String sqlId){
        MappedStatement now = session.getConfiguration().getMappedStatement(sqlId);
        SqlSource source =  now.getSqlSource();
        BoundSql boundSql = source.getBoundSql(null);
        return boundSql.getSql();
    }

}
