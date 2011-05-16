package erwins.gsample.sdk


import org.junit.Test
import org.apache.commons.io.output.FileWriterWithEncodingimport javax.xml.parsers.DocumentBuilderFactoryimport java.io.StringReaderimport java.io.InputStream
public class XmlParserUse{

    /** 
     * 간단한 XML 파싱
     * XmlParser : groovy.util.node를 사용한다. 풀메모리 사용. 저용량 적합.
     * XmlSluper : 메모리 사용하지 않음.
     *  */
    @Test
    public void parse(){
        def xml = '''
        <plan>
            <week capacity="8">
                <task done="2" total="2" title="read XML chapter"/>
                <task done="3" total="3" title="try some reporting"/>
                <task done="1" total="2" title="use in current project"/>
            </week>
            <week capacity="8">
                <task done="1" total="1" title="re-read DB chapter"/>
                <task done="2" total="3" title="use DB/XML combination"/>
            </week>
            <xx type="hidden">ㅋㅋㅋ</xx>
        </plan>'''
        
        //def plan = new XmlParser().parse(new File('data/plan.xml'))
        def plan = new XmlParser().parseText(xml) // 
        assert 'plan' == plan.name()
        assert plan.week.size() == 2
        assert 'week' == plan.week[0].name()
        assert 'task' == plan.week[0].task[0].name()
        assert 'read XML chapter' == plan.week[0].task[0].'@title'
        assert plan.week.task.'@done'*.toInteger().sum() == 9
		plan.week.task*.each { println it.'@done'  }
        
        assert plan.xx[0].name() == 'xx'
        assert plan.xx[0].@type == 'hidden'
        assert plan.xx[0].value() == ["ㅋㅋㅋ"];
        assert plan.xx[0].text() == 'ㅋㅋㅋ' //이런 XML작성방법은 비추천이다.
        
        assert plan.breadthFirst()*.name().join('->') == 'plan->week->week->xx->task->task->task->task->task'
        assert plan.depthFirst()*.name().join('->') == 'plan->week->task->task->task->week->task->task->xx'
        
        assert plan.week.task.findAll{it.@title =~ 'XML'}.size() == 2
   }
   
} 
