package com.wing.database.service;

import com.wing.database.model.Torrent;

public class TorrentPersistenceManager extends FilePersistenceManager<Torrent> {

	@Override
	protected Class<Torrent> forClass() {
		return Torrent.class;
	}

}
