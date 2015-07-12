package com.wing.manager.web;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;

import com.wing.manager.service.WebManagerService;

@ApplicationPath("/*")
public class ManagerApplication extends ResourceConfig {

	public ManagerApplication() {
		// packages("com.wing.manager.service", "com.wing.manager.web.provider");
		register(WebManagerService.class);
		// packages(WebManagerService.class.getPackage().getName());
	}
}
