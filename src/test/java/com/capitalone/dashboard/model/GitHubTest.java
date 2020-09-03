package com.capitalone.dashboard.model;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

public class GitHubTest {

	private GitHub repo1;
	private GitHub repo2;
	private GitHub repo3;
	
	private String url1 = "url1";
	private String branch1 = "branch1";
	private String userId1 = "userId1";
	private String password1 = "password1";
	private String personalAccessToken1 = "personalAccessToken1";
	private int lastPrPage1 = 1;
	
	@Before
	public void setup() {
		repo1 = new GitHub();
		repo1.setRepoUrl(url1);
		repo1.setBranch(branch1);
		repo1.setUserId(userId1);
		repo1.setPassword(password1);
		repo1.setPersonalAccessToken(personalAccessToken1);
		repo1.setLastPrPage(lastPrPage1);
		
		repo2 = new GitHub();
		repo2.setRepoUrl(url1);
		repo2.setBranch(branch1);
		repo2.setUserId(userId1);
		repo2.setPassword(password1);
		repo2.setPersonalAccessToken(personalAccessToken1);
		repo2.setLastPrPage(lastPrPage1);
		
		repo3 = new GitHub();
		repo3.setRepoUrl("different");
		repo3.setBranch("branch3");
		repo3.setUserId("userId3");
		repo3.setPassword("password3");
		repo3.setPersonalAccessToken("personalAccessToken3");
		repo3.setLastPrPage(3);
	}
	
	@Test
	public void testEquals() {
		assertFalse(repo1.equals(null));
		assertTrue(repo1.equals(repo1));
		assertFalse(repo1.equals(new Object()));
		assertTrue(repo1.equals(repo2));
		assertFalse(repo1.equals(repo3));
	}
	
	@Test
	public void testHashCode() {
		assertEquals(repo1.hashCode(), repo2.hashCode());
		assertNotEquals(repo1.hashCode(), repo3.hashCode());
	}
	
	@Test
	public void testLastUpdateTime() throws ParseException {
		String sDate1="14/08/2020 10:53";
		Date date1;
		date1 = new SimpleDateFormat("dd/MM/yyyy hh:mm").parse(sDate1);
		repo3.setLastUpdateTime(date1);
		assertEquals(date1,repo3.getLastUpdateTime());
		repo3.removeLastUpdateDate();
		assertNull(repo3.getLastUpdateTime());
	}
	
	@Test
	public void testGetters() {
		assertEquals(url1,repo1.getRepoUrl());
		assertEquals(branch1,repo1.getBranch());
		assertEquals(userId1,repo1.getUserId());
		assertEquals(password1,repo1.getPassword());
		assertEquals(personalAccessToken1,repo1.getPersonalAccessToken());
		assertEquals(lastPrPage1,repo1.getLastPrPage());
	}

}
