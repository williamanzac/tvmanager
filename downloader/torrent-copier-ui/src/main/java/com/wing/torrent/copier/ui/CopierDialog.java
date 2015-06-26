package com.wing.torrent.copier.ui;

import static javax.swing.UIManager.getSystemLookAndFeelClassName;
import static javax.swing.UIManager.setLookAndFeel;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.wing.database.model.Configuration;
import com.wing.database.model.Torrent;
import com.wing.manager.service.ManagerService;
import com.wing.torrent.copier.TorrentCopier;
import com.wing.torrent.copier.ui.components.CheckTreeManager;
import com.wing.torrent.copier.ui.components.CopyTask;
import com.wing.torrent.copier.ui.components.FileTask;
import com.wing.torrent.copier.ui.components.FileTreeCellRenderer;

import javax.swing.JProgressBar;

public class CopierDialog extends JDialog {
	private static final long serialVersionUID = 6735931867561967734L;

	private final JPanel contentPanel = new JPanel();

	private final ManagerService managerService;

	private final TorrentCopier copier = new TorrentCopier();

	private final FileSystemView fileSystemView;
	private DefaultTreeModel treeModel;
	private JTree tree;
	private CheckTreeManager checkTreeManager;
	private JProgressBar progressBar;
	private JButton moveButton;
	private JButton copyButton;
	private JButton delButton;

	private class ButtonActions implements ActionListener {
		private final class FileTaskListener implements PropertyChangeListener {
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
		}

		@Override
		public void actionPerformed(final ActionEvent event) {
			final String command = event.getActionCommand();
			switch (command) {
			case "move":
				EventQueue.invokeLater(() -> {
					moveButton.setEnabled(false);
					copyButton.setEnabled(false);
					delButton.setEnabled(false);
					tree.setEnabled(false);
					try {
						final Configuration configuration = managerService.loadConfiguration();
						final File targetDir = configuration.showDestination;
						final TreePath checkedPaths[] = checkTreeManager.getSelectionModel().getSelectionPaths();
						for (final TreePath path : checkedPaths) {
							final DefaultMutableTreeNode sourceNode = (DefaultMutableTreeNode) path
									.getLastPathComponent();
							final File source = (File) sourceNode.getUserObject();
							final File dest = new File(targetDir, source.getName());
							System.out.println("moving: " + source + " to " + dest);
							copier.moveFileTo(source, dest);
						}
					} catch (final Exception e) {
						e.printStackTrace();
					}
					moveButton.setEnabled(true);
					copyButton.setEnabled(true);
					delButton.setEnabled(true);
					tree.setEnabled(true);
				});
				break;
			case "copy":
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
						final FileTask task = new CopyTask(source, dest);
						task.addPropertyChangeListener(new FileTaskListener());
						task.execute();
					}
				} catch (final Exception e) {
					e.printStackTrace();
				}
				break;
			case "delete":
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
							copier.removeFile(source);
						}
					} catch (final Exception e) {
						e.printStackTrace();
					}
					moveButton.setEnabled(true);
					copyButton.setEnabled(true);
					delButton.setEnabled(true);
					tree.setEnabled(true);
				});
				break;
			case "OK":
				setVisible(false);
				break;
			}
		}
	}

	/**
	 * Launch the application.
	 */
	public static void main(final String[] args) {
		try {
			setLookAndFeel(getSystemLookAndFeelClassName());
			final CopierDialog dialog = new CopierDialog(null);
			dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public CopierDialog(final ManagerService managerService) {
		this.managerService = managerService;
		setModal(true);
		setTitle("Torrent File Manager");
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		fileSystemView = FileSystemView.getFileSystemView();
		final ButtonActions buttonActions = new ButtonActions();

		{
			final JScrollPane scrollPane = new JScrollPane();
			contentPanel.add(scrollPane);
			{
				final DefaultMutableTreeNode root = new DefaultMutableTreeNode();
				treeModel = new DefaultTreeModel(root);

				tree = new JTree(treeModel);
				tree.setRootVisible(true);
				tree.setCellRenderer(new FileTreeCellRenderer());
				tree.expandRow(0);
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
			}
		}
		{
			final JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				progressBar = new JProgressBar();
				progressBar.setStringPainted(true);
				buttonPane.add(progressBar);
			}
			{
				moveButton = new JButton("Move");
				moveButton.setActionCommand("move");
				moveButton.addActionListener(buttonActions);
				moveButton.setEnabled(false);
				buttonPane.add(moveButton);
			}
			{
				copyButton = new JButton("Copy");
				copyButton.setActionCommand("copy");
				copyButton.addActionListener(buttonActions);
				copyButton.setEnabled(false);
				buttonPane.add(copyButton);
			}
			{
				delButton = new JButton("Delete");
				delButton.setActionCommand("delete");
				delButton.addActionListener(buttonActions);
				delButton.setEnabled(false);
				buttonPane.add(delButton);
			}
			{
				final JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				okButton.addActionListener(buttonActions);
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
	}

	public void manageTorrentFiles(final Torrent torrent) throws Exception {
		EventQueue.invokeLater(() -> {
			Configuration configuration;
			try {
				configuration = managerService.loadConfiguration();
				final File dest = configuration.torrentDestination;
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
			} catch (final Exception e) {
				e.printStackTrace();
			}
		});
		setVisible(true);
	}
}
