package com.capitalone.dashboard.rest;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.capitalone.dashboard.model.ComponentData;
import com.capitalone.dashboard.service.WorkflowServiceImpl;

@RestController
@RequestMapping("/collector/workflows")
public class WorkflowController {
	private static final Log LOG = LogFactory.getLog(WorkflowController.class);

	private final WorkflowServiceImpl workflowService;

	@Autowired
	public WorkflowController(WorkflowServiceImpl workflowService) {
		this.workflowService = workflowService;
	}

	@RequestMapping(value = "/meta/count", method = GET, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<ComponentData> getWorkflowMetaCount() {
		LOG.debug("Call Recieved @ /collector/workflows/meta/count :: ");
		return ResponseEntity.ok(workflowService.getWorkflowMetaCount());
	}
	
	@RequestMapping(value = "/meta/data", method = GET, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<ComponentData> getWorkflowMetaData() {
		LOG.debug("Call Recieved @ /collector/workflows/meta/data :: ");
		return ResponseEntity.ok(workflowService.getWorkflowMetaData());
	}
	
	@RequestMapping(value = "/stats", method = GET, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<ComponentData> getWorkflowStats() {
		LOG.debug("Call Recieved @ /collector/workflows/stats :: ");
		return ResponseEntity.ok(workflowService.getWorkflowStats());
	}
		
	@RequestMapping(value = "/enabled", method = GET, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<ComponentData> getEnabledWorkflows() {
		LOG.debug("Call Recieved @ /collector/workflows/enabled :: ");
		return ResponseEntity.ok(workflowService.getEnabledWorkflows());
	}

	@RequestMapping(value = "/{workflowId}/runs", method = GET, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<ComponentData> getRunsByWorkflowId(@PathVariable("workflowId") String workflowId) {
		LOG.debug("Call Recieved @ /collector/workflows/"+workflowId+"/runs :: ");
		return ResponseEntity.ok(workflowService.getRunsByWorkflowId(workflowId));
	}
	  
	@RequestMapping(value = "/jobs/{jobId}", method = GET, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<ComponentData> getJobByJobId(@PathVariable("jobId") String jobId) {
		LOG.debug("Call Recieved @ /collector/workflows/jobs/"+jobId+" :: ");
		return ResponseEntity.ok(workflowService.getJobByJobId(jobId));
	}
	  
	@RequestMapping(value = "/runs/{runId}/jobs", method = GET, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<ComponentData> getJobsByRunId(@PathVariable("runId") String runId) {
		LOG.debug("Call Recieved @ /collector/workflows/runs/"+runId+"/jobs :: ");
		return ResponseEntity.ok(workflowService.getJobsByRunId(runId));
	}
	  
	@RequestMapping(value = "/{workflowId}/aggregates/runs/status", method = GET, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<ComponentData> getDistinctRunStatusByWorkflow(@PathVariable("workflowId") String workflowId) {
		LOG.debug("Call Recieved @ /collector/workflows/"+workflowId+"/aggregates/runs/status/ :: ");
		return ResponseEntity.ok(workflowService.getDistinctRunStatusByWorkflow(workflowId));
	}
	
}
