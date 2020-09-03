
package com.capitalone.dashboard.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.capitalone.dashboard.model.WorkflowRunJob;

public interface WorkflowRunJobRepository<T extends WorkflowRunJob> extends CrudRepository<T, ObjectId> {

	@Query(value = "{'jobId': ?0}")
	public WorkflowRunJob findByJobId(String jobId);

	@Query(value = "{'runId': ?0}")
	public List<WorkflowRunJob> findByRunId(String runId);

}
