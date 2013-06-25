package de.tu.darmstadt.informatik.ausland.database.hibernate.dao.visitor;

import org.hibernate.Session;

import de.tu.darmstadt.informatik.ausland.model.BeanVisitorInterface;

public abstract class BeanHibernateVisitor implements BeanVisitorInterface {

	/**
	 * active session to use for some actions
	 */
	private Session session;

	/**
	 * object
	 */
	private Object databaseOject;
	
	public BeanHibernateVisitor(Session session, Object databaseObject) {
		this.databaseOject = databaseObject;
		this.session = session;
	}

	Session getSession() {
		return session;
	}

	Object getDatabaseObject() {
		return databaseOject;
	}
	
	void setDatabaseObject(Object databaseObject) {
		this.databaseOject = databaseObject;
	}
}