package de.tu.darmstadt.informatik.ausland.database.hibernate.dao;

import java.io.Serializable;
import java.util.SortedSet;
import java.util.TreeSet;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.metamodel.relational.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlainGenericAccessObject<D extends Identifier<S>, M extends IdentifierBean<S>, S extends Serializable> extends GenericAccessObject<D, M, S> {

	/**
	 * logger
	 */
	private static Logger logger = LoggerFactory.getLogger(GenericAccessObject.class);
	
	public PlainGenericAccessObject(Class<D> databaseClass, Class<M> beanClass) {
		super(databaseClass, beanClass);
	}
	
	/**
	 * find a database object by id and convert it to a model object
	 * 
	 * @param objectID
	 *            id of wanted object
	 * 
	 * @return found object or null
	 */
	@Override
	public M findByID(S objectID) {
		Session session = createSession();
		try {
			logger.debug("load {} with id {}", databaseClass.getSimpleName(), objectID);
			Object object = session.get(databaseClass, objectID);
			if (object == null) {
				logger.warn("object with id '{}' and type '{}' does not exist", objectID, databaseClass.getSimpleName());
				return null;
			}

			M model = beanClass.newInstance();
			PlainDatabaseToModelVisitor visitor;
			visitor = new PlainDatabaseToModelVisitor(model);
			ObjectVisitable.class.cast(object).accept(visitor);

			closeSession(session);
			return model;
		} catch (HibernateException | InstantiationException | IllegalAccessException exception) {
			rollback(session, exception);
		}
		logger.debug("no object of the type '{}' with id '{}' found", databaseClass.getSimpleName(), objectID);
		return null;
	}

	/**
	 * find all object of the given database object class and convert them to
	 * the given model object class
	 * 
	 * @param criteria
	 *            to find objects
	 * 
	 * @return list of all found objects
	 */
	public SortedSet<M> findAll(QueryBean query) {
		SortedSet<M> list = new TreeSet<>();
		Session session = createSession();
		try {
			Criteria findCriteria = createCriteria(query, session);
			for (Object object : findCriteria.list()) {
				M model = beanClass.newInstance();
				PlainDatabaseToModelVisitor visitor;
				visitor = new PlainDatabaseToModelVisitor(model);
				ObjectVisitable.class.cast(object).accept(visitor);
				list.add(model);
			}
			closeSession(session);
		} catch (HibernateException | InstantiationException | IllegalAccessException exception) {
			rollback(session, exception);
		}
		return list;
	}
}