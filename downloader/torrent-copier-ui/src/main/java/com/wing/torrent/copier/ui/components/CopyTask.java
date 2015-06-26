package com.wing.torrent.copier.ui.components;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

public class CopyTask extends FileTask {
	public CopyTask(final File source, final File target) {
		super(source, target);
	}

	@Override
	public Void doInBackground() throws Exception {
		retrieveTotalBytes(source);

		copyFiles(source, target);
		return null;
	}

	private void copyFiles(final File sourceFile, final File targetFile) throws IOException {
		try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(sourceFile));
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(targetFile));) {
			int count;
			byte[] buffer = new byte[4096];
			while (-1 != (count = IOUtils.read(bis, buffer))) {
				IOUtils.write(buffer, bos);
				copiedBytes += count;
				setProgress((int) (copiedBytes * 100 / totalBytes));
			}
		}
	}
}