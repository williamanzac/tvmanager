package com.wing.torrent.downloader.ui.components;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

public class CheckTreeCellRenderer extends JPanel implements TreeCellRenderer {
	private static final long serialVersionUID = -3493623740230573135L;

	private final CheckTreeSelectionModel selectionModel;
	private final TreeCellRenderer delegate;
	private final TristateCheckBox checkBox = new TristateCheckBox();

	public CheckTreeCellRenderer(final TreeCellRenderer delegate, final CheckTreeSelectionModel selectionModel) {
		this.delegate = delegate;
		this.selectionModel = selectionModel;
		setLayout(new BorderLayout());
		setOpaque(false);
		checkBox.setOpaque(false);
	}

	@Override
	public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean selected,
			final boolean expanded, final boolean leaf, final int row, final boolean hasFocus) {
		final Component renderer = delegate.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row,
				hasFocus);

		final TreePath path = tree.getPathForRow(row);
		if (path != null) {
			if (selectionModel.isPathSelected(path, true)) {
				checkBox.setState(TristateCheckBox.State.SELECTED);
			} else {
				checkBox.setState(selectionModel.isPartiallySelected(path) ? TristateCheckBox.State.DONT_CARE
						: TristateCheckBox.State.NOT_SELECTED);
			}
		}
		removeAll();
		add(checkBox, BorderLayout.WEST);
		add(renderer, BorderLayout.CENTER);
		return this;
	}
}