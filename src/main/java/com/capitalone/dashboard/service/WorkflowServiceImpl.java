package com.capitalone.dashboard.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import com.capitalone.dashboard.collector.GitHubSettings;
import com.capitalone.dashboard.model.ComponentData;
import com.capitalone.dashboard.model.Series;
import com.capitalone.dashboard.model.Workflow;
import com.capitalone.dashboard.model.WorkflowRun;
import com.capitalone.dashboard.model.WorkflowRunJob;
import com.capitalone.dashboard.repository.WorkflowCustomRepository;
import com.capitalone.dashboard.repository.WorkflowRepository;
import com.capitalone.dashboard.repository.WorkflowRunJobRepository;
import com.capitalone.dashboard.repository.WorkflowRunRepository;

@Service
public class WorkflowServiceImpl implements WorkflowService {
	private static final Log LOG = LogFactory.getLog(WorkflowService.class);

	@Autowired
	WorkflowRepository<Workflow> workflowRepository;
	
	@Autowired
	WorkflowRunRepository<WorkflowRun> workflowRunRepository;
	
	@Autowired
	WorkflowRunJobRepository<WorkflowRunJob> workflowRunJobRepository;
	
	@Autowired
	WorkflowCustomRepository workflowCustomRepository;

	@Autowired
	GitHubSettings gitHubSettings;

	private boolean isConfigSet() {
		// return collectorItemRepository.findAll().iterator().hasNext();
		return false;
	}

	@Override
	public ComponentData getWorkflowMetaCount() {
		// TODO Auto-generated method stub
		CrudRepository[] obj = { workflowRepository, workflowRunRepository, workflowRunJobRepository };
		String[] names = { "Workflow", "WorkflowRun", "WorkflowRunJob" };
		int i = 0;
		ComponentData componentData = new ComponentData();
		List<Series> data = new ArrayList<Series>();
		for (CrudRepository o : obj) {
			Series series = new Series();
			series.setName(names[i++]);
			series.setValue(o.count());

			data.add(series);
		}

		componentData.setData(data);
		return componentData;
	}
	

	@Override
	public ComponentData getWorkflowMetaData() {
		ComponentData componentData = new ComponentData();
		// TODO should there be a "workflowSettings" from which we get this URI?
		//String[] uris = gitHubSettings.getGitHubStatsUri().split(",");
		String[] uris = {"mock uri1"};
		JSONObject data = new JSONObject();

		for (String uri : uris) {
			switch (uri.toUpperCase()) {
			case "WORKFLOWS":
				List<Workflow> workflows = (List<Workflow>) workflowRepository.findAll();
				data.put("workflows", workflows);
				break;

			case "WORKFLOWRUNS":
				List<WorkflowRun> workflowRuns = (List<WorkflowRun>) workflowRunRepository.findAll();
				data.put("workflowRuns", workflowRuns);
				break;

			case "WORKFLOWRUNJOBS":
				List<WorkflowRunJob> workflowRunJobs = (List<WorkflowRunJob>) workflowRunJobRepository.findAll();
				data.put("workflowRunJobs", workflowRunJobs);
				break;

			default:
				break;
			}

		}

		// TODO is this needed?
		//List<Processes> processes = (List<Processes>) processesRepository.findAll();
		//data.put("processes", processes);

		componentData.setData(data);
		return componentData;
	}
		
	@Override
	public ComponentData getWorkflowStats() {
		ComponentData componentData = new ComponentData();
		// TODO what does this need?
		//List<CPUStats> cpuStats = (List<CPUStats>) cpuStatsRepository.findAll();
		List<Series> data = new ArrayList<Series>();
		/*if (cpuStats != null && cpuStats.size() > 0) {
			CPUStats stats = cpuStats.get(0);

			Series systemCpuUsage = new Series();
			systemCpuUsage.setName("System Usage");
			systemCpuUsage.setValue(stats.getStatsUsage().getSystemCpuUsage());

			data.add(systemCpuUsage);

			Series memoryStatsUsage = new Series();
			memoryStatsUsage.setName("Memory Usage");
			memoryStatsUsage.setValue(stats.getMemoryStats().getUsage());
			data.add(memoryStatsUsage);

			Series memoryStatsMaxUsage = new Series();
			memoryStatsMaxUsage.setName("Memory Max Usage");
			memoryStatsMaxUsage.setValue(stats.getMemoryStats().getMaxUsage());
			data.add(memoryStatsMaxUsage);

			Series memoryStatsLimit = new Series();
			memoryStatsLimit.setName("Memory Limit");
			memoryStatsLimit.setValue(stats.getMemoryStats().getLimit());
			data.add(memoryStatsLimit);

		}*/

		componentData.setData(data);
		return componentData;
	}
	
	@Override
	public ComponentData getEnabledWorkflows() {
		ComponentData componentData = new ComponentData();
		List<Workflow> workflows = (List<Workflow>) workflowRepository.findEnabledWorkflows(true);
		componentData.setData(workflows);
		return componentData;
	}
	
	@Override
	public ComponentData getRunsByWorkflowId(String workflowId) {
		ComponentData componentData = new ComponentData();
		List<WorkflowRun> runs = (List<WorkflowRun>) workflowRunRepository.findByWorkflowId(workflowId);
		componentData.setData(runs);
		return componentData;
	}
	
	@Override
	public ComponentData getJobByJobId(String jobId) {
		ComponentData componentData = new ComponentData();
		WorkflowRunJob job = (WorkflowRunJob) workflowRunJobRepository.findByJobId(jobId);
		componentData.setData(job);
		return componentData;
	}
	
	@Override
	public ComponentData getJobsByRunId(String runId) {
		ComponentData componentData = new ComponentData();
		List<WorkflowRunJob> jobs = (List<WorkflowRunJob>) workflowRunJobRepository.findByRunId(runId);
		componentData.setData(jobs);
		return componentData;
	}
	
	@Override
	public ComponentData getDistinctRunStatusByWorkflow(String workflowId) {
		ComponentData componentData = new ComponentData();
		JSONObject status = workflowCustomRepository.findDistinctRunStatusByWorkflow(workflowId);
		componentData.setData(status);
		return componentData;
	}

}
