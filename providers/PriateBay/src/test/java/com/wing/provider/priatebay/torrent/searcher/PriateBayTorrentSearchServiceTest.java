package com.wing.provider.priatebay.torrent.searcher;

import org.junit.Before;
import org.junit.Test;

import com.wing.provider.priatebay.proxy.ProxyService;

public class PriateBayTorrentSearchServiceTest {
	private PriateBayTorrentSearchService cut;

	@Before
	public void setup() {
		cut = new PriateBayTorrentSearchService(new ProxyService());
	}

	@Test
	public void verifyTorrentSearch() throws Exception {
		cut.searchTorrent("bones", 10, 20);
	}
}
