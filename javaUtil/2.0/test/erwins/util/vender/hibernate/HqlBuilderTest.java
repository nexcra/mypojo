
package erwins.util.vender.hibernate;

import org.junit.Test;

import erwins.util.collections.map.SearchMap;
import erwins.util.exception.Check;

public class HqlBuilderTest {

    @Test
    public void hqlRoot() {
        SearchMap map = new SearchMap(1);
        map.put("label.id", new String[]{"51","101"});
        hqlMap(map);
    }

    private void hqlMap(SearchMap map) {
        HqlBuilderMap hql = new HqlBuilderMap(map);
        hql.select("book").from("Book book");
        hql.openSubQuery("book.id")
            .select("b.id").from("Label a").join("a.books b");
            if(!map.isEmpty("keyWord")){
                hql.open().iLike("b.bookName", "keyWord").iLike("b.writer", "keyWord")
                .iLike("b.description", "keyWord").iLike("b.translator","keyWord").close();    
            }
            Integer[] ints = map.getIntegerIds("label.id");
            if(ints.length!=0){
                hql.open().eq("a.id",ints).close();
                hql.groupBy("b.id having count(*) = " + ints.length);    
            }
        hql.closeSubQuery();
        Check.isEquals(hql.hqlStringForCount(),"select count( distinct book) from Book book where book.id in ( select b.id from Label a inner join a.books b group by b.id having count(*) = 2 ) ");
        Check.isEquals(hql.hqlString(),"select distinct book from Book book where book.id in ( select distinct b.id from Label a inner join a.books b group by b.id having count(*) = 2 ) ");
    }
}
