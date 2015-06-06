package com.wing.provider.priatebay.torrent.searcher;

import java.io.InputStream;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.ccil.cowan.tagsoup.Parser;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import com.wing.provider.priatebay.proxy.ProxyService;
import com.wing.torrent.searcher.TorrentSearchService;

public class PriateBayTorrentSearchService implements TorrentSearchService {

	private final ProxyService proxyService;
	private final SAXReader htmlReader = new SAXReader(new Parser());

	private static final String searchPattern = "{0}/search/{1}/0/99/0"; // {1}==bones%20s10e20

	public PriateBayTorrentSearchService(ProxyService proxyService) {
		this.proxyService = proxyService;
	}

	protected InputStream getResponse(final String url) throws Exception {
		final HttpClient client = new HttpClient();
		final GetMethod method = new GetMethod(url);
		// method.getParams().setCookiePolicy(CookiePolicy.RFC_2109);
		client.executeMethod(method);
		return method.getResponseBodyAsStream();
	}

	private String baseURL() throws Exception {
		return proxyService.listProxies().get(0);
	}

	public List<String> searchTorrent(String showName, int season, int episode)
			throws Exception {
		String url = MessageFormat.format(searchPattern, baseURL(),
				searchURL(showName, season, episode));
		final InputStream response = getResponse(url);
		final Document document = htmlReader.read(response);
		return parseXML(document);
	}

	private List<String> parseXML(Document document) {
		System.out.println(document.asXML());
		return null;
	}

	private String searchURL(String show, final int season, final int episode)
			throws Exception {
		final String formatString = String.format("%1$s s%2$02de%3$02d", show,
				season, episode);
		return URLEncoder.encode(formatString, "UTF-8");
	}

}
