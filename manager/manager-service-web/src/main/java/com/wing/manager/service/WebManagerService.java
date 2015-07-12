package com.wing.manager.service;

import java.util.List;

import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import com.wing.configuration.service.ConfigurationService;
import com.wing.database.model.Configuration;
import com.wing.database.model.Show;
import com.wing.database.model.Torrent;
import com.wing.database.service.ShowPersistenceManager;
import com.wing.database.service.TorrentPersistenceManager;
import com.wing.provider.torrentproject.torrent.searcher.TorrentProjectTorrentSearchService;
import com.wing.provider.tvrage.searcher.TvRageShowSearchService;
import com.wing.torrent.searcher.TorrentSearchService;

public class WebManagerService extends DefaultManagerService {

	static final TvRageShowSearchService searchService = new TvRageShowSearchService();
	static final ShowPersistenceManager showManager = new ShowPersistenceManager();
	static final TorrentPersistenceManager torrentPersistenceManager = new TorrentPersistenceManager();
	static final TorrentSearchService torrentSearchService = new TorrentProjectTorrentSearchService();
	static final ConfigurationService configurationService = new ConfigurationService();

	public WebManagerService() {
		super(searchService, showManager, torrentPersistenceManager, torrentSearchService, configurationService);
	}

	@Override
	public List<Show> searchShow(@PathParam("show") final String show) throws Exception {
		return super.searchShow(show);
	}

	@Override
	public List<Show> listShows() throws Exception {
		return super.listShows();
	}

	@Override
	public void saveShow(final Show show) throws Exception {
		super.saveShow(show);
	}

	@Override
	public void removeShow(final Show show) throws Exception {
		super.removeShow(show);
	}

	@Override
	public void updateEpisodes(final Show show) throws Exception {
		super.updateEpisodes(show);
	}

	@Override
	public List<Torrent> listTorrents() throws Exception {
		return super.listTorrents();
	}

	@Override
	public void saveTorrent(final Torrent torrent) throws Exception {
		super.saveTorrent(torrent);
	}

	@Override
	public void removeTorrent(Torrent torrent) throws Exception {
		super.removeTorrent(torrent);
	}

	@Override
	public List<Torrent> searchForEpisode(@QueryParam("name") final String name,
			@QueryParam("season") final int season, @QueryParam("episode") final int episode) throws Exception {
		return super.searchForEpisode(name, season, episode);
	}

	@Override
	public Configuration loadConfiguration() throws Exception {
		return super.loadConfiguration();
	}

	@Override
	public void saveConfiguration(Configuration configuration) throws Exception {
		super.saveConfiguration(configuration);
	}

	@Override
	public Torrent getTorrent(@PathParam("hash") String hash) throws Exception {
		return super.getTorrent(hash);
	}

	@Override
	public List<Torrent> searchFor(@QueryParam("query") final String query) throws Exception {
		return super.searchFor(query);
	}
}
