package com.wing.torrent.copier;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.wing.database.service.DatabasePersistenceManager;

public class TaskPersistenceManager extends DatabasePersistenceManager<FileTask> {

	@Override
	public FileTask retrieve(final String id) throws Exception {
		final PreparedStatement statement = con.prepareStatement("select * from filetasks where id=?");
		statement.setString(1, id);
		final ResultSet resultSet = statement.executeQuery();
		FileTask task = null;
		while (resultSet.next()) {
			final String type = resultSet.getString(7);
			switch (type) {
			case "CopyTask":
				task = new CopyTask();
				break;
			case "MoveTask":
				task = new MoveTask();
				break;
			case "DeleteTask":
				task = new DeleteTask();
				break;
			default:
				throw new Exception("Unknown task type: " + type);
			}
			task.setCopiedBytes(resultSet.getInt(5));
			task.setId(UUID.fromString(resultSet.getString(1)));
			task.setProgress(resultSet.getInt(6));
			String string = resultSet.getString(2);
			if (string != null) {
				task.setSource(new File(string));
			}
			string = resultSet.getString(3);
			if (string != null) {
				task.setTarget(new File(string));
			}
			task.setTotalBytes(resultSet.getLong(4));
		}
		return task;
	}

	// "filetasks (id,source,target,totalBytes,copiedBytes,progress,type");

	@Override
	public void save(final FileTask value) throws Exception {
		final String id = value.getId().toString();
		final FileTask existing = retrieve(id);
		PreparedStatement statement;
		if (existing == null) {
			// create
			statement = con
					.prepareStatement("insert into filetasks (id,source,target,totalBytes,copiedBytes,progress,type) values (?,?,?,?,?,?,?)");
			statement.setLong(5, value.getCopiedBytes());
			statement.setString(1, value.getId().toString());
			statement.setInt(6, value.getProgress());
			if (value.getSource() != null) {
				statement.setString(2, value.getSource().getAbsolutePath());
			} else {
				statement.setString(2, null);
			}
			if (value.getTarget() != null) {
				statement.setString(3, value.getTarget().getAbsolutePath());
			} else {
				statement.setString(3, null);
			}
			statement.setLong(4, value.getTotalBytes());
			if (value instanceof CopyTask) {
				statement.setString(7, "CopyTask");
			} else if (value instanceof MoveTask) {
				statement.setString(7, "MoveTask");
			} else if (value instanceof DeleteTask) {
				statement.setString(7, "DeleteTask");
			}
		} else {
			// update
			statement = con
					.prepareStatement("update filetasks set (source,target,totalBytes,copiedBytes,progress) = (?,?,?,?,?) where id = ?");
			statement.setLong(4, value.getCopiedBytes());
			statement.setString(6, value.getId().toString());
			statement.setInt(5, value.getProgress());
			if (value.getSource() != null) {
				statement.setString(1, value.getSource().getAbsolutePath());
			} else {
				statement.setString(1, null);
			}
			if (value.getTarget() != null) {
				statement.setString(2, value.getTarget().getAbsolutePath());
			} else {
				statement.setString(2, null);
			}
			statement.setLong(3, value.getTotalBytes());
		}
		statement.executeUpdate();
	}

	@Override
	public void delete(final String id) throws Exception {
		final PreparedStatement statement = con.prepareStatement("delete from filetasks where id=?");
		statement.setString(1, id);
		statement.executeUpdate();
	}

	@Override
	public List<FileTask> list() throws Exception {
		final PreparedStatement statement = con.prepareStatement("select * from filetasks");
		final ResultSet resultSet = statement.executeQuery();
		final List<FileTask> tasks = new ArrayList<>();
		while (resultSet.next()) {
			final String type = resultSet.getString(7);
			final FileTask task;
			switch (type) {
			case "CopyTask":
				task = new CopyTask();
				break;
			case "MoveTask":
				task = new MoveTask();
				break;
			case "DeleteTask":
				task = new DeleteTask();
				break;
			default:
				throw new Exception("Unknown task type: " + type);
			}
			task.setCopiedBytes(resultSet.getInt(5));
			task.setId(UUID.fromString(resultSet.getString(1)));
			task.setProgress(resultSet.getInt(6));
			String string = resultSet.getString(2);
			if (string != null) {
				task.setSource(new File(string));
			}
			string = resultSet.getString(3);
			if (string != null) {
				task.setTarget(new File(string));
			}
			task.setTotalBytes(resultSet.getLong(4));

			tasks.add(task);
		}
		return tasks;
	}
}
