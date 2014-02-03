
package erwins.test.runAndTakeResult;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import oracle.sql.TIMESTAMP;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;

import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.Sets;
import com.google.common.collect.TreeRangeMap;

import erwins.util.dateTime.JodaUtil.Joda;
import erwins.util.lib.CollectionUtil;
import erwins.util.vender.apache.Poi;

public class StrongBatchDay{
    
	@SuppressWarnings("unchecked")
	@Test
    public void test() throws SQLException{
    	
    	Map<String,Object> empty = Maps.newHashMap();
    	empty.put("JOB_NAME_KR", "");
    	
        String basicDate = "20140116";
        String file = "C:/DATA/download/batch_"+basicDate+".xls";
        Poi p = new Poi();
        
        RangeMap<Long,Integer> rm = TreeRangeMap.create();
        MutableDateTime time =  Joda.YMD.get(basicDate).toMutableDateTime();
        DateTime end = Joda.YMD.get(basicDate).plusDays(1);
        
        int intervalMin = 10;
        List<String> headers1 = Lists.newArrayList();
        List<String> headers2 = Lists.newArrayList();
        while(time.isBefore(end)){
        	DateTime before = time.toDateTime();
    		time.addMinutes(intervalMin);
    		Range<Long> range = Range.closedOpen(before.getMillis(), time.getMillis());
    		headers1.add(before.toString(HH));
    		headers2.add(before.toString(mm) +" ~ " +time.toDateTime().minusMinutes(1).toString(mm));
    		rm.put(range,headers1.size());
        }
        
        p.addSheet("jobState", headers1.toArray(new String[headers1.size()]),headers2.toArray(new String[headers2.size()]));
        
        Multimap<Integer,Map<String,Object>> multiMap = ArrayListMultimap.create();
        //DB();
        //운영_잡러너.l
        //List<Object> list =  (List<Object>) 운영_잡러너.list("SELECT c.JOB_NAME_KR,c.JOB_NAME,START_TIME,END_TIME,STATUS FROM BATCH_JOB_EXECUTION a  JOIN BATCH_JOB_INSTANCE b ON a.JOB_INSTANCE_ID = b.JOB_INSTANCE_ID JOIN JOB_STATUS c ON b.JOB_NAME = c.JOB_NAME WHERE START_TIME BETWEEN TO_DATE('"+basicDate+"','yyyyMMdd') AND TO_DATE('"+basicDate+"','yyyyMMdd')+1 AND JOB_PERIOD = '일' AND JOB_TYPE = 'ocjobrunner'");
        List<Object> list =  null; 
        for(Object each : list){
        	Map<String,Object> resultSet = (Map<String,Object>)each;
        	TIMESTAMP startTime = (TIMESTAMP) resultSet.get("START_TIME");
        	TIMESTAMP endTime = (TIMESTAMP) resultSet.get("END_TIME");
        	Range<Long> batchRange = Range.closedOpen(startTime.timestampValue().getTime(), endTime.timestampValue().getTime());
        	RangeMap<Long,Integer> subMap = rm.subRangeMap(batchRange);
        	TreeSet<Integer> minMax = Sets.newTreeSet(subMap.asMapOfRanges().values());
        	resultSet.put("first", minMax.first());
        	resultSet.put("last", minMax.last());
        	for(Integer order : subMap.asMapOfRanges().values()) multiMap.put(order, resultSet);
        }
        
        List<String[]> lines = Lists.newArrayList();
        List<Map<String,Object>> before =  Lists.newArrayList();
        TreeSet<Integer> set = Sets.newTreeSet(multiMap.asMap().keySet());
        for(Integer col : set){
        	List<Map<String,Object>> datas = (List<Map<String,Object>>) multiMap.get(col);
        	List<Map<String,Object>> currentResult = CollectionUtil.reOrderLikeBefore(before,datas,empty);
        	for(int i=0;i<currentResult.size();i++){
        		Map<String,Object> data = currentResult.get(i);
        		String text = data.get("JOB_NAME_KR").toString();
        		if(data.get("first") != data.get("last")){
        			if(col == data.get("first")) {
        				text += " <"+mmss(data,"START_TIME") + " 시작>";
        			}
        			if(col == data.get("last")){
        				text += " <"+mmss(data,"END_TIME") + " 종료>";
        			}
        		}else{
        			if(!Strings.isNullOrEmpty(text)) text += " <"+ mmss(data,"START_TIME") + "~" + mmss(data,"END_TIME")+ ">";
        		}
        		add(lines, headers1.size(), i, col, text);
        	}
        	before = currentResult;
        }
        
        for(String[] each : lines) p.addValuesArray(each);
        p.getMerge(0).setAbleRow(0).setAbleCol().merge();
        p.wrap().write(file);
        
    }
    
    public static String mmss(Map<String,Object> data,String key) throws SQLException{
    	TIMESTAMP time = (TIMESTAMP)data.get(key);    	
    	if(time==null) {
    		Object STATUS = data.get("STATUS");
    		if(STATUS==null) return ""; //더미데이터
    		return STATUS.toString();
    	}
    	return new DateTime(time.timestampValue().getTime()).toString(mmss);
    }
    
    private static void add(List<String[]> lines,int max, Integer row,Integer col,String data){
    	if(lines.size() <= row){
    		String[] line = new String[max];
            for(int i=0;i<line.length;i++) line[i] = "";
            lines.add(line);
    	}
    	lines.get(row)[col-1] = data;
    }
    
    public static DateTimeFormatter HH = DateTimeFormat.forPattern("HH시");
    public static DateTimeFormatter mm = DateTimeFormat.forPattern("mm분");
    public static DateTimeFormatter mmss = DateTimeFormat.forPattern("mm분ss초");
	
}