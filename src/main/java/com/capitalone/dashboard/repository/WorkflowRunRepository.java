
package com.capitalone.dashboard.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.capitalone.dashboard.model.WorkflowRun;

public interface WorkflowRunRepository<T extends WorkflowRun> extends CrudRepository<T, ObjectId> {

	@Query(value = "{'runId': ?0}")
	public WorkflowRun findByRunId(String runId);

	@Query(value = "{'workflowId': ?0}")
	public List<WorkflowRun> findByWorkflowId(String workflowId);

}
