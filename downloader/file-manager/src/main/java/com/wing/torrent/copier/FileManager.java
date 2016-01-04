package com.wing.torrent.copier;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.List;

public class FileManager {

	private final TaskPersistenceManager persistenceManager = new TaskPersistenceManager();

	private final Thread actionThread;
	private boolean stopping;
	private FileTask currentTask;

	private class ChangeListener implements PropertyChangeListener {
		@Override
		public void propertyChange(final PropertyChangeEvent evt) {
			try {
				persistenceManager.save(currentTask);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}

	public FileManager() throws Exception {
		final ChangeListener changeListener = new ChangeListener();
		actionThread = new Thread(() -> {
			while (!stopping) {
				if (currentTask == null || currentTask.getProgress() == 100) {
					// new task
				try {
					final List<FileTask> list = getTasks();
					for (final FileTask fileTask : list) {
						if (fileTask.getProgress() < 100) {
							currentTask = fileTask;
							currentTask.addPropertyChangeListener(changeListener);
							break;
						}
					}
				} catch (final Exception e) {
					stopping = true;
					e.printStackTrace();
				}
			} else {
				// start task
				currentTask.run();
				currentTask.removePropertyChangeListener(changeListener);
				if (currentTask.getProgress() >= 100 && !currentTask.isStopping()) {
					try {
						persistenceManager.delete(currentTask.getId().toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			try {
				Thread.sleep(1000);
			} catch (final Exception e) {
				stopping = true;
				e.printStackTrace();
			}
		}
	}	);
	}

	public List<FileTask> getTasks() throws Exception {
		final List<FileTask> list = persistenceManager.list();
		Collections.sort(list, (o1, o2) -> {
			if (o1 instanceof DeleteTask && !(o2 instanceof DeleteTask)) {
				return 1;
			}
			if (o2 instanceof DeleteTask && !(o1 instanceof DeleteTask)) {
				return -1;
			}
			return o1.source.compareTo(o2.source);
		});
		return list;
	}

	public void addTask(final FileTask task) throws Exception {
		persistenceManager.save(task);
	}

	public FileTask getTask(final String key) throws Exception {
		return persistenceManager.retrieve(key);
	}

	public void deleteTask(final String key) throws Exception {
		persistenceManager.delete(key);
	}

	public void start() {
		stopping = false;
		actionThread.start();
	}

	public void stop() {
		stopping = true;
		if (currentTask != null) {
			currentTask.setStopping(true);
		}
		try {
			actionThread.join();
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}

	public FileTask getCurrentTask() {
		return currentTask;
	}
}
