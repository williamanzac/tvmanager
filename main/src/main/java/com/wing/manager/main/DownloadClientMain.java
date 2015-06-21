package com.wing.manager.main;

import static java.awt.EventQueue.invokeLater;
import static javax.swing.UIManager.getSystemLookAndFeelClassName;
import static javax.swing.UIManager.setLookAndFeel;

import javax.swing.JFrame;

import com.wing.provider.utorrent.downloader.UTorrentDownloader;
import com.wing.torrent.downloader.ui.DownloaderClientUI;

public class DownloadClientMain extends AbstractMain {
	private static UTorrentDownloader torrentDownloader;

	public static void main(final String[] args) throws Exception {
		setLookAndFeel(getSystemLookAndFeelClassName());
		invokeLater(() -> {
			try {
				torrentDownloader = new UTorrentDownloader(managerService);
				final DownloaderClientUI frame = new DownloaderClientUI(managerService, torrentDownloader);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		});
	}
}
