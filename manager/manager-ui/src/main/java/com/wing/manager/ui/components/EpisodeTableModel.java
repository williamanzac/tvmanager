package com.wing.manager.ui.components;

import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.wing.database.model.Episode;

public class EpisodeTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 808297617040940353L;

	private List<Episode> episodes;

	@Override
	public int getColumnCount() {
		return 4;
	}

	@Override
	public int getRowCount() {
		return episodes == null ? 0 : episodes.size();
	}

	@Override
	public Object getValueAt(final int row, final int col) {
		final Episode episode = episodes.get(row);
		if (episode != null) {
			switch (col) {
			case 0:
				return String.format("s%1$02de%2$02d", episode.getSeason(), episode.getNumber());
			case 1:
				return episode.getTitle();
			case 2:
				return episode.getAirdate();
			case 3:
				return episode.getState() == null ? null : episode.getState().name();
			}
		}
		return null;
	}

	@Override
	public Class<?> getColumnClass(final int columnIndex) {
		if (columnIndex == 2) {
			return Date.class;
		}
		return String.class;
	}

	@Override
	public String getColumnName(final int column) {
		switch (column) {
		case 0:
			return "Episode";
		case 1:
			return "Title";
		case 2:
			return "Date";
		case 3:
			return "Status";
		}
		return super.getColumnName(column);
	}

	public void setEpisodes(final List<Episode> episodes) {
		this.episodes = episodes;
		fireTableDataChanged();
	}

	public Episode getEpisode(final int index) {
		if (index < 0) {
			return null;
		}
		return episodes.get(index);
	}
}
