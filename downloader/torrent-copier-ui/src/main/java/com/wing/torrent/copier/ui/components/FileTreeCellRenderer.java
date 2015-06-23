package com.wing.torrent.copier.ui.components;

import java.awt.Component;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class FileTreeCellRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = -1650672874639869958L;

	private final FileSystemView fileSystemView;

	private final JLabel label;

	public FileTreeCellRenderer() {
		label = new JLabel();
		label.setOpaque(true);
		fileSystemView = FileSystemView.getFileSystemView();
	}

	@Override
	public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean selected,
			final boolean expanded, final boolean leaf, final int row, final boolean hasFocus) {
		final DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		final File file = (File) node.getUserObject();
		label.setIcon(fileSystemView.getSystemIcon(file));
		label.setText(fileSystemView.getSystemDisplayName(file));
		if (file != null)
			label.setToolTipText(file.getPath());

		if (selected) {
			label.setBackground(backgroundSelectionColor);
			label.setForeground(textSelectionColor);
		} else {
			label.setBackground(backgroundNonSelectionColor);
			label.setForeground(textNonSelectionColor);
		}

		return label;
	}
}