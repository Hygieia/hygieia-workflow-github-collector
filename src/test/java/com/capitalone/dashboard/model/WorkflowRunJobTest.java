package com.capitalone.dashboard.model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class WorkflowRunJobTest {
	
	private String workflowId = "8675309";
	private String runId = "001";
	private String jobId = "001";
	private String name = "build";
	private String status = "completed";
	private String conclusion = "success";
	private String startedAt = "17/08/1956 12:01";
	private String completedAt = "17/08/2020 12:01";

	private String workflowId2 = "8675310";
	private String runId2 = "002";
	private String jobId2 = "002";
	private String name2 = "build2";
	private String status2 = "queued";
	private String conclusion2 = "neutral";
	private String startedAt2 = "14/08/2020 12:01";
	private String completedAt2 = "14/08/2020 14:01";

	private List<WorkflowRunJobStep> steps;
	private WorkflowRunJobStep step1;
	private WorkflowRunJobStep step2;
	private WorkflowRunJobStep step3;
	private String stepNumber = "001";
	private String stepNumber2 = "002";
	
	@Before
	public void setup() {
		steps = new ArrayList<WorkflowRunJobStep>();
		
		step1 = new WorkflowRunJobStep(workflowId,runId,jobId,stepNumber,status,conclusion,startedAt,completedAt,name);
		steps.add(step1);

		step2 = new WorkflowRunJobStep(workflowId,runId,jobId,stepNumber,status,conclusion,startedAt,completedAt,name);
		steps.add(step2);

		step3 = new WorkflowRunJobStep(workflowId,runId,jobId,stepNumber,status,conclusion,startedAt,completedAt,name);

		step3.setWorkflowId(workflowId2);
		step3.setRunId(runId2);
		step3.setJobId(jobId2);
		step3.setStepNumber(stepNumber2);
		step3.setStatus(status2);
		step3.setConclusion(conclusion2);
		step3.setName(name2);
		step3.setStartedAt(startedAt2);
		step3.setCompletedAt(completedAt2);
		steps.add(step3);
	}

	@Test
	public void testConstructorWithSetters() {
		WorkflowRunJob wfrj = new WorkflowRunJob(workflowId, runId, jobId, status, conclusion, startedAt, completedAt, name);
		assertEquals(workflowId,wfrj.getWorkflowId());
		assertEquals(runId,wfrj.getRunId());
		assertEquals(runId,wfrj.getJobId());
		assertEquals(status,wfrj.getStatus());
		assertEquals(conclusion,wfrj.getConclusion());
		assertEquals(startedAt,wfrj.getStartedAt());
		assertEquals(completedAt,wfrj.getCompletedAt());
		assertEquals(name,wfrj.getName());

		wfrj.setWorkflowId(workflowId2);
		wfrj.setRunId(runId2);
		wfrj.setJobId(jobId2);
		wfrj.setStatus(status2);
		wfrj.setConclusion(conclusion2);
		wfrj.setName(name2);
		wfrj.setStartedAt(startedAt2);
		wfrj.setCompletedAt(completedAt2);
		assertEquals(workflowId2,wfrj.getWorkflowId());
		assertEquals(runId2,wfrj.getRunId());
		assertEquals(name2,wfrj.getName());
		assertEquals(status2,wfrj.getStatus());
		assertEquals(conclusion2,wfrj.getConclusion());
		assertEquals(startedAt2,wfrj.getStartedAt());
		assertEquals(completedAt2,wfrj.getCompletedAt());
		
		wfrj.setWorkflowRunJobSteps(steps);
		List<WorkflowRunJobStep> steps1 = wfrj.getWorkflowRunJobSteps();
		
		int i = 0;
		WorkflowRunJobStep stepOne = null;
		WorkflowRunJobStep stepTwo = null;
		for (WorkflowRunJobStep step : steps1) {
			if (i == 0) {
				stepOne = step;
			}
			else if (i == 1) {
				stepTwo = step;
				assertEquals(stepOne.getWorkflowId(),stepTwo.getWorkflowId());
				assertEquals(stepOne.getRunId(),stepTwo.getRunId());
				assertEquals(stepOne.getJobId(),stepTwo.getJobId());
				assertEquals(stepOne.getStepNumber(),stepTwo.getStepNumber());
				assertEquals(stepOne.getStatus(),stepTwo.getStatus());
				assertEquals(stepOne.getConclusion(),stepTwo.getConclusion());
				assertEquals(stepOne.getStartedAt(),stepTwo.getStartedAt());
				assertEquals(stepOne.getCompletedAt(),stepTwo.getCompletedAt());
				assertEquals(stepOne.getName(),stepTwo.getName());
			}
			else if (i == 2) {
				stepTwo = step;
				assertNotEquals(stepOne.getWorkflowId(),stepTwo.getWorkflowId());
				assertNotEquals(stepOne.getRunId(),stepTwo.getRunId());
				assertNotEquals(stepOne.getJobId(),stepTwo.getJobId());
				assertNotEquals(stepOne.getStepNumber(),stepTwo.getStepNumber());
				assertNotEquals(stepOne.getStatus(),stepTwo.getStatus());
				assertNotEquals(stepOne.getConclusion(),stepTwo.getConclusion());
				assertNotEquals(stepOne.getStartedAt(),stepTwo.getStartedAt());
				assertNotEquals(stepOne.getCompletedAt(),stepTwo.getCompletedAt());
				assertNotEquals(stepOne.getName(),stepTwo.getName());
			}
			i++;
		}

	}

}
