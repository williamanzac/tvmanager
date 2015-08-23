package com.wing.manager.service;

import static java.text.MessageFormat.format;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		final Map<Integer, Episode> episodeMap = new HashMap<>();
		for (final Episode episode : filteredList) {
			episodeMap.put(episode.getEpnum(), episode);
		}
		final List<Episode> newList = searchService.getEpisodeList(show.getId());
		for (final Episode episode : newList) {
			final Episode orgEpi = episodeMap.get(episode.getEpnum());
			if (orgEpi == null) {
				episode.setShowId(show.getId());
				episodeMap.put(episode.getEpnum(), episode);
			} else {
				orgEpi.setAirdate(episode.getAirdate());
				orgEpi.setTitle(episode.getTitle());
			}
		}
		for (final Episode episode : episodeMap.values()) {
			saveEpisode(episode);
		}
	}

	@Override
	public void removeEpisode(int showId, int epnum) throws Exception {
		episodePersistenceManager.delete(format("{0,number,##}{1,number,##}", showId, epnum));
	}

	@Override
	public List<Episode> listEpisodes(int showId) throws Exception {
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
