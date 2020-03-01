package org.barcodeapi.core;

import org.barcodeapi.core.utils.Log;
import org.barcodeapi.core.utils.Log.LOG;
import org.barcodeapi.server.api.BarcodeAPIHandler;
import org.barcodeapi.server.api.CacheHandler;
import org.barcodeapi.server.api.DefaultHandler;
import org.barcodeapi.server.api.SessionHandler;
import org.barcodeapi.server.api.StaticHandler;
import org.barcodeapi.server.api.StatsHandler;
import org.barcodeapi.server.api.TypesHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;

/**
 * This class should handle the processing of the command line arguments passed
 * on startup in addition to the setup of the main Jetty API server and it's
 * associated handlers.
 * 
 * @author Matthew R. Clark, 2019
 *
 */
public class ServerLoader {

	// The port for the API server to bind to.
	private int serverPort = 8080;

	// Whether static resources should be served.
	private boolean serverStatic = true;

	// The instance of the running Jetty server and it's handlers.
	private Server server;
	private HandlerCollection handlers;

	/**
	 * Initialize the server loader by processing the command line arguments
	 * supplied by the user.
	 * 
	 * @param args
	 */
	public ServerLoader(String[] args) {

		parseLaunchArgs(args);
	}

	/**
	 * Main entry point to start the server; jetty will be initialized followed by
	 * each handler. Once initialized the server will be started and ready to server
	 * requests.
	 * 
	 * @throws Exception
	 */
	public void launch() throws Exception {

		initJetty();

		initStatsHandler();

		initSessionHandler();

		initCacheHandler();

		initTypesHandler();

		initApiHandler();

		initResourceHandler();

		startServer();
	}

	/**
	 * Loop and parse each provided argument.
	 * 
	 * @param args
	 */
	private void parseLaunchArgs(String[] args) {

		// Do nothing if null arguments
		if (args == null) {

			return;
		}

		// Loop all arguments
		for (int x = 0; x < args.length; x++) {

			switch (args[x]) {

			case "--port":
				serverPort = Integer.parseInt(args[++x]);
				break;

			case "--no-web":
				serverStatic = false;
				break;

			default:
				System.err.println("Unknown argument [ " + args[x] + " ]");
				System.exit(1);
				break;
			}
		}
	}

	/**
	 * Initialize the Jetty server.
	 */
	private void initJetty() {

		Log.out(LOG.SERVER, "Initializing Jetty...");

		// initialize handler collection
		handlers = new HandlerCollection();

		// initialize API server
		server = new Server(serverPort);
		server.setHandler(handlers);

		server.setErrorHandler(new DefaultHandler());
	}

	/**
	 * Initialize the statistics end-point.
	 */
	private void initStatsHandler() {

		// setup statistics handler
		Log.out(LOG.SERVER, "Initializing handler: /stats");
		ContextHandler statsHandler = new ContextHandler();
		statsHandler.setHandler(new StatsHandler());
		statsHandler.setContextPath("/stats");
		handlers.addHandler(statsHandler);
	}

	/**
	 * Initialize the session end-point.
	 */
	private void initSessionHandler() {

		// setup statistics handler
		Log.out(LOG.SERVER, "Initializing handler: /session");
		ContextHandler sessionHandler = new ContextHandler();
		sessionHandler.setHandler(new SessionHandler());
		sessionHandler.setContextPath("/session");
		handlers.addHandler(sessionHandler);
	}

	/**
	 * Initialize the cache end-point.
	 */
	private void initCacheHandler() {

		// setup statistics handler
		Log.out(LOG.SERVER, "Initializing handler: /cache");
		ContextHandler cacheHandler = new ContextHandler();
		cacheHandler.setHandler(new CacheHandler());
		cacheHandler.setContextPath("/cache");
		handlers.addHandler(cacheHandler);
	}

	/**
	 * Initialize the types end-point.
	 */
	private void initTypesHandler() {

		// setup statistics handler
		Log.out(LOG.SERVER, "Initializing handler: /types");
		ContextHandler typesHandler = new ContextHandler();
		typesHandler.setHandler(new TypesHandler());
		typesHandler.setContextPath("/types");
		handlers.addHandler(typesHandler);
	}

	/**
	 * Initialize the main API handler.
	 */
	private void initApiHandler() {

		// setup API handler
		Log.out(LOG.SERVER, "Initializing handler: /api");
		ContextHandler apiHandler = new ContextHandler();
		apiHandler.setHandler(new BarcodeAPIHandler());
		apiHandler.setContextPath("/api");
		handlers.addHandler(apiHandler);
	}

	/**
	 * Initialize the resource handler.
	 */
	private void initResourceHandler() throws Exception {

		if (!serverStatic) {
			return;
		}

		// Instantiate the static resource handler and add it to the collection
		Log.out(LOG.SERVER, "Initializing static resource handler...");
		ContextHandler apiHandler = new ContextHandler();
		apiHandler.setHandler(new StaticHandler());
		apiHandler.setContextPath("/");
		handlers.addHandler(apiHandler);
	}

	/**
	 * Start the Jetty server.
	 * 
	 * @throws Exception
	 */
	private boolean startServer() {

		try {

			// start server
			server.start();
			return true;
		} catch (Exception e) {

			System.err.println("Failed to start server.");
			e.printStackTrace(System.err);
			return false;
		}
	}

	/**
	 * Stop the Jetty server.
	 * 
	 * @throws Exception
	 */
	public boolean stop() {

		try {

			// stop server
			server.stop();
			return true;
		} catch (Exception e) {

			System.err.println("Failed to stop server.");
			e.printStackTrace(System.err);
			return false;
		}
	}
}
