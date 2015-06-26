package com.wing.torrent.copier.ui.components;

import java.io.File;

import javax.swing.SwingWorker;

public abstract class FileTask extends SwingWorker<Void, Integer> {

	protected final File source;
	protected final File target;
	protected long totalBytes = 0L;
	protected long copiedBytes = 0L;

	public FileTask(final File source, final File target) {
		super();
		this.source = source;
		this.target = target;
	}

	protected void retrieveTotalBytes(final File sourceFile) {
		totalBytes = sourceFile.length();
	}

}