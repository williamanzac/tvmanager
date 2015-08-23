package com.wing.database.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.wing.database.api.PersistenceManager;

public abstract class DatabasePersistenceManager<T> implements PersistenceManager<T> {

	protected static Connection con;

	static {
		try {
			Class.forName("org.hsqldb.jdbc.JDBCDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace(System.out);
		}

		try {
			con = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost:1234/tvdb", "SA", "");
		} catch (SQLException e) {
			e.printStackTrace(System.out);
		}
	}
}
