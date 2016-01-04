package com.wing.manager.service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.wing.configuration.service.ConfigurationService;
import com.wing.database.api.PersistenceManager;
import com.wing.database.model.Configuration;
import com.wing.database.model.Episode;
import com.wing.database.model.Show;
import com.wing.database.model.Torrent;
import com.wing.database.service.EpisodePersistenceManager;
import com.wing.search.service.ShowSearchService;
import com.wing.torrent.searcher.TorrentSearchService;

public class DefaultManagerService implements ManagerService {

	private final ShowSearchService searchService;
	private final PersistenceManager<Show> showManager;
	private final PersistenceManager<Torrent> torrentPersistenceManager;
	private final EpisodePersistenceManager episodePersistenceManager;
	private final TorrentSearchService torrentSearchService;
	private final ConfigurationService configurationService;

	public DefaultManagerService(final ShowSearchService searchService, final PersistenceManager<Show> showManager,
			final PersistenceManager<Torrent> torrentPersistenceManager,
			final TorrentSearchService torrentSearchService, final ConfigurationService configurationService,
			final EpisodePersistenceManager episodePersistenceManager) {
		super();
		this.searchService = searchService;
		this.showManager = showManager;
		this.torrentPersistenceManager = torrentPersistenceManager;
		this.torrentSearchService = torrentSearchService;
		this.configurationService = configurationService;
		this.episodePersistenceManager = episodePersistenceManager;
	}

	@Override
	public List<Show> searchShow(final String show) throws Exception {
		return searchService.searchShow(show);
	}

	@Override
	public List<Show> listShows() throws Exception {
		return showManager.list();
	}

	@Override
	public void saveShow(final Show show) throws Exception {
		showManager.save(show);
	}

	@Override
	public void removeShow(final int showId) throws Exception {
		showManager.delete(Integer.toString(showId));
	}

	@Override
	public void updateEpisodes(final int showId) throws Exception {
		final Collection<Episode> filteredList = listEpisodes(showId);
		final Show show = showManager.retrieve(Integer.toString(showId));
		final Map<String, Episode> episodeMap = new HashMap<>();
		for (final Episode episode : filteredList) {
			episodeMap.put(getNumber(episode), episode);
		}
		final List<Episode> newList = searchService.getEpisodeList(show.getId());
		System.out.println(newList);
		for (final Episode episode : newList) {
			System.out.println("episode: " + episode);
			final Episode orgEpi = episodeMap.get(getNumber(episode));
			System.out.println("orgEpi: " + orgEpi);
			if (orgEpi == null) {
				episode.setShowId(show.getId());
				episodeMap.put(getNumber(episode), episode);
			} else {
				orgEpi.setShowId(show.getId());
				orgEpi.setAirdate(episode.getAirdate());
				orgEpi.setTitle(episode.getTitle());
			}
		}
		for (final Entry<String, Episode> entry : episodeMap.entrySet()) {
			saveEpisode(entry.getValue());
		}
	}

	private String getNumber(final Episode episode) {
		return String.format("s%1$02de%2$02d", episode.getSeason(), episode.getNumber());
	}

	@Override
	public void removeEpisode(final int showId, final int season, final int number) throws Exception {
		episodePersistenceManager.delete(showId, season, number);
	}

	@Override
	public List<Episode> listEpisodes(final int showId) throws Exception {
		final List<Episode> allEpisodes = episodePersistenceManager.list(showId);
		// final Collection<Episode> filteredList = Collections2.filter(allEpisodes, new Predicate<Episode>() {
		// @Override
		// public boolean apply(final Episode input) {
		// return input.getShowId() == showId;
		// }
		// });
		// ArrayList<Episode> list = new ArrayList<>(filteredList);
		Collections.sort(allEpisodes);
		return allEpisodes;
	}

	@Override
	public void saveEpisode(final Episode episode) throws Exception {
		episodePersistenceManager.save(episode);
	}

	@Override
	public List<Torrent> listTorrents() throws Exception {
		return torrentPersistenceManager.list();
	}

	@Override
	public void saveTorrent(final Torrent torrent) throws Exception {
		torrentPersistenceManager.save(torrent);
	}

	@Override
	public void removeTorrent(final Torrent torrent) throws Exception {
		torrentPersistenceManager.delete(torrent.getHash());
	}

	@Override
	public List<Torrent> searchForEpisode(final String name, final int season, final int episode) throws Exception {
		return torrentSearchService.searchTorrent(name, season, episode);
	}

	@Override
	public Configuration loadConfiguration() throws Exception {
		return configurationService.loadConfiguration();
	}

	@Override
	public void saveConfiguration(final Configuration configuration) throws Exception {
		configurationService.saveConfiguration(configuration);
	}

	@Override
	public Torrent getTorrent(final String hash) throws Exception {
		return torrentPersistenceManager.retrieve(hash);
	}

	@Override
	public List<Torrent> searchFor(final String query) throws Exception {
		return torrentSearchService.searchTorrent(query);
	}
}
