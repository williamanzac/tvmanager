package com.wing.provider.torrentproject.torrent.searcher;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.wing.database.model.Torrent;

public class TorrentProjectTorrentSearchServiceTest {
	private TorrentProjectTorrentSearchService cut;

	@Before
	public void setup() {
		cut = new TorrentProjectTorrentSearchService();
	}

	@Test
	@Ignore
	public void verifyTorrentSearch() throws Exception {
		List<Torrent> list = cut.searchTorrent("bones", 10, 20);
		Assert.assertNotNull(list);
		Assert.assertEquals(15, list.size());
	}

	@Test
	public void verifyParsingRSS() throws Exception {
		final SAXReader reader = new SAXReader();
		final Document document = reader.read(getClass().getResourceAsStream("/torrentproject.rss.xml"));
		List<Torrent> list = cut.parseXML(document);
		Assert.assertNotNull(list);
		Assert.assertEquals(15, list.size());
	}
}
