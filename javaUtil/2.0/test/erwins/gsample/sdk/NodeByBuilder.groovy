package erwins.gsample.sdk


import org.junit.Test

/** 컬렉션을 사용하는것과 비슷하지만 depthFirst,breadthFirst,print 등의 기능이 추가된다.
* 이외에 children parent 등등의 기능이 있다.
* 계층형 구조를 사용할때 쉽게 쓰자. */
public class NodeByBuilder{

    @Test
    public void build(){
        def builder = new NodeBuilder()
        def ulcDate = new Date(107,0,1)
        def invoices = builder.build{  //build대신 아무거나 됨
            qwe(date: ulcDate){
                item(count:5){
                    product(name:'ULC', dollar:1499)
                }
                item(count:1){
                    product(name:'Visual Editor', dollar:499)
                }
            }
            qwe(date: new Date(106,1,2)){
                item(count:4) {
                    product(name:'Visual Editor', dollar:499)
                }
            }
        }
		//컬렉션 문법이 아니라 @를 붙이거나 ''로 또한 감싸야 한다.
        assert [ulcDate] == invoices.grep { it.item.product.any{ it.'@name' == 'ULC' } }.'@date';
        assert invoices.qwe[0].item[0].@count == 5
        //깊이 우선 탐색을 이용한 모든 노드의 컬랙션
        assert invoices.depthFirst()*.name() == ["build", "qwe", "item", "product", "item", "product", "qwe", "item", "product"];
        //너비 우선 탐색을 이용한 모든 노드의 컬랙션
        assert invoices.breadthFirst()*.name() == ["build", "qwe", "qwe", "item", "item", "item", "product", "product", "product"];
		
		def writer = new StringWriter()
		invoices.print(new PrintWriter(writer)); //중첩구조를 정리해서 리턴해준다.
		assert writer.toString().contains('date:Mon Jan 01 00:00:00 KST 2007')
   }
   
} 
