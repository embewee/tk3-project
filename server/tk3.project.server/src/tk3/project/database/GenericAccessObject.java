package tk3.project.database;

import java.io.Serializable;
import java.util.SortedSet;
import java.util.TreeSet;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.metamodel.relational.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tk3.project.database.hibernate.HibernateFactory;

/**
 * Abstract class to access database classes
 * 
 * @author Ingo Reimund
 * @since 06.04.2013
 * 
 * @param <D>
 *            type of class used in the database
 * @param <M>
 *            type of the model that carry the database information
 * @param <S>
 *            type of the key used by the class saved in the database
 */
public class GenericAccessObject<D extends Identifier<S>, M extends IdentifierBean<S>, S extends Serializable> {

	/**
	 * logger
	 */
	private static Logger logger = LoggerFactory.getLogger(GenericAccessObject.class);

	/**
	 * class of the database class
	 */
	Class<D> databaseClass;

	/**
	 * class of the bean class
	 */
	Class<M> beanClass;

	public GenericAccessObject(Class<D> databaseClass, Class<M> beanClass) {
		this.databaseClass = databaseClass;
		this.beanClass = beanClass;
	}

	/**
	 * save the given object
	 * 
	 * @param toSave
	 *            object that carry the data to save
	 */
	public S save(M toSave) {
		Session session = createSession();
		try {
			if (toSave.getID() != null && session.get(databaseClass, toSave.getID()) != null) {
				logger.warn("object of type '{}' with id '{}' already exists", databaseClass.getSimpleName(),
						toSave.getID());
				return null;
			}
			D databaseObject = databaseClass.newInstance();
			PartialModelToDatabaseVisitor visitor;
			visitor = new PartialModelToDatabaseVisitor(session, databaseObject);
			toSave.accept(visitor);

			logger.debug("save object of type '{}'", databaseClass.getSimpleName());
			session.save(databaseObject);
			S id = databaseObject.getID();

			closeSession(session);
			return id;
		} catch (HibernateException | InstantiationException | IllegalAccessException exception) {
			rollback(session, exception);
		}
		return null;
	}

	/**
	 * update the given object
	 * 
	 * @param toUpdate
	 *            object that carry the data to update
	 * 
	 */
	public UpdateReturnType update(M toUpdate) {
		Session session = createSession();
		try {
			Object object = session.get(databaseClass, toUpdate.getID());
			if (object == null) {
				logger.warn("object of type '{}' and with id '{}' does not exist", databaseClass.getSimpleName(),
						toUpdate.getID());
				return UpdateReturnType.NOTFOUND;
			}

			PartialModelToDatabaseVisitor visitor;
			visitor = new PartialModelToDatabaseVisitor(session, object);
			toUpdate.accept(visitor);

			logger.debug("update object of type '{}' and id '{}'", databaseClass.getSimpleName(), toUpdate.getID());
			session.update(object);

			closeSession(session);
			return UpdateReturnType.UPDATED;
		} catch (HibernateException exception) {
			rollback(session, exception);
		}
		return UpdateReturnType.ERROR;
	}

	/**
	 * find a database object by id and convert it to a model object
	 * 
	 * @param objectID
	 *            id of wanted object
	 * 
	 * @return found object or null
	 */
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
			PartialDatabaseToModelVisitor visitor;
			visitor = new PartialDatabaseToModelVisitor(model);
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
				PartialDatabaseToModelVisitor visitor;
				visitor = new PartialDatabaseToModelVisitor(model);
				ObjectVisitable.class.cast(object).accept(visitor);
				list.add(model);
			}
			closeSession(session);
		} catch (HibernateException | InstantiationException | IllegalAccessException exception) {
			rollback(session, exception);
		}
		return list;
	}

	/**
	 * convert a map of criteria to a hibernate criteria object
	 * 
	 * @param givenCriteria
	 *            given criteria
	 * @param session
	 *            active hibernate session
	 * @return hibernate criteria
	 */
	Criteria createCriteria(QueryBean query, Session session) {
		Criteria criteria = session.createCriteria(databaseClass);
		try {
			D object = databaseClass.newInstance();
			ObjectHibernateVisitor visitor = new CriteriaObjectVisitor(criteria, query);
			object.accept(visitor);
		} catch (InstantiationException | IllegalAccessException exception) {
			logger.warn("can not add find critiera for request", exception);
			return session.createCriteria(databaseClass);
		}
		return criteria;
	}

	/**
	 * delete the object with the given identifier
	 * 
	 * @param identifier
	 *            of the object that should be delete
	 */
	public DeleteReturnType delete(S objectID) {
		Session session = createSession();
		try {
			Object object = session.get(databaseClass, objectID);
			if (object == null) {
				logger.warn("object with id '{}' and type '{}' does not exist", objectID, databaseClass.getSimpleName());
				return DeleteReturnType.NOTFOUND;
			}

			logger.debug("try to delete object of type '{}' with id '{}'", databaseClass.getSimpleName(), objectID);
			session.delete(object);

			closeSession(session);
			return DeleteReturnType.DELETED;
		} catch (HibernateException exception) {
			rollback(session, exception);
			return DeleteReturnType.ERROR;
		}
	}

	/**
	 * create new session and start transaction
	 * 
	 * @return new session
	 */
	Session createSession() {
		logger.debug("create new session");
		Session session = HibernateFactory.getInstance().createSession();
		session.beginTransaction();
		return session;
	}

	/**
	 * close session and associated transaction
	 * 
	 * @param transaction
	 *            to close
	 * @param session
	 *            to close
	 */
	void closeSession(Session session) {
		Transaction transaction = session.getTransaction();
		if (!transaction.wasCommitted()) {
			logger.debug("commit transactions");
			transaction.commit();
		}

		logger.debug("close open session");
		session.close();
	}

	/**
	 * roll back of last session transaction
	 * 
	 * @param exception
	 *            reason to roll back
	 * @param session
	 *            to roll back
	 */
	void rollback(Session session, Exception exception) {
		logger.error(exception.getMessage(), exception);
		Transaction transaction = session.getTransaction();

		logger.info("rollback transaction");
		transaction.rollback();
		session.close();
	}

	Class<D> getDatabaseClass() {
		return databaseClass;
	}

	Class<M> getBeanClass() {
		return beanClass;
	}
}
