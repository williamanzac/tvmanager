package com.wing.torrent.downloader.components;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.wing.database.model.Torrent;
import com.wing.database.service.TorrentPersistenceManager;

public class TorrentTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -2842329820530402416L;

	private static final String[] columnNames = { "Name", "State", "Downloaded" };

	private List<Torrent> list;
	private final TorrentPersistenceManager torrentPersistenceManager;

	public TorrentTableModel(final TorrentPersistenceManager torrentPersistenceManager) throws Exception {
		this.torrentPersistenceManager = torrentPersistenceManager;
		list = torrentPersistenceManager.list();
		final Thread thread = new Thread(() -> {
			while (true) {
				try {
					list = torrentPersistenceManager.list();
					fireTableDataChanged();
					Thread.sleep(10000);
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public String getColumnName(final int column) {
		return columnNames[column];
	}

	@Override
	public int getRowCount() {
		return list == null ? 0 : list.size();
	}

	@Override
	public Object getValueAt(final int row, final int col) {
		final Torrent torrent = list.get(row);
		if (torrent != null) {
			switch (col) {
			case 0:
				return torrent.getTitle();
			case 1:
				return torrent.getState();
			case 2:
				return torrent.getPercentComplete();
			}
		}
		return null;
	}

	public void add(final Torrent torrent) throws Exception {
		final int i = list.size();
		list.add(torrent);
		torrentPersistenceManager.save(torrent.getHash(),torrent);
		fireTableRowsInserted(i, i);
	}

	public Torrent remove(final int index) throws Exception {
		final Torrent torrent = list.remove(index);
		// managerService.removeTorrent(torrent); TODO
		fireTableRowsDeleted(index, index);
		return torrent;
	}
}
