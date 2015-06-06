package com.wing.manager.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
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

import com.wing.database.model.Episode;
import com.wing.database.model.EpisodeState;
import com.wing.database.model.Show;
import com.wing.manager.service.ManagerService;
import com.wing.manager.ui.components.EpisodeTableModel;
import com.wing.manager.ui.components.ShowListCellRenderer;
import com.wing.manager.ui.components.ShowListModel;
import com.wing.search.service.ShowSearchService;
import com.wing.search.ui.SearchDialog;

public class ManagerWindow extends JFrame {

	private static final long serialVersionUID = -4672001174118379855L;

	private final JPanel contentPane;
	private final JTable episodeTable;

	private final ManagerService managerService;
	private final ShowSearchService searchService;

	private final ShowListModel listModel;
	private final JList<Show> showList;

	private final EpisodeTableModel tableModel;

	private final JButton delShowButton;

	private final JButton updateButton;

	private final JButton downloadButton;

	private final JButton copyButton;

	private final JButton watchButton;

	private class ButtonActions implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent event) {
			final String command = event.getActionCommand();
			if ("addShow".equals(command)) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						final SearchDialog dialog = new SearchDialog(
								searchService);
						dialog.setVisible(true);
						final Show show = dialog.getSelectedShow();
						if (show != null) {
							try {
								listModel.add(show);
							} catch (final Exception e) {
								e.printStackTrace();
							}
						}
					}
				});
			} else if ("delShow".equals(command)) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							listModel.remove(showList.getSelectedIndex());
						} catch (final Exception e) {
							e.printStackTrace();
						}
					}
				});
			} else if ("update".equals(command)) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						final Show show = showList.getSelectedValue();
						if (show != null) {
							try {
								managerService.updateEpisodes(show);
								tableModel.setEpisodes(show.getEpisodeList());
								managerService.saveShow(show);
							} catch (final Exception e) {
								e.printStackTrace();
							}
						}
					}
				});
			}
		}
	}

	private class ShowSelectionListener implements ListSelectionListener {
		@Override
		public void valueChanged(final ListSelectionEvent e) {
			final Show show = listModel.getElementAt(showList
					.getSelectedIndex());
			final boolean b = show != null;
			if (b) {
				tableModel.setEpisodes(show.getEpisodeList());
			}
			delShowButton.setEnabled(b);
			updateButton.setEnabled(b);
		}
	}

	private class EpisodeSelectionListener implements ListSelectionListener {

		@Override
		public void valueChanged(final ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {
				final Show show = listModel.getElementAt(showList
						.getSelectedIndex());
				final Episode episode = show.getEpisodeList().get(
						episodeTable.getSelectedRow());
				watchButton
						.setEnabled(episode.getState() == null
								|| EpisodeState.WATCHED.compareTo(episode
										.getState()) < 0);
				copyButton
						.setEnabled(episode.getState() == null
								|| EpisodeState.DOWNLOADED.compareTo(episode
										.getState()) < 0);
				downloadButton
						.setEnabled(episode.getState() == null
								|| EpisodeState.QUEUED.compareTo(episode
										.getState()) > 0);
			}
		}
	}

	/**
	 * Launch the application.
	 */
	public static void main(final String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					final ManagerWindow frame = new ManagerWindow(null, null);
					frame.setVisible(true);
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * 
	 * @throws Exception
	 */
	public ManagerWindow(final ManagerService managerService,
			final ShowSearchService searchService) throws Exception {
		setTitle("TV Show Manager");
		this.managerService = managerService;
		this.searchService = searchService;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(null);
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		final ButtonActions buttonActions = new ButtonActions();

		tableModel = new EpisodeTableModel();
		episodeTable = new JTable(tableModel);
		episodeTable.setFillsViewportHeight(true);
		episodeTable
				.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		episodeTable.getSelectionModel().addListSelectionListener(
				new EpisodeSelectionListener());

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

		final JButton addShowButton = new JButton("Add Show");
		addShowButton.setToolTipText("Add a new Show");
		addShowButton.setActionCommand("addShow");
		addShowButton.addActionListener(buttonActions);
		toolBar.add(addShowButton);

		delShowButton = new JButton("Remove Show");
		delShowButton.setToolTipText("Remove the currently selected Show");
		delShowButton.setActionCommand("delShow");
		delShowButton.addActionListener(buttonActions);
		delShowButton.setEnabled(false);
		toolBar.add(delShowButton);

		toolBar.addSeparator();

		updateButton = new JButton("Update");
		updateButton
				.setToolTipText("Update the Episode List for the current show");
		updateButton.setActionCommand("update");
		updateButton.addActionListener(buttonActions);
		updateButton.setEnabled(false);
		toolBar.add(updateButton);

		toolBar.addSeparator();

		watchButton = new JButton("Watched");
		watchButton.setToolTipText("Mark the Current Episode as being watched");
		watchButton.setEnabled(false);
		toolBar.add(watchButton);

		copyButton = new JButton("Copy");
		copyButton.setToolTipText("Copy the current to target");
		copyButton.setEnabled(false);
		toolBar.add(copyButton);

		downloadButton = new JButton("Download");
		downloadButton
				.setToolTipText("Find and Queue the current Episode for download");
		downloadButton.setEnabled(false);
		toolBar.add(downloadButton);
	}
}
