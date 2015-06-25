package com.wing.provider.ttorrent.downloader;

import java.io.File;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.PatternLayout;

import com.turn.ttorrent.client.Client;
import com.turn.ttorrent.client.Client.ClientState;
import com.turn.ttorrent.client.SharedTorrent;
import com.wing.database.model.Configuration;
import com.wing.database.model.Torrent;
import com.wing.database.model.TorrentState;
import com.wing.manager.service.ManagerService;
import com.wing.torrent.downloader.TorrentDownloader;

public class TTorrentTorrentDownloader extends TorrentDownloader {

	private final List<Torrent> queue = new ArrayList<>();
	private final Map<Client, Torrent> downloadQueue = new HashMap<>();

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

	public TTorrentTorrentDownloader(final ManagerService managerService) {
		super(managerService);
		BasicConfigurator.configure(new ConsoleAppender(new PatternLayout("%d [%-25t] %-5p: %m%n")));
		final DownloadObserver observer = new DownloadObserver();
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
					final InputStream response = getResponse(torrent2.getUrl().toString());
					final byte[] byteArray = IOUtils.toByteArray(response);
					// System.out.println(new String(byteArray));
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
			} catch (final Exception e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(1000);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}	);
	}

	protected InputStream getResponse(final String url) throws Exception {
		final HttpClient client = new HttpClient();
		final GetMethod method = new GetMethod(url);
		// method.getParams().setCookiePolicy(CookiePolicy.RFC_2109);
		client.executeMethod(method);
		return method.getResponseBodyAsStream();
	}

	@Override
	public void start() {
		running = true;
		monitorThread.start();
	}

	@Override
	public void stop() throws InterruptedException {
		running = false;
		for (final Client client : downloadQueue.keySet()) {
			client.stop();
		}
		monitorThread.join();
	}

	@Override
	public void addTorrent(File torrent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startTorrent(Torrent torrent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pauseTorrent(Torrent torrent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void stopTorrent(Torrent torrent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeTorrent(Torrent torrent) {
		// TODO Auto-generated method stub

	}
}
