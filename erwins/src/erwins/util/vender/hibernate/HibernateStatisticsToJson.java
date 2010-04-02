package erwins.util.vender.hibernate;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.hibernate.stat.EntityStatistics;
import org.hibernate.stat.QueryStatistics;
import org.hibernate.stat.SecondLevelCacheStatistics;
import org.hibernate.stat.Statistics;

import erwins.util.morph.JDissolver;

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
			JSONObject json = JDissolver.instance().getByDomain(each, false);
			double hitRate =  each.getCacheHitCount()==0 ? 0 : each.getCacheHitCount() / each.getCachePutCount();
			json.put("cacheHitRate", hitRate);
			array.add(json);
		}
		return array;
	}
	
	public static JSONArray each2ndCache(Statistics stats) {
		JSONArray array = new JSONArray();
		for(String eachName : stats.getSecondLevelCacheRegionNames()){
			SecondLevelCacheStatistics each = stats.getSecondLevelCacheStatistics(eachName);
			JSONObject json = JDissolver.instance().getByDomain(each, false);
			double hitRate =  each.getHitCount()==0 ? 0 : each.getHitCount() / each.getPutCount();
			json.put("hitRate", hitRate);
			array.add(json);
		}
		return array;
	}
	
	public static JSONArray eachEntity(Statistics stats) {
		JSONArray array = new JSONArray();
		for(String eachName : stats.getEntityNames()){
			EntityStatistics each = stats.getEntityStatistics(eachName);
			array.add(JDissolver.instance().getByDomain(each, false));
		}
		return array;
	}		

	/** 1개의 로우만을 가진다. */
	public static JSONArray summary(Statistics stats) {
		JSONArray summary = new JSONArray();
		JSONObject json = JDissolver.instance().getByDomain(stats, false);
		double secondLevelCacheHitRate =  stats.getSecondLevelCacheHitCount()==0 ? 0 : stats.getSecondLevelCacheHitCount() / stats.getSecondLevelCachePutCount();
		json.put("secondLevelCacheHitRate", secondLevelCacheHitRate);
		double queryCacheHitRate =  stats.getQueryCacheHitCount()==0 ? 0 : stats.getQueryCacheHitCount() / stats.getQueryCachePutCount();
		json.put("queryCacheHitRate", queryCacheHitRate);
		summary.add(json);
		return summary;
	}
}
