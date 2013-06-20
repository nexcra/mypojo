package erwins.gsample.sdk


import org.junit.Test
import org.apache.commons.io.output.FileWriterWithEncoding
/** XML빌더.. 좀 구리다. */
public class MarkupBuilderUse{

    @Test
    public void build(){
        def writer = new StringWriter()
        def builder = new groovy.xml.MarkupBuilder(writer)
        def ulcDate = new Date(107,0,1)
		def Date d = new Date(111,0,1)
        def invoices = builder.'web-app'{  //build대신 아무거나 됨
            한글(date: ulcDate){
				item(count:5){
					[1..5]*.each{ product(name:'ULC', dollar:1499+it) }
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
		println writer
        assert writer.toString().contains('한글')
   } 
    
    /** 파일로 만드는것도 가능하다. */
    //@Test
    public void build2(){
        File xml = new File('D:/markup.html');
        def writer = new FileWriter(xml);
        def html   = new groovy.xml.MarkupBuilder(writer)                                   
        html.html {
			head {
				title 'Constructed by 한글잘됨'
			}
			body {
				2.times{
					h1 "What can I do with 한글잘됨? $it"    
				}
	            form (action:'한글잘됨?') {
					for (line in ['Produce HTML','Produce XML','Have some fun']){
						input(type:'checkbox',checked:'checked', id:line, '')
						label(for:line, line)
						br('')
					}
				}
			}
		}
        assert xml.exists();
        writer.close();
        assert xml.delete();
   }
   
} 
