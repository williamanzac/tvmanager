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
import com.wing.manager.service.ManagerService;
import com.wing.torrent.copier.ui.components.CheckTreeManager;
import com.wing.torrent.copier.ui.components.CopyTask;
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
	private JButton moveButton;
	private JButton copyButton;
	private JButton delButton;

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
					final Torrent torrent = managerService.listTorrents().get(torrentTable.getSelectedRow());
					if (torrent != null) {
						final String title = torrent.getTitle();
						final File source = new File(dest, title);
						System.out.println(source);
						final DefaultMutableTreeNode root = new DefaultMutableTreeNode(source);
						final File[] files = fileSystemView.getFiles(source, true); // !!
					System.out.println(files);
					for (final File child : files) {
						System.out.println(child);
						root.add(new DefaultMutableTreeNode(child));
					}
					treeModel.setRoot(root);
					tree.setRootVisible(true);
				} else {
					final DefaultMutableTreeNode root = new DefaultMutableTreeNode();
					treeModel.setRoot(root);
					tree.setRootVisible(false);
				}
			} catch (final Exception e1) {
				e1.printStackTrace();
			}
		})	;
		}
	}

	private class ButtonActions implements ActionListener {
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
			case "moveFile":
				try {
					moveButton.setEnabled(false);
					copyButton.setEnabled(false);
					delButton.setEnabled(false);
					tree.setEnabled(false);

					final Configuration configuration = managerService.loadConfiguration();
					final File targetDir = configuration.showDestination;
					final TreePath checkedPaths[] = checkTreeManager.getSelectionModel().getSelectionPaths();
					for (final TreePath path : checkedPaths) {
						final DefaultMutableTreeNode sourceNode = (DefaultMutableTreeNode) path.getLastPathComponent();
						final File source = (File) sourceNode.getUserObject();
						final File dest = new File(targetDir, source.getName());
						System.out.println("moving: " + source + " to " + dest);
						final MoveTask task = new MoveTask(source, dest);
						task.addPropertyChangeListener(new PropertyChangeListener() {
							@Override
							public void propertyChange(PropertyChangeEvent evt) {
								if ("progress".equals(evt.getPropertyName())) {
									int progress = (Integer) evt.getNewValue();
									progressBar.setValue(progress);
									if (progress >= 100) {
										moveButton.setEnabled(true);
										copyButton.setEnabled(true);
										delButton.setEnabled(true);
										tree.setEnabled(true);
									}
								}
							}
						});
						task.execute();
					}
				} catch (final Exception e1) {
					e1.printStackTrace();
				}
				break;
			case "copyFile":
				try {
					moveButton.setEnabled(false);
					copyButton.setEnabled(false);
					delButton.setEnabled(false);
					tree.setEnabled(false);

					final Configuration configuration = managerService.loadConfiguration();
					final File targetDir = configuration.showDestination;
					final TreePath checkedPaths[] = checkTreeManager.getSelectionModel().getSelectionPaths();
					for (final TreePath path : checkedPaths) {
						final DefaultMutableTreeNode sourceNode = (DefaultMutableTreeNode) path.getLastPathComponent();
						final File source = (File) sourceNode.getUserObject();
						final File dest = new File(targetDir, source.getName());
						System.out.println("copying: " + source + " to " + dest);
						final CopyTask task = new CopyTask(source, dest);
						task.addPropertyChangeListener(new PropertyChangeListener() {
							@Override
							public void propertyChange(PropertyChangeEvent evt) {
								if ("progress".equals(evt.getPropertyName())) {
									int progress = (Integer) evt.getNewValue();
									progressBar.setValue(progress);
									if (progress >= 100) {
										moveButton.setEnabled(true);
										copyButton.setEnabled(true);
										delButton.setEnabled(true);
										tree.setEnabled(true);
									}
								}
							}
						});
						task.execute();
					}
				} catch (final Exception e1) {
					e1.printStackTrace();
				}
				break;
			case "deleteFile":
				EventQueue.invokeLater(() -> {
					moveButton.setEnabled(false);
					copyButton.setEnabled(false);
					delButton.setEnabled(false);
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
					moveButton.setEnabled(true);
					copyButton.setEnabled(true);
					delButton.setEnabled(true);
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
		setBounds(100, 100, 450, 300);
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
		splitPane.setDividerLocation(0.6d);
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

		final JButton startButton = new JButton("Start");
		startButton.setToolTipText("Start downloading the torrent");
		startButton.setActionCommand("startTorrent");
		startButton.addActionListener(buttonActions);
		toolBar.add(startButton);

		final JButton pauseButton = new JButton("Pause");
		pauseButton.setToolTipText("Pause the torrent");
		pauseButton.setActionCommand("pauseTorrent");
		pauseButton.addActionListener(buttonActions);
		toolBar.add(pauseButton);

		final JButton stopButton = new JButton("Stop");
		stopButton.setToolTipText("Stop downloading the torrent");
		stopButton.setActionCommand("stopTorrent");
		stopButton.addActionListener(buttonActions);
		toolBar.add(stopButton);

		final JButton delButton = new JButton("Remove");
		delButton.setToolTipText("Remove the torrent");
		delButton.setActionCommand("delTorrent");
		delButton.addActionListener(buttonActions);
		toolBar.add(delButton);

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
				moveButton.setEnabled(enabled);
				copyButton.setEnabled(enabled);
				delButton.setEnabled(enabled);
			}
		});
		scrollPane.setViewportView(tree);

		final JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		panel.add(buttonPane, BorderLayout.SOUTH);

		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		buttonPane.add(progressBar);

		moveButton = new JButton("Move");
		moveButton.setActionCommand("moveFile");
		moveButton.addActionListener(buttonActions);
		moveButton.setEnabled(false);
		buttonPane.add(moveButton);

		copyButton = new JButton("Copy");
		copyButton.setActionCommand("copyFile");
		copyButton.addActionListener(buttonActions);
		copyButton.setEnabled(false);
		buttonPane.add(copyButton);

		delButton = new JButton("Delete");
		delButton.setActionCommand("deleteFile");
		delButton.addActionListener(buttonActions);
		delButton.setEnabled(false);
		buttonPane.add(delButton);
	}
}
