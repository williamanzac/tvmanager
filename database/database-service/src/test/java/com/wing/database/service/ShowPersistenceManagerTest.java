package com.wing.database.service;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import org.hsqldb.lib.FileUtil;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.wing.database.model.Show;

public class ShowPersistenceManagerTest {
	private static ShowPersistenceManager cut;
	private Connection con;

	@BeforeClass
	public static void setup() {
		cut = new ShowPersistenceManager();
	}

	@Before
	public void setupCon() throws Exception {
		con = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost:1234/tvdb", "SA", "");
	}

	@AfterClass
	public static void cleanup() {
		for (final File file : FileUtil.getDatabaseFileList("./db")) {
			if (!file.delete()) {
				file.deleteOnExit();
			}
		}
	}

	@Test
	public void verifyShowTables() throws Exception {
		final PreparedStatement statement = con.prepareStatement("select * from shows");
		final ResultSet resultSet = statement.executeQuery();
		Assert.assertNotNull(resultSet);
	}

	@Test
	public void verifySavingShow() throws Exception {
		final Show show = new Show();
		show.setId(12345);
		show.setName("Test");
		cut.save(show);

		final Show show2 = cut.retrieve("12345");
		Assert.assertEquals(12345, show2.getId());
	}

	@Test
	public void verifyDeletingShow() throws Exception {
		final Show show = new Show();
		show.setId(12345);
		show.setName("Test");
		cut.save(show);

		cut.delete("12345");

		final Show show2 = cut.retrieve("12345");
		Assert.assertNull(show2);
	}

	@Test
	public void verifyListingShows() throws Exception {
		final Show show1 = new Show();
		show1.setId(12345);
		show1.setName("Test1");
		cut.save(show1);

		final Show show2 = new Show();
		show2.setId(54321);
		show2.setName("Test2");
		cut.save(show2);

		final List<Show> list = cut.list();
		Assert.assertNotNull(list);
		Assert.assertEquals(2, list.size());
	}
}
