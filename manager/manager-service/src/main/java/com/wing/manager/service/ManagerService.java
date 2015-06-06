package com.wing.manager.service;

import java.util.List;

import com.wing.database.model.Show;

public interface ManagerService {
	public List<Show> searchShow(final String show) throws Exception;

	public List<Show> listShows() throws Exception;

	public void saveShow(Show show) throws Exception;

	public void removeShow(Show show) throws Exception;

	public void updateEpisodes(Show show) throws Exception;
}
