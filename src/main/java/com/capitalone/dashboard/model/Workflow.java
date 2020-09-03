package com.capitalone.dashboard.model;

import java.util.List;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "github_workflow")
public class Workflow extends CollectorItem{
	@Indexed(unique = true)
	private String workflowId;
	private String name;
	private String state;
	private String createdAt;
	private String updatedAt;
	private Boolean enabled;
		
	public Workflow(String workflowId, String name, String state, Boolean enabled) {
		this.workflowId = workflowId;
		this.name = name;
		this.state = state;
		this.enabled = enabled;
	}
	
	public Workflow(String workflowId, String name, String state, Boolean enabled,
			String createdAt, String updatedAt) {
		this.workflowId = workflowId;
		this.name = name;
		this.state = state;
		this.enabled = enabled;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public String getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}
		
}
