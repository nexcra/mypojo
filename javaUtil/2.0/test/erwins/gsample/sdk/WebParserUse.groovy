package erwins.gsample.sdk


import org.junit.Test
import com.gargoylesoftware.htmlunit.WebClient
import groovy.util.XmlSlurperimport com.sun.org.apache.xerces.internal.parsers.SAXParserimport org.cyberneko.html.parsers.SAXParser/** 파서가 어디까지 지원하는지? 쓰기는 힘들듯. */
public class WebParserUse{

    /** Bole된 헤더제목만 긁어온다.  attribute는 못읽는듯? */
    @Test
    public void parser(){
        final def url = 'http://www.google.com'
        def html = new XmlSlurper(new SAXParser()).parse(url)
		assert html.toString().contains('google')
		def bolded = html.'**'
		/*
        assert html
        def bolded = html.'**'.findAll{ it.name() == 'B' }
        assert  bolded.A[0]
        def out = bolded.A*.text().collect{ it.trim() }
        out.removeAll([''])
        out[2..5].each{ assert it!='' }  //?? ㅋㅋ
        */
   }
    
    /** HTML을 읽고, 내용 입력후에 전송까지 가능하다. 하지만 워낙 변수가 많음으로.. */
    public void htmlunit(){
        WebClient client = new WebClient()
        client.javaScriptEnabled = false;
        def page   = client.getPage('http://www.google.com')
        def input  = page.forms[0].getInputByName('q')
        input.valueAttribute = 'Groovy'
        page       = page.forms[0].submit()
        def hits   = page.anchors.grep { it.classAttribute == 'l' } [0..2]
        //hits.each  { println it.hrefAttribute.padRight(30) + ' : ' + it.asText() }
        hits.each  { assert it.asText().toUpperCase().toString().contains('GROOVY') };
   }
    

   
}

