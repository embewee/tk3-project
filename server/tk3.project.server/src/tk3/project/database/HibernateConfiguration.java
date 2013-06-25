package de.tu.darmstadt.informatik.ausland.database.hibernate;

import java.util.Properties;

import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tu.darmstadt.informatik.ausland.database.DBConfiguration;
import de.tu.darmstadt.informatik.ausland.util.properties.HibernateProperties;
import de.tu.darmstadt.informatik.ausland.util.register.Register;

/**
 * configuration for hibernate
 * 
 * @author IngoReimund
 * 
 */
public class HibernateConfiguration extends DBConfiguration {

	/**
	 * logger
	 */
	private static Logger logger = LoggerFactory.getLogger(HibernateConfiguration.class);

	/**
	 * path to hibernate config
	 */
	private String pathToHibernateConfig;

	/**
	 * hibernate configuration
	 */
	private Configuration configuration;

	public HibernateConfiguration() {
		this(Register.getInstance().get(Properties.class).getProperty(HibernateProperties.DB_HIBERNATE_CONFIG.name()));
	}

	public HibernateConfiguration(String hibernateConfig) {
		super();
		logger.info("load hibernate config '{}'", hibernateConfig);
		this.pathToHibernateConfig = hibernateConfig;

	}

	/**
	 * @return the hibernateConfig
	 */
	public String getPathToHibernateConfiguration() {
		return pathToHibernateConfig;
	}

	/**
	 * @return the configuration
	 */
	public Configuration getHibernateConfiguration() {
		if (configuration == null) {
			configuration = new Configuration().configure(pathToHibernateConfig);
		}
		return configuration;
	}
}