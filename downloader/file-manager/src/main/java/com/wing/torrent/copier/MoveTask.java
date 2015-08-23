package com.wing.torrent.copier;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.io.FileUtils;

@XmlRootElement
public class MoveTask extends FileTask {
	MoveTask() {
		super();
	}

	public MoveTask(final File source, final File target) {
		super(source, target);
	}

	@Override
	public void run() {
		if (target != null && target.isDirectory() && source.isFile()) {
			target = new File(target, source.getName());
		}
		try (InputStream bis = new BufferedInputStream(new FileInputStream(source));
				OutputStream bos = new BufferedOutputStream(new FileOutputStream(target));) {
			int count;
			byte[] buffer = new byte[4096];
			while (-1 != (count = bis.read(buffer)) && !stopping) {
				bos.write(buffer, 0, count);
				copiedBytes += count;
				// System.out.println("moved: " + copiedBytes + " of " + totalBytes);
				setProgress((int) (copiedBytes * 100 / totalBytes));
			}
			FileUtils.deleteQuietly(source);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}