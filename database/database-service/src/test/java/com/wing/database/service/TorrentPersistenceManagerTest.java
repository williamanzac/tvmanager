package com.wing.database.service;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.List;

import org.hsqldb.lib.FileUtil;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.wing.database.model.Torrent;

public class TorrentPersistenceManagerTest {
	private static TorrentPersistenceManager cut;
	private Connection con;

	@BeforeClass
	public static void setup() {
		cut = new TorrentPersistenceManager();
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
	public void verifyTorrentTables() throws Exception {
		final PreparedStatement statement = con.prepareStatement("select * from torrents");
		final ResultSet resultSet = statement.executeQuery();
		Assert.assertNotNull(resultSet);
	}

	@Test
	public void verifySavingTorrent() throws Exception {
		final Torrent torrent = new Torrent();
		torrent.setHash("12345");
		torrent.setTitle("Test");
		torrent.setPubDate(new Date());
		cut.save(torrent);

		final Torrent torrent2 = cut.retrieve("12345");
		Assert.assertEquals("12345", torrent2.getHash());
	}

	@Test
	public void verifyDeletingTorrent() throws Exception {
		final Torrent torrent = new Torrent();
		torrent.setHash("12345");
		torrent.setTitle("Test");
		torrent.setPubDate(new Date());
		cut.save(torrent);

		cut.delete("12345");

		final Torrent torrent2 = cut.retrieve("12345");
		Assert.assertNull(torrent2);
	}

	@Test
	public void verifyListingTorrents() throws Exception {
		final Torrent torrent1 = new Torrent();
		torrent1.setHash("12345");
		torrent1.setTitle("Test1");
		torrent1.setPubDate(new Date());
		cut.save(torrent1);

		final Torrent torrent2 = new Torrent();
		torrent2.setHash("54321");
		torrent2.setTitle("Test2");
		torrent2.setPubDate(new Date());
		cut.save(torrent2);

		final List<Torrent> list = cut.list();
		Assert.assertNotNull(list);
		Assert.assertEquals(2, list.size());
	}
}
