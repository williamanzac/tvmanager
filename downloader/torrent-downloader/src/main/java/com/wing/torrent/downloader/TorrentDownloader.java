package com.wing.torrent.downloader;

import java.io.File;

import com.wing.database.model.Torrent;
import com.wing.manager.service.ManagerService;

public abstract class TorrentDownloader {

	protected final ManagerService managerService;

	public TorrentDownloader(final ManagerService managerService) {
		this.managerService = managerService;
	}

	public abstract void start();

	public abstract void stop() throws InterruptedException;

	public abstract Torrent addTorrent(File torrent) throws Exception;

	public abstract void startTorrent(Torrent torrent) throws Exception;

	public abstract void pauseTorrent(Torrent torrent) throws Exception;

	public abstract void stopTorrent(Torrent torrent) throws Exception;

	public abstract void removeTorrent(Torrent torrent) throws Exception;
}
