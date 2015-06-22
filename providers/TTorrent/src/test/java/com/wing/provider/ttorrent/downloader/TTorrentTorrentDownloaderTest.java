package com.wing.provider.ttorrent.downloader;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Test;

public class TTorrentTorrentDownloaderTest {

	@Test
	public void verifyHandleRedirect() throws Exception {
		final HttpClient client = new HttpClient();
		final GetMethod method = new GetMethod(
				"http://torrentproject.se/torrent/D809D0F134A455262930FB65D8B5858FF7893C3A.torrent");
		client.executeMethod(method);
		for (Header header : method.getResponseHeaders()) {
			System.out.println(header);
		}
		System.out.println(method.getResponseBodyAsString());
	}
}
