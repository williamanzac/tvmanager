package com.wing.file.manager.ui.components;

import java.util.List;

import javax.swing.AbstractListModel;

import com.wing.torrent.copier.FileTask;
import com.wing.torrent.copier.FileManager;

public class FileTaskListModel extends AbstractListModel<FileTask> {
	private static final long serialVersionUID = 637499522844142872L;

	private List<FileTask> list;
	private final FileManager torrentCopier;

	public FileTaskListModel(final FileManager torrentCopier) throws Exception {
		this.torrentCopier = torrentCopier;
		list = torrentCopier.getTasks();
		final Thread thread = new Thread(() -> {
			while (true) {
				try {
					list = torrentCopier.getTasks();
					fireContentsChanged(this, 0, getSize());
					Thread.sleep(1000);
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}

	@Override
	public int getSize() {
		return list.size();
	}

	@Override
	public FileTask getElementAt(final int index) {
		return index < 0 ? null : list.get(index);
	}

	public void add(final FileTask show) throws Exception {
		final int i = list.size();
		list.add(show);
		torrentCopier.addTask(show);
		fireIntervalAdded(this, i, i);
	}

	public FileTask remove(final int index) throws Exception {
		final FileTask show = list.remove(index);
		torrentCopier.deleteTask(show.getId().toString());
		fireIntervalRemoved(this, index, index);
		return show;
	}
}
