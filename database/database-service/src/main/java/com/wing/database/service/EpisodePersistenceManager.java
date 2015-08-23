package com.wing.database.service;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.wing.database.model.Episode;
import com.wing.database.model.EpisodeState;

public class EpisodePersistenceManager extends DatabasePersistenceManager<Episode> {

	public List<Episode> list(final int showId) throws Exception {
		final PreparedStatement statement = con.prepareStatement("select * from episodes where showId = ?");
		statement.setInt(1, showId);
		final ResultSet resultSet = statement.executeQuery();
		final List<Episode> episodes = new ArrayList<>();
		while (resultSet.next()) {
			final Episode episode = new Episode();
			episode.setAirdate(resultSet.getDate(5));
			episode.setEpnum(resultSet.getInt(4));
			episode.setLink(resultSet.getString(6));
			episode.setNumber(resultSet.getInt(2));
			episode.setSeason(resultSet.getInt(3));
			episode.setShowId(resultSet.getInt(1));
			final String string = resultSet.getString(8);
			if (string != null) {
				episode.setState(EpisodeState.valueOf(string));
			}
			episode.setTitle(resultSet.getString(7));
			episode.setTorrentHash(resultSet.getString(9));

			episodes.add(episode);
		}
		return episodes;
	}

	@Override
	public Episode retrieve(final String key) throws Exception {
		final PreparedStatement statement = con
				.prepareStatement("select * from episodes where showId = ? and epnum = ?");
		statement.setInt(1, showId(key));
		statement.setInt(2, epnum(key));
		final ResultSet resultSet = statement.executeQuery();
		Episode episode = null;
		while (resultSet.next()) {
			episode = new Episode();
			episode.setAirdate(resultSet.getDate(5));
			episode.setEpnum(resultSet.getInt(4));
			episode.setLink(resultSet.getString(6));
			episode.setNumber(resultSet.getInt(2));
			episode.setSeason(resultSet.getInt(3));
			episode.setShowId(resultSet.getInt(1));
			final String string = resultSet.getString(8);
			if (string != null) {
				episode.setState(EpisodeState.valueOf(string));
			}
			episode.setTitle(resultSet.getString(7));
			episode.setTorrentHash(resultSet.getString(9));
		}
		return episode;
	}

	public String key(final Episode episode) {
		return episode.getShowId() + "-" + episode.getEpnum();
	}

	private int showId(final String key) {
		return Integer.parseInt(key.split("-")[0]);
	}

	private int epnum(final String key) {
		return Integer.parseInt(key.split("-")[1]);
	}

	@Override
	public void save(final Episode value) throws Exception {
		final Episode existing = retrieve(key(value));
		PreparedStatement statement;
		if (existing == null) {
			// add new row
			statement = con
					.prepareStatement("insert into episodes (showId,number,season,epnum,airdate,link,title,state,torrentHash) values (?,?,?,?,?,?,?,?,?)");
			if (value.getAirdate() != null) {
				statement.setDate(5, new Date(value.getAirdate().getTime()));
			} else {
				statement.setDate(5, null);
			}
			statement.setInt(4, value.getEpnum());
			statement.setString(6, value.getLink());
			statement.setInt(2, value.getNumber());
			statement.setInt(3, value.getSeason());
			statement.setInt(1, value.getShowId());
			if (value.getState() != null) {
				statement.setString(8, value.getState().name());
			} else {
				statement.setString(8, null);
			}
			statement.setString(7, value.getTitle());
			statement.setString(9, value.getTorrentHash());
		} else {
			// update row
			statement = con
					.prepareStatement("update episodes set (number,season,airdate,link,title,state,torrentHash) = (?,?,?,?,?,?,?) where showId = ? and epnum = ?");
			if (value.getAirdate() != null) {
				statement.setDate(3, new Date(value.getAirdate().getTime()));
			} else {
				statement.setDate(3, null);
			}
			statement.setInt(9, value.getEpnum());
			statement.setString(4, value.getLink());
			statement.setInt(1, value.getNumber());
			statement.setInt(2, value.getSeason());
			statement.setInt(8, value.getShowId());
			if (value.getState() != null) {
				statement.setString(6, value.getState().name());
			} else {
				statement.setString(6, null);
			}
			statement.setString(5, value.getTitle());
			statement.setString(7, value.getTorrentHash());
		}
		statement.executeUpdate();
	}

	@Override
	public void delete(final String key) throws Exception {
		final PreparedStatement statement = con.prepareStatement("delete from episodes where showId = ? and epnum = ?");
		statement.setInt(1, showId(key));
		statement.setInt(2, epnum(key));
		statement.executeUpdate();
	}

	@Override
	public List<Episode> list() throws Exception {
		final PreparedStatement statement = con.prepareStatement("select * from episodes");
		final ResultSet resultSet = statement.executeQuery();
		final List<Episode> episodes = new ArrayList<>();
		while (resultSet.next()) {
			final Episode episode = new Episode();
			episode.setAirdate(resultSet.getDate(5));
			episode.setEpnum(resultSet.getInt(4));
			episode.setLink(resultSet.getString(6));
			episode.setNumber(resultSet.getInt(2));
			episode.setSeason(resultSet.getInt(3));
			episode.setShowId(resultSet.getInt(1));
			final String string = resultSet.getString(8);
			if (string != null) {
				episode.setState(EpisodeState.valueOf(string));
			}
			episode.setTitle(resultSet.getString(7));
			episode.setTorrentHash(resultSet.getString(9));

			episodes.add(episode);
		}
		return episodes;
	}
}
