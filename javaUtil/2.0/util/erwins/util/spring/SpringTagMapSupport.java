package erwins.util.spring;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;

import erwins.util.collections.AbstractMapSupport;


/** 
 * EL에서 MAP 형식으로 HTML을 구성하도록 도와주는 헬퍼
 * */ 
public abstract class SpringTagMapSupport extends AbstractMapSupport<String,Map<String,String>>{
	
	protected Splitter splitter = Splitter.on('|').omitEmptyStrings().trimResults();
	protected Map<String,Map<String,String>> springTag = new ConcurrentHashMap<String, Map<String,String>>();
	protected Map<String,String> ALL = ImmutableMap.of("ALL","전체");

}

