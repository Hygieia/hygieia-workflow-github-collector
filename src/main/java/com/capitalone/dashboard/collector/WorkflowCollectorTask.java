package com.capitalone.dashboard.collector;


import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.CollectionError;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.GitHub;
import com.capitalone.dashboard.model.Workflow;
import com.capitalone.dashboard.model.WorkflowRun;
import com.capitalone.dashboard.model.WorkflowRunJob;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.GitHubRepository;
import com.capitalone.dashboard.repository.WorkflowRepository;
import com.capitalone.dashboard.repository.WorkflowRunJobRepository;
import com.capitalone.dashboard.repository.WorkflowRunRepository;

/**
 * CollectorTask that fetches Commit information from GitHub
 */
@Component
public class WorkflowCollectorTask extends CollectorTask<Collector> {
    private static final Log LOG = LogFactory.getLog(WorkflowCollectorTask.class);

    private final BaseCollectorRepository<Collector> collectorRepository;
    private final GitHubRepository gitHubRepository;
    private final WorkflowClient workflowClient;
    private final GitHubSettings gitHubSettings;
    private final ComponentRepository dbComponentRepository;
    private static final long FOURTEEN_DAYS_MILLISECONDS = 14 * 24 * 60 * 60 * 1000;
    private static final String API_RATE_LIMIT_MESSAGE = "API rate limit exceeded";
    private WorkflowRepository<Workflow> workflowRepository;
    private WorkflowRunRepository<WorkflowRun> workflowRunRepository;
    private WorkflowRunJobRepository<WorkflowRunJob> workflowRunJobRepository;

    Predicate<Workflow> checkWorkFlowExist = workflow -> {return workflowRepository.exists(workflow.getWorkflowId());};
    
    @Autowired
    public WorkflowCollectorTask(TaskScheduler taskScheduler,
                               BaseCollectorRepository<Collector> collectorRepository,
                               GitHubRepository gitHubRepository,
                               WorkflowClient workflowClient,
                               GitHubSettings gitHubSettings,
                               ComponentRepository dbComponentRepository,
                               WorkflowRepository<Workflow> workflowRepository,
                               WorkflowRunRepository<WorkflowRun> workflowRunRepository,
                               WorkflowRunJobRepository<WorkflowRunJob> workflowRunJobRepository) {
        super(taskScheduler, "GitHub");
        this.collectorRepository = collectorRepository;
        this.gitHubRepository = gitHubRepository;
        this.workflowClient = workflowClient;
        this.gitHubSettings = gitHubSettings;
        this.dbComponentRepository = dbComponentRepository;
        this.workflowRepository = workflowRepository;
        this.workflowRunRepository = workflowRunRepository;
        this.workflowRunJobRepository = workflowRunJobRepository;
    }

    @Override
    public Collector getCollector() {
        Collector protoType = new Collector();
        protoType.setName("GitHubWorkflow");
        protoType.setCollectorType(CollectorType.GitWorkflow);
        protoType.setOnline(true);
        protoType.setEnabled(true);

        Map<String, Object> allOptions = new HashMap<>();
        allOptions.put(GitHub.REPO_URL, "");
        allOptions.put(GitHub.BRANCH, "");
        allOptions.put(GitHub.USER_ID, "");
        allOptions.put(GitHub.PASSWORD, "");
        allOptions.put(GitHub.PERSONAL_ACCESS_TOKEN, "");
        protoType.setAllFields(allOptions);

        Map<String, Object> uniqueOptions = new HashMap<>();
        uniqueOptions.put(GitHub.REPO_URL, "");
        uniqueOptions.put(GitHub.BRANCH, "");
        protoType.setUniqueFields(uniqueOptions);
        return protoType;
    }

    @Override
    public BaseCollectorRepository<Collector> getCollectorRepository() {
        return collectorRepository;
    }

    @Override
    public String getCron() {
        return gitHubSettings.getCron();
    }

    /**
     * Clean up unused deployment collector items
     *
     * @param collector the {@link Collector}
     */
    @SuppressWarnings("PMD.AvoidDeeplyNestedIfStmts") // agreed, fixme
    private void clean(Collector collector) {
        Set<ObjectId> uniqueIDs = new HashSet<>();
        /**
         * Logic: For each component, retrieve the collector item list of the type SCM.
         * Store their IDs in a unique set ONLY if their collector IDs match with GitHub collectors ID.
         */
        for (com.capitalone.dashboard.model.Component comp : dbComponentRepository.findAll()) {
            if (comp.getCollectorItems() != null && !comp.getCollectorItems().isEmpty()) {
                List<CollectorItem> itemList = comp.getCollectorItems().get(CollectorType.SCM);
                if (itemList != null) {
                    for (CollectorItem ci : itemList) {
                        if (ci != null && ci.getCollectorId().equals(collector.getId())) {
                            uniqueIDs.add(ci.getId());
                        }
                    }
                }
            }
        }

        /**
         * Logic: Get all the collector items from the collector_item collection for this collector.
         * If their id is in the unique set (above), keep them enabled; else, disable them.
         */
        List<GitHub> repoList = new ArrayList<>();
        Set<ObjectId> gitID = new HashSet<>();
        gitID.add(collector.getId());
        for (GitHub repo : gitHubRepository.findByCollectorIdIn(gitID)) {
            if (repo.isPushed()) {continue;}

            repo.setEnabled(uniqueIDs.contains(repo.getId()));
            repoList.add(repo);
        }
        gitHubRepository.save(repoList);
    }

    @Override
    public void collect(Collector collector) {

        logBanner("Starting...");
        long start = System.currentTimeMillis();
        int repoCount = 0;
    
        //clean(collector);
        
        String proxyUrl = gitHubSettings.getProxy();
        String proxyPort = gitHubSettings.getProxyPort();
        String proxyUser= gitHubSettings.getProxyUser();
        String proxyPassword= gitHubSettings.getProxyPassword();
        
        if (!StringUtils.isEmpty(proxyUrl) && !StringUtils.isEmpty(proxyPort)) {
            System.setProperty("http.proxyHost", proxyUrl);
            System.setProperty("https.proxyHost", proxyUrl);
            System.setProperty("http.proxyPort", proxyPort);
            System.setProperty("https.proxyPort", proxyPort);
        
         if (!StringUtils.isEmpty(proxyUser) && !StringUtils.isEmpty(proxyPassword)) {
            System.setProperty("http.proxyUser", proxyUser);
            System.setProperty("https.proxyUser", proxyUser);
            System.setProperty("http.proxyPassword", proxyPassword);
            System.setProperty("https.proxyPassword", proxyPassword);
         }
         }
            
 		for (GitHub repo : enabledRepos(collector)) {
            if (repo.getErrorCount() < gitHubSettings.getErrorThreshold()) {
                boolean firstRun = ((repo.getLastUpdated() == 0) || ((start - repo.getLastUpdated()) > FOURTEEN_DAYS_MILLISECONDS));
                repo.removeLastUpdateDate();  //moved last update date to collector item. This is to clean old data.
                try {
                    LOG.info(repo.getOptions().toString() + "::" + repo.getBranch() + ":: get workflows");
                    
                    // Step 1: Get all the Workflows and save if not exist
                    workflowClient.getWorkflows(repo).parallelStream().filter(checkWorkFlowExist).
                    forEach(workflow -> {
                    	workflowRepository.save(workflow);
                    });
                  
                    List<Workflow> workflows = (List<Workflow>) workflowRepository.findEnabledWorkflows(Boolean.TRUE);
                    
                    // Step 2: Get all runs & jobs associated with each "enabled" (active) workflow
                    workflows.parallelStream().forEach(workflow -> {
                    	
                    	String workflowId = workflow.getWorkflowId();
                    	
                    	List<WorkflowRun> workflowRuns;
						try {
							workflowRuns = workflowClient.getWorkflowRuns(repo, 
									workflow.getWorkflowId());
      
                        for (WorkflowRun workflowRun : workflowRuns) {
                        	
                        	String runId = workflowRun.getRunId();
                        	
                        	workflowRun.setWorkflowId(workflowId);
                        	workflowRunRepository.save(workflowRun);
                        	
                        	List<WorkflowRunJob> workflowRunJobs;
							try {
								workflowRunJobs = workflowClient.getWorkflowRunJobs(repo, 
										workflow.getWorkflowId(), workflowRun.getRunId());
                        	
								for (WorkflowRunJob job : workflowRunJobs) {
									job.setWorkflowId(workflowId);
									job.setRunId(runId);
									workflowRunJobRepository.save(job);
								}
			                } catch (RestClientException | MalformedURLException ex) {
			                    LOG.error("Error fetching workflowRunJobs for:" + repo.getRepoUrl(), ex);
			                    CollectionError error = new CollectionError(CollectionError.UNKNOWN_HOST, repo.getRepoUrl());
			                    repo.getErrors().add(error);
							} catch (HygieiaException he) {
			                    LOG.error("Error fetching workflowRunJobs for:" + repo.getRepoUrl(), he);
			                    CollectionError error = new CollectionError("Bad repo url", repo.getRepoUrl());
			                    repo.getErrors().add(error);
			                }
                        }
		                } catch (RestClientException | MalformedURLException ex) {
		                    LOG.error("Error fetching workflowRuns for:" + repo.getRepoUrl(), ex);
		                    CollectionError error = new CollectionError(CollectionError.UNKNOWN_HOST, repo.getRepoUrl());
		                    repo.getErrors().add(error);
						} catch (HygieiaException he) {
		                    LOG.error("Error fetching workflowRuns for:" + repo.getRepoUrl(), he);
		                    CollectionError error = new CollectionError("Bad repo url", repo.getRepoUrl());
		                    repo.getErrors().add(error);
		                }
                        
                        workflowRepository.save(workflow);

                    });
                    
                                     
                } catch (HttpStatusCodeException hc) {
                    LOG.error("Error fetching workflows for:" + repo.getRepoUrl(), hc);
                    if (! (isRateLimitError(hc) || hc.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE) ) {
                        CollectionError error = new CollectionError(hc.getStatusCode().toString(), hc.getMessage());
                        repo.getErrors().add(error);
                    }
                } catch (ResourceAccessException ex) {
                    //handle case where repo is valid but github returns connection refused due to outages??
                    if (ex.getMessage() != null && ex.getMessage().contains("Connection refused")) {
                        LOG.error("Error fetching workflows for:" + repo.getRepoUrl(), ex);
                    } else {
                        LOG.error("Error fetching workflows for:" + repo.getRepoUrl(), ex);
                        CollectionError error = new CollectionError(CollectionError.UNKNOWN_HOST, repo.getRepoUrl());
                        repo.getErrors().add(error);
                    }
                } catch (RestClientException | MalformedURLException ex) {
                    LOG.error("Error fetching workflows for:" + repo.getRepoUrl(), ex);
                    CollectionError error = new CollectionError(CollectionError.UNKNOWN_HOST, repo.getRepoUrl());
                    repo.getErrors().add(error);
                } catch (HygieiaException he) {
                    LOG.error("Error fetching workflows for:" + repo.getRepoUrl(), he);
                    CollectionError error = new CollectionError("Bad repo url", repo.getRepoUrl());
                    repo.getErrors().add(error);
                }
                gitHubRepository.save(repo);
            }
            repoCount++;
        }
        log("New Repos", start, repoCount);

        log("Finished", start);
    }


    private boolean isRateLimitError(HttpStatusCodeException hc) {
        String response = hc.getResponseBodyAsString();
        return StringUtils.isEmpty(response) ? false : response.contains(API_RATE_LIMIT_MESSAGE);
    }

    private List<GitHub> enabledRepos(Collector collector) {
        List<GitHub> repos = (List<GitHub>) gitHubRepository.findEnabledGitHubRepos(collector.getId());

        List<GitHub> pulledRepos 
                =  (List<GitHub>) Optional.ofNullable(repos)
                .orElseGet(Collections::emptyList).stream()
                .filter(pulledRepo -> !pulledRepo.isPushed())
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(pulledRepos)) { return new ArrayList<>(); }

        return pulledRepos;
    }
}
