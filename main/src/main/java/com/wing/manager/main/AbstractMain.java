package com.wing.manager.main;

import com.wing.configuration.service.ConfigurationService;
import com.wing.database.service.EpisodePersistenceManager;
import com.wing.database.service.ShowPersistenceManager;
import com.wing.database.service.TorrentPersistenceManager;
import com.wing.manager.service.DefaultManagerService;
import com.wing.manager.service.ManagerService;
import com.wing.provider.thetvdb.search.TheTVDBSearchService;
import com.wing.provider.torrentproject.torrent.searcher.TorrentProjectTorrentSearchService;
import com.wing.provider.utorrent.downloader.UTorrentDownloader;
import com.wing.torrent.copier.FileManager;
import com.wing.torrent.searcher.TorrentSearchService;

public class AbstractMain {

	static TheTVDBSearchService searchService;
	static final ShowPersistenceManager persistenceManager = new ShowPersistenceManager();
	static final TorrentPersistenceManager torrentPersistenceManager = new TorrentPersistenceManager();
	static final EpisodePersistenceManager episodePersistenceManager = new EpisodePersistenceManager();
	static final TorrentSearchService torrentSearchService = new TorrentProjectTorrentSearchService();
	static final ConfigurationService configurationService = new ConfigurationService();
	protected static ManagerService managerService;
	// static ManagerService managerService = ApiProxy.create(ManagerService.class, "http://localhost:8080");
	protected static UTorrentDownloader torrentDownloader;
	protected static FileManager torrentCopier;

	static {
		try {
			searchService = new TheTVDBSearchService(configurationService);
		} catch (final Exception e) {
			e.printStackTrace();
		}

		managerService = new DefaultManagerService(searchService, persistenceManager, torrentPersistenceManager,
				torrentSearchService, configurationService, episodePersistenceManager);

		try {
			torrentDownloader = new UTorrentDownloader(managerService);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		try {
			torrentCopier = new FileManager();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}
