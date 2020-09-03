package com.capitalone.dashboard.model;

	import java.util.List;

import org.bson.types.ObjectId;
	import org.springframework.data.mongodb.core.index.Indexed;
	import org.springframework.data.mongodb.core.mapping.Document;

	/*Model to store a GitHub Workflow Run Job Data*/

	@Document(collection="github_workflow_run_job")
	public class WorkflowRunJob extends BaseModel{
		@Indexed(unique = true)
		private String workflowId;
		private String runId;
		private String jobId;
		private String status;
		private String conclusion;
		private String startedAt;
		private String completedAt;
		private String name;
		private List<WorkflowRunJobStep> workflowRunJobSteps;
		
		public WorkflowRunJob(String workflowId, String runId, String jobId,
				String status, String conclusion, String startedAt, String completedAt,
				String name) {
			this.workflowId = workflowId;
			this.runId = runId;
			this.jobId = jobId;
			this.status = status;
			this.conclusion = conclusion;
			this.startedAt = startedAt;
			this.completedAt = completedAt;
			this.name = name;
		}

		public String getWorkflowId() {
			return workflowId;
		}

		public void setWorkflowId(String workflowId) {
			this.workflowId = workflowId;
		}

		public String getRunId() {
			return runId;
		}

		public void setRunId(String runId) {
			this.runId = runId;
		}

		public String getJobId() {
			return jobId;
		}

		public void setJobId(String jobId) {
			this.jobId = jobId;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getConclusion() {
			return conclusion;
		}

		public void setConclusion(String conclusion) {
			this.conclusion = conclusion;
		}

		public String getStartedAt() {
			return startedAt;
		}

		public void setStartedAt(String startedAt) {
			this.startedAt = startedAt;
		}

		public String getCompletedAt() {
			return completedAt;
		}

		public void setCompletedAt(String completedAt) {
			this.completedAt = completedAt;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public List<WorkflowRunJobStep> getWorkflowRunJobSteps() {
			return workflowRunJobSteps;
		}

		public void setWorkflowRunJobSteps(List<WorkflowRunJobStep> workflowRunJobSteps) {
			this.workflowRunJobSteps = workflowRunJobSteps;
		}

}
