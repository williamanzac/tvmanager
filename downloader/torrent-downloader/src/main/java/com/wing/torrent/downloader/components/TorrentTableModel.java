package com.wing.torrent.downloader.components;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.wing.database.model.Torrent;
import com.wing.manager.service.ManagerService;

public class TorrentTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -2842329820530402416L;

	private static final String[] columnNames = { "Name", "State", "Downloaded" };

	private List<Torrent> list;
	private ManagerService managerService;

	public TorrentTableModel(ManagerService managerService) throws Exception {
		this.managerService = managerService;
		list = managerService.listTorrents();
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						list = managerService.listTorrents();
						fireTableDataChanged();
						Thread.sleep(10000);
					} catch (Exception e) {
						e.printStackTrace();
					}
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
	public String getColumnName(int column) {
		return columnNames[column];
	}

	@Override
	public int getRowCount() {
		return list == null ? 0 : list.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		Torrent torrent = list.get(row);
		if (torrent != null) {
			switch (col) {
			case 0:
				return torrent.getName();
			case 1:
				return torrent.getState();
			case 2:
				return torrent.getPercentComplete();
			}
		}
		return null;
	}

	public void add(final Torrent torrent) throws Exception {
		int i = list.size();
		list.add(torrent);
		managerService.saveTorrent(torrent);
		fireTableRowsInserted(i, i);
	}

	public Torrent remove(final int index) throws Exception {
		Torrent torrent = list.remove(index);
		// managerService.removeTorrent(torrent); TODO
		fireTableRowsDeleted(index, index);
		return torrent;
	}
}
