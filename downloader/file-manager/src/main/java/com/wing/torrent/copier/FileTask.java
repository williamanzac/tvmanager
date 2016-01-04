package com.wing.torrent.copier;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.UUID;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
public abstract class FileTask implements Runnable {

	private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

	@XmlElement
	private UUID id = UUID.randomUUID();
	@XmlElement
	protected File source;
	@XmlElement
	protected File target;
	@XmlElement
	protected long totalBytes = 0L;
	@XmlTransient
	protected long copiedBytes = 0L;
	@XmlTransient
	protected boolean stopping = false;

	@XmlElement
	private int progress;

	protected FileTask() {
	}

	public FileTask(final File source, final File target) {
		super();
		this.source = source;
		this.target = target;
		totalBytes = source.length();
	}

	protected void setProgress(final int progress) {
		final int oldValue = this.progress;
		this.progress = progress;
		changeSupport.firePropertyChange("progress", oldValue, progress);
	}

	public int getProgress() {
		return progress;
	}

	public UUID getId() {
		return id;
	}

	public File getSource() {
		return source;
	}

	public File getTarget() {
		return target;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(listener);
	}

	public void setStopping(boolean stopping) {
		this.stopping = stopping;
	}

	public boolean isStopping() {
		return stopping;
	}

	void setId(UUID id) {
		this.id = id;
	}

	void setSource(File source) {
		this.source = source;
	}

	void setTarget(File target) {
		this.target = target;
	}

	void setTotalBytes(long totalBytes) {
		this.totalBytes = totalBytes;
	}

	void setCopiedBytes(long copiedBytes) {
		this.copiedBytes = copiedBytes;
	}

	public long getTotalBytes() {
		return totalBytes;
	}

	public long getCopiedBytes() {
		return copiedBytes;
	}
}
