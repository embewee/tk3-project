package de.tu.darmstadt.informatik.ausland.database.hibernate.dao.visitor;

import de.tu.darmstadt.informatik.ausland.model.database.ObjectVisitorInterface;

public abstract class ObjectHibernateVisitor implements ObjectVisitorInterface {

	/**
	 * object
	 */
	private Object object;
	
	public ObjectHibernateVisitor(Object object) {
		this.object = object;
	}

	Object getObject() {
		return object;
	}
	
	void setObject(Object object) {
		this.object = object;
	}
}