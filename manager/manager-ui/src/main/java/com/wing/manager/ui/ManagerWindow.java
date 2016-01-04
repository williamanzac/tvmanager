package com.wing.manager.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.wing.configuration.ui.ConfigurationDialog;
import com.wing.database.model.Episode;
import com.wing.database.model.EpisodeState;
import com.wing.database.model.Show;
import com.wing.database.model.Torrent;
import com.wing.manager.service.ManagerService;
import com.wing.manager.ui.components.EpisodeTableModel;
import com.wing.manager.ui.components.ShowListCellRenderer;
import com.wing.manager.ui.components.ShowListModel;
import com.wing.manager.ui.components.UIButton;
import com.wing.search.ui.SearchDialog;

public class ManagerWindow extends JFrame {

	private static final long serialVersionUID = -4672001174118379855L;

	private final JPanel contentPane;
	private final JTable episodeTable;

	private final ManagerService managerService;

	private final ShowListModel listModel;
	private final JList<Show> showList;

	private final EpisodeTableModel tableModel;

	private final UIButton delShowButton;

	private final UIButton updateButton;

	private final UIButton copyButton;

	private final UIButton watchButton;
	private final UIButton searchButton;

	private class ButtonActions implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent event) {
			final String command = event.getActionCommand();
			switch (command) {
			case "addShow":
				EventQueue.invokeLater(() -> {
					final SearchDialog dialog = new SearchDialog(managerService);
					dialog.setVisible(true);
					final Show show = dialog.getSelectedShow();
					if (show != null) {
						try {
							listModel.add(show);
						} catch (final Exception e) {
							e.printStackTrace();
						}
					}
				});
				break;
			case "delShow":
				EventQueue.invokeLater(() -> {
					try {
						final Show show = listModel.remove(showList.getSelectedIndex());
						if (show != null) {
							final List<Episode> listEpisodes = managerService.listEpisodes(show.getId());
							for (final Episode episode : listEpisodes) {
								managerService.removeEpisode(show.getId(), episode.getSeason(), episode.getNumber());
							}
						}
					} catch (final Exception e) {
						e.printStackTrace();
					}
				});
				break;
			case "update":
				EventQueue.invokeLater(() -> {
					final Show show = showList.getSelectedValue();
					if (show != null) {
						try {
							managerService.updateEpisodes(show.getId());
							tableModel.setEpisodes(managerService.listEpisodes(show.getId()));
							managerService.saveShow(show);
						} catch (final Exception e) {
							e.printStackTrace();
						}
					}
				});
				break;
			case "searchEpisode":
				EventQueue.invokeLater(() -> {
					final com.wing.torrent.searcher.SearchDialog dialog = new com.wing.torrent.searcher.SearchDialog(
							managerService);
					final Show show = showList.getSelectedValue();
					final Episode episode = tableModel.getEpisode(episodeTable.getSelectedRow());
					if (episode != null) {
						try {
							dialog.searchFor(show.getName(), episode.getSeason(), episode.getNumber());
							final Torrent torrent = dialog.getSelectedTorrent();
							if (torrent != null) {
								managerService.saveTorrent(torrent);
								episode.setTorrentHash(torrent.getHash());
								episode.setState(EpisodeState.QUEUED);
								managerService.saveEpisode(episode);
							}
						} catch (final Exception e) {
							e.printStackTrace();
						}
					}
				});
				break;
			case "watchEpisode":
				EventQueue.invokeLater(() -> {
					for (final int i : episodeTable.getSelectedRows()) {
						final Episode selectedEpisode = tableModel.getEpisode(i);
						if (selectedEpisode != null) {
							selectedEpisode.setState(EpisodeState.WATCHED);
							try {
								managerService.saveEpisode(selectedEpisode);
							} catch (final Exception e) {
								e.printStackTrace();
							}
						}
					}
				});
				break;
			case "copyEpisode":
				// EventQueue.invokeLater(() -> {
				// final CopierDialog dialog = new CopierDialog(torrentCopier);// managerService
				// final Show show = showList.getSelectedValue();
				// final Episode episode = show.getEpisodeList().get(episodeTable.getSelectedRow());
				// if (episode.getTorrentHash() != null) {
				// try {
				// final Torrent torrent = managerService.getTorrent(episode.getTorrentHash());
				// // dialog.manageTorrentFiles(torrent);
				// } catch (final Exception e) {
				// e.printStackTrace();
				// }
				// }
				// });
				break;
			case "configuration":
				EventQueue.invokeLater(() -> {
					try {
						final ConfigurationDialog configurationDialog = new ConfigurationDialog(managerService);
						configurationDialog.setVisible(true);
					} catch (final Exception e) {
						e.printStackTrace();
					}
				});
				break;
			}
		}
	}

	private class ShowSelectionListener implements ListSelectionListener {
		@Override
		public void valueChanged(final ListSelectionEvent e) {
			final Show show = listModel.getElementAt(showList.getSelectedIndex());
			final boolean b = show != null;
			if (b) {
				try {
					tableModel.setEpisodes(managerService.listEpisodes(show.getId()));
				} catch (final Exception e1) {
					e1.printStackTrace();
				}
			}
			delShowButton.setEnabled(b);
			updateButton.setEnabled(b);
		}
	}

	private class EpisodeSelectionListener implements ListSelectionListener {
		@Override
		public void valueChanged(final ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {
				final Episode episode = tableModel.getEpisode(episodeTable.getSelectedRow());
				if (episode != null) {
					watchButton.setEnabled(episode.getState() == null
							|| EpisodeState.WATCHED.compareTo(episode.getState()) > 0);
					copyButton.setEnabled(episode.getState() == null
							|| EpisodeState.DOWNLOADED.compareTo(episode.getState()) > 0);
					searchButton.setEnabled(episode.getState() == null
							|| EpisodeState.QUEUED.compareTo(episode.getState()) > 0);
				}
			}
		}
	}

	/**
	 * Create the frame.
	 *
	 * @throws Exception
	 */
	public ManagerWindow(final ManagerService managerService) throws Exception {
		setTitle("TV Show Manager");
		this.managerService = managerService;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(50, 50, 1024, 600);
		setIconImage(new ImageIcon(getClass().getResource("main.png")).getImage());
		final ButtonActions buttonActions = new ButtonActions();

		contentPane = new JPanel();
		contentPane.setBorder(null);
		contentPane.setLayout(new BorderLayout());
		setContentPane(contentPane);

		tableModel = new EpisodeTableModel();
		episodeTable = new JTable(tableModel);
		episodeTable.setFillsViewportHeight(true);
		episodeTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		episodeTable.getSelectionModel().addListSelectionListener(new EpisodeSelectionListener());
		episodeTable.setShowHorizontalLines(true);
		episodeTable.setShowVerticalLines(false);

		final JScrollPane tableScrollPane = new JScrollPane(episodeTable);

		listModel = new ShowListModel(managerService);
		showList = new JList<>(listModel);
		showList.setCellRenderer(new ShowListCellRenderer());
		showList.addListSelectionListener(new ShowSelectionListener());
		showList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		final JScrollPane listScrollPane = new JScrollPane(showList);

		final JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.3);
		splitPane.setLeftComponent(listScrollPane);
		splitPane.setRightComponent(tableScrollPane);
		contentPane.add(splitPane, BorderLayout.CENTER);

		final JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		contentPane.add(toolBar, BorderLayout.NORTH);

		final UIButton addShowButton = new UIButton("../add.png");
		addShowButton.setToolTipText("Add a new Show");
		addShowButton.setActionCommand("addShow");
		addShowButton.addActionListener(buttonActions);
		toolBar.add(addShowButton);

		delShowButton = new UIButton("../delete.png");
		delShowButton.setToolTipText("Remove the currently selected Show");
		delShowButton.setActionCommand("delShow");
		delShowButton.addActionListener(buttonActions);
		delShowButton.setEnabled(false);
		toolBar.add(delShowButton);

		toolBar.addSeparator();

		updateButton = new UIButton("../update.png");
		updateButton.setToolTipText("Update the Episode List for the current show");
		updateButton.setActionCommand("update");
		updateButton.addActionListener(buttonActions);
		updateButton.setEnabled(false);
		toolBar.add(updateButton);

		toolBar.addSeparator();

		watchButton = new UIButton("../watched.png");
		watchButton.setToolTipText("Mark the Current Episode as being watched");
		watchButton.setEnabled(false);
		watchButton.setActionCommand("watchEpisode");
		watchButton.addActionListener(buttonActions);
		toolBar.add(watchButton);

		copyButton = new UIButton("../copy.png");
		copyButton.setToolTipText("Copy the current to target");
		copyButton.setEnabled(false);
		copyButton.setActionCommand("copyEpisode");
		copyButton.addActionListener(buttonActions);
		toolBar.add(copyButton);

		searchButton = new UIButton("../search.png");
		searchButton.setToolTipText("Find and Queue the current Episode for download");
		searchButton.setEnabled(false);
		searchButton.setActionCommand("searchEpisode");
		searchButton.addActionListener(buttonActions);
		toolBar.add(searchButton);

		toolBar.addSeparator();

		final UIButton configButton = new UIButton("../settings.png");
		configButton.setActionCommand("configuration");
		configButton.addActionListener(buttonActions);
		toolBar.add(configButton);
	}
}
