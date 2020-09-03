package com.capitalone.dashboard.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class SeriesTest {
	
	private String name = "name";
	private String value = "value";
	private String name2 = "name2";
	private String value2 = "value2";
	
	@Test
	public void testSeries() {
		Series series = new Series(name,value);
		String myName = (String) series.getName();
		assertEquals(myName,name);
		String myValue = (String) series.getValue();
		assertEquals(myValue,value);
		series.setName(name2);
		assertEquals(name2,series.getName());
		series.setValue(value2);
		assertEquals(value2,series.getValue());
	}

}
