package de.tu.darmstadt.informatik.ausland.database.hibernate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
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

	/**
	 * single instance of hibernate factory
	 */
	private static HibernateFactory factory;

	/**
	 * @return instance of hibernate factory
	 */
	public static HibernateFactory getInstance() {
		if (factory == null) {
			factory = new HibernateFactory();
		}
		return factory;
	}

	/**
	 * reset the current factory
	 */
	public static void loadConfiguration(HibernateConfiguration configuration) {
		logger.debug("load new configuration '{}'", configuration.getPathToHibernateConfiguration());
		if (factory != null) {
			factory.close();
		}
		factory = new HibernateFactory(configuration);
	}

	/**
	 * hibernate session factory
	 */
	private SessionFactory sessionFactory;

	private HibernateFactory(HibernateConfiguration configuration) {
		try {
			logger.info("initialize hibernate session factory");
			ServiceRegistryBuilder registryBuilder = new ServiceRegistryBuilder();
			registryBuilder.applySettings(configuration.getHibernateConfiguration().getProperties());
			ServiceRegistry registry = registryBuilder.buildServiceRegistry();

			sessionFactory = configuration.getHibernateConfiguration().buildSessionFactory(registry);
			logger.info("hibernate session factory initialized successful");
		} catch (Exception e) {
			logger.error("hibernate session factory not initialized", e);
		}
	}

	/**
	 * Configure hibernate and create a hibernate session factory
	 */
	private HibernateFactory() {
		this(new HibernateConfiguration());
	}

	/**
	 * open a new hibernate session
	 * 
	 * @return hibernate session
	 */
	public synchronized Session createSession() {
		if (!sessionFactory.isClosed()) {
			return sessionFactory.openSession();
		}
		return null;
	}

	/**
	 * close active factory
	 */
	public synchronized void close() {
		if (sessionFactory != null && !sessionFactory.isClosed()) {
			sessionFactory.close();
		}
	}
}