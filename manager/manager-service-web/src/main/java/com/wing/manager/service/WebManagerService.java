package com.wing.manager.service;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.wing.configuration.service.ConfigurationService;
import com.wing.database.model.Configuration;
import com.wing.database.model.Show;
import com.wing.database.model.Torrent;
import com.wing.database.service.ShowPersistenceManager;
import com.wing.database.service.TorrentPersistenceManager;
import com.wing.provider.torrentproject.torrent.searcher.TorrentProjectTorrentSearchService;
import com.wing.provider.tvrage.searcher.TvRageShowSearchService;
import com.wing.torrent.searcher.TorrentSearchService;

@Path("/manager")
public class WebManagerService extends DefaultManagerService {

	static final TvRageShowSearchService searchService = new TvRageShowSearchService();
	static final ShowPersistenceManager showManager = new ShowPersistenceManager();
	static final TorrentPersistenceManager torrentPersistenceManager = new TorrentPersistenceManager();
	static final TorrentSearchService torrentSearchService = new TorrentProjectTorrentSearchService();
	static final ConfigurationService configurationService = new ConfigurationService();

	public WebManagerService() {
		super(searchService, showManager, torrentPersistenceManager, torrentSearchService, configurationService);
	}

	@GET
	@Path("/shows/{show:.*}")
	@Produces(MediaType.APPLICATION_XML)
	@Override
	public List<Show> searchShow(@PathParam("show") final String show) throws Exception {
		return super.searchShow(show);
	}

	@GET
	@Path("/shows")
	@Produces(MediaType.APPLICATION_XML)
	@Override
	public List<Show> listShows() throws Exception {
		return super.listShows();
	}

	@PUT
	@Path("/shows")
	@Consumes(MediaType.APPLICATION_XML)
	@Override
	public void saveShow(final Show show) throws Exception {
		super.saveShow(show);
	}

	@POST
	@Path("/shows")
	@Consumes(MediaType.APPLICATION_XML)
	@Override
	public void removeShow(final Show show) throws Exception {
		super.removeShow(show);
	}

	@PUT
	@Path("/shows/episodes")
	@Consumes(MediaType.APPLICATION_XML)
	@Override
	public void updateEpisodes(final Show show) throws Exception {
		super.updateEpisodes(show);
	}

	@GET
	@Path("/torrents")
	@Produces(MediaType.APPLICATION_XML)
	@Override
	public List<Torrent> listTorrents() throws Exception {
		return super.listTorrents();
	}

	@PUT
	@Path("/torrents")
	@Consumes(MediaType.APPLICATION_XML)
	@Override
	public void saveTorrent(final Torrent torrent) throws Exception {
		super.saveTorrent(torrent);
	}

	@DELETE
	@Path("/torrents")
	@Consumes(MediaType.APPLICATION_XML)
	@Override
	public void removeTorrent(Torrent torrent) throws Exception {
		super.removeTorrent(torrent);
	}

	@GET
	@Path("/shows/search")
	@Produces(MediaType.APPLICATION_XML)
	@Override
	public List<Torrent> searchForEpisode(@QueryParam("name") final String name,
			@QueryParam("season") final int season, @QueryParam("episode") final int episode) throws Exception {
		return super.searchForEpisode(name, season, episode);
	}

	@GET
	@Path("/configuration")
	@Produces(MediaType.APPLICATION_XML)
	@Override
	public Configuration loadConfiguration() throws Exception {
		return super.loadConfiguration();
	}

	@PUT
	@Path("/configuration")
	@Consumes(MediaType.APPLICATION_XML)
	@Override
	public void saveConfiguration(Configuration configuration) throws Exception {
		super.saveConfiguration(configuration);
	}

	@GET
	@Path("/torrents/{hash:.*}")
	@Produces(MediaType.APPLICATION_XML)
	@Override
	public Torrent getTorrent(@PathParam("hash") String hash) throws Exception {
		return super.getTorrent(hash);
	}

	@GET
	@Path("/torrents/search")
	@Produces(MediaType.APPLICATION_XML)
	@Override
	public List<Torrent> searchFor(@QueryParam("query") final String query) throws Exception {
		return super.searchFor(query);
	}
}
