package com.wing.manager.service;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import com.wing.configuration.service.ConfigurationService;
import com.wing.database.api.PersistenceManager;
import com.wing.database.model.Configuration;
import com.wing.database.model.Episode;
import com.wing.database.model.Show;
import com.wing.database.model.Torrent;
import com.wing.search.service.ShowSearchService;
import com.wing.torrent.searcher.TorrentSearchService;

@Path("/manager")
public class DefaultManagerService implements ManagerService {

	private final ShowSearchService searchService;
	private final PersistenceManager<Show> showManager;
	private final PersistenceManager<Torrent> torrentPersistenceManager;
	private final TorrentSearchService torrentSearchService;
	private final ConfigurationService configurationService;

	public DefaultManagerService(final ShowSearchService searchService, final PersistenceManager<Show> showManager,
			final PersistenceManager<Torrent> torrentPersistenceManager,
			final TorrentSearchService torrentSearchService, final ConfigurationService configurationService) {
		super();
		this.searchService = searchService;
		this.showManager = showManager;
		this.torrentPersistenceManager = torrentPersistenceManager;
		this.torrentSearchService = torrentSearchService;
		this.configurationService = configurationService;
	}

	@GET
	@Path("/shows/{show:.*}")
	@Override
	public List<Show> searchShow(@PathParam("show") final String show) throws Exception {
		return searchService.searchShow(show);
	}

	@GET
	@Path("/shows")
	@Override
	public List<Show> listShows() throws Exception {
		return showManager.list();
	}

	@PUT
	@Path("/shows")
	@Override
	public void saveShow(final Show show) throws Exception {
		showManager.save(Integer.toString(show.getId()), show);
	}

	@POST
	@Path("/shows")
	@Override
	public void removeShow(final Show show) throws Exception {
		showManager.delete(Integer.toString(show.getId()));
	}

	@PUT
	@Path("/shows/episodes")
	@Override
	public void updateEpisodes(final Show show) throws Exception {
		final SortedSet<Episode> episodeList = new TreeSet<>();
		if (show.getEpisodeList() != null) {
			episodeList.addAll(show.getEpisodeList());
		}
		final List<Episode> newList = searchService.getEpisodeList(show.getId());
		episodeList.addAll(newList);
		show.setEpisodeList(episodeList);
	}

	@GET
	@Path("/torrents")
	@Override
	public List<Torrent> listTorrents() throws Exception {
		return torrentPersistenceManager.list();
	}

	@PUT
	@Path("/torrents")
	@Override
	public void saveTorrent(final Torrent torrent) throws Exception {
		torrentPersistenceManager.save(torrent.getHash(), torrent);
	}

	@DELETE
	@Path("/torrents")
	@Override
	public void removeTorrent(Torrent torrent) throws Exception {
		torrentPersistenceManager.delete(torrent.getHash());
	}

	@GET
	@Path("/shows/search")
	@Override
	public List<Torrent> searchForEpisode(@QueryParam("name") final String name,
			@QueryParam("season") final int season, @QueryParam("episode") final int episode) throws Exception {
		return torrentSearchService.searchTorrent(name, season, episode);
	}

	@GET
	@Path("/configuration")
	@Override
	public Configuration loadConfiguration() throws Exception {
		return configurationService.loadConfiguration();
	}

	@PUT
	@Path("/configuration")
	@Override
	public void saveConfiguration(Configuration configuration) throws Exception {
		configurationService.saveConfiguration(configuration);
	}

	@GET
	@Path("/torrents/{hash:.*}")
	@Override
	public Torrent getTorrent(@PathParam("hash") String hash) throws Exception {
		return torrentPersistenceManager.retrieve(hash);
	}

	@GET
	@Path("/torrents/search")
	@Override
	public List<Torrent> searchFor(@QueryParam("query") final String query) throws Exception {
		return torrentSearchService.searchTorrent(query);
	}
}
