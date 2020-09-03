package com.capitalone.dashboard.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class WorkflowRunTest {
	
	private String workflowId = "8675309";
	private String runId = "001";
	private String event = "build";
	private String status = "completed";
	private String conclusion = "success";
	private String createdAt = "17/08/1956 12:01";
	private String updatedAt = "17/08/2020 12:01";

	private String workflowId2 = "8675310";
	private String runId2 = "002";
	private String event2 = "build2";
	private String status2 = "queued";
	private String conclusion2 = "neutral";
	private String createdAt2 = "14/08/2020 12:01";
	private String updatedAt2 = "14/08/2020 14:01";

	@Test
	public void testConstructor1() {
		WorkflowRun wfr = new WorkflowRun(workflowId, runId, status, conclusion);
		assertEquals(workflowId,wfr.getWorkflowId());
		assertEquals(runId,wfr.getRunId());
		assertEquals(status,wfr.getStatus());
		assertEquals(conclusion,wfr.getConclusion());
		assertNull(wfr.getCreatedAt());
		assertNull(wfr.getUpdatedAt());
	}

	@Test
	public void testConstructor2WithSetters() {
		WorkflowRun wfr = new WorkflowRun(workflowId, runId, status, conclusion, event, createdAt, updatedAt);
		assertEquals(workflowId,wfr.getWorkflowId());
		assertEquals(runId,wfr.getRunId());
		assertEquals(status,wfr.getStatus());
		assertEquals(conclusion,wfr.getConclusion());
		assertEquals(createdAt,wfr.getCreatedAt());
		assertEquals(updatedAt,wfr.getUpdatedAt());
		assertEquals(event,wfr.getEvent());
		
		wfr.setWorkflowId(workflowId2);
		wfr.setRunId(runId2);
		wfr.setStatus(status2);
		wfr.setConclusion(conclusion2);
		wfr.setEvent(event2);
		wfr.setCreatedAt(createdAt2);
		wfr.setUpdatedAt(updatedAt2);
		assertEquals(workflowId2,wfr.getWorkflowId());
		assertEquals(runId2,wfr.getRunId());
		assertEquals(status2,wfr.getStatus());
		assertEquals(conclusion2,wfr.getConclusion());
		assertEquals(createdAt2,wfr.getCreatedAt());
		assertEquals(updatedAt2,wfr.getUpdatedAt());
		assertEquals(event2,wfr.getEvent());
	}

}
