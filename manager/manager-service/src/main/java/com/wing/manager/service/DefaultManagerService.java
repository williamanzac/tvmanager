package com.wing.manager.service;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.wing.configuration.service.ConfigurationService;
import com.wing.database.api.PersistenceManager;
import com.wing.database.model.Configuration;
import com.wing.database.model.Episode;
import com.wing.database.model.Show;
import com.wing.database.model.Torrent;
import com.wing.search.service.ShowSearchService;
import com.wing.torrent.searcher.TorrentSearchService;

public class DefaultManagerService implements ManagerService {

	private final ShowSearchService searchService;
	private final PersistenceManager<Show> showManager;
	private final PersistenceManager<Torrent> torrentPersistenceManager;
	private final TorrentSearchService torrentSearchService;
	private final ConfigurationService configurationService;

	public DefaultManagerService(final ShowSearchService searchService, final PersistenceManager<Show> showManager,
			final PersistenceManager<Torrent> torrentPersistenceManager,
			final TorrentSearchService torrentSearchService, final ConfigurationService configurationService) {
		super();
		this.searchService = searchService;
		this.showManager = showManager;
		this.torrentPersistenceManager = torrentPersistenceManager;
		this.torrentSearchService = torrentSearchService;
		this.configurationService = configurationService;
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
		showManager.save(Integer.toString(show.getId()), show);
	}

	@Override
	public void removeShow(final Show show) throws Exception {
		showManager.delete(Integer.toString(show.getId()));
	}

	@Override
	public void updateEpisodes(final Show show) throws Exception {
		final SortedSet<Episode> episodeList = new TreeSet<>(show.getEpisodeList());
		final List<Episode> newList = searchService.getEpisodeList(show.getId());
		episodeList.addAll(newList);
		show.setEpisodeList(episodeList);
	}

	@Override
	public List<Torrent> listTorrents() throws Exception {
		return torrentPersistenceManager.list();
	}

	@Override
	public void saveTorrent(final Torrent torrent) throws Exception {
		torrentPersistenceManager.save(torrent.getHash(), torrent);
	}
	
	@Override
	public void removeTorrent(Torrent torrent) throws Exception {
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
	public void saveConfiguration() throws Exception {
		configurationService.saveConfiguration();
	}
}
