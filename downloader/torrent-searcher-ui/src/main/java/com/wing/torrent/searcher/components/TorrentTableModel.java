package com.wing.torrent.searcher.components;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.wing.database.model.Torrent;

public class TorrentTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1141029170127565347L;

	private List<Torrent> torrents = new ArrayList<>();

	private final String[] columnNames = { "Title", "Categories", "Date", "Size", "Seeds", "Leachers", "Hash" };
	private final Class<?>[] columnTypes = { String.class, String.class, Date.class, Long.class, Integer.class,
			Integer.class, String.class };

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public String getColumnName(final int column) {
		return columnNames[column];
	}

	@Override
	public Class<?> getColumnClass(final int columnIndex) {
		return columnTypes[columnIndex];
	}

	@Override
	public int getRowCount() {
		return torrents == null ? 0 : torrents.size();
	}

	@Override
	public Object getValueAt(final int row, final int col) {
		final Torrent torrent = torrents.get(row);
		if (torrent != null) {
			switch (col) {
			case 0:
				return torrent.getTitle();
			case 1:
				return torrent.getCategories().toString();
			case 2:
				return torrent.getPubDate();
			case 3:
				return torrent.getSize();
			case 4:
				return torrent.getSeeds();
			case 5:
				return torrent.getLeechers();
			case 6:
				return torrent.getHash();
			}
		}
		return null;
	}

	public List<Torrent> getTorrents() {
		return torrents;
	}

	public void setTorrents(final List<Torrent> torrents) {
		this.torrents = torrents;
		fireTableDataChanged();
	}
}
