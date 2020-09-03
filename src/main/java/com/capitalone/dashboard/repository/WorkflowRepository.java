package com.capitalone.dashboard.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.capitalone.dashboard.model.Workflow;

public interface WorkflowRepository<T extends Workflow> extends CrudRepository<T, ObjectId> {

	public boolean exists(String workflowId);
	
	@Query(value = "{'enabled' : ?0}")
	public List<Workflow> findEnabledWorkflows(Boolean enabled);
	
	@Query(value = "{'workflowId' : ?0}")
	public Workflow findByWorkflowId(String workflowId);
 
}
