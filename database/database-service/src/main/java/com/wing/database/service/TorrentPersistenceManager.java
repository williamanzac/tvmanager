package com.wing.database.service;

import com.wing.database.model.Torrent;

public class TorrentPersistenceManager extends FilePersistenceManager<Torrent> {

	@SuppressWarnings("unchecked")
	@Override
	protected Class<Torrent>[] forClasses() {
		return new Class[] { Torrent.class };
	}
}
