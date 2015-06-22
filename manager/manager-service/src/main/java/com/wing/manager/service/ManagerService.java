package com.wing.manager.service;

import java.util.List;

import com.wing.database.model.Configuration;
import com.wing.database.model.Show;
import com.wing.database.model.Torrent;

public interface ManagerService {
	List<Show> searchShow(String show) throws Exception;

	List<Show> listShows() throws Exception;

	void saveShow(Show show) throws Exception;

	void removeShow(Show show) throws Exception;

	void updateEpisodes(Show show) throws Exception;

	List<Torrent> listTorrents() throws Exception;

	void saveTorrent(Torrent torrent) throws Exception;

	void removeTorrent(Torrent torrent) throws Exception;

	List<Torrent> searchForEpisode(String name, int season, int episode) throws Exception;

	Configuration loadConfiguration() throws Exception;

	void saveConfiguration(Configuration configuration) throws Exception;
}
