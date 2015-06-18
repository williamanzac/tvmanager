package com.wing.torrent.downloader;

import static javax.swing.UIManager.getSystemLookAndFeelClassName;
import static javax.swing.UIManager.setLookAndFeel;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;

import com.wing.configuration.service.ConfigurationService;
import com.wing.database.model.Torrent;
import com.wing.database.service.TorrentPersistenceManager;
import com.wing.torrent.downloader.components.ProgressCellRender;
import com.wing.torrent.downloader.components.TorrentTableModel;

public class DownloaderClientUI extends JFrame {

	private static final long serialVersionUID = 5881242422562272594L;

	private final JPanel contentPane;

	private final TorrentTableModel tableModel;

	private final TorrentPersistenceManager torrentPersistenceManager;

	private final ConfigurationService configurationService;

	private final TorrentDownloader torrentDownloader;

	private class ButtonActions implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			final String command = e.getActionCommand();
			switch (command) {
			case "addTorrent":
				final JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.showOpenDialog(DownloaderClientUI.this);
				final File file = chooser.getSelectedFile();
				if (file != null) {
					try {
						final Torrent torrent = new Torrent();
						final com.turn.ttorrent.common.Torrent ttorrent = com.turn.ttorrent.common.Torrent.load(file);
						torrent.setUrl(file.toURI().toURL());
						torrent.setTitle(ttorrent.getName());
						torrent.setHash(ttorrent.getHexInfoHash());
						tableModel.add(torrent);
						torrentPersistenceManager.save(torrent.getHash(), torrent);
					} catch (final Exception e1) {
						e1.printStackTrace();
					}
				}
				break;
			case "configuration":
				// TODO show configuration dialog
			}
		}
	}

	private class WindowActions extends WindowAdapter {
		@Override
		public void windowClosing(final WindowEvent e) {
			try {
				torrentDownloader.stop();
			} catch (final InterruptedException e1) {
				e1.printStackTrace();
			}
			super.windowClosing(e);
		}
	}

	/**
	 * Launch the application.
	 *
	 * @throws Exception
	 */
	public static void main(final String[] args) throws Exception {
		setLookAndFeel(getSystemLookAndFeelClassName());
		EventQueue.invokeLater(() -> {
			try {
				final DownloaderClientUI frame = new DownloaderClientUI(null, null);
				frame.setVisible(true);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Create the frame.
	 *
	 * @throws Exception
	 */
	public DownloaderClientUI(final TorrentPersistenceManager torrentPersistenceManager,
			final ConfigurationService configurationService) throws Exception {
		this.torrentPersistenceManager = torrentPersistenceManager;
		this.configurationService = configurationService;
		setTitle("Torrent Download Client");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addWindowListener(new WindowActions());
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		final ButtonActions buttonActions = new ButtonActions();

		tableModel = new TorrentTableModel(torrentPersistenceManager);
		final JTable torrentTable = new JTable(tableModel);
		torrentTable.setFillsViewportHeight(true);
		torrentTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		torrentTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		torrentTable.getColumnModel().getColumn(2).setCellRenderer(new ProgressCellRender());

		final JScrollPane tableScrollPane = new JScrollPane(torrentTable);

		contentPane.add(tableScrollPane, BorderLayout.CENTER);

		final JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		contentPane.add(toolBar, BorderLayout.NORTH);

		final JButton addButton = new JButton("Add Torrent");
		addButton.setToolTipText("Add a torrent to the queue");
		addButton.setActionCommand("addTorrent");
		addButton.addActionListener(buttonActions);
		toolBar.add(addButton);

		toolBar.addSeparator();

		final JButton configButton = new JButton("Configuration");
		configButton.setActionCommand("configuration");
		configButton.addActionListener(buttonActions);
		toolBar.add(configButton);

		torrentDownloader = new TorrentDownloader(torrentPersistenceManager, configurationService);
		torrentDownloader.start();
	}
}
