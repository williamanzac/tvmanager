package com.wing.torrent.downloader;

import com.wing.manager.service.ManagerService;

public abstract class TorrentDownloader {

	protected final ManagerService managerService;

	public TorrentDownloader(final ManagerService managerService) {
		this.managerService = managerService;
	}

	public abstract void start();

	public abstract void stop() throws InterruptedException;
}
