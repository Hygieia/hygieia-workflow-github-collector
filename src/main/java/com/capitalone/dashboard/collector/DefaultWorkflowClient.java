package com.capitalone.dashboard.collector;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.CommitType;
import com.capitalone.dashboard.model.GitHub;
import com.capitalone.dashboard.model.GitHubParsed;
import com.capitalone.dashboard.model.Workflow;
import com.capitalone.dashboard.model.WorkflowRun;
import com.capitalone.dashboard.model.WorkflowRunJob;
import com.capitalone.dashboard.model.WorkflowRunJobStep;
import com.capitalone.dashboard.util.Encryption;
import com.capitalone.dashboard.util.EncryptionException;
import com.capitalone.dashboard.util.Supplier;

/**
 * GitHubClient implementation that uses SVNKit to fetch information about
 * Subversion repositories.
 */

@Component
public class DefaultWorkflowClient implements WorkflowClient {
    private static final Log LOG = LogFactory.getLog(DefaultWorkflowClient.class);

    private final GitHubSettings settings;

    private final RestOperations restOperations;	

    private static final int FIRST_RUN_HISTORY_DEFAULT = 14;

    @Autowired
    public DefaultWorkflowClient(GitHubSettings settings,
                               Supplier<RestOperations> restOperationsSupplier) {
        this.settings = settings;
        this.restOperations = restOperationsSupplier.get();
    }
    
    /**
     * Gets workflows for a given repo
     * @param repo
     * @return list of workflows
     * @throws RestClientException
     * @throws MalformedURLException
     * @throws HygieiaException
     */
	@Override
	public List<Workflow> getWorkflows(GitHub repo)
			throws MalformedURLException, HygieiaException {

        List<Workflow> workflows = new ArrayList<>();
        int page=1;
        int perPage=100;

        // format URL
        String repoUrl = (String) repo.getOptions().get("url");
        GitHubParsed gitHubParsed = new GitHubParsed(repoUrl);
        String apiUrl = gitHubParsed.getApiUrl();
        // To Do check changes the url: workflowRuns
        String queryUrl = apiUrl.concat("/actions/workflows/" +
        		"?branch=" + repo.getBranch());
//        		"?branch=" + repo.getBranch() + "&page=" + page + "&perPage=" + perPage);
        String decryptedPassword = repo.getPassword();// Decrypting is not required decryptString(repo.getPassword(), settings.getKey());
        String personalAccessToken = (String) repo.getPersonalAccessToken();
        String decryptedPersonalAccessToken = personalAccessToken;//decryptString(personalAccessToken, settings.getKey());
        boolean lastPage = false;
        String queryUrlPage = queryUrl;
        while (!lastPage) {
            LOG.info("Executing " + queryUrlPage);
            ResponseEntity<String> response = makeRestCall(queryUrlPage, repo.getUserId(), decryptedPassword,decryptedPersonalAccessToken);
            JSONObject jsonObject = (JSONObject) parseAsObject(response);
            JSONArray jsonArray = (JSONArray) jsonObject.get("workflows");
            for (Object item : jsonArray) {
                JSONObject itemObject = (JSONObject) item;

            	String workflowId = str(jsonObject, "id");
                String name = str(jsonObject, "name");
                String state = str(jsonObject, "state");
                String created_at = str(jsonObject, "created_at");
                String updated_at = str(jsonObject, "updated_at");
                Boolean enabled = state.equals("active");
 
                Workflow workflow = new Workflow(workflowId,
                		name,state,enabled,created_at,updated_at);
                workflows.add(workflow);
  
            }
            if (CollectionUtils.isEmpty(jsonArray)) {
                lastPage = true;
            } else {
                if (isThisLastPage(response)) {
                    lastPage = true;
                } else {
                    lastPage = false;
                    queryUrlPage = getNextPageUrl(response);
                }
            }
        }
        return workflows;
	}

    /**
     * Gets workflowRuns for a given repo & workflowId
     * @param repo
     * @param workflowId
     * @return list of workflowRuns
     * @throws RestClientException
     * @throws MalformedURLException
     * @throws HygieiaException
     */
	@Override
	public List<WorkflowRun> getWorkflowRuns(GitHub repo, String pWorkflowId)
			throws MalformedURLException, HygieiaException {

        List<WorkflowRun> workflowRuns = new ArrayList<>();
        int page=1;
        int perPage=100;

        // format URL
        String repoUrl = (String) repo.getOptions().get("url");
        GitHubParsed gitHubParsed = new GitHubParsed(repoUrl);
        String apiUrl = gitHubParsed.getApiUrl();
        // To Do check changes the url: workflowRuns
        String queryUrl = apiUrl.concat("/actions/workflows/" + pWorkflowId + "/runs" +
        		"?branch=" + repo.getBranch());
//				"?branch=" + repo.getBranch() + "&page=" + page + "&perPage=" + perPage);
        String decryptedPassword =      repo.getPassword();// Decrypting is not required decryptString(repo.getPassword(), settings.getKey());
        String personalAccessToken = (String) repo.getPersonalAccessToken();
        String decryptedPersonalAccessToken = personalAccessToken;//decryptString(personalAccessToken, settings.getKey());
        boolean lastPage = false;
        String queryUrlPage = queryUrl;
        while (!lastPage) {
            LOG.info("Executing " + queryUrlPage);
            ResponseEntity<String> response = makeRestCall(queryUrlPage, repo.getUserId(), decryptedPassword,decryptedPersonalAccessToken);
            JSONObject runObject = parseAsObject(response);
            JSONArray jsonArray = (JSONArray) runObject.get("workflow_runs");
            for (Object item : jsonArray) {
                JSONObject jsonObject = (JSONObject) item;

            	String workflowId = str(jsonObject, "workflow_id");
                String runId = str(jsonObject, "id");
                String status = str(jsonObject, "status");
                String conclusion = str(jsonObject, "conclusion");
 
                WorkflowRun workflowRun = new WorkflowRun(workflowId,
                		runId, status, conclusion);
                workflowRuns.add(workflowRun);
  
            }
            if (CollectionUtils.isEmpty(jsonArray)) {
                lastPage = true;
            } else {
                if (isThisLastPage(response)) {
                    lastPage = true;
                } else {
                    lastPage = false;
                    queryUrlPage = getNextPageUrl(response);
                }
            }
        }
        return workflowRuns;
	}

    /**
     * Gets workflowRunJobs for a given repo & workflowRunId
     * @param repo
     * @param workflowId
     * @param workflowRunId
     * @return list of workflowRunJobs
     * @throws RestClientException
     * @throws MalformedURLException
     * @throws HygieiaException
     */
	@Override
	public List<WorkflowRunJob> getWorkflowRunJobs(GitHub repo, String pWorkflowId, String pWorkflowRunId)
			throws MalformedURLException, HygieiaException {
        List<WorkflowRunJob> workflowRunJobs = new ArrayList<>();
        List<WorkflowRunJobStep> workflowRunJobSteps = new ArrayList<>();
        int page=1;
        int perPage=100;

        // format URL
        String repoUrl = (String) repo.getOptions().get("url");
        GitHubParsed gitHubParsed = new GitHubParsed(repoUrl);
        String apiUrl = gitHubParsed.getApiUrl();
        // To Do check changes the url: workflowRuns
        String queryUrl = apiUrl.concat("/actions/run/" + pWorkflowRunId + "/jobs" +
        		"?branch=" + repo.getBranch());
//        		"?branch=" + repo.getBranch() + "&page=" + page + "&perPage=" + perPage);
        String decryptedPassword =      repo.getPassword();// Decrypting is not required decryptString(repo.getPassword(), settings.getKey());
        String personalAccessToken = (String) repo.getPersonalAccessToken();
        String decryptedPersonalAccessToken = personalAccessToken;//decryptString(personalAccessToken, settings.getKey());
        boolean lastPage = false;
        String queryUrlPage = queryUrl;
        while (!lastPage) {
            LOG.info("Executing " + queryUrlPage);
            ResponseEntity<String> response = makeRestCall(queryUrlPage, repo.getUserId(), decryptedPassword,decryptedPersonalAccessToken);
            JSONObject runObject = parseAsObject(response);
            JSONArray jsonArray = (JSONArray) runObject.get("jobs");
            for (Object item : jsonArray) {
                JSONObject jsonObject = (JSONObject) item;
                String jobId = str(jsonObject, "id");
                String runId = str(jsonObject, "run_id");
                String status = str(jsonObject, "status");
                String conclusion = str(jsonObject, "conclusion");
                String started_at = str(jsonObject,"started_at");
                String completed_at = str(jsonObject,"completed_at");
                String name = str(jsonObject,"name");
 
                WorkflowRunJob workflowRunJob = new WorkflowRunJob(pWorkflowId,
                		runId, jobId, status, conclusion, started_at, completed_at, name);
                
                workflowRunJobSteps.clear();
                JSONArray stepArray = (JSONArray) jsonObject.get("steps");
                for (Object step : stepArray) {
                	JSONObject stepObject = (JSONObject) step;
                	String stepNumber = str(stepObject,"number");
                	status = str(stepObject,"status");
                	conclusion = str(stepObject,"conclusion");
                    started_at = str(stepObject,"started_at");
                    completed_at = str(stepObject,"completed_at");
                    name = str(stepObject,"name");
                    
                    WorkflowRunJobStep workflowRunJobStep = new WorkflowRunJobStep(pWorkflowId,
                    		runId, jobId, stepNumber, status, conclusion, started_at,
                    		completed_at, name);
                    
                    workflowRunJobSteps.add(workflowRunJobStep);
                }
                
                workflowRunJob.setWorkflowRunJobSteps(workflowRunJobSteps);
                workflowRunJobs.add(workflowRunJob);
  
            }
            if (CollectionUtils.isEmpty(jsonArray)) {
                lastPage = true;
            } else {
                if (isThisLastPage(response)) {
                    lastPage = true;
                } else {
                    lastPage = false;
                    queryUrlPage = getNextPageUrl(response);
                }
            }
        }
        return workflowRunJobs;
	}



    // Utilities

    /**
     * See if it is the last page: obtained from the response header
     * @param response
     * @return
     */
    private boolean isThisLastPage(ResponseEntity<String> response) {
        HttpHeaders header = response.getHeaders();
        List<String> link = header.get("Link");
        if (link == null || link.isEmpty()) {
            return true;
        } else {
            for (String l : link) {
                if (l.contains("rel=\"next\"")) {
                    return false;
                }
            }
        }
        return true;
    }

    private String getNextPageUrl(ResponseEntity<String> response) {
        String nextPageUrl = "";
        HttpHeaders header = response.getHeaders();
        List<String> link = header.get("Link");
        if (link == null || link.isEmpty()) {
            return nextPageUrl;
        } else {
            for (String l : link) {
                if (l.contains("rel=\"next\"")) {
                    String[] parts = l.split(",");
                    if (parts != null && parts.length > 0) {
                        for(int i=0; i<parts.length; i++) {
                            if (parts[i].contains("rel=\"next\"")) {
                                nextPageUrl = parts[i].split(";")[0];
                                nextPageUrl = nextPageUrl.replaceFirst("<","");
                                nextPageUrl = nextPageUrl.replaceFirst(">","").trim();
                                // Github Link headers for 'next' and 'last' are URL Encoded
                                String decodedPageUrl;
                                try {
                                    decodedPageUrl = URLDecoder.decode(nextPageUrl, StandardCharsets.UTF_8.name());
                                } catch (UnsupportedEncodingException e) {
                                    decodedPageUrl = URLDecoder.decode(nextPageUrl);
                                }
                                return decodedPageUrl;
                            }
                        }
                    }
                }
            }
        }
        return nextPageUrl;
    }

    /**
     * Checks rate limit
     * @param response
     * @return boolean
     */
    private boolean isRateLimitReached(ResponseEntity<String> response) {
        HttpHeaders header = response.getHeaders();
        List<String> limit = header.get("X-RateLimit-Remaining");
        boolean rateLimitReached =  CollectionUtils.isEmpty(limit) ? false : Integer.valueOf(limit.get(0)) < settings.getRateLimitThreshold();
        if (rateLimitReached) {
            LOG.error("Github rate limit reached. Threshold =" + settings.getRateLimitThreshold() + ". Current remaining ="+Integer.valueOf(limit.get(0)));
        }
        return rateLimitReached;
    }

    private ResponseEntity<String> makeRestCall(String url, String userId,
                                                String password,String personalAccessToken) {
        // Basic Auth only.
        if (!"".equals(userId) && !"".equals(password)) {
            return restOperations.exchange(url, HttpMethod.GET, new HttpEntity<>(createHeaders(userId, password)), String.class);
        } else if ((personalAccessToken!=null && !"".equals(personalAccessToken)) ) {
            return restOperations.exchange(url, HttpMethod.GET,new HttpEntity<>(createHeaders(personalAccessToken)),String.class);
        } else if (settings.getPersonalAccessToken() != null && !"".equals(settings.getPersonalAccessToken())){
            return restOperations.exchange(url, HttpMethod.GET, new HttpEntity<>(createHeaders(settings.getPersonalAccessToken())), String.class);
        }else {
            return restOperations.exchange(url, HttpMethod.GET, null, String.class);
        }
    }

    private HttpHeaders createHeaders(final String userId, final String password) {
        String auth = userId + ":" + password;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.US_ASCII));
        String authHeader = "Basic " + new String(encodedAuth);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        return headers;
    }

    private HttpHeaders createHeaders(final String token) {
        String authHeader = "token " + token;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        return headers;
    }

    private JSONArray parseAsArray(ResponseEntity<String> response) {
        try {
            return (JSONArray) new JSONParser().parse(response.getBody());
        } catch (ParseException pe) {
            LOG.error(pe.getMessage());
        }
        return new JSONArray();
    }

    private JSONObject parseAsObject(ResponseEntity<String> response) {
        try {
            return (JSONObject) new JSONParser().parse(response.getBody());
        } catch (ParseException pe) {
            LOG.error(pe.getMessage());
        }
        return new JSONObject();
    }

    private int asInt(JSONObject json, String key) {
        String val = str(json, key);
        try {
            if (val != null) {
                return Integer.parseInt(val);
            }
        } catch (NumberFormatException ex) {
            LOG.error(ex.getMessage());
        }
        return 0;
    }

    private String str(JSONObject json, String key) {
        Object value = json.get(key);
        return value == null ? null : value.toString();
    }

    /**
     * Get run date based off of firstRun boolean
     * @param repo
     * @param firstRun
     * @return
     */
    private Date getRunDate(GitHub repo, boolean firstRun) {
        if (firstRun) {
            int firstRunDaysHistory = settings.getFirstRunHistoryDays();
            if (firstRunDaysHistory > 0) {
                return getDate(new Date(), -firstRunDaysHistory, 0);
            } else {
                return getDate(new Date(), -FIRST_RUN_HISTORY_DEFAULT, 0);
            }
        } else {
            return getDate(new Date(repo.getLastUpdated()), 0, -10);
        }
    }


    /**
     * Date utility
     * @param dateInstance
     * @param offsetDays
     * @param offsetMinutes
     * @return
     */
    private Date getDate(Date dateInstance, int offsetDays, int offsetMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateInstance);
        cal.add(Calendar.DATE, offsetDays);
        cal.add(Calendar.MINUTE, offsetMinutes);
        return cal.getTime();
    }

    /**
     * Decrypt string
     * @param string
     * @param key
     * @return String
     */
    public static String decryptString(String string, String key) {
        if (!StringUtils.isEmpty(string)) {
            try {
                return Encryption.decryptString(
                        string, key);
            } catch (EncryptionException e) {
                LOG.error(e.getMessage());
            }
        }
        return "";
    }


    /**
     * Format date the way Github api wants
     * @param dt
     * @return String
     */

    private static String getTimeForApi (Date dt) {
        Calendar calendar = new GregorianCalendar();
        TimeZone timeZone = calendar.getTimeZone();
        Calendar cal = Calendar.getInstance(timeZone);
        cal.setTime(dt);
        return String.format("%tFT%<tRZ", cal);
    }

}

// X-RateLimit-Remaining