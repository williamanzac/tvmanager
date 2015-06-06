package com.wing.provider.tvrage.searcher;

import static java.text.MessageFormat.format;

import java.io.InputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.wing.database.model.Episode;
import com.wing.database.model.Show;
import com.wing.search.service.HttpShowSearchService;

public class TvRageShowSearchService extends HttpShowSearchService {

	private static final String searchURL = "http://services.tvrage.com/feeds/search.php?show={0}";
	private static final String episodeURL = "http://services.tvrage.com/feeds/episode_list.php?sid={0}";
	private final SAXReader xmlReader = new SAXReader();
	private final SimpleDateFormat episodeFormat = new SimpleDateFormat(
			"yyyy-M-d");

	public List<Show> searchShow(final String show) throws Exception {
		final String url = format(searchURL, URLEncoder.encode(show, "UTF-8"));
		final InputStream response = getResponse(url);
		final Document document = xmlReader.read(response);
		return processShowXML(document);
	}

	public List<Episode> getEpisodeList(final int showId) throws Exception {
		final String url = format(episodeURL, Integer.toString(showId));
		final InputStream response = getResponse(url);
		final Document document = xmlReader.read(response);
		return processEpisodeXML(document);
	}

	@SuppressWarnings("unchecked")
	private List<Episode> processEpisodeXML(final Document document)
			throws Exception {
		final List<Element> seasonNodes = document.selectNodes("//Season");
		if (seasonNodes == null || seasonNodes.isEmpty()) {
			return null;
		}
		final List<Episode> episodes = new ArrayList<>();
		for (final Element seasonNode : seasonNodes) {
			final int season = Integer
					.parseInt(seasonNode.attributeValue("no"));
			final List<Element> episodeNodes = seasonNode
					.selectNodes("episode");
			for (final Element episodeNode : episodeNodes) {
				final Episode episode = new Episode();
				final Element numberNode = (Element) episodeNode
						.selectSingleNode("epnum");
				final Element seasonNumNode = (Element) episodeNode
						.selectSingleNode("seasonnum");
				final Element airdateNode = (Element) episodeNode
						.selectSingleNode("airdate");
				final Element titleNode = (Element) episodeNode
						.selectSingleNode("title");

				final int number = Integer.parseInt(numberNode.getTextTrim());
				final int seasonNum = Integer.parseInt(seasonNumNode
						.getTextTrim());
				final String title = titleNode.getTextTrim();
				String textTrim = airdateNode.getTextTrim();
				final Date airdate = episodeFormat.parse(textTrim);

				episode.setAirdate(airdate);
				episode.setEpnum(number);
				episode.setSeason(season);
				episode.setNumber(seasonNum);
				episode.setTitle(title);

				episodes.add(episode);
			}
		}
		return episodes;
	}

	@SuppressWarnings("unchecked")
	List<Show> processShowXML(final Document document) {
		final List<Element> showNodes = document.selectNodes("//show");
		if (showNodes == null || showNodes.isEmpty()) {
			return null;
		}
		final List<Show> shows = new ArrayList<>();
		for (final Element showNode : showNodes) {
			final Show show = new Show();
			final Element idNode = (Element) showNode
					.selectSingleNode("showid");
			final Element nameNode = (Element) showNode
					.selectSingleNode("name");
			final Element statusNode = (Element) showNode
					.selectSingleNode("status");

			final int showId = Integer.parseInt(idNode.getTextTrim());
			final String name = nameNode.getTextTrim();
			final String status = statusNode.getTextTrim();

			show.setId(showId);
			show.setName(name);
			show.setStatus(status);

			shows.add(show);
		}

		return shows;
	}
}