package com.wing.database.service;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class DatabaseService {

	public static void main(final String[] args) throws Exception {
		final ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");

		final Server jettyServer = new Server(8080);
		jettyServer.setHandler(context);

		final ServletHolder jerseyServlet = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class,
				"/*");
		jerseyServlet.setInitOrder(0);

		// Tells the Jersey Servlet which REST service/class to load.
		jerseyServlet.setInitParameter("jersey.config.server.provider.classnames",
				FilePersistenceManager.class.getCanonicalName());

		try {
			jettyServer.start();
			jettyServer.join();
		} finally {
			// jettyServer.destroy();
			jettyServer.stop();
		}
	}

}
