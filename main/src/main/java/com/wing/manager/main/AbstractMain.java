package com.wing.manager.main;

import com.wing.configuration.service.ConfigurationService;
import com.wing.database.service.ShowPersistenceManager;
import com.wing.database.service.TorrentPersistenceManager;
import com.wing.manager.service.DefaultManagerService;
import com.wing.provider.torrentproject.torrent.searcher.TorrentProjectTorrentSearchService;
import com.wing.provider.tvrage.searcher.TvRageShowSearchService;
import com.wing.provider.utorrent.downloader.UTorrentDownloader;
import com.wing.torrent.copier.TorrentCopier;
import com.wing.torrent.searcher.TorrentSearchService;

public class AbstractMain {

	static final TvRageShowSearchService searchService = new TvRageShowSearchService();
	static final ShowPersistenceManager persistenceManager = new ShowPersistenceManager();
	static final TorrentPersistenceManager torrentPersistenceManager = new TorrentPersistenceManager();
	static final TorrentSearchService torrentSearchService = new TorrentProjectTorrentSearchService();
	static final ConfigurationService configurationService = new ConfigurationService();
	protected static final DefaultManagerService managerService = new DefaultManagerService(searchService,
			persistenceManager, torrentPersistenceManager, torrentSearchService, configurationService);
	protected static UTorrentDownloader torrentDownloader;
	protected static TorrentCopier torrentCopier;

	static {
		try {
			torrentDownloader = new UTorrentDownloader(managerService);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			torrentCopier = new TorrentCopier();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
