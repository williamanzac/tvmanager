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

import com.wing.database.model.Episode;

public class EpisodePersistenceManagerTest {
	private static EpisodePersistenceManager cut;
	private Connection con;

	@BeforeClass
	public static void setup() {
		cut = new EpisodePersistenceManager();
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
	public void verifyEpisodeTables() throws Exception {
		final PreparedStatement statement = con.prepareStatement("select * from episodes");
		final ResultSet resultSet = statement.executeQuery();
		Assert.assertNotNull(resultSet);
	}

	@Test
	public void verifySavingEpisode() throws Exception {
		final Episode episode = new Episode();
		episode.setShowId(12345);
		episode.setEpnum(1);
		episode.setTitle("Test");
		cut.save(episode);

		final Episode episode2 = cut.retrieve("12345-1");
		Assert.assertEquals(12345, episode2.getShowId());
		Assert.assertEquals(1, episode2.getEpnum());
	}

	@Test
	public void verifyDeletingEpisode() throws Exception {
		final Episode episode = new Episode();
		episode.setShowId(12345);
		episode.setEpnum(1);
		episode.setTitle("Test");
		cut.save(episode);

		cut.delete("12345-1");

		final Episode episode2 = cut.retrieve("12345-1");
		Assert.assertNull(episode2);
	}

	@Test
	public void verifyListingEpisodes() throws Exception {
		final Episode episode1 = new Episode();
		episode1.setShowId(12345);
		episode1.setEpnum(1);
		episode1.setTitle("Test1");
		cut.save(episode1);

		final Episode episode2 = new Episode();
		episode2.setShowId(54321);
		episode2.setEpnum(5);
		episode2.setTitle("Test2");
		cut.save(episode2);

		final List<Episode> list = cut.list();
		Assert.assertNotNull(list);
		Assert.assertEquals(2, list.size());
	}
}
