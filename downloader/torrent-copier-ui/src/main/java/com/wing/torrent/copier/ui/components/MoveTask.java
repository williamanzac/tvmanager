package com.wing.torrent.copier.ui.components;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.SwingWorker;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class MoveTask extends SwingWorker<Void, Integer> {
	private final File source;
	private final File target;
	private long totalBytes = 0L;
	private long copiedBytes = 0L;

	public MoveTask(final File source, final File target) {
		this.source = source;
		this.target = target;
	}

	@Override
	public Void doInBackground() throws Exception {
		retrieveTotalBytes(source);

		moveFiles(source, target);
		return null;
	}

	private void retrieveTotalBytes(final File sourceFile) {
		totalBytes = sourceFile.length();
	}

	private void moveFiles(final File sourceFile, final File targetFile) throws IOException {
		try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(sourceFile));
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(targetFile));) {
			int count;
			byte[] buffer = new byte[4096];
			while (-1 != (count = IOUtils.read(bis, buffer))) {
				IOUtils.write(buffer, bos);
				copiedBytes += count;
				setProgress((int) (copiedBytes * 100 / totalBytes));
			}
			FileUtils.deleteQuietly(sourceFile);
		}
	}
}