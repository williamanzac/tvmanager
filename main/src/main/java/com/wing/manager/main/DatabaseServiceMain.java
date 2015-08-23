package com.wing.manager.main;

import com.wing.database.service.DatabaseService;

public class DatabaseServiceMain {

	public static void main(String[] args) throws Exception {
		DatabaseService service = new DatabaseService();
		service.init();
	}
}
