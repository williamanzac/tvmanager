package com.wing.manager.ui.components;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import com.wing.database.model.Show;

public class ShowListCellRenderer extends DefaultListCellRenderer {

	private static final long serialVersionUID = -1274511146153429092L;

	@Override
	public Component getListCellRendererComponent(final JList<?> list, final Object value, final int index,
			final boolean isSelected, final boolean cellHasFocus) {
		return super.getListCellRendererComponent(list, ((Show) value).getName(), index, isSelected, cellHasFocus);
	}

}
