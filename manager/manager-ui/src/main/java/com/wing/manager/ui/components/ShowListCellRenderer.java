package com.wing.manager.ui.components;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import com.wing.database.model.Show;

public class ShowListCellRenderer extends DefaultListCellRenderer {

	private static final long serialVersionUID = -1274511146153429092L;

	private final JLabel label = new JLabel();

	public ShowListCellRenderer() {
		label.setOpaque(true);
	}

	@Override
	public Component getListCellRendererComponent(final JList<?> list, final Object value, final int index,
			final boolean isSelected, final boolean cellHasFocus) {
		label.setText(((Show) value).getName());
		if (isSelected) {
			label.setBackground(list.getSelectionBackground());
			label.setForeground(list.getSelectionForeground());
		} else {
			label.setBackground(list.getBackground());
			label.setForeground(list.getForeground());
		}
		setEnabled(list.isEnabled());
		setFont(list.getFont());
		return label;
	}

}
