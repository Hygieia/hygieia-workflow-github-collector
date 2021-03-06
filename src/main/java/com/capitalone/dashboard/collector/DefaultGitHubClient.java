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
import com.capitalone.dashboard.util.Encryption;
import com.capitalone.dashboard.util.EncryptionException;
import com.capitalone.dashboard.util.Supplier;

/**
 * GitHubClient implementation that uses SVNKit to fetch information about
 * Subversion repositories.
 */

@Component
public class DefaultGitHubClient implements GitHubClient {
    private static final Log LOG = LogFactory.getLog(DefaultGitHubClient.class);

    private final GitHubSettings settings;

    private final RestOperations restOperations;	

    private static final int FIRST_RUN_HISTORY_DEFAULT = 14;

    @Autowired
    public DefaultGitHubClient(GitHubSettings settings,
                               Supplier<RestOperations> restOperationsSupplier) {
        this.settings = settings;
        this.restOperations = restOperationsSupplier.get();
    }

	@Override
	public List<Workflow> getWorkflow(GitHub repo, boolean firstRun, List<Pattern> commitExclusionPatterns)
			throws MalformedURLException, HygieiaException {
		// TODO Auto-generated method stub

        List<Workflow> worflows = new ArrayList<>();

        // format URL
        String repoUrl = (String) repo.getOptions().get("url");
        GitHubParsed gitHubParsed = new GitHubParsed(repoUrl);
        String apiUrl = gitHubParsed.getApiUrl();
        // To Do change the url: workflow
        String queryUrl = apiUrl.concat("/commits?sha=" + repo.getBranch()
                + "&since=" + getTimeForApi(getRunDate(repo, firstRun)));
        String decryptedPassword =      repo.getPassword();// Decryting is not required decryptString(repo.getPassword(), settings.getKey());
        String personalAccessToken = (String) repo.getPersonalAccessToken();
        String decryptedPersonalAccessToken = personalAccessToken;//decryptString(personalAccessToken, settings.getKey());
        boolean lastPage = false;
        String queryUrlPage = queryUrl;
        while (!lastPage) {
            LOG.info("Executing " + queryUrlPage);
            ResponseEntity<String> response = makeRestCall(queryUrlPage, repo.getUserId(), decryptedPassword,decryptedPersonalAccessToken);
           
            
            
            
            
        }
        return worflows;
	}
    /**
     * Gets commits for a given repo
     * @param repo
     * @param firstRun
     * @return list of commits
     * @throws RestClientException
     * @throws MalformedURLException
     * @throws HygieiaException
     */
    @Override
    public List<Commit> getCommits(GitHub repo, boolean firstRun, List<Pattern> commitExclusionPatterns) throws RestClientException, MalformedURLException, HygieiaException {

        List<Commit> commits = new ArrayList<>();

        // format URL
        String repoUrl = (String) repo.getOptions().get("url");
        GitHubParsed gitHubParsed = new GitHubParsed(repoUrl);
        String apiUrl = gitHubParsed.getApiUrl();

        String queryUrl = apiUrl.concat("/commits?sha=" + repo.getBranch()
                + "&since=" + getTimeForApi(getRunDate(repo, firstRun)));
        String decryptedPassword =      repo.getPassword();//decryptString(repo.getPassword(), settings.getKey());
        String personalAccessToken = (String) repo.getPersonalAccessToken();
        String decryptedPersonalAccessToken = personalAccessToken;//decryptString(personalAccessToken, settings.getKey());
        boolean lastPage = false;
        String queryUrlPage = queryUrl;
        while (!lastPage) {
            LOG.info("Executing " + queryUrlPage);
            ResponseEntity<String> response = makeRestCall(queryUrlPage, repo.getUserId(), decryptedPassword,decryptedPersonalAccessToken);
            JSONArray jsonArray = parseAsArray(response);
            for (Object item : jsonArray) {
                JSONObject jsonObject = (JSONObject) item;
                String sha = str(jsonObject, "sha");
                JSONObject commitObject = (JSONObject) jsonObject.get("commit");
                JSONObject commitAuthorObject = (JSONObject) commitObject.get("author");
                String message = str(commitObject, "message");
                String author = str(commitAuthorObject, "name");
                long timestamp = new DateTime(str(commitAuthorObject, "date"))
                        .getMillis();
                JSONObject authorObject = (JSONObject) jsonObject.get("author");
                String authorLogin = "";
                if (authorObject != null) {
                    authorLogin = str(authorObject, "login");
                }
                JSONArray parents = (JSONArray) jsonObject.get("parents");
                List<String> parentShas = new ArrayList<>();
                if (parents != null) {
                    for (Object parentObj : parents) {
                        parentShas.add(str((JSONObject) parentObj, "sha"));
                    }
                }

                Commit commit = new Commit();
                commit.setTimestamp(System.currentTimeMillis());
                commit.setScmUrl(repo.getRepoUrl());
                commit.setScmBranch(repo.getBranch());
                commit.setScmRevisionNumber(sha);
                commit.setScmParentRevisionNumbers(parentShas);
                commit.setScmAuthor(author);
                commit.setScmAuthorLogin(authorLogin);
                commit.setScmCommitLog(message);
                commit.setScmCommitTimestamp(timestamp);
                commit.setNumberOfChanges(1);
                commit.setType(getCommitType(CollectionUtils.size(parents), message, commitExclusionPatterns));
                commits.add(commit);
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
        return commits;
    }

    private CommitType getCommitType(int parentSize, String commitMessage, List<Pattern> commitExclusionPatterns) {
        if (parentSize > 1) return CommitType.Merge;
        if (settings.getNotBuiltCommits() == null) return CommitType.New;
        if (!CollectionUtils.isEmpty(commitExclusionPatterns)) {
            for (Pattern pattern : commitExclusionPatterns) {
                if (pattern.matcher(commitMessage).matches()) {
                    return CommitType.NotBuilt;
                }
            }
        }
        return CommitType.New;
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