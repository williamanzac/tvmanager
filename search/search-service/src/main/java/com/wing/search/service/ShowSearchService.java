package com.wing.search.service;

import java.util.List;

import com.wing.database.model.Episode;
import com.wing.database.model.Show;

public interface ShowSearchService {
	public List<Show> searchShow(final String show) throws Exception;

	public List<Episode> getEpisodeList(final int showId) throws Exception;
}
