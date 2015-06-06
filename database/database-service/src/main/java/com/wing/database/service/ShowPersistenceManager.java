package com.wing.database.service;

import com.wing.database.model.Show;

public class ShowPersistenceManager extends FilePersistenceManager<Show> {

	@Override
	protected Class<Show> forClass() {
		return Show.class;
	}
}
