package com.wing.torrent.copier;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.UUID;

import org.hsqldb.lib.FileUtil;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TaskPersistenceManagerTest {
	private static TaskPersistenceManager cut;
	private Connection con;

	@BeforeClass
	public static void setup() {
		cut = new TaskPersistenceManager();
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
	public void verifyTaskTables() throws Exception {
		final PreparedStatement statement = con.prepareStatement("select * from filetasks");
		final ResultSet resultSet = statement.executeQuery();
		Assert.assertNotNull(resultSet);
	}

	@Test
	public void verifySavingTask() throws Exception {
		final CopyTask torrent = new CopyTask();
		torrent.setSource(new File("test.tst"));
		final UUID id = torrent.getId();
		cut.save(torrent);

		final CopyTask torrent2 = (CopyTask) cut.retrieve(id.toString());
		Assert.assertEquals("test.tst", torrent2.getSource().getName());
	}

	@Test
	public void verifyDeletingTask() throws Exception {
		final CopyTask torrent = new CopyTask();
		torrent.setSource(new File("test.tst"));
		final UUID id = torrent.getId();
		cut.save(torrent);

		cut.delete(id.toString());

		final CopyTask torrent2 = (CopyTask) cut.retrieve(id.toString());
		Assert.assertNull(torrent2);
	}

	@Test
	public void verifyListingTasks() throws Exception {
		final CopyTask torrent1 = new CopyTask();
		torrent1.setSource(new File("test1.tst"));
		cut.save(torrent1);

		final CopyTask torrent2 = new CopyTask();
		torrent2.setSource(new File("test2.tst"));
		cut.save(torrent2);

		final List<FileTask> list = cut.list();
		Assert.assertNotNull(list);
		Assert.assertEquals(2, list.size());
	}
}
