package tk3.project.database.hibernate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

/**
 * singleton factory for hibernate configurations and sessions
 * 
 * @author Ingo Reimund
 * @create 05.11.2012
 * 
 */
public class HibernateFactory {

	/**
	 * logger
	 */
	private static Logger logger = LoggerFactory.getLogger(HibernateFactory.class);
	
	public static String HIBERNATE_CONFIG_PATH = "hibernate.cfg.xml";

	/**
	 * hibernate session factory
	 */
	private static SessionFactory sessionFactory;

	public static void loadConfiguration() {
		try {
			logger.info("initialize hibernate session factory");
			Configuration configuration = new Configuration().configure(HIBERNATE_CONFIG_PATH);
			ServiceRegistryBuilder registryBuilder = new ServiceRegistryBuilder();
			registryBuilder.applySettings(configuration.getProperties());
			ServiceRegistry registry = registryBuilder.buildServiceRegistry();

			sessionFactory = configuration.buildSessionFactory(registry);
			logger.info("hibernate session factory initialized successful");
		} catch (Exception e) {
			logger.error("hibernate session factory not initialized", e);
		}
	}

	/**
	 * create a new hiberante session
	 * 
	 * @return hibernate session
	 */
	public Session createSession() {
		return sessionFactory.openSession();
	}
}