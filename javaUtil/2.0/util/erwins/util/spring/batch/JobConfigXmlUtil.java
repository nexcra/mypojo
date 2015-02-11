package erwins.util.spring.batch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.w3c.dom.Element;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import erwins.util.lib.FileUtil;
import erwins.util.text.StringUtil;
import erwins.util.xml.XmlParseUtil;

/**
 * 스프링배치 컨피그파일을 읽어서 배치와 스탭의 이름(ID)/설명 문구를 가져온다.
 * @author sin
 */
public class JobConfigXmlUtil {
    
    private XmlParseUtil xmlParser = new XmlParseUtil();
    private Map<String,List<BatchStep>> flowMap = new HashMap<String,List<BatchStep>>();
    
    @Test
    public void test() throws IOException{
    	File xmlFile = new File("C:/DATA/PROJECT/workspaceMacpert2/ocjobrunner2/src/com/openclick/ocjobrunner/config/job.xml");
        List<BatchJob> values = new JobConfigXmlUtil().readXmlToBatchJob(xmlFile);
        for(BatchJob batchJob :  values){
        	System.out.println("========== " + batchJob.getJobName() + " (" + batchJob.getJobDescription() + ") ============");
        	for(int i=0;i<batchJob.getBatchSteps().size();i++){
        		BatchStep step = batchJob.getBatchSteps().get(i);
        		System.out.println(StringUtil.leftPad(i+1, 2) + " " + step.getStepDescription() + " (" + step.getStepDescription()+")");
        	}
        }
    }
    
    /** 
     * 해당 설정파일을 읽어서 BatchJob으로 변환해준다.
     * 변경된 파일을 읽어서 DB에 저장할때 사용된다
     * XML의 description -->> 한글이름으로 간주한다 
     *  */
    public List<BatchJob> readXmlToBatchJob(File xmlFile) throws IOException{
        
    	List<BatchJob> jobs = Lists.newArrayList();
        
        String xml = FileUtil.readFileToString(xmlFile,"UTF-8");
        List<Element> nodes = xmlParser.parseOnlyElement(xml);
        
        for(Element each : nodes){
            String nodeName = each.getNodeName();
            if(!nodeName.startsWith("batch")) continue;
            
            String jobId = each.getAttribute("id");
            String abstractVal = each.getAttribute("abstract");
            if(StringUtil.isEquals(abstractVal,"true")) continue;
            
            if(nodeName.equals("batch:flow")){
                List<BatchStep> batchSteps = elementToStep(each, jobId);
                flowMap.put(jobId, batchSteps);
            }else if(nodeName.equals("batch:job")){
                
                Element desc = xmlParser.getChildByName(each, "batch:description");
                String jobDescription = desc==null ? jobId : desc.getTextContent();
                
                BatchJob job = new BatchJob();
                job.setJobName(jobId);
                job.setJobDescription(jobDescription);
                
                List<BatchStep> batchSteps = elementToStep(each, jobId);
                Preconditions.checkState(batchSteps.size() != 0, "하나 이상의 step은 존재해야 한다. : " +jobId);
                job.setBatchSteps(batchSteps);
                for(BatchStep batchStep : batchSteps) batchStep.setBatchJob(job);  
                jobs.add(job);
            }
        }
        
        //스텝 1개인 애는 생락(스텝 설명문구를 잡 설명문구로 대체) 가능. 
        for(BatchJob job : jobs){
            List<BatchStep> steps =  job.getBatchSteps();
            if(steps.size()==1){
                BatchStep singleStep = steps.get(0);
                if(singleStep.getStepName().equals(singleStep.getStepDescription())) {
                	singleStep.setStepDescription(job.getJobDescription());
                }
            }
        }
        
        return jobs;
    }

    private List<BatchStep> elementToStep(Element each, String jobId) {
        
        List<BatchStep> batchSteps = new ArrayList<BatchStep>();
        //int sortOrder = 0;
        List<Element> children = xmlParser.getChildrenByName(each, "batch:step","batch:flow");
        for(Element child : children){
            String stepId = child.getAttribute("id");
            
            String tagName = child.getTagName();
            if(tagName.equals("batch:step")){
                Element eachDesc = xmlParser.getChildByName(child, "batch:description");
                String stepDescription = eachDesc==null ? stepId : eachDesc.getTextContent();
                
                BatchStep step = new BatchStep();
                step.setStepName(stepId);
                step.setStepDescription(stepDescription);
                //step.setSortOrder(sortOrder++);
                batchSteps.add(step);
            }else{
                String parent = child.getAttribute("parent");
                List<BatchStep> stepInFlow = flowMap.get(parent);
                Preconditions.checkState(stepInFlow != null, "선행되는 flow id가 없습니다. 선행 flow를 앞에 두세요 : " +parent);
                batchSteps.addAll(stepInFlow);
            }
        }
        return batchSteps;
    }

}