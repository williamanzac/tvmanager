package com.wing.torrent.downloader.ui.components;

import java.awt.Component;

import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class ProgressCellRender extends JProgressBar implements TableCellRenderer {

	private static final long serialVersionUID = 8226506466968456994L;

	public ProgressCellRender() {
		setStringPainted(true);
	}

	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
			final boolean hasFocus, final int row, final int column) {
		int progress = 0;
		if (value instanceof Float) {
			progress = Math.round((Float) value);
		} else if (value instanceof Integer) {
			progress = (int) value;
		}
		setValue(progress);
		return this;
	}
}