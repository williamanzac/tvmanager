package com.wing.torrent.downloader.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.io.FileUtils;

import com.wing.configuration.ui.ConfigurationDialog;
import com.wing.database.model.Configuration;
import com.wing.database.model.Torrent;
import com.wing.database.model.TorrentState;
import com.wing.manager.service.ManagerService;
import com.wing.torrent.copier.ui.components.CheckTreeManager;
import com.wing.torrent.copier.ui.components.CopyTask;
import com.wing.torrent.copier.ui.components.FileTask;
import com.wing.torrent.copier.ui.components.FileTreeCellRenderer;
import com.wing.torrent.copier.ui.components.MoveTask;
import com.wing.torrent.downloader.TorrentDownloader;
import com.wing.torrent.downloader.ui.components.ProgressCellRender;
import com.wing.torrent.downloader.ui.components.TorrentTableModel;

public class DownloaderClientUI extends JFrame {

	private static final long serialVersionUID = 5881242422562272594L;

	private final JPanel contentPane;

	private final TorrentTableModel tableModel;

	private final TorrentDownloader torrentDownloader;

	private final ManagerService managerService;
	private JTable torrentTable;
	private final ButtonActions buttonActions = new ButtonActions();

	private FileSystemView fileSystemView;
	private DefaultTreeModel treeModel;
	private JTree tree;
	private CheckTreeManager checkTreeManager;
	private JProgressBar progressBar;
	private JButton moveFileButton;
	private JButton copyFileButton;
	private JButton delFileButton;

	private JButton startTorrentButton;
	private JButton pauseTorrentButton;
	private JButton stopTorrentButton;
	private JButton delTorrentButton;

	private final class TorrentSelectionListener implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting()) {
				return;
			}
			EventQueue.invokeLater(() -> {
				try {
					Configuration configuration = managerService.loadConfiguration();
					final File dest = configuration.torrentDestination;
					int selectedRow = torrentTable.getSelectedRow();

					startTorrentButton.setEnabled(false);
					pauseTorrentButton.setEnabled(false);
					stopTorrentButton.setEnabled(false);
					delTorrentButton.setEnabled(false);

					if (selectedRow < 0) {
						return;
					}
					final Torrent torrent = managerService.listTorrents().get(selectedRow);
					if (torrent != null) {
						final String title = torrent.getTitle();
						final File source = new File(dest, title);
						System.out.println(source);
						final DefaultMutableTreeNode root = new DefaultMutableTreeNode(source);
						final File[] files = fileSystemView.getFiles(source, true);
						System.out.println(files);
						for (final File child : files) {
							System.out.println(child);
							root.add(new DefaultMutableTreeNode(child));
						}
						treeModel.setRoot(root);
						tree.setRootVisible(true);

						startTorrentButton.setEnabled(torrent.getState() == TorrentState.QUEUED
								|| torrent.getState() == TorrentState.PAUSED);
						pauseTorrentButton.setEnabled(torrent.getState() == TorrentState.DOWNLOADING);
						stopTorrentButton.setEnabled(torrent.getState() == TorrentState.DOWNLOADING);
						delTorrentButton.setEnabled(true);
					} else {
						final DefaultMutableTreeNode root = new DefaultMutableTreeNode();
						treeModel.setRoot(root);
						tree.setRootVisible(false);
					}
				} catch (final Exception e1) {
					e1.printStackTrace();
				}
			});
		}
	}

	private class ButtonActions implements ActionListener {
		private final class FileTaskListener implements PropertyChangeListener {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if ("progress".equals(evt.getPropertyName())) {
					int progress = (Integer) evt.getNewValue();
					progressBar.setValue(progress);
					if (progress >= 100) {
						moveFileButton.setEnabled(true);
						copyFileButton.setEnabled(true);
						delFileButton.setEnabled(true);
						tree.setEnabled(true);
					}
				}
			}
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			final String command = e.getActionCommand();
			switch (command) {
			case "addTorrent":
				EventQueue.invokeLater(() -> {
					final JFileChooser chooser = new JFileChooser();
					chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					chooser.showOpenDialog(DownloaderClientUI.this);
					final File file = chooser.getSelectedFile();
					if (file != null) {
						try {
							final Torrent torrent = torrentDownloader.addTorrent(file);
							tableModel.add(torrent);
							managerService.saveTorrent(torrent);
						} catch (final Exception e1) {
							e1.printStackTrace();
						}
					}
				});
				break;
			case "configuration":
				EventQueue.invokeLater(() -> {
					try {
						final ConfigurationDialog configurationDialog = new ConfigurationDialog(managerService);
						configurationDialog.setVisible(true);
					} catch (final Exception e1) {
						e1.printStackTrace();
					}
				});
				break;
			case "copyFile":
			case "moveFile":
				try {
					moveFileButton.setEnabled(false);
					copyFileButton.setEnabled(false);
					delFileButton.setEnabled(false);
					tree.setEnabled(false);

					final Configuration configuration = managerService.loadConfiguration();
					final File targetDir = configuration.showDestination;
					final TreePath checkedPaths[] = checkTreeManager.getSelectionModel().getSelectionPaths();
					for (final TreePath path : checkedPaths) {
						final DefaultMutableTreeNode sourceNode = (DefaultMutableTreeNode) path.getLastPathComponent();
						final File source = (File) sourceNode.getUserObject();
						final File dest = new File(targetDir, source.getName());
						final FileTask task;
						if ("moveFile".equals(command)) {
							System.out.println("moving: " + source + " to " + dest);
							task = new MoveTask(source, dest);
						} else {
							System.out.println("copying: " + source + " to " + dest);
							task = new CopyTask(source, dest);
						}
						task.addPropertyChangeListener(new FileTaskListener());
						task.execute();
					}
				} catch (final Exception e1) {
					e1.printStackTrace();
				}
				break;
			case "deleteFile":
				EventQueue.invokeLater(() -> {
					moveFileButton.setEnabled(false);
					copyFileButton.setEnabled(false);
					delFileButton.setEnabled(false);
					tree.setEnabled(false);
					try {
						final TreePath checkedPaths[] = checkTreeManager.getSelectionModel().getSelectionPaths();
						for (final TreePath path : checkedPaths) {
							final DefaultMutableTreeNode sourceNode = (DefaultMutableTreeNode) path
									.getLastPathComponent();
							final File source = (File) sourceNode.getUserObject();
							System.out.println("deleting: " + source);
							FileUtils.forceDelete(source);
						}
					} catch (final Exception e1) {
						e1.printStackTrace();
					}
					moveFileButton.setEnabled(true);
					copyFileButton.setEnabled(true);
					delFileButton.setEnabled(true);
					tree.setEnabled(true);
				});
				break;
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
	 * Create the frame.
	 *
	 * @throws Exception
	 */
	public DownloaderClientUI(final ManagerService managerService, final TorrentDownloader torrentDownloader)
			throws Exception {
		this.managerService = managerService;
		setTitle("Torrent Download Client");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addWindowListener(new WindowActions());
		setSize(800, 700);
		contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());
		setContentPane(contentPane);

		final JTabbedPane infoPane = new JTabbedPane();

		final JPanel filesPanel = new JPanel(new BorderLayout());

		infoPane.addTab("Files", filesPanel);

		initFilesPane(filesPanel);

		tableModel = new TorrentTableModel(managerService);
		torrentTable = new JTable(tableModel);
		torrentTable.setFillsViewportHeight(true);
		torrentTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		torrentTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		torrentTable.getColumnModel().getColumn(2).setCellRenderer(new ProgressCellRender());
		torrentTable.getSelectionModel().addListSelectionListener(new TorrentSelectionListener());

		final JScrollPane tableScrollPane = new JScrollPane(torrentTable);

		final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, tableScrollPane, infoPane);
		EventQueue.invokeLater(() -> {
			splitPane.setDividerLocation(0.6d);
		});
		contentPane.add(splitPane, BorderLayout.CENTER);

		final JToolBar toolBar = initTootbar();
		contentPane.add(toolBar, BorderLayout.NORTH);

		this.torrentDownloader = torrentDownloader;
		this.torrentDownloader.start();
	}

	private JToolBar initTootbar() {
		final JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);

		final JButton addButton = new JButton("Add Torrent");
		addButton.setToolTipText("Add a torrent to the queue");
		addButton.setActionCommand("addTorrent");
		addButton.addActionListener(buttonActions);
		toolBar.add(addButton);

		toolBar.addSeparator();

		startTorrentButton = new JButton("Start");
		startTorrentButton.setToolTipText("Start downloading the torrent");
		startTorrentButton.setActionCommand("startTorrent");
		startTorrentButton.addActionListener(buttonActions);
		startTorrentButton.setEnabled(false);
		toolBar.add(startTorrentButton);

		pauseTorrentButton = new JButton("Pause");
		pauseTorrentButton.setToolTipText("Pause the torrent");
		pauseTorrentButton.setActionCommand("pauseTorrent");
		pauseTorrentButton.addActionListener(buttonActions);
		pauseTorrentButton.setEnabled(false);
		toolBar.add(pauseTorrentButton);

		stopTorrentButton = new JButton("Stop");
		stopTorrentButton.setToolTipText("Stop downloading the torrent");
		stopTorrentButton.setActionCommand("stopTorrent");
		stopTorrentButton.addActionListener(buttonActions);
		stopTorrentButton.setEnabled(false);
		toolBar.add(stopTorrentButton);

		delTorrentButton = new JButton("Remove");
		delTorrentButton.setToolTipText("Remove the torrent");
		delTorrentButton.setActionCommand("delTorrent");
		delTorrentButton.addActionListener(buttonActions);
		delTorrentButton.setEnabled(false);
		toolBar.add(delTorrentButton);

		toolBar.addSeparator();

		final JButton configButton = new JButton("Configuration");
		configButton.setActionCommand("configuration");
		configButton.addActionListener(buttonActions);
		toolBar.add(configButton);

		return toolBar;
	}

	private void initFilesPane(final JPanel panel) {
		fileSystemView = FileSystemView.getFileSystemView();
		final JScrollPane scrollPane = new JScrollPane();
		panel.add(scrollPane);
		final DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		treeModel = new DefaultTreeModel(root);

		tree = new JTree(treeModel);
		tree.setRootVisible(false);
		tree.setCellRenderer(new FileTreeCellRenderer());
		tree.expandRow(0);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		checkTreeManager = new CheckTreeManager(tree);
		checkTreeManager.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				final boolean enabled = checkTreeManager.getSelectionModel().getSelectionCount() > 0;
				moveFileButton.setEnabled(enabled);
				copyFileButton.setEnabled(enabled);
				delFileButton.setEnabled(enabled);
			}
		});
		scrollPane.setViewportView(tree);

		final JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		panel.add(buttonPane, BorderLayout.SOUTH);

		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		buttonPane.add(progressBar);

		moveFileButton = new JButton("Move");
		moveFileButton.setActionCommand("moveFile");
		moveFileButton.addActionListener(buttonActions);
		moveFileButton.setEnabled(false);
		buttonPane.add(moveFileButton);

		copyFileButton = new JButton("Copy");
		copyFileButton.setActionCommand("copyFile");
		copyFileButton.addActionListener(buttonActions);
		copyFileButton.setEnabled(false);
		buttonPane.add(copyFileButton);

		delFileButton = new JButton("Delete");
		delFileButton.setActionCommand("deleteFile");
		delFileButton.addActionListener(buttonActions);
		delFileButton.setEnabled(false);
		buttonPane.add(delFileButton);
	}
}
