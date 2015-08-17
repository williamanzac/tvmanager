package com.wing.database.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.wing.database.model.Episode;

public class EpisodePersistenceManager extends FilePersistenceManager<Episode> {

	@SuppressWarnings("unchecked")
	@Override
	protected Class<Episode>[] forClasses() {
		return new Class[] { Episode.class };
	}

	public List<Episode> list(final int showId) throws Exception {
		final File baseDir = baseDir();
		final String[] files = baseDir.list((dir, name) -> name.startsWith(Integer.toString(showId))
				&& name.endsWith(".xml"));
		final List<Episode> list = new ArrayList<>();
		if (files != null && files.length > 0) {
			for (final String name : files) {
				list.add(retrieve(name.substring(0, name.indexOf("."))));
			}
		}
		return list;
	}
}
