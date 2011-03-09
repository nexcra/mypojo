package erwins.util.openApi

import org.junit.Test

/** 낄낄~ 깔끔하구나. */
public class GoogleXmlParser{
    
    private static final String url = 'http://www.google.co.kr';
    
    private final Node plan;

    public GoogleXmlParser(String xml){
        plan = new XmlParser().parseText(xml);
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




