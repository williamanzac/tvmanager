package com.wing.manager.web;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.PatternLayout;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

public class ManagerServiceMain {

	public static void main(final String[] args) throws Exception {
		BasicConfigurator.configure(new ConsoleAppender(new PatternLayout("%d [%-25t] %-5p: %m%n")));
		final Server server = new Server(8080);

		ServletContextHandler context = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);
		context.addServlet(new ServletHolder(new ServletContainer(new ManagerApplication())), "/*");

		try {
			server.start();
			server.join();
		} finally {
			// jettyServer.destroy();
			server.stop();
		}
	}
}
