package com.wing.torrent.downloader;

import static javax.swing.UIManager.getSystemLookAndFeelClassName;
import static javax.swing.UIManager.setLookAndFeel;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;

import com.wing.database.model.Torrent;
import com.wing.manager.service.ManagerService;
import com.wing.torrent.downloader.components.ProgressCellRender;
import com.wing.torrent.downloader.components.TorrentTableModel;

public class DownloaderClientUI extends JFrame {

	private static final long serialVersionUID = 5881242422562272594L;

	private final JPanel contentPane;

	private final JTextField targetField;

	private final TorrentTableModel tableModel;

	private final ManagerService managerService;

	private class ButtonActions implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			final String command = e.getActionCommand();
			if ("addTorrent".equals(command)) {
				final JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.showOpenDialog(DownloaderClientUI.this);
				final File file = chooser.getSelectedFile();
				final File targetDir = new File(targetField.getText());
				if (file != null && targetDir.isDirectory()) {
					final Torrent torrent = new Torrent();
					torrent.setTorrentFile(file.getAbsolutePath());
					torrent.setDestination(targetField.getText());
					torrent.setName(file.getName());
					try {
						tableModel.add(torrent);
					} catch (final Exception e1) {
						e1.printStackTrace();
					}
				}
			} else if ("browseTarget".equals(command)) {
				final JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.showOpenDialog(DownloaderClientUI.this);
				final File targetDir = chooser.getSelectedFile();
				if (targetDir != null && targetDir.isDirectory()) {
					targetField.setText(targetDir.getAbsolutePath());
				}
			}
		}
	}

	/**
	 * Launch the application.
	 * 
	 * @throws Exception
	 */
	public static void main(final String[] args) throws Exception {
		setLookAndFeel(getSystemLookAndFeelClassName());
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					final DownloaderClientUI frame = new DownloaderClientUI(
							null);
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
	public DownloaderClientUI(final ManagerService managerService)
			throws Exception {
		this.managerService = managerService;
		setTitle("Torrent Download Client");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		final ButtonActions buttonActions = new ButtonActions();

		tableModel = new TorrentTableModel(managerService);
		final JTable torrentTable = new JTable(tableModel);
		torrentTable.setFillsViewportHeight(true);
		torrentTable
				.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		torrentTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		torrentTable.getColumnModel().getColumn(2)
				.setCellRenderer(new ProgressCellRender());

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

		targetField = new JTextField();
		toolBar.add(targetField);

		final JButton browseButton = new JButton("Browse");
		browseButton.setToolTipText("Choose Torrent download desination");
		browseButton.setActionCommand("browseTarget");
		browseButton.addActionListener(buttonActions);
		toolBar.add(browseButton);
		TorrentDownloader downloader = new TorrentDownloader(managerService);
	}
}
