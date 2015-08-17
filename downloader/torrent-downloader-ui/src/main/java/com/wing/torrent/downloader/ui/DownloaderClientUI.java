package com.wing.torrent.downloader.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
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

import com.wing.configuration.ui.ConfigurationDialog;
import com.wing.database.model.Configuration;
import com.wing.database.model.Torrent;
import com.wing.database.model.TorrentState;
import com.wing.manager.service.ManagerService;
import com.wing.torrent.copier.CopyTask;
import com.wing.torrent.copier.DeleteTask;
import com.wing.torrent.copier.FileManager;
import com.wing.torrent.copier.FileTask;
import com.wing.torrent.copier.MoveTask;
import com.wing.torrent.downloader.TorrentDownloader;
import com.wing.torrent.downloader.ui.components.CheckTreeManager;
import com.wing.torrent.downloader.ui.components.FileTreeCellRenderer;
import com.wing.torrent.downloader.ui.components.ProgressCellRender;
import com.wing.torrent.downloader.ui.components.TorrentTableModel;
import com.wing.torrent.downloader.ui.components.UIButton;
import com.wing.torrent.searcher.SearchDialog;

public class DownloaderClientUI extends JFrame {

	private static final long serialVersionUID = 5881242422562272594L;

	private final JPanel contentPane;

	private final TorrentTableModel tableModel;

	private final TorrentDownloader torrentDownloader;

	private final ManagerService managerService;
	private final JTable torrentTable;
	private final ButtonActions buttonActions = new ButtonActions();

	private FileSystemView fileSystemView;
	private DefaultTreeModel treeModel;
	private JTree tree;
	private CheckTreeManager checkTreeManager;

	private JButton moveFileButton;
	private JButton copyFileButton;
	private JButton delFileButton;

	private UIButton startTorrentButton;
	private UIButton pauseTorrentButton;
	private UIButton stopTorrentButton;
	private UIButton delTorrentButton;

	private final FileManager torrentCopier;

	private final class TorrentSelectionListener implements ListSelectionListener {
		private List<DefaultMutableTreeNode> getNodes(File file) {
			final List<DefaultMutableTreeNode> nodes = new ArrayList<>();
			final File[] files = fileSystemView.getFiles(file, true);
			// System.out.println(files);
			for (final File child : files) {
				// System.out.println(child);
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(child);
				nodes.add(node);
				List<DefaultMutableTreeNode> childNodes = getNodes(child);
				for (DefaultMutableTreeNode defaultMutableTreeNode : childNodes) {
					node.add(defaultMutableTreeNode);
				}
			}
			return nodes;
		}

		@Override
		public void valueChanged(final ListSelectionEvent e) {
			if (e.getValueIsAdjusting()) {
				return;
			}
			EventQueue.invokeLater(() -> {
				try {
					final Configuration configuration = managerService.loadConfiguration();
					final File dest = configuration.torrentDestination;
					final int selectedRow = torrentTable.getSelectedRow();

					startTorrentButton.setEnabled(false);
					pauseTorrentButton.setEnabled(false);
					stopTorrentButton.setEnabled(false);
					delTorrentButton.setEnabled(false);

					checkTreeManager.getSelectionModel().clearSelection();

					if (selectedRow < 0) {
						final DefaultMutableTreeNode root = new DefaultMutableTreeNode();
						treeModel.setRoot(root);
						tree.setRootVisible(false);
						return;
					}
					final Torrent torrent = managerService.listTorrents().get(selectedRow);
					if (torrent != null) {
						final String title = torrent.getTitle();
						final File source = new File(dest, title);
						final DefaultMutableTreeNode root = new DefaultMutableTreeNode(source);
						List<DefaultMutableTreeNode> nodes = getNodes(source);
						for (DefaultMutableTreeNode defaultMutableTreeNode : nodes) {
							root.add(defaultMutableTreeNode);
						}
						treeModel.setRoot(root);
						tree.setRootVisible(true);

						startTorrentButton.setEnabled(torrent.getState() == TorrentState.QUEUED
								|| torrent.getState() == TorrentState.PAUSED);
						pauseTorrentButton.setEnabled(torrent.getState() == TorrentState.DOWNLOADING);
						stopTorrentButton.setEnabled(true);
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
		@Override
		public void actionPerformed(final ActionEvent e) {
			final String command = e.getActionCommand();
			int selectedRow = torrentTable.getSelectedRow();
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
			case "searchTorrent":
				EventQueue.invokeLater(() -> {
					final SearchDialog dialog = new SearchDialog(managerService);
					dialog.setVisible(true);
					try {
						final Torrent torrent = dialog.getSelectedTorrent();
						if (torrent != null) {
							managerService.saveTorrent(torrent);
						}
					} catch (final Exception e1) {
						e1.printStackTrace();
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
						if (source.isFile()) {
							final File dest = new File(targetDir, source.getName());
							final FileTask task;
							if ("moveFile".equals(command)) {
								System.out.println("moving: " + source + " to " + dest);
								task = new MoveTask(source, dest);
							} else {
								System.out.println("copying: " + source + " to " + dest);
								task = new CopyTask(source, dest);
							}
							torrentCopier.addTask(task);
						}
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
							torrentCopier.addTask(new DeleteTask(source));
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
			case "startTorrent":
				try {
					final Torrent torrent = managerService.listTorrents().get(selectedRow);
					if (torrent != null) {
						torrentDownloader.startTorrent(torrent);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				break;
			case "pauseTorrent":
				try {
					final Torrent torrent = managerService.listTorrents().get(selectedRow);
					if (torrent != null) {
						torrentDownloader.pauseTorrent(torrent);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				break;
			case "stopTorrent":
				try {
					final Torrent torrent = managerService.listTorrents().get(selectedRow);
					if (torrent != null) {
						torrentDownloader.stopTorrent(torrent);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				break;
			case "delTorrent":
				try {
					final Torrent torrent = managerService.listTorrents().get(selectedRow);
					if (torrent != null) {
						torrentDownloader.removeTorrent(torrent);
						tableModel.remove(selectedRow);
						final Configuration configuration = managerService.loadConfiguration();
						final File dest = configuration.torrentDestination;
						final String title = torrent.getTitle();
						final File source = new File(dest, title);
						System.out.println("deleting: " + source);
						torrentCopier.addTask(new DeleteTask(source));
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
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
	public DownloaderClientUI(final ManagerService managerService, final TorrentDownloader torrentDownloader,
			final FileManager torrentCopier) throws Exception {
		this.managerService = managerService;
		this.torrentCopier = torrentCopier;
		setTitle("Torrent Download Client");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addWindowListener(new WindowActions());
		setSize(800, 500);
		contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());
		setContentPane(contentPane);
		setIconImage(new ImageIcon(getClass().getResource("main.png")).getImage());

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

		final UIButton addButton = new UIButton("../open.png");
		addButton.setToolTipText("Add a torrent to the queue");
		addButton.setActionCommand("addTorrent");
		addButton.addActionListener(buttonActions);
		toolBar.add(addButton);

		final UIButton searchButton = new UIButton("../search.png");
		searchButton.setToolTipText("Search for a torrent to the queue");
		searchButton.setActionCommand("searchTorrent");
		searchButton.addActionListener(buttonActions);
		toolBar.add(searchButton);

		toolBar.addSeparator();

		startTorrentButton = new UIButton("../start.png");
		startTorrentButton.setToolTipText("Start downloading the torrent");
		startTorrentButton.setActionCommand("startTorrent");
		startTorrentButton.addActionListener(buttonActions);
		startTorrentButton.setEnabled(false);
		toolBar.add(startTorrentButton);

		pauseTorrentButton = new UIButton("../pause.png");
		pauseTorrentButton.setToolTipText("Pause the torrent");
		pauseTorrentButton.setActionCommand("pauseTorrent");
		pauseTorrentButton.addActionListener(buttonActions);
		pauseTorrentButton.setEnabled(false);
		toolBar.add(pauseTorrentButton);

		stopTorrentButton = new UIButton("../stop.png");
		stopTorrentButton.setToolTipText("Stop downloading the torrent");
		stopTorrentButton.setActionCommand("stopTorrent");
		stopTorrentButton.addActionListener(buttonActions);
		stopTorrentButton.setEnabled(false);
		toolBar.add(stopTorrentButton);

		delTorrentButton = new UIButton("../delete.png");
		delTorrentButton.setToolTipText("Remove the torrent");
		delTorrentButton.setActionCommand("delTorrent");
		delTorrentButton.addActionListener(buttonActions);
		delTorrentButton.setEnabled(false);
		toolBar.add(delTorrentButton);

		toolBar.addSeparator();

		final UIButton configButton = new UIButton("../settings.png");
		configButton.setToolTipText("Configuration");
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
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		checkTreeManager = new CheckTreeManager(tree);
		checkTreeManager.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(final TreeSelectionEvent e) {
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

		moveFileButton = new UIButton("../move.png");
		moveFileButton.setActionCommand("moveFile");
		moveFileButton.addActionListener(buttonActions);
		moveFileButton.setEnabled(false);
		buttonPane.add(moveFileButton);

		copyFileButton = new UIButton("../copy.png");
		copyFileButton.setActionCommand("copyFile");
		copyFileButton.addActionListener(buttonActions);
		copyFileButton.setEnabled(false);
		buttonPane.add(copyFileButton);

		delFileButton = new UIButton("../delete.png");
		delFileButton.setActionCommand("deleteFile");
		delFileButton.addActionListener(buttonActions);
		delFileButton.setEnabled(false);
		buttonPane.add(delFileButton);
	}
}
