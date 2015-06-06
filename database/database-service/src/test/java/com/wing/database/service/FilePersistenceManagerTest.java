package com.wing.database.service;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class FilePersistenceManagerTest {

	private FilePersistenceManager cut;

	@XmlRootElement
	private static class TestObject {
		@XmlElement
		String field;
	}

	@Before
	public void setup() throws Exception {
		cut = Mockito.spy(FilePersistenceManager.class);
	}

	@Test
	public void saveObject() throws Exception {
		TestObject testing = new TestObject();
		testing.field = "testing";
		cut.save("test", testing);
	}
	
	@Test
	public void getObject() throws Exception{
		TestObject object = (TestObject) cut.retrieve("test");
	}
}
