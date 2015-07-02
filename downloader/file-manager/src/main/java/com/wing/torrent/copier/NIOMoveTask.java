package com.wing.torrent.copier;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.io.FileUtils;

@XmlRootElement
public class NIOMoveTask extends FileTask {
	@SuppressWarnings("unused")
	private NIOMoveTask() {
		super();
	}

	public NIOMoveTask(final File source, final File target) {
		super(source, target);
	}

	@Override
	public void run() {
		if (target != null && target.isDirectory() && source.isFile()) {
			target = new File(target, source.getName());
		}
		// System.out.println("moving: " + source + " to " + target);
		try (FileInputStream inStream = new FileInputStream(source);
				FileOutputStream outStream = new FileOutputStream(target);
				FileChannel in = inStream.getChannel();
				FileChannel out = outStream.getChannel();) {
			int count;
			ByteBuffer buffer = ByteBuffer.allocate(4096);
			while (-1 != (count = in.read(buffer)) && !stopping) {
				buffer.flip();
				out.write(buffer);
				copiedBytes += count;
				// System.out.println("moved: " + copiedBytes + " of " + totalBytes);
				setProgress((int) (copiedBytes * 100 / totalBytes));
				buffer.clear();
			}
			FileUtils.deleteQuietly(source);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}