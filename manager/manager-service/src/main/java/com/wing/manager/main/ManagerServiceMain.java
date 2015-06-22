package com.wing.manager.main;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.PatternLayout;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.wing.manager.service.DefaultManagerService;

public class ManagerServiceMain {

	public static void main(final String[] args) throws Exception {
		BasicConfigurator.configure(new ConsoleAppender(new PatternLayout("%d [%-25t] %-5p: %m%n")));
		final ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");

		final Server jettyServer = new Server(8080);
		jettyServer.setHandler(context);

		final ServletHolder jerseyServlet = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class,
				"/*");
		jerseyServlet.setInitOrder(0);

		// Tells the Jersey Servlet which REST service/class to load.
		jerseyServlet.setInitParameter("jersey.config.server.provider.classnames",
				DefaultManagerService.class.getCanonicalName());

		try {
			jettyServer.start();
			jettyServer.join();
		} finally {
			// jettyServer.destroy();
			jettyServer.stop();
		}
	}
}
