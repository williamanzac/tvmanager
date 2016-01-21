package com.wing.database.service;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jersey.repackaged.com.google.common.base.Joiner;

import com.wing.database.model.Torrent;
import com.wing.database.model.TorrentState;

public class TorrentPersistenceManager extends DatabasePersistenceManager<Torrent> {

	@Override
	public Torrent retrieve(final String hash) throws Exception {
		final PreparedStatement statement = con.prepareStatement("select * from torrents where hash=?");
		statement.setString(1, hash.toUpperCase());
		final ResultSet resultSet = statement.executeQuery();
		Torrent torrent = null;
		while (resultSet.next()) {
			torrent = new Torrent();
			final String[] strings = resultSet.getString(8).split(",");
			final Set<String> categories = new HashSet<>(Arrays.asList(strings));
			torrent.setCategories(categories);
			torrent.setHash(resultSet.getString(1));
			torrent.setLeechers(resultSet.getInt(4));
			torrent.setPercentComplete(resultSet.getFloat(10));
			torrent.setPubDate(resultSet.getDate(7));
			torrent.setSeeds(resultSet.getInt(3));
			torrent.setSize(resultSet.getLong(5));
			final int state = resultSet.getInt(9);
			if (state >= 0) {
				torrent.setState(TorrentState.values()[state]);
			}
			torrent.setTitle(resultSet.getString(2));
			final String url = resultSet.getString(6);
			if (url != null) {
				torrent.setUrl(url);
			}
		}
		return torrent;
	}

	@Override
	public void save(final Torrent value) throws Exception {
		final Torrent existing = retrieve(value.getHash());
		PreparedStatement statement;
		if (existing == null) {
			// add new row
			statement = con
					.prepareStatement("insert into torrents (hash,title,seeds,leechers,size,url,pubDate,categories,state,percentComplete) values (?,?,?,?,?,?,?,?,?,?)");
			statement.setString(8, Joiner.on(",").join(value.getCategories()));
			statement.setString(1, value.getHash().toUpperCase());
			statement.setInt(4, value.getLeechers());
			statement.setFloat(10, value.getPercentComplete());
			if (value.getPubDate() != null) {
				statement.setDate(7, new Date(value.getPubDate().getTime()));
			} else {
				statement.setDate(7, null);
			}
			statement.setInt(3, value.getSeeds());
			statement.setLong(5, value.getSize());
			if (value.getState() != null) {
				statement.setInt(9, value.getState().ordinal());
			} else {
				statement.setInt(9, -1);
			}
			statement.setString(2, value.getTitle());
			if (value.getUrl() != null) {
				statement.setString(6, value.getUrl());
			} else {
				statement.setString(6, null);
			}
		} else {
			// update row
			statement = con
					.prepareStatement("update torrents set (title,seeds,leechers,size,url,pubDate,categories,state,percentComplete) = (?,?,?,?,?,?,?,?,?) where hash = ?");
			statement.setString(7, Joiner.on(",").join(value.getCategories()));
			statement.setString(10, value.getHash().toUpperCase());
			statement.setInt(3, value.getLeechers());
			statement.setFloat(9, value.getPercentComplete());
			if (value.getPubDate() != null) {
				statement.setDate(6, new Date(value.getPubDate().getTime()));
			} else {
				statement.setDate(6, null);
			}
			statement.setInt(2, value.getSeeds());
			statement.setLong(4, value.getSize());
			if (value.getState() != null) {
				statement.setInt(8, value.getState().ordinal());
			} else {
				statement.setInt(8, -1);
			}
			statement.setString(1, value.getTitle());
			if (value.getUrl() != null) {
				statement.setString(5, value.getUrl());
			} else {
				statement.setString(5, null);
			}
		}
		statement.executeUpdate();
	}

	@Override
	public void delete(final String hash) throws Exception {
		final PreparedStatement statement = con.prepareStatement("delete from torrents where hash=?");
		statement.setString(1, hash.toUpperCase());
		statement.executeUpdate();
	}

	@Override
	public List<Torrent> list() throws Exception {
		final PreparedStatement statement = con.prepareStatement("select * from torrents");
		final ResultSet resultSet = statement.executeQuery();
		final List<Torrent> torrents = new ArrayList<>();
		while (resultSet.next()) {
			final Torrent torrent = new Torrent();
			final String[] strings = resultSet.getString(8).split(",");
			final Set<String> categories = new HashSet<>(Arrays.asList(strings));
			torrent.setCategories(categories);
			torrent.setHash(resultSet.getString(1).toUpperCase());
			torrent.setLeechers(resultSet.getInt(4));
			torrent.setPercentComplete(resultSet.getFloat(10));
			torrent.setPubDate(resultSet.getDate(7));
			torrent.setSeeds(resultSet.getInt(3));
			torrent.setSize(resultSet.getLong(5));
			final int state = resultSet.getInt(9);
			if (state >= 0) {
				torrent.setState(TorrentState.values()[state]);
			}
			torrent.setTitle(resultSet.getString(2));
			final String url = resultSet.getString(6);
			if (url != null) {
				torrent.setUrl(url);
			}

			torrents.add(torrent);
		}
		return torrents;
	}
}
