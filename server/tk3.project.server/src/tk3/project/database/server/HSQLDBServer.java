package tk3.project.database.server;

import org.hsqldb.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * manage hsqldb server
 * 
 * @author Ingo Reimund
 * @create 13.11.2012
 * 
 */
public class HSQLDBServer {

	/**
	 * logger
	 */
	private static Logger logger = LoggerFactory.getLogger(HSQLDBServer.class);
	
	/**
	 * path to database
	 */
	public static String DATABASE_PATH = "jdbc:hsqldb:hsql://localhost/database";
	
	/**
	 * name of the database
	 */
	public static String DATABASE_NAME = "database";

	/**
	 * hsqldb server
	 */
	private Server server;

	/**
	 * set name of the database and path to the database files
	 * 
	 * @param databaseName
	 * @param databasePath
	 */
	public HSQLDBServer() {
		logger.info("initialize hsqldbserver with name '{}' and path '{}'", DATABASE_NAME,
				DATABASE_PATH);
		server = new Server();
		server.setDatabasePath(0, DATABASE_PATH);
		server.setDatabaseName(0, DATABASE_NAME);
	}

	/**
	 * stop hsqldb server
	 */
	public void start() {
		logger.info("start hsqldb-Server");
		server.start();
	}

	/**
	 * start hsqldb server
	 */
	public void stop() {
		logger.info("stop hsqldb-Server");
		server.stop();
	}
}