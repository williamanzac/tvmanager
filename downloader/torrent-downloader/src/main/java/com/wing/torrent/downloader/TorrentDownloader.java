package com.wing.torrent.downloader;

import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import com.turn.ttorrent.client.Client;
import com.turn.ttorrent.client.SharedTorrent;
import com.wing.database.model.Torrent;
import com.wing.database.model.TorrentState;
import com.wing.manager.service.ManagerService;

public class TorrentDownloader {

	private final List<Torrent> queue = new ArrayList<>();
	private final Map<Client, Torrent> downloadQueue = new HashMap<>();

	private final ManagerService managerService;
	private final Thread monitorThread;
	private boolean running = true;

	private class DownloadObserver implements Observer {
		@Override
		public void update(final Observable o, final Object arg) {
			final Client client = (Client) o;
			final float completion = client.getTorrent().getCompletion();
			final Torrent torrent = downloadQueue.get(client);
			torrent.setPercentComplete(completion);
			if (client.getTorrent().isComplete()) {
				torrent.setState(TorrentState.DOWNLOADED);
			}
			System.out.println(completion);
			try {
				managerService.saveTorrent(torrent);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}

	public TorrentDownloader(final ManagerService managerService) {
		super();
		final DownloadObserver observer = new DownloadObserver();
		this.managerService = managerService;
		this.monitorThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (running) {
					try {
						final List<Torrent> torrents = managerService
								.listTorrents();
						for (final Torrent torrent : torrents) {
							if (!queue.contains(torrent)) {
								torrent.setState(TorrentState.QUEUED);
								queue.add(torrent);
								managerService.saveTorrent(torrent);
							}
						}
						if (downloadQueue.size() < 5) {
							for (final Torrent torrent : torrents) {
								if (TorrentState.QUEUED == torrent.getState()) {
									torrent.setState(TorrentState.DOWNLOADING);
									managerService.saveTorrent(torrent);

									final Client client = new Client(
											InetAddress.getLocalHost(),
											SharedTorrent.fromFile(
													new File(torrent
															.getTorrentFile()),
													new File(torrent
															.getDestination())));
									client.setMaxUploadRate(0.1d);
									client.download();
									client.addObserver(observer);
									downloadQueue.put(client, torrent);
									break;
								}
							}
						}
						Thread.sleep(1000);
					} catch (final Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		monitorThread.start();
	}

	public void stop() throws InterruptedException {
		running = false;
		for (final Client client : downloadQueue.keySet()) {
			client.stop();
		}
		monitorThread.join();
	}
}
