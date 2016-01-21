package com.wing.provider.torrentproject.torrent.searcher;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.ccil.cowan.tagsoup.Parser;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.wing.database.model.Torrent;
import com.wing.torrent.searcher.TorrentSearchService;

public class TorrentProjectTorrentSearchService implements TorrentSearchService {

	private static final String searchPattern = "https://torrentproject.se/?s={0}";// ubuntu+dvd //&out=rss
	// private static final Pattern seedsPattern = Pattern.compile("<seeds>(.*)</seeds>");
	// private static final Pattern leechersPattern = Pattern.compile("<leechers>(.*)</leechers>");
	// private static final Pattern sizePattern = Pattern.compile("<size>(.*)</size>");
	// private static final Pattern hashPattern = Pattern.compile("<hash>(.*)</hash>");
	// private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss ZZZZ");
	private static final Pattern hashPattern = Pattern.compile("magnet:\\?xt=urn:btih:([0-9a-z]+)&");

	// private final SAXReader xmlReader = new SAXReader();
	private final SAXReader htmlReader = new SAXReader(new Parser());

	private String getMagnetLink(final String url) throws Exception {
		final InputStream response = getResponse(url);
		final Document document = htmlReader.read(response);
		return parseDetail(document);
	}

	@SuppressWarnings("unchecked")
	String parseDetail(final Document document) throws UnsupportedEncodingException {
		System.out.println(document.asXML());
		final List<Element> linkNodes = document.selectNodes("//html:a[contains(@href,'magnet:')]");
		for (final Element element : linkNodes) {
			final String link = element.attributeValue("href");
			System.out.println(link);
			if (link.startsWith("magnet:")) {
				return link;
			}
		}
		return null;
	}

	@Override
	public List<Torrent> searchTorrent(final String showName, final int season, final int episode) throws Exception {
		final String query = episodeQuery(showName, season, episode);
		return searchTorrent(query);
	}

	@SuppressWarnings("unchecked")
	List<Torrent> parseXML(final Document document) throws Exception {
		System.out.println(document.asXML());
		final List<Element> itemNodes = document.selectNodes("//html:div[@class='torrent']");
		if (itemNodes == null || itemNodes.isEmpty()) {
			return null;
		}
		final List<Torrent> torrents = new ArrayList<>();
		for (final Element itemNode : itemNodes) {
			final Element titleNode = (Element) itemNode.selectSingleNode("html:h3/html:a");
			final Element categoryNode = (Element) itemNode
					.selectSingleNode("html:div[@class='gl sti']/html:span[@class='bc cate']");
			// final Element dateElement = (Element)
			// itemNode.selectSingleNode("html:div[@class='gl sti']/html:span[@class='bc cated']");
			final Element seedersNode = (Element) itemNode
					.selectSingleNode("html:div[@class='gl sti']/html:span[@class='bc seeders']/html:span/html:b");
			final Element leechersNode = (Element) itemNode
					.selectSingleNode("html:div[@class='gl sti']/html:span[@class='bc leechers']/html:span/html:b");

			final String title = titleNode.getTextTrim();
			final String url = getMagnetLink(titleNode.attributeValue("href"));

			final Set<String> categories = new HashSet<>();
			for (final String category : categoryNode.getTextTrim().split("\\s+")) {
				categories.add(category);
			}
			// final Date date = dateFormat.parse(dateElement.getTextTrim());
			final Matcher m = hashPattern.matcher(url);
			String hash = null;
			if (m.find()) {
				hash = m.group(1);
			}

			final Torrent torrent = new Torrent();
			torrent.setTitle(title);
			torrent.setUrl(url);
			torrent.setLeechers(Integer.parseInt(leechersNode.getTextTrim()));
			torrent.setSeeds(Integer.parseInt(seedersNode.getTextTrim()));
			torrent.setHash(hash);
			// torrent.setSize(size);
			torrent.setCategories(categories);
			// torrent.setPubDate(date);

			torrents.add(torrent);
		}
		return torrents;
	}

	// private void parseDescription(final String description, final Torrent torrent) throws DocumentException {
	// final Matcher seedsMatcher = seedsPattern.matcher(description);
	// if (seedsMatcher.find()) {
	// final int seeds = Integer.parseInt(seedsMatcher.group(1));
	// torrent.setSeeds(seeds);
	// }
	// final Matcher leechersMatcher = leechersPattern.matcher(description);
	// if (leechersMatcher.find()) {
	// final int leechers = Integer.parseInt(leechersMatcher.group(1));
	// torrent.setLeechers(leechers);
	// }
	// final Matcher sizeMatcher = sizePattern.matcher(description);
	// if (sizeMatcher.find()) {
	// final long size = Long.parseLong(sizeMatcher.group(1));
	// torrent.setSize(size);
	// }
	// final Matcher hashMatcher = hashPattern.matcher(description);
	// if (hashMatcher.find()) {
	// final String hash = hashMatcher.group(1);
	// torrent.setHash(hash);
	// }
	// }

	protected InputStream getResponse(final String url) throws Exception {
		final HttpClient client = new HttpClient();
		final GetMethod method = new GetMethod(url);
		// method.getParams().setCookiePolicy(CookiePolicy.RFC_2109);
		client.executeMethod(method);
		return method.getResponseBodyAsStream();
	}

	private String episodeQuery(final String show, final int season, final int episode) throws Exception {
		final String formatString = String.format("%1$s s%2$02de%3$02d", show, season, episode);
		return formatString;
	}

	@Override
	public List<Torrent> searchTorrent(String query) throws Exception {
		query = query.replaceAll("\\s", "+");
		final String url = MessageFormat.format(searchPattern, query);
		final InputStream response = getResponse(url);
		final Document document = htmlReader.read(response);
		final List<Torrent> torrents = parseXML(document);
		sort(torrents);
		return torrents;
	}

	void sort(final List<Torrent> torrents) {
		if (torrents == null) {
			return;
		}
		Collections.sort(torrents, (o1, o2) -> {
			int compare = Integer.compare(o1.getSeeds(), o2.getSeeds()) * -1;
			if (compare == 0) {
				// compare = o1.getPubDate().compareTo(o2.getPubDate());
				// if (compare == 0) {
				compare = o1.getTitle().compareTo(o2.getTitle());
				// }
			}
			return compare;
		});
	}
}
