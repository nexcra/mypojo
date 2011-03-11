package erwins.gsample.sdk


import org.junit.Test
import erwins.util.tools.*

/** 자바로 짠다면 상당히 길어질 것이다.
 * 부가적인 메서드의 도움이 필요하다면 NodeBuilder를 사용할것 */
public class NodeByCollection{
    
    
    @Test
    public void literal(){
        def ulcDate = new Date(107,0,1)
        def ulc = new Product(dollar:1499, name:'ULC')
        def ve  = new Product(dollar:499,  name:'Visual Editor')
        def invoices = [
            new Invoice(date:ulcDate, items: [
                new LineItem(count:5, product:ulc),
                new LineItem(count:1, product:ve)
            ]),
            new Invoice(date:[107,1,2], items: [
                new LineItem(count:4, product:ve)
            ])
        ];
		// 이경우 invoices[0].items.size()와 달리 [[A,B],C] 이런식의 구조가 된다. 따라서 2가 정답 
        assert invoices.items.size() == 2;
		
		//flatten()으로 일반화 가능하다. 아래 2가지 다 동일한 표현식. (collect가 더 직관적이나 길다.)
        assert [5*1499, 499, 4*499] == invoices.items.flatten()*.total();
		assert [5*1499, 499, 4*499] == invoices.items.flatten().collect {it.total()}

		// 중요!! grep으로 배열객체를 리턴받은 후 .product.name를 조회하면 결과를 배열로 리턴해준다. 매우 편리한 특성
        assert ['ULC'] == invoices.items.flatten().grep{it.total() > 7000}.product.name;
		//약간 복잡한 객체도 간단히 검색 가능하다.
		assert invoices.grep{it.items.any{it.product == ulc}}.date == [ulcDate];
   }
}

class Invoice {                                          
	List    items;
    Date    date;
}                                                        
class LineItem {                                         
    Product product                                      
    int     count                                        
    int total() {                                        
        return product.dollar * count ;
    }                                                    
}                                                        
class Product {                                          
    String  name                                         
    def     dollar                                       
}   
