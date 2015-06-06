package com.wing.manager.service;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.wing.database.api.PersistenceManager;
import com.wing.database.model.Episode;
import com.wing.database.model.Show;
import com.wing.search.service.ShowSearchService;

public class DefaultManagerService implements ManagerService {

	private final ShowSearchService searchService;
	private final PersistenceManager<Show> showManager;

	public DefaultManagerService(final ShowSearchService searchService,
			final PersistenceManager<Show> showManager) {
		super();
		this.searchService = searchService;
		this.showManager = showManager;
	}

	@Override
	public List<Show> searchShow(final String show) throws Exception {
		return searchService.searchShow(show);
	}

	@Override
	public List<Show> listShows() throws Exception {
		return showManager.list();
	}

	@Override
	public void saveShow(final Show show) throws Exception {
		showManager.save(Integer.toString(show.getId()), show);
	}

	@Override
	public void removeShow(final Show show) throws Exception {
		showManager.delete(Integer.toString(show.getId()));
	}

	@Override
	public void updateEpisodes(final Show show) throws Exception {
		final SortedSet<Episode> episodeList=new TreeSet<>(show.getEpisodeList());
		final List<Episode> newList = searchService.getEpisodeList(show
				.getId());
		episodeList.addAll(newList);
		show.setEpisodeList(episodeList);
	}
}
