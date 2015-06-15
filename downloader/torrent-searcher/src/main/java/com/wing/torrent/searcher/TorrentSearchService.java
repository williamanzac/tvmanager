package com.wing.torrent.searcher;

import java.util.List;

import com.wing.database.model.Torrent;

public interface TorrentSearchService {
	public List<Torrent> searchTorrent(String showName, int season, int episode) throws Exception;
}
