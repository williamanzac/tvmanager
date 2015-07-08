package com.wing.manager.web;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.PatternLayout;
import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import com.wing.manager.service.WebManagerService;

public class ManagerServiceMain {

	public static void main(final String[] args) throws Exception {
		BasicConfigurator.configure(new ConsoleAppender(new PatternLayout("%d [%-25t] %-5p: %m%n")));
		// final ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		// context.setContextPath("/");
		//
		// final Server jettyServer = new Server(8080);
		// jettyServer.setHandler(context);
		//
		// final ServletHolder jerseyServlet = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class,
		// "/*");
		// jerseyServlet.setInitOrder(0);
		//
		// // Tells the Jersey Servlet which REST service/class to load.
		// jerseyServlet.setInitParameter("jersey.config.server.provider.classnames",
		// WebManagerService.class.getCanonicalName());

		final URI baseUri = UriBuilder.fromUri("http://localhost/").port(8080).build();
		final ResourceConfig config = new ResourceConfig(WebManagerService.class);
		final Server server = JettyHttpContainerFactory.createServer(baseUri, config);

		try {
			server.start();
			server.join();
		} finally {
			// jettyServer.destroy();
			server.stop();
		}
	}
}
