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

import com.wing.database.model.Configuration;
import com.wing.database.model.Episode;
import com.wing.database.model.Show;
import com.wing.database.model.Torrent;

@Path("/manager")
public interface ManagerService {
	@GET
	@Path("/shows/{show:.*}")
	@Produces(MediaType.APPLICATION_XML)
	List<Show> searchShow(@PathParam("show") final String show) throws Exception;

	@GET
	@Path("/shows")
	@Produces(MediaType.APPLICATION_XML)
	List<Show> listShows() throws Exception;

	@PUT
	@Path("/shows")
	@Consumes(MediaType.APPLICATION_XML)
	void saveShow(final Show show) throws Exception;

	@POST
	@Path("/shows/{id:.*}")
	@Consumes(MediaType.APPLICATION_XML)
	void removeShow(final int showId) throws Exception;

	@PUT
	@Path("/shows/{id:.*}/episodes")
	@Consumes(MediaType.APPLICATION_XML)
	void updateEpisodes(final int showId) throws Exception;

	@GET
	@Path("/shows/{id:.*}/episodes")
	@Produces(MediaType.APPLICATION_XML)
	List<Episode> listEpisodes(final int showId) throws Exception;

	@PUT
	@Path("/episodes")
	@Consumes(MediaType.APPLICATION_XML)
	void saveEpisode(final Episode episode) throws Exception;

	@POST
	@Path("/shows/{showId:\\d+}/episodes/{season:\\d+}/{number:\\d+}")
	@Consumes(MediaType.APPLICATION_XML)
	void removeEpisode(final int showId, final int season, final int number) throws Exception;

	@GET
	@Path("/torrents")
	@Produces(MediaType.APPLICATION_XML)
	List<Torrent> listTorrents() throws Exception;

	@PUT
	@Path("/torrents")
	@Consumes(MediaType.APPLICATION_XML)
	void saveTorrent(final Torrent torrent) throws Exception;

	@DELETE
	@Path("/torrents")
	@Consumes(MediaType.APPLICATION_XML)
	void removeTorrent(Torrent torrent) throws Exception;

	@GET
	@Path("/shows/search")
	@Produces(MediaType.APPLICATION_XML)
	List<Torrent> searchForEpisode(@QueryParam("name") final String name, @QueryParam("season") final int season,
			@QueryParam("episode") final int episode) throws Exception;

	@GET
	@Path("/configuration")
	@Produces(MediaType.APPLICATION_XML)
	Configuration loadConfiguration() throws Exception;

	@PUT
	@Path("/configuration")
	@Consumes(MediaType.APPLICATION_XML)
	void saveConfiguration(Configuration configuration) throws Exception;

	@GET
	@Path("/torrents/{hash:.*}")
	@Produces(MediaType.APPLICATION_XML)
	Torrent getTorrent(@PathParam("hash") String hash) throws Exception;

	@GET
	@Path("/torrents/search")
	@Produces(MediaType.APPLICATION_XML)
	List<Torrent> searchFor(@QueryParam("query") final String query) throws Exception;
}
