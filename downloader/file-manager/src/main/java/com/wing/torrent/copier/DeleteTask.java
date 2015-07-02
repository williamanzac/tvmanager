package com.wing.torrent.copier;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.io.FileUtils;

@XmlRootElement
public class DeleteTask extends FileTask {
	@SuppressWarnings("unused")
	private DeleteTask() {
		super();
	}

	public DeleteTask(File source) {
		super(source, null);
	}

	@Override
	public void run() {
		setProgress(0);
		try {
			if (source.exists()) {
				FileUtils.forceDelete(source);
			}
			setProgress(100);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
