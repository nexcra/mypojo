package erwins.util.openApi

/** 낄낄~ 깔끔하구나. 대충 아래의 구조
 * <?xml version="1.0"?>
<xml_api_reply version="1">
<weather module_id="0" tab_id="0" mobile_row="0" mobile_zipped="1" row="0" section="0" >
	<forecast_information>
		<city data="seoul"/>
		<postal_code data="seoul"/>
		<latitude_e6 data=""/>
		<longitude_e6 data=""/>
		<forecast_date data="2011-07-14"/>
		<current_date_time data="2011-07-14 07:59:54 +0000"/>
		<unit_system data="SI"/>
		</forecast_information>
		<current_conditions>
			<condition data="흐림"/>
			<temp_f data="75"/>
			<temp_c data="24"/>
			<humidity data="습도: 89%"/>
			<icon data="/ig/images/weather/cloudy.gif"/>
			<wind_condition data="바람: 북동풍, 3 km/h"/>
		</current_conditions>
		<forecast_conditions>
			<day_of_week data="목"/>
			<low data="23"/>
			<high data="24"/>
			<icon data="/ig/images/weather/thunderstorm.gif"/>
			<condition data="강우(천둥, 번개 동반)"/>
			</forecast_conditions>
		<forecast_conditions>
			~~~
		</forecast_conditions>
	</weather>
</xml_api_reply>*/
public class GoogleXmlParser{
    
    private static final String url = 'http://www.google.co.kr';
    
    private final Node plan;

    public GoogleXmlParser(String xml){
        plan = new XmlParser().parseText(xml);
    }
	
	def static rains = ['비','우']
	
	public isRain(){
		def casts =  plan.weather[0].forecast_conditions.collect { [ weather : it.condition[0].@data,dayOfWeek:it.day_of_week[0].@data]  }
		return casts.findAll { rains.any{ a -> it.weather.contains(a) }   }.collect { "$it.dayOfWeek : $it.weather"  }.join('\r')
	}
    
    /** 오늘부터 4일간의 간략 일기예보를 리턴한다. */
    public String[] simpleCast(){
        return plan.weather[0].forecast_conditions.collect{"${it.day_of_week[0].@data}:${it.condition[0].@data} (${it.low[0].@data}~${it.high[0].@data})"};
    }
    
    /** HTML로된 Table을 리턴한다. */
    public String buildHtmlTable(){
        def weather = plan.weather[0];
        def current = weather.current_conditions[0];
        def info = weather.forecast_information[0];
        
        def writer = new StringWriter()
        def builder = new groovy.xml.MarkupBuilder(writer)
        
        builder.table(class:'box'){
            tr{
                td(colspan:5,"${info.city[0].@data} , 측정시각 : ${info.current_date_time[0].@data.substring(11,11+5)}")
            }
            tr{
                td '지금';
                weather.forecast_conditions.each{con -> //내부로 감싸면 it은 null이 된다.
                    td con.day_of_week[0].@data
                }
            }
            tr{
                td{
                    img(src:"${url}${current.icon[0].@data}")
                }
                weather.forecast_conditions.each{con -> //내부로 감싸면 it은 null이 된다.
                    td{
                        img(src:"${url}${con.icon[0].@data}")
                    }
                }
            }
            tr{
                td weather.current_conditions[0].condition[0].@data
                weather.forecast_conditions.each{con ->
                    td con.condition[0].@data
                }
            }
            tr{
                td(colspan:5,"${current.humidity[0].@data} ${current.wind_condition[0].@data}")
            }
        }
        return writer.toString();
    }
    
}




