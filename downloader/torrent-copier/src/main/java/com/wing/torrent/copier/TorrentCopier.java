package com.wing.torrent.copier;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class TorrentCopier {

	public void copyFileTo(File source, File dest) throws IOException {
		FileUtils.copyFile(source, dest);
	}

	public void moveFileTo(File source, File dest) throws IOException {
		FileUtils.moveFile(source, dest);
	}

	public void removeFile(File file) {
		FileUtils.deleteQuietly(file);
	}
}
