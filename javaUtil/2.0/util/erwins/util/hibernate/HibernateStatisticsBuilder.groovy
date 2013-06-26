package erwins.util.hibernate


import org.hibernate.stat.EntityStatistics 
import org.hibernate.stat.QueryStatistics 
import org.hibernate.stat.SecondLevelCacheStatistics 
import org.hibernate.stat.Statistics;

/** XML빌더.. 좀 구리다. */
public class HibernateStatisticsBuilder{

	private Statistics stats;
	
	def writer = new StringWriter()
	def builder = new groovy.xml.MarkupBuilder(writer);
	
	String css = "";
	
	public HibernateStatisticsBuilder(Statistics stats){
		this.stats = stats;
	}

    public String build(){
		builder.h1 '전체 현황'
		/*double queryCacheHitRatio = queryCacheHitCount / (queryCacheHitCount + queryCacheMissCount); ???? */
		builder.table(class:css){
			thead{
				tr{
					th "Load"
					th "Fetch"
					th "Insert"
					th "Update"
					th "Delete"
					th "SessionOpen"
					th "SessionClose"
					th "TransactionCount"
					th "2nd HitRate"
					th "Query HitRate"
				}	
			}
			tbody{
				tr{
					td stats.getEntityLoadCount()
					td stats.getEntityFetchCount()
					td stats.getEntityInsertCount()
					td stats.getEntityUpdateCount()
					td stats.getEntityDeleteCount()
					td stats.getSessionOpenCount()
					td stats.getSessionCloseCount()
					td stats.getTransactionCount()
					td stats.getSecondLevelCacheHitCount()==0 ? 0 : stats.getSecondLevelCacheHitCount() / stats.getSecondLevelCachePutCount();
					td stats.getQueryCacheHitCount()==0 ? 0 : stats.getQueryCacheHitCount() /  stats.getQueryCacheHitCount() ;
				}	
			}
        }
		builder.h1 '개별 Entity 현황'
		builder.table(class:css){
			thead{
				tr{
					th "이름"
					th "Load"
					th "Fetch"
					th "Insert"
					th "Update"
					th "Delete"
					
				}	
			}
			tbody{
				stats.getEntityNames().each { entityName ->
					EntityStatistics e = stats.getEntityStatistics(entityName);
					tr{
						td entityName.toString()
						td e.getLoadCount()
						td e.getFetchCount()
						td e.getInsertCount()
						td e.getUpdateCount()
						td e.getDeleteCount()
					}
				}
			}
        }

		builder.h1 '개별 2nd Cache 현황'
		builder.table(class:css){
			thead{
				tr{
					th "이름"
					th "Put"
					th "Hit"
					th "Miss"
					th "HitRate"
				}	
			}
			tbody{
				stats.getSecondLevelCacheRegionNames().each { regionName ->
					SecondLevelCacheStatistics e = stats.getSecondLevelCacheStatistics(regionName);
					tr{
						td e.getCategoryName()
						td e.getPutCount()
						td e.getHitCount()
						td e.getMissCount()
						td (e.getHitCount()!=0 ? e.getHitCount() / e.getPutCount() : 0)
					}
				}
			}
        }

		builder.h1 '개별 Query 현황'
		builder.table(class:css){
			thead{
				tr{
					th "Query"
					th "누적 Execute"
					th "누적 Row"
					th "평균실행시간(min/max)"
					th "Put"
					th "Hit"
					th "Miss"
				}
			}
			tbody{
				stats.getQueries().each { queryName ->
					QueryStatistics e = stats.getQueryStatistics(queryName);
					tr{
						td e.getCategoryName()
						td e.getExecutionCount()
						td e.getExecutionRowCount()
						td e.getExecutionAvgTime() + "(" + e.getExecutionMinTime() + "/" + e.getExecutionMaxTime() + ")"
						td e.getCachePutCount()
						td e.getCacheHitCount()
						td e.getCacheMissCount()
					}
				}
			}
        }
		return writer.toString();
   }
   
} 
