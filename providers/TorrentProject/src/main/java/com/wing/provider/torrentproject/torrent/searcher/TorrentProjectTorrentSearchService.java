package com.wing.provider.torrentproject.torrent.searcher;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.wing.database.model.Torrent;
import com.wing.torrent.searcher.TorrentSearchService;

public class TorrentProjectTorrentSearchService implements TorrentSearchService {

	private static final String searchPattern = "https://torrentproject.se/?s={0}&out=rss";// ubuntu+dvd
	private static final Pattern seedsPattern = Pattern.compile("<seeds>(.*)</seeds>");
	private static final Pattern leechersPattern = Pattern.compile("<leechers>(.*)</leechers>");
	private static final Pattern sizePattern = Pattern.compile("<size>(.*)</size>");
	private static final Pattern hashPattern = Pattern.compile("<hash>(.*)</hash>");
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss ZZZZ");

	private final SAXReader xmlReader = new SAXReader();

	@Override
	public List<Torrent> searchTorrent(final String showName, final int season, final int episode) throws Exception {
		final String url = MessageFormat.format(searchPattern, searchURL(showName, season, episode));
		final InputStream response = getResponse(url);
		final Document document = xmlReader.read(response);
		return parseXML(document);
	}

	@SuppressWarnings("unchecked")
	List<Torrent> parseXML(final Document document) throws DocumentException, MalformedURLException, ParseException {
		System.out.println(document.asXML());
		final List<Element> itemNodes = document.selectNodes("//item");
		if (itemNodes == null || itemNodes.isEmpty()) {
			return null;
		}
		final List<Torrent> torrents = new ArrayList<>();
		for (final Element itemNode : itemNodes) {
			final Element titleNode = (Element) itemNode.selectSingleNode("title");
			final Element enclosureNode = (Element) itemNode.selectSingleNode("enclosure");
			final Element descriptionNode = (Element) itemNode.selectSingleNode("description");
			final List<Element> categoryNodes = itemNode.selectNodes("category");
			final Element dateElement = (Element) itemNode.selectSingleNode("pubDate");

			final String title = titleNode.getTextTrim();
			final String url = enclosureNode.attributeValue("url");
			final String description = descriptionNode.getTextTrim();

			final Set<String> categories = new HashSet<>();
			for (final Element categoryNode : categoryNodes) {
				categories.add(categoryNode.getTextTrim());
			}
			final Date date = dateFormat.parse(dateElement.getTextTrim());

			final Torrent torrent = new Torrent();
			torrent.setTitle(title);
			torrent.setUrl(new URL(url));
			parseDescription(description, torrent);
			torrent.setCategories(categories);
			torrent.setPubDate(date);

			torrents.add(torrent);
		}
		return torrents;
	}

	private void parseDescription(final String description, final Torrent torrent) throws DocumentException {
		final Matcher seedsMatcher = seedsPattern.matcher(description);
		if (seedsMatcher.find()) {
			final int seeds = Integer.parseInt(seedsMatcher.group(1));
			torrent.setSeeds(seeds);
		}
		final Matcher leechersMatcher = leechersPattern.matcher(description);
		if (leechersMatcher.find()) {
			final int leechers = Integer.parseInt(leechersMatcher.group(1));
			torrent.setLeechers(leechers);
		}
		final Matcher sizeMatcher = sizePattern.matcher(description);
		if (sizeMatcher.find()) {
			final long size = Long.parseLong(sizeMatcher.group(1));
			torrent.setSize(size);
		}
		final Matcher hashMatcher = hashPattern.matcher(description);
		if (hashMatcher.find()) {
			final String hash = hashMatcher.group(1);
			torrent.setHash(hash);
		}
	}

	protected InputStream getResponse(final String url) throws Exception {
		final HttpClient client = new HttpClient();
		final GetMethod method = new GetMethod(url);
		// method.getParams().setCookiePolicy(CookiePolicy.RFC_2109);
		client.executeMethod(method);
		return method.getResponseBodyAsStream();
	}

	private String searchURL(final String show, final int season, final int episode) throws Exception {
		final String formatString = String.format("%1$s s%2$02de%3$02d", show, season, episode);
		return formatString.replaceAll("\\s", "+");
	}
}
