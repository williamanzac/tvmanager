package com.wing.manager.main;

import static javax.swing.UIManager.getSystemLookAndFeelClassName;
import static javax.swing.UIManager.setLookAndFeel;

import java.awt.EventQueue;

import javax.swing.JFrame;

import com.wing.database.service.ShowPersistenceManager;
import com.wing.database.service.TorrentPersistenceManager;
import com.wing.manager.service.DefaultManagerService;
import com.wing.manager.ui.ManagerWindow;
import com.wing.provider.torrentproject.torrent.searcher.TorrentProjectTorrentSearchService;
import com.wing.provider.tvrage.searcher.TvRageShowSearchService;
import com.wing.torrent.searcher.TorrentSearchService;

public class Main {

	public static void main(final String[] args) throws Exception {
		setLookAndFeel(getSystemLookAndFeelClassName());
		final TvRageShowSearchService searchService = new TvRageShowSearchService();
		final ShowPersistenceManager persistenceManager = new ShowPersistenceManager();
		final TorrentPersistenceManager torrentPersistenceManager = new TorrentPersistenceManager();
		final TorrentSearchService torrentSearchService = new TorrentProjectTorrentSearchService();
		final DefaultManagerService managerService = new DefaultManagerService(searchService, persistenceManager,
				torrentPersistenceManager, torrentSearchService);
		EventQueue.invokeLater(() -> {
			try {
				final ManagerWindow frame = new ManagerWindow(managerService, searchService);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		});
		// EventQueue.invokeLater(new Runnable() {
		// public void run() {
		// try {
		// final DownloaderClientUI frame = new DownloaderClientUI(
		// managerService);
		// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// frame.setVisible(true);
		// } catch (final Exception e) {
		// e.printStackTrace();
		// }
		// }
		// });
	}
}
