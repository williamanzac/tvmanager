package com.wing.provider.utorrent.downloader;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import org.ccil.cowan.tagsoup.Parser;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.json.JSONArray;
import org.json.JSONObject;

import com.wing.database.model.Configuration;
import com.wing.database.model.Torrent;
import com.wing.database.model.TorrentState;
import com.wing.manager.service.ManagerService;
import com.wing.torrent.downloader.TorrentDownloader;

public class UTorrentDownloader extends TorrentDownloader {

	private static String authtoken;
	private final SAXReader htmlReader = new SAXReader(new Parser());
	private final Map<String, Torrent> queue = new HashMap<>();
	private final HttpClient client = new HttpClient();
	private final Thread monitorThread;
	private boolean running = true;

	public UTorrentDownloader(final ManagerService managerService) throws Exception {
		super(managerService);
		HttpState state = client.getState();
		Configuration configuration = managerService.loadConfiguration();
		Credentials credentials = new UsernamePasswordCredentials(configuration.torrentUsername,
				configuration.torrentPassword);
		state.setCredentials(null, null, credentials);
		monitorThread = new Thread(() -> {
			while (running) {
				try {
					// get current list
				final List<Torrent> torrents = managerService.listTorrents();
				for (final Torrent torrent : torrents) {
					queue.put(torrent.getHash(), torrent);
					if (torrent.getState() == null) {
						// add to uTorrent
						addTorrent(torrent);
					}
				}

				// update from uTorrent complete/state
				final List<Torrent> torrentList = getTorrentList();
				for (final Torrent torrent : torrentList) {
					final Torrent torrent2 = queue.get(torrent.getHash());
					if (torrent2 != null) {
						torrent2.setPercentComplete(torrent.getPercentComplete());
						torrent2.setState(torrent.getState());
						managerService.saveTorrent(torrent2);
					} else {
						queue.put(torrent.getHash(), torrent);
						managerService.saveTorrent(torrent);
					}
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

	public void start() {
		running = true;
		monitorThread.start();
	}

	public void stop() throws InterruptedException {
		running = false;
		monitorThread.join();
	}

	protected InputStream getResponse(final String url) throws Exception {
		final GetMethod method = new GetMethod(url);
		// method.getParams().setCookiePolicy(CookiePolicy.RFC_2109);
		client.executeMethod(method);
		if (method.getStatusCode() != 200) {
			throw new Exception(method.getStatusCode() + ": " + method.getStatusText());
		}
		return method.getResponseBodyAsStream();
	}

	protected JSONObject getuTorrentResponse(final String additional) throws Exception {
		final String token = getToken();
		final InputStream inputStream = getResponse(buildWebUIUrl() + "?token=" + token + additional);
		final String result = IOUtils.toString(inputStream);
		return new JSONObject(result);
	}

	private String buildWebUIUrl() {
		return "http://localhost:64717/gui/";
	}

	synchronized String getToken() throws Exception {
		if (authtoken == null) {
			final InputStream stream = getResponse(buildWebUIUrl() + "token.html");
			final Document document = htmlReader.read(stream);
			authtoken = parseToken(document);
		}
		return authtoken;
	}

	String parseToken(Document document) throws Exception {
		System.out.println(document.asXML());
		final Element node = (Element) document.selectSingleNode("//html:div");
		final String string = node.getTextTrim();
		return string;
	}

	List<Torrent> getTorrentList() throws Exception {
		final JSONObject object = getuTorrentResponse("&list=1");
		return parseTorrentList(object);
	}

	void addTorrent(Torrent torrent) throws Exception {
		getuTorrentResponse("&action=add-url&s=" + URLEncoder.encode(torrent.getUrl().toString(), "UTF-8"));
	}

	List<Torrent> parseTorrentList(final JSONObject object) {
		final JSONArray jsonArray = object.getJSONArray("torrents");
		final List<Torrent> torrents = new ArrayList<>();
		for (int i = 0; i < jsonArray.length(); i++) {
			final JSONArray tor = jsonArray.getJSONArray(i);
			final String hash = tor.getString(0);
			final int status = tor.getInt(1);
			final String name = tor.getString(2);
			final int size = tor.getInt(3); // bytes
			final int percent = tor.getInt(4);
			// final int downloaded = tor.getInt(5);// bytes
			// final int uploaded = tor.getInt(6);// bytes
			// final int ratio = tor.getInt(7);
			// final int uploadSpeed = tor.getInt(8);
			// final int downSpeed = tor.getInt(9);
			// final int eta = tor.getInt(10);
			// final String label = tor.getString(11);
			final boolean finished = (percent == 1000l);

			final Torrent torrent = new Torrent();
			torrent.setHash(hash);
			torrent.setPercentComplete((((float) percent) / 1000f) * 100);
			torrent.setSize(size);
			torrent.setTitle(name);
			torrent.setState(convertState(status, finished));

			torrents.add(torrent);
		}
		return torrents;
	}

	private TorrentState convertState(final int status, final boolean finished) {
		// Convert bitwise int to uTorrent status codes
		// Now based on http://forum.utorrent.com/viewtopic.php?id=50779
		if ((status & 1) == 1) {
			// Started
			if ((status & 32) == 32) {
				// Paused
				return TorrentState.PAUSED;
			} else if (finished) {
				return TorrentState.DONE;
			} else {
				return TorrentState.DOWNLOADING;
			}
		} else if ((status & 2) == 2) {
			// Checking
			return TorrentState.VALIDATING;
		} else if ((status & 16) == 16) {
			// Error
			return TorrentState.ERROR;
		} else if ((status & 128) == 128) {
			// Queued
			return TorrentState.QUEUED;
		} else {
			return TorrentState.WAITING;
		}
	}
}
