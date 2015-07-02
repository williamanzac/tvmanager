package com.wing.torrent.copier;

import com.wing.database.service.FilePersistenceManager;

public class TaskPersistenceManager extends FilePersistenceManager<FileTask> {

	@SuppressWarnings("unchecked")
	@Override
	protected Class<FileTask>[] forClasses() {
		return new Class[] { FileTask.class, CopyTask.class, MoveTask.class, DeleteTask.class };
	}
}
