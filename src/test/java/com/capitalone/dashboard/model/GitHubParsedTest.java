package com.capitalone.dashboard.model;

import static org.junit.Assert.*;

import java.net.MalformedURLException;

import org.junit.Test;

import com.capitalone.dashboard.misc.HygieiaException;

public class GitHubParsedTest {

	private String host = "github.company.com";
	private String owner = "jack";
	private String repoName = "somejavacode";
	private String parsedUrl = "http://"+host+"/"+owner+"/"+repoName;
	private String url = parsedUrl+".git";
	private String apiUrl = "http://api.github.com/repos/"+owner+"/"+repoName;
	
	private String badUrl1 = "bad/url";
	private String badUrl2 = "http://bad/url";
	
	@Test
	public void testGoodConstructor() throws MalformedURLException, HygieiaException {
		GitHubParsed ghParsed = new GitHubParsed(url);
		assertEquals(parsedUrl,ghParsed.getUrl());
		assertEquals(owner,ghParsed.getOwner());
		assertEquals(repoName,ghParsed.getRepoName());
		assertEquals(apiUrl,ghParsed.getApiUrl());
		assertEquals(host,ghParsed.getHost());
	}
	
	@SuppressWarnings("unused")
	@Test(expected=MalformedURLException.class)
	public void testContructorWithMalformedUrl() throws MalformedURLException, HygieiaException {
		GitHubParsed ghParsed = new GitHubParsed(badUrl1);	
	}
	
	@SuppressWarnings("unused")
	@Test(expected=HygieiaException.class)
	public void testContructorWithHygieiaException() throws MalformedURLException, HygieiaException {
		GitHubParsed ghParsed = new GitHubParsed(badUrl2);	
	}

}
