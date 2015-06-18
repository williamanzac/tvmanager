package com.wing.torrent.downloader;

import static javax.swing.UIManager.getSystemLookAndFeelClassName;
import static javax.swing.UIManager.setLookAndFeel;

import java.awt.EventQueue;

import javax.swing.JFrame;

import com.wing.configuration.service.ConfigurationService;
import com.wing.database.service.TorrentPersistenceManager;

public class Main {

	public static void main(final String[] args) throws Exception {
		setLookAndFeel(getSystemLookAndFeelClassName());
		final TorrentPersistenceManager torrentPersistenceManager = new TorrentPersistenceManager();
		final ConfigurationService configurationService = new ConfigurationService();
		EventQueue
				.invokeLater(() -> {
					try {
						final DownloaderClientUI frame = new DownloaderClientUI(torrentPersistenceManager,
								configurationService); 
						frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
						frame.setVisible(true);
					} catch (final Exception e) {
						e.printStackTrace();
					}
				});
	}
}
