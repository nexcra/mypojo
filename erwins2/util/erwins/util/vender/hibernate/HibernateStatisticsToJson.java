package erwins.util.vender.hibernate;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.hibernate.stat.EntityStatistics;
import org.hibernate.stat.QueryStatistics;
import org.hibernate.stat.SecondLevelCacheStatistics;
import org.hibernate.stat.Statistics;

import erwins.util.morph.BeanToJson;

/** 연결된 Flex와 같이 사용하자. */
public class HibernateStatisticsToJson{
	
	public static JSONObject hibernateStatistics(Statistics stats) {
		JSONObject result = new JSONObject();
		result.put("summary", summary(stats));
		result.put("eachEntity", eachEntity(stats));
		result.put("each2ndCache", each2ndCache(stats));
		result.put("eachQuery", eachQuery(stats));
		return result;
	}
	
	public static JSONArray eachQuery(Statistics stats) {
		JSONArray array = new JSONArray();
		for(String eachName : stats.getQueries()){
			QueryStatistics each = stats.getQueryStatistics(eachName);
			JSONObject json = BeanToJson.create().getByDomain(each, false);
			
			if(each.getCacheHitCount()==0) json.put("cacheHitRate", 0);
			else{
				double sum =  each.getCacheHitCount() + each.getCacheMissCount();
				json.put("cacheHitRate", each.getCacheHitCount() / sum * 100);
			}
			
			array.add(json);
		}
		return array;
	}
	
	public static JSONArray each2ndCache(Statistics stats) {
		JSONArray array = new JSONArray();
		for(String eachName : stats.getSecondLevelCacheRegionNames()){
			SecondLevelCacheStatistics each = stats.getSecondLevelCacheStatistics(eachName);
			JSONObject json = BeanToJson.create().getByDomain(each, false);
			
			if(each.getHitCount()==0) json.put("hitRate",0);
			else{
				double sum =  each.getHitCount() + each.getMissCount();	
				json.put("hitRate",each.getHitCount() / sum * 100);
			}
			
			array.add(json);
		}
		return array;
	}
	
	public static JSONArray eachEntity(Statistics stats) {
		JSONArray array = new JSONArray();
		for(String eachName : stats.getEntityNames()){
			EntityStatistics each = stats.getEntityStatistics(eachName);
			array.add(BeanToJson.create().getByDomain(each, false));
		}
		return array;
	}		

	/** 1개의 로우만을 가진다. */
	public static JSONArray summary(Statistics stats) {
		JSONArray summary = new JSONArray();
		JSONObject json = BeanToJson.create().getByDomain(stats, false);
		
		if(stats.getSecondLevelCacheHitCount()==0) json.put("secondLevelCacheHitRate", 0);
		else{
			double sum = stats.getSecondLevelCacheHitCount() + stats.getSecondLevelCacheMissCount();
			json.put("secondLevelCacheHitRate", stats.getSecondLevelCacheHitCount() / sum * 100 );
		}
		
		if(stats.getQueryCacheHitCount()==0) json.put("queryCacheHitRate", 0);
		else{
			double sum = stats.getQueryCacheHitCount() + stats.getQueryCacheMissCount();
			json.put("queryCacheHitRate", stats.getQueryCacheHitCount() / sum * 100);	
		}
		
		summary.add(json);
		return summary;
	}
}
