package com.wing.search.ui.components;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.wing.database.model.Show;

public class ShowTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -6544388767409503171L;

	private List<Show> shows = new ArrayList<>();

	@Override
	public String getColumnName(final int column) {
		switch (column) {
		case 0:
			return "Name";
		case 1:
			return "Status";
		default:
			return super.getColumnName(column);
		}
	}

	@Override
	public Class<?> getColumnClass(final int columnIndex) {
		switch (columnIndex) {
		case 0:
			return String.class;
		case 1:
			return String.class;
		default:
			return super.getColumnClass(columnIndex);
		}
	}

	@Override
	public int getRowCount() {
		return shows.size();
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public Object getValueAt(final int row, final int col) {
		final Show show = shows.get(row);
		if (show != null) {
			switch (col) {
			case 0:
				return show.getName();
			case 1:
				return show.getStatus();
			}
		}
		return null;
	}

	public List<Show> getShows() {
		return shows;
	}

	public void setShows(final List<Show> shows) {
		this.shows = shows;
		fireTableDataChanged();
	}
}
