package com.wing.database.service;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.hsqldb.server.Server;

public class DatabaseService {

	private Server server;
	protected Connection con;

	private void updateSchema() throws SQLException {
		con.createStatement()
				.executeUpdate(
						"create table if not exists torrents (hash varchar(48) primary key,title varchar(256),seeds integer,leechers integer,size bigint,url varchar(256),pubDate timestamp,categories varchar(256),state integer,percentComplete float)");
		con.createStatement()
				.executeUpdate(
						"create table if not exists shows (id integer primary key,name varchar(256),link varchar(256),country varchar(256),started varchar(256),ended varchar(256),seasons integer,status varchar(256),classification varchar(256),genres varchar(256))");
		con.createStatement()
				.executeUpdate(
						"create table if not exists episodes (showId integer,number integer,season integer,epnum integer,airdate timestamp,link varchar(256),title varchar(256),state varchar(256),torrentHash varchar(256))");
		con.createStatement()
				.executeUpdate(
						"create table if not exists filetasks (id varchar(256) primary key,source varchar(256),target varchar(256),totalBytes bigint,copiedBytes bigint,progress integer,type varchar(20))");
	}

	public void init() throws Exception {
		server = new Server();
		server.setAddress("localhost");
		server.setDatabaseName(0, "tvdb");
		server.setDatabasePath(0, "file:./db");
		server.setPort(1234);
		server.setTrace(true);
		server.setLogWriter(new PrintWriter(System.out));
		server.start();

		try {
			Class.forName("org.hsqldb.jdbc.JDBCDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace(System.out);
		}

		try {
			con = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost:1234/tvdb", "SA", "");
			updateSchema();
		} catch (SQLException e) {
			e.printStackTrace(System.out);
		}
	}
}
