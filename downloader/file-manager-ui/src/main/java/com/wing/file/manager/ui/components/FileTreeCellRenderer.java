package com.wing.file.manager.ui.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class FileTreeCellRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = -1650672874639869958L;

	private final FileSystemView fileSystemView;

	private final JLabel label;
	private final JPanel panel;
	private final JPanel buttonPanel;
	private final JButton copyButton;
	private final JButton deleteButton;

	public FileTreeCellRenderer() {
		label = new JLabel();
		label.setBorder(BorderFactory.createEmptyBorder(16, 0, 20, 0));

		copyButton = new JButton(new ImageIcon(getClass().getResource("../copy.png")));
		deleteButton = new JButton(new ImageIcon(getClass().getResource("../delete.png")));

		final FlowLayout flowLayout = new FlowLayout();
		flowLayout.setHgap(2);
		flowLayout.setVgap(0);

		buttonPanel = new JPanel();
		buttonPanel.setLayout(flowLayout);
		buttonPanel.setOpaque(false);
		buttonPanel.add(copyButton);
		buttonPanel.add(deleteButton);

		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(label, BorderLayout.CENTER);
		panel.add(buttonPanel, BorderLayout.EAST);

		fileSystemView = FileSystemView.getFileSystemView();
	}

	@Override
	public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean selected,
			final boolean expanded, final boolean leaf, final int row, final boolean hasFocus) {
		final DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		final File file = (File) node.getUserObject();
		label.setIcon(fileSystemView.getSystemIcon(file));
		label.setText(fileSystemView.getSystemDisplayName(file));
		if (file != null) {
			label.setToolTipText(file.getPath());
		}

		if (selected) {
			panel.setBackground(getBackgroundSelectionColor());
			panel.setForeground(getTextSelectionColor());
		} else {
			panel.setBackground(getBackgroundNonSelectionColor());
			panel.setForeground(getTextNonSelectionColor());
		}
		setEnabled(isEnabled());
		setFont(getFont());

		return panel;
	}
}