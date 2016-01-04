package com.wing.provider.thetvdb.search;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.omertron.thetvdbapi.TheTVDBApi;
import com.omertron.thetvdbapi.model.Series;
import com.wing.configuration.service.ConfigurationService;
import com.wing.database.model.Episode;
import com.wing.database.model.Show;
import com.wing.search.service.ShowSearchService;

public class TheTVDBSearchService implements ShowSearchService {
	private final String apiKey;

	public TheTVDBSearchService(final ConfigurationService configurationService) throws Exception {
		apiKey = configurationService.loadConfiguration().tvdbApiKey;
	}

	@Override
	public List<Show> searchShow(final String show) throws Exception {
		final TheTVDBApi api = new TheTVDBApi(apiKey);
		final List<Series> series = api.searchSeries(show, null);
		System.out.println(series);
		final List<Show> shows = new ArrayList<>();
		for (final Series series2 : series) {
			final Show show2 = seriesToShow(series2);
			shows.add(show2);
		}
		return shows;
	}

	private Show seriesToShow(final Series series) {
		final Show show = new Show();
		show.setClassification(series.getContentRating());
		// show.setCountry(series.);
		// show.setEnded(series.);
		show.setGenres(series.getGenres());
		show.setId(Integer.parseInt(series.getId()));
		// show.setLink(series.);
		show.setName(series.getSeriesName());
		// show.setSeasons(series.get);
		show.setStarted(series.getFirstAired());
		show.setStatus(series.getStatus());
		return show;
	}

	@Override
	public List<Episode> getEpisodeList(final int showId) throws Exception {
		final TheTVDBApi api = new TheTVDBApi(apiKey);
		final List<com.omertron.thetvdbapi.model.Episode> allEpisodes = api
				.getAllEpisodes(String.valueOf(showId), null);
		System.out.println(allEpisodes);
		final List<Episode> episodes = new ArrayList<>();
		for (final com.omertron.thetvdbapi.model.Episode series2 : allEpisodes) {
			final Episode episode = toEpisode(series2);
			episodes.add(episode);
		}
		return episodes;
	}

	private Episode toEpisode(final com.omertron.thetvdbapi.model.Episode series2) throws ParseException {
		final Episode episode = new Episode();
		final DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		if (StringUtils.isNoneBlank(series2.getFirstAired())) {
			episode.setAirdate(format.parse(series2.getFirstAired()));
		}
		if (StringUtils.isNoneBlank(series2.getAbsoluteNumber())) {
			episode.setEpnum(Integer.parseInt(series2.getAbsoluteNumber()));
		}
		// episode.setLink(link);
		episode.setNumber(series2.getEpisodeNumber());
		episode.setSeason(series2.getSeasonNumber());
		// episode.setShowId(showId);
		// episode.setState(state);
		episode.setTitle(series2.getEpisodeName());
		return episode;
	}
}
