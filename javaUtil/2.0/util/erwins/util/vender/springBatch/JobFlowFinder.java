package erwins.util.vender.springBatch;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.set.ListOrderedSet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.State;
import org.springframework.batch.core.job.flow.support.StateTransition;
import org.springframework.batch.core.job.flow.support.state.DecisionState;
import org.springframework.batch.core.job.flow.support.state.FlowState;
import org.springframework.batch.core.job.flow.support.state.StepState;

import com.google.common.collect.Maps;

import erwins.util.lib.ReflectionUtil;
import erwins.util.lib.StringUtil;

/**
 * 배치와, 스템간의 연결관계를 만들어준다.
 * 여기에 적절한 한글 이름을 달아주면 된다.
 */
public class JobFlowFinder {
    
	@Resource private Map<String,Job> jobs;
	@Resource private Map<String,Flow> flows;
    
    private Map<String,ListOrderedSet> flowMap = Maps.newConcurrentMap();
    private Map<String,ListOrderedSet> jobMap = Maps.newConcurrentMap();
    
    public Map<String,ListOrderedSet> find(){
    	flowMap = Maps.newConcurrentMap();
    	jobMap = Maps.newConcurrentMap();
    	for(Flow flow : flows.values()){
    		ListOrderedSet set = flowSet(flow);
    		flowMap.put(flow.getName(), set);
    	}
    	for(Job job : jobs.values()){
    		Flow flow = ReflectionUtil.findFieldValue(job, "flow");
    		ListOrderedSet set = flowSet(flow);
    		ListOrderedSet newSet = new ListOrderedSet();
    		for(Object key : set){
    			ListOrderedSet flowSteps = flowMap.get(key);
    			if(flowSteps==null) newSet.add(key);
    			else for(Object flowStep : flowSteps) newSet.add(flowStep);
    		}
    		jobMap.put(job.getName(), newSet);
    	}
    	return jobMap;
    }

	private ListOrderedSet flowSet(Flow flow) {
		ListOrderedSet set  = new ListOrderedSet();
		List<StateTransition> stateTranslations = ReflectionUtil.findFieldValue(flow, "stateTransitions");
		for(StateTransition each : stateTranslations){
			State state = each.getState();
			if(state instanceof StepState){
				StepState stepState = (StepState) state;
				set.add(stepState.getStep().getName());
			}else{
				String stateName = state.getName();
				String[] names = StringUtil.getExtentions(stateName);
				if(StringUtil.isStartsWithAny(names[1],"fail","end")) continue; //나머지는 이제 다 플로우이다.
				
				State innerState = ReflectionUtil.findFieldValue(state, "state");
				if(innerState instanceof FlowState){
					FlowState flowState = (FlowState) innerState;
					for(Flow innerFlow : flowState.getFlows()){
						ListOrderedSet innerSet = flowSet(innerFlow);
						for(Object key : innerSet) set.add(key);
					}	
				}else if(innerState instanceof DecisionState){
					//무시
				}else{
					throw new RuntimeException("알려지지 않은 state" + innerState.getClass());
				}
			}
			
		}
		return set;
	}
    

}
