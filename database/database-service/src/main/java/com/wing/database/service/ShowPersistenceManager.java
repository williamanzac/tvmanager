package com.wing.database.service;

import com.wing.database.model.Show;

public class ShowPersistenceManager extends FilePersistenceManager<Show> {

	@SuppressWarnings("unchecked")
	@Override
	protected Class<Show>[] forClasses() {
		return new Class[] { Show.class };
	}
}
