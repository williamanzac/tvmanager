package com.wing.database.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jersey.repackaged.com.google.common.base.Joiner;

import com.wing.database.model.Show;

public class ShowPersistenceManager extends DatabasePersistenceManager<Show> {

	@Override
	public Show retrieve(final String id) throws Exception {
		final PreparedStatement statement = con.prepareStatement("select * from shows where id=?");
		statement.setInt(1, Integer.parseInt(id));
		final ResultSet resultSet = statement.executeQuery();
		Show show = null;
		while (resultSet.next()) {
			show = new Show();
			show.setClassification(resultSet.getString(9));
			show.setCountry(resultSet.getString(4));
			show.setEnded(resultSet.getString(6));
			final String[] strings = resultSet.getString(10).split(",");
			final List<String> genres = new ArrayList<>(Arrays.asList(strings));
			show.setGenres(genres);
			show.setId(resultSet.getInt(1));
			show.setLink(resultSet.getString(3));
			show.setName(resultSet.getString(2));
			show.setSeasons(resultSet.getInt(7));
			show.setStarted(resultSet.getString(5));
			show.setStatus(resultSet.getString(8));
		}
		return show;
	}

	@Override
	public void save(final Show value) throws Exception {
		final Show existing = retrieve(Integer.toString(value.getId()));
		PreparedStatement statement;
		if (existing == null) {
			// add new row
			statement = con
					.prepareStatement("insert into shows (id,name,link,country,started,ended,seasons,status,classification,genres) values (?,?,?,?,?,?,?,?,?,?)");
			statement.setString(9, value.getClassification());
			statement.setString(4, value.getCountry());
			statement.setString(6, value.getEnded());
			statement.setString(10, Joiner.on(",").join(value.getGenres()));
			statement.setInt(1, value.getId());
			statement.setString(3, value.getLink());
			statement.setString(2, value.getName());
			statement.setInt(7, value.getSeasons());
			statement.setString(5, value.getStarted());
			statement.setString(8, value.getStatus());
		} else {
			// update row
			statement = con
					.prepareStatement("update shows set (name,link,country,started,ended,seasons,status,classification,genres) = (?,?,?,?,?,?,?,?,?) where id = ?");
			statement.setString(8, value.getClassification());
			statement.setString(3, value.getCountry());
			statement.setString(5, value.getEnded());
			statement.setString(9, Joiner.on(",").join(value.getGenres()));
			statement.setInt(10, value.getId());
			statement.setString(2, value.getLink());
			statement.setString(1, value.getName());
			statement.setInt(6, value.getSeasons());
			statement.setString(4, value.getStarted());
			statement.setString(7, value.getStatus());
		}
		statement.executeUpdate();
	}

	@Override
	public void delete(final String id) throws Exception {
		final PreparedStatement statement = con.prepareStatement("delete from shows where id=?");
		statement.setInt(1, Integer.parseInt(id));
		statement.executeUpdate();
	}

	@Override
	public List<Show> list() throws Exception {
		final PreparedStatement statement = con.prepareStatement("select * from shows");
		final ResultSet resultSet = statement.executeQuery();
		final List<Show> shows = new ArrayList<>();
		while (resultSet.next()) {
			final Show show = new Show();
			show.setClassification(resultSet.getString(9));
			show.setCountry(resultSet.getString(4));
			show.setEnded(resultSet.getString(6));
			final String[] strings = resultSet.getString(10).split(",");
			final List<String> genres = new ArrayList<>(Arrays.asList(strings));
			show.setGenres(genres);
			show.setId(resultSet.getInt(1));
			show.setLink(resultSet.getString(3));
			show.setName(resultSet.getString(2));
			show.setSeasons(resultSet.getInt(7));
			show.setStarted(resultSet.getString(5));
			show.setStatus(resultSet.getString(8));

			shows.add(show);
		}
		return shows;
	}
}
