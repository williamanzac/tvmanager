package com.wing.manager.service;

import java.util.List;

import com.wing.database.model.Show;
import com.wing.database.model.Torrent;

public interface ManagerService {
	public List<Show> searchShow(final String show) throws Exception;

	public List<Show> listShows() throws Exception;

	public void saveShow(Show show) throws Exception;

	public void removeShow(Show show) throws Exception;

	public void updateEpisodes(Show show) throws Exception;

	public List<Torrent> listTorrents() throws Exception;

	public void saveTorrent(Torrent torrent) throws Exception;

	public List<Torrent> searchForEpisode(final String name, final int season, final int episode) throws Exception;
}
