package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.ComponentData;

public interface WorkflowService {
	
	 ComponentData getWorkflowMetaCount();
	
	 ComponentData getWorkflowMetaData();
	
	 ComponentData getWorkflowStats(); 
		
	 ComponentData getEnabledWorkflows(); 
		
	 ComponentData getRunsByWorkflowId(String workflowId); 
		
	 ComponentData getJobByJobId(String jobId); 
		
	 ComponentData getJobsByRunId(String runId);
	 
	 ComponentData getDistinctRunStatusByWorkflow(String workflowId);

}
