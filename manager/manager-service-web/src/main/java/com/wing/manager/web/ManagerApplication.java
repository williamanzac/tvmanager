package com.wing.manager.web;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;

import com.wing.manager.service.DefaultManagerService;

@ApplicationPath("/*")
public class ManagerApplication extends ResourceConfig {

	public ManagerApplication() {
		register(DefaultManagerService.class);
	}
}
