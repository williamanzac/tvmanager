package com.wing.torrent.copier.ui.components;

import java.awt.Component;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.ListCellRenderer;

import org.apache.commons.lang.StringUtils;

import com.wing.torrent.copier.CopyTask;
import com.wing.torrent.copier.DeleteTask;
import com.wing.torrent.copier.FileTask;
import com.wing.torrent.copier.MoveTask;
import com.wing.torrent.copier.TorrentCopier;

public class FileTaskListRenderer extends JPanel implements ListCellRenderer<FileTask> {
	private static final long serialVersionUID = -236177389017890980L;

	private static final int MAXIMUM_LENGTH = 32;

	private final JLabel title;
	private final JLabel info;
	private final JProgressBar progress;
	private final TorrentCopier torrentCopier;

	public FileTaskListRenderer(final TorrentCopier torrentCopier) {
		this.torrentCopier = torrentCopier;
		title = new JLabel();
		info = new JLabel();
		progress = new JProgressBar();

		final BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(layout);
		Font infoFont = info.getFont();
		infoFont = infoFont.deriveFont(infoFont.getSize() + 5f);
		info.setFont(infoFont);

		setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		title.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		info.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		add(title);
		add(info);
		add(progress);
	}

	@Override
	public Component getListCellRendererComponent(final JList<? extends FileTask> list, final FileTask value,
			final int index, final boolean isSelected, final boolean cellHasFocus) {
		if (isSelected) {
			this.setForeground(list.getSelectionForeground());
			this.setBackground(list.getSelectionBackground());
		} else {
			this.setForeground(list.getForeground());
			this.setBackground(list.getBackground());
		}
		final int displayProgress = value.getProgress();
		final String displaySource = toDisplayPath(value.getSource());
		final String displayTarget = toDisplayPath(value.getTarget());
		if (value instanceof CopyTask) {
			title.setText("Copying " + displaySource + " to " + displayTarget);
			progress.setIndeterminate(false);
			info.setText("Progress: " + displayProgress + "%");
		} else if (value instanceof MoveTask) {
			title.setText("Moving " + displaySource + " to " + displayTarget);
			progress.setIndeterminate(false);
			info.setText("Progress: " + displayProgress + "%");
		} else if (value instanceof DeleteTask) {
			title.setText("Deleting " + displaySource);
			progress.setIndeterminate(true);
			info.setText("");
		}
		progress.setValue(displayProgress);
		return this;
	}

	String toDisplayPath(final File file) {
		String path;
		try {
			path = file.getCanonicalPath();
		} catch (final IOException e) {
			path = file.getAbsolutePath();
		}
		if (path.length() > MAXIMUM_LENGTH) {
			// shorten
			final List<String> parts = new ArrayList<>();
			final String[] split = StringUtils.split(path, File.separatorChar);
			for (final String string : split) {
				parts.add(string);
			}
			while (path.length() > MAXIMUM_LENGTH && parts.size() > 2) {
				parts.set(parts.size() - 2, "...");
				path = StringUtils.join(parts, File.separator);
				parts.remove(parts.size() - 2);
			}
		}
		return path;
	}
}
