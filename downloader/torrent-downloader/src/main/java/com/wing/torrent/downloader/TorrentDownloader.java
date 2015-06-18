package com.wing.torrent.downloader;

import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.apache.commons.io.IOUtils;

import com.turn.ttorrent.client.Client;
import com.turn.ttorrent.client.Client.ClientState;
import com.turn.ttorrent.client.SharedTorrent;
import com.wing.database.model.Configuration;
import com.wing.database.model.Torrent;
import com.wing.database.model.TorrentState;
import com.wing.manager.service.ManagerService;

public class TorrentDownloader {

	private final List<Torrent> queue = new ArrayList<>();
	private final Map<Client, Torrent> downloadQueue = new HashMap<>();

	private final ManagerService managerService;
	// private final ConfigurationService configurationService;
	private final Thread monitorThread;
	private boolean running = true;

	private class DownloadObserver implements Observer {
		@Override
		public void update(final Observable o, final Object arg) {
			final Client client = (Client) o;
			final ClientState state = client.getState();
			final SharedTorrent sharedTorrent = client.getTorrent();
			final float completion = sharedTorrent.getCompletion();
			System.out.println(state + "," + completion);
			final Torrent torrent = downloadQueue.get(client);
			torrent.setPercentComplete(completion);
			switch (state) {
			case DONE:
				if (sharedTorrent.isComplete()) {
					torrent.setState(TorrentState.DONE);
					downloadQueue.remove(client);
					queue.remove(torrent);
				}
				break;
			case ERROR:
				torrent.setState(TorrentState.ERROR);
				downloadQueue.remove(client);
				queue.remove(torrent);
				break;
			case SHARING:
				torrent.setState(TorrentState.DOWNLOADING);
				break;
			case VALIDATING:
				torrent.setState(TorrentState.VALIDATING);
				break;
			case WAITING:
				torrent.setState(TorrentState.QUEUED);
				break;
			default:
				break;
			}
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
		monitorThread = new Thread(() -> {
			while (running) {
				try {
					final List<Torrent> torrents = managerService.listTorrents();
					for (final Torrent torrent1 : torrents) {
						if (!queue.contains(torrent1) && TorrentState.DONE != torrent1.getState()
								&& TorrentState.ERROR != torrent1.getState()) {
							queue.add(torrent1);
							if (torrent1.getState() == null) {
								torrent1.setState(TorrentState.QUEUED);
								managerService.saveTorrent(torrent1);
							}
						}
					}
					// get the next torrent in the queue and start downloading.
				if (downloadQueue.isEmpty() && !queue.isEmpty()) {
					final Torrent torrent2 = queue.get(0);
					final byte[] byteArray = IOUtils.toByteArray(torrent2.getUrl().openStream());
					final Configuration loadConfiguration = managerService.loadConfiguration();
					final File torrentDestination = loadConfiguration.torrentDestination;
					final SharedTorrent torrent = new SharedTorrent(byteArray, torrentDestination);
					final Client client = new Client(InetAddress.getLocalHost(), torrent);
					client.setMaxUploadRate(0.1d);
					client.download();
					client.addObserver(observer);

					downloadQueue.put(client, torrent2);

					torrent2.setTitle(torrent.getName());
					torrent2.setState(TorrentState.DOWNLOADING);
					managerService.saveTorrent(torrent2);

					client.waitForCompletion();
				}
				Thread.sleep(1000);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	});
	}

	public void start() {
		running = true;
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
