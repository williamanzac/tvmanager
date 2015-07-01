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

import com.wing.database.model.Torrent;
import com.wing.provider.priatebay.proxy.ProxyService;
import com.wing.torrent.searcher.TorrentSearchService;

public class PriateBayTorrentSearchService implements TorrentSearchService {

	private final ProxyService proxyService;
	private final SAXReader htmlReader = new SAXReader(new Parser());

	private static final String searchPattern = "{0}/search/{1}/0/99/0"; // {1}==bones%20s10e20

	public PriateBayTorrentSearchService(final ProxyService proxyService) {
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

	@Override
	public List<Torrent> searchTorrent(final String showName, final int season, final int episode) throws Exception {
		return searchTorrent(episodeQuery(showName, season, episode));
	}

	private List<Torrent> parseXML(final Document document) {
		System.out.println(document.asXML());
		return null;
	}

	private String episodeQuery(final String show, final int season, final int episode) throws Exception {
		final String formatString = String.format("%1$s s%2$02de%3$02d", show, season, episode);
		return formatString;
	}

	@Override
	public List<Torrent> searchTorrent(String query) throws Exception {
		query = URLEncoder.encode(query, "UTF-8");
		final String url = MessageFormat.format(searchPattern, baseURL(), query);
		final InputStream response = getResponse(url);
		final Document document = htmlReader.read(response);
		return parseXML(document);
	}
}
