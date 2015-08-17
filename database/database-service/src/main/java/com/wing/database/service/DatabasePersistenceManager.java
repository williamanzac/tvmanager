package com.wing.database.service;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.hsqldb.server.Server;

import com.wing.database.api.PersistenceManager;

public abstract class DatabasePersistenceManager<T> implements PersistenceManager<T> {

	private static Server server;
	protected static Connection con;

	static {
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
			con.createStatement().executeUpdate(
					"create table contacts (name varchar(45),email varchar(45),phone varchar(45))");
		} catch (SQLException e) {
			e.printStackTrace(System.out);
		}
	}

	abstract boolean hasSchema();

	abstract boolean createSchema();
}
