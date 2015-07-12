package com.wing.file.manager.ui;

import static javax.swing.UIManager.getSystemLookAndFeelClassName;
import static javax.swing.UIManager.setLookAndFeel;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import com.wing.file.manager.ui.components.FileTreeCellRenderer;
import com.wing.file.manager.ui.components.UIButton;

public class FileManager extends JFrame {
	private static final long serialVersionUID = 5110010088383327110L;

	private FileSystemView fileSystemView;
	private DefaultTreeModel treeModel;
	private JTree tree;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					setLookAndFeel(getSystemLookAndFeelClassName());
					FileManager window = new FileManager();
					window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public FileManager() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		setTitle("File Manager");
		setBounds(100, 100, 450, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setIconImage(new ImageIcon(getClass().getResource("main.png")).getImage());

		fileSystemView = FileSystemView.getFileSystemView();

		final File source = new File("d:\\downloads\\torrents");
		final DefaultMutableTreeNode root = new DefaultMutableTreeNode(source);
		// EventQueue.invokeLater(() -> {
		List<DefaultMutableTreeNode> nodes = getNodes(source);
		for (DefaultMutableTreeNode defaultMutableTreeNode : nodes) {
			root.add(defaultMutableTreeNode);
		}
		// });

		treeModel = new DefaultTreeModel(root);
		treeModel.setRoot(root);

		tree = new JTree(treeModel);
		tree.setCellRenderer(new FileTreeCellRenderer());
		tree.expandRow(0);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setRowHeight(32);
		tree.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

		getContentPane().add(tree, BorderLayout.NORTH);

		JScrollPane scrollPane = new JScrollPane(tree);
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		getContentPane().add(toolBar, BorderLayout.NORTH);

		UIButton configButton = new UIButton("../settings.png");
		toolBar.add(configButton);
	}

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
}
