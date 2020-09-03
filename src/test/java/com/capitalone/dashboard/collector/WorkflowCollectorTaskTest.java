package com.capitalone.dashboard.collector;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.GitHub;
import com.capitalone.dashboard.model.Workflow;
import com.capitalone.dashboard.model.WorkflowRun;
import com.capitalone.dashboard.model.WorkflowRunJob;
import com.capitalone.dashboard.model.WorkflowRunJobStep;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.GitHubRepository;
import com.capitalone.dashboard.repository.WorkflowRepository;
import com.capitalone.dashboard.repository.WorkflowRunJobRepository;
import com.capitalone.dashboard.repository.WorkflowRunRepository;

@RunWith(MockitoJUnitRunner.class)
public class WorkflowCollectorTaskTest {

	@Mock private ComponentRepository dbComponentRepository;
	@Mock private GitHubRepository gitHubRepository;
	@Mock private WorkflowRepository<Workflow> workflowRepository;
	@Mock private WorkflowRunRepository<WorkflowRun> workflowRunRepository;
	@Mock private WorkflowRunJobRepository<WorkflowRunJob> workflowRunJobRepository;
	@Mock private GitHub repo1;
    @Mock private GitHub repo2;
    @Mock private WorkflowClient workflowClient;
    
    @Mock private GitHubSettings gitHubSettings;
	
	@InjectMocks private WorkflowCollectorTask task;
	
	@Test
	public void testCollect() throws MalformedURLException, HygieiaException {
		when(dbComponentRepository.findAll()).thenReturn(components());
		
		GitHub repo1 = getRepo1();
		
        ObjectId gitID = new ObjectId("111ca42a258ad365fbb64ecc");
        when(gitHubRepository.findGitHubRepo(gitID,repo1.getRepoUrl(),repo1.getBranch())).thenReturn(getRepo1());
        
        Collector collector = new Collector();
        collector.setEnabled(true);
        collector.setName("collector");
        collector.setId(new ObjectId("111ca42a258ad365fbb64ecc"));

        when(gitHubRepository.findEnabledGitHubRepos(collector.getId())).thenReturn(getEnabledRepos());
       
        when(gitHubSettings.getErrorThreshold()).thenReturn(1);
        
        when(workflowClient.getWorkflows(getRepo1())).thenReturn(getWorkflows());
        
        when(workflowRepository.findEnabledWorkflows(Boolean.TRUE)).thenReturn(getEnabledWorkflows());
        when(workflowRepository.exists(anyString())).thenReturn(true);
        
        when(workflowClient.getWorkflowRuns(getRepo1(), "8675309")).thenReturn(getWorkflowRuns());
        
        when(workflowClient.getWorkflowRunJobs(getRepo1(), "8675309", "001")).thenReturn(getWorkflowRunJob1());
        
        when(workflowClient.getWorkflowRunJobs(getRepo1(), "8675309", "002")).thenReturn(getWorkflowRunJob2());
        
        task.collect(collector);

	}

    private List<GitHub> getEnabledRepos() {
        List<GitHub> gitHubs = new ArrayList<GitHub>();
        gitHubs.add(getRepo1());
        return gitHubs;
    }
    
	private ArrayList<com.capitalone.dashboard.model.Component> components() {
        ArrayList<com.capitalone.dashboard.model.Component> cArray = new ArrayList<com.capitalone.dashboard.model.Component>();
        com.capitalone.dashboard.model.Component c = new Component();
        c.setId(new ObjectId());
        c.setName("COMPONENT1");
        c.setOwner("JOHN");

        CollectorType scmType = CollectorType.SCM;
        CollectorItem ci1 = new CollectorItem();
        ci1.setId(new ObjectId("1c1ca42a258ad365fbb64ecc"));
        ci1.setNiceName("ci1");
        ci1.setEnabled(true);
        ci1.setPushed(false);
        ci1.setCollectorId(new ObjectId("111ca42a258ad365fbb64ecc"));
        c.addCollectorItem(scmType, ci1);

        CollectorItem ci2 = new CollectorItem();
        ci2.setId(new ObjectId("1c2ca42a258ad365fbb64ecc"));
        ci2.setNiceName("ci2");
        ci2.setEnabled(true);
        ci2.setPushed(false);
        ci2.setCollectorId(new ObjectId("111ca42a258ad365fbb64ecc"));
        c.addCollectorItem(scmType, ci2);

        CollectorItem ci3 = new CollectorItem();
        ci3.setId(new ObjectId("1c3ca42a258ad365fbb64ecc"));
        ci3.setNiceName("ci3");
        ci3.setEnabled(true);
        ci3.setPushed(false);
        ci3.setCollectorId(new ObjectId("222ca42a258ad365fbb64ecc"));
        c.addCollectorItem(scmType, ci3);

        cArray.add(c);

        return cArray;
    }
	
	private GitHub getRepo1() {
		GitHub repo1 = new GitHub();
        repo1.setEnabled(true);
        repo1.setId(new ObjectId("1c1ca42a258ad365fbb64ecc"));
        repo1.setCollectorId(new ObjectId("111ca42a258ad365fbb64ecc"));
        repo1.setNiceName("repo1-ci1");
		repo1.setRepoUrl("http://github.company.com/jack/somejavacode");
		repo1.setBranch("branch");
		repo1.setUserId("userID");
		repo1.setPassword("password");
		String sDate1="13/08/2020";
		Date date1;
		try {
			date1 = new SimpleDateFormat("dd/MM/yyyy").parse(sDate1);
			repo1.setLastUpdateTime(date1);
		} catch (ParseException pe) {
			pe.printStackTrace();
		}
		repo1.setPersonalAccessToken("personalAccessToken");
		return repo1;
	}
	
	private List<Workflow> getWorkflows() {
		ArrayList<Workflow> wfs = new ArrayList<Workflow>();
		Workflow wf1 = new Workflow("8675309","wf1","active",true,"created_at","updated_at");
		wfs.add(wf1);
		Workflow wf2 = new Workflow("8675310","wf2","active",true,"created_at","updated_at");
		wfs.add(wf2);
		Workflow wf3 = new Workflow("8675311","wf3","inactive",false,"created_at","updated_at");
		wfs.add(wf3);
		return wfs;
	}
	
	private List<Workflow> getEnabledWorkflows() {
		ArrayList<Workflow> wfs = new ArrayList<Workflow>();
		Workflow wf1 = new Workflow("8675309","wf1","active",true,"created_at","updated_at");
		wfs.add(wf1);
		Workflow wf2 = new Workflow("8675310","wf2","active",true,"created_at","updated_at");
		wfs.add(wf2);
		return wfs;
	}
	
	private List<WorkflowRun> getWorkflowRuns() {
		ArrayList<WorkflowRun> wfrs = new ArrayList<WorkflowRun>();
		WorkflowRun wfr1 = new WorkflowRun("8675309","001","completed","success","build","created_at","updated_at");
		wfrs.add(wfr1);
		WorkflowRun wfr2 = new WorkflowRun("8675309","002","queued","neutral","build","created_at","updated_at");
		wfrs.add(wfr2);
		return wfrs;
	}
	
	private List<WorkflowRunJob> getWorkflowRunJob1() {
		ArrayList<WorkflowRunJob> wfrjs = new ArrayList<WorkflowRunJob>();
		WorkflowRunJob wfrj1 = new WorkflowRunJob("8675309","001","001","completed","success","build","created_at","updated_at");
		ArrayList<WorkflowRunJobStep> wfrjss = new ArrayList<WorkflowRunJobStep>();
		WorkflowRunJobStep wfrjs1 = new WorkflowRunJobStep("8675309","001","001","001","completed",
				"success","created_at","updated_at","step-1");
		wfrjss.add(wfrjs1);
		WorkflowRunJobStep wfrjs2 = new WorkflowRunJobStep("8675309","001","001","002","completed",
				"success","created_at","updated_at","step-2");
		wfrjss.add(wfrjs2);
		wfrj1.setWorkflowRunJobSteps(wfrjss);
		wfrjs.add(wfrj1);
		return wfrjs;
	}
	
	private List<WorkflowRunJob> getWorkflowRunJob2() {
		ArrayList<WorkflowRunJob> wfrjs = new ArrayList<WorkflowRunJob>();
		WorkflowRunJob wfrj1 = new WorkflowRunJob("8675309","002","001","completed","success","build","created_at","updated_at");
		ArrayList<WorkflowRunJobStep> wfrjss = new ArrayList<WorkflowRunJobStep>();
		WorkflowRunJobStep wfrjs1 = new WorkflowRunJobStep("8675309","002","001","001","completed",
				"success","created_at","updated_at","step-1");
		wfrjss.add(wfrjs1);
		WorkflowRunJobStep wfrjs2 = new WorkflowRunJobStep("8675309","002","001","002","completed",
				"success","created_at","updated_at","step-2");
		wfrjss.add(wfrjs2);
		wfrj1.setWorkflowRunJobSteps(wfrjss);
		wfrjs.add(wfrj1);
		return wfrjs;
	}

}
