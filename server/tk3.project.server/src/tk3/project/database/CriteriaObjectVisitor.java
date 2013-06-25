package de.tu.darmstadt.informatik.ausland.database.hibernate.dao.visitor;


import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import de.tu.darmstadt.informatik.ausland.model.QueryBean;
import de.tu.darmstadt.informatik.ausland.model.database.Conversion;
import de.tu.darmstadt.informatik.ausland.model.database.Country;
import de.tu.darmstadt.informatik.ausland.model.database.Course;
import de.tu.darmstadt.informatik.ausland.model.database.CourseClassification;
import de.tu.darmstadt.informatik.ausland.model.database.CourseType;
import de.tu.darmstadt.informatik.ausland.model.database.FieldOfChoice;
import de.tu.darmstadt.informatik.ausland.model.database.Module;
import de.tu.darmstadt.informatik.ausland.model.database.ModuleClassification;
import de.tu.darmstadt.informatik.ausland.model.database.Note;
import de.tu.darmstadt.informatik.ausland.model.database.Region;
import de.tu.darmstadt.informatik.ausland.model.database.Status;
import de.tu.darmstadt.informatik.ausland.model.database.StudyProgram;
import de.tu.darmstadt.informatik.ausland.model.database.University;
import de.tu.darmstadt.informatik.ausland.model.database.Visibility;
import de.tu.darmstadt.informatik.ausland.util.parameter.QueryParameter;

public class CriteriaObjectVisitor extends ObjectHibernateVisitor {

	private QueryBean query;

	public CriteriaObjectVisitor(Criteria criteria, QueryBean query) {
		super(criteria);
		this.query = query;
	}

	@Override
	public void visit(Course course) {
		Criteria criteria = Criteria.class.cast(getObject());
		if (query.getUniversityID() != null) {
			Integer value = query.getUniversityID();
			criteria.createCriteria(QueryParameter.UNIVERSITY).add(Restrictions.eq("id", value));
		}
	}

	@Override
	public void visit(Conversion conversion) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(CourseClassification courseClassification) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(FieldOfChoice fieldOfChoice) {
		Criteria criteria = Criteria.class.cast(getObject());
		if (query.getDeprecated() != null) {
			Boolean value = query.getDeprecated();
			criteria.add(Restrictions.eq(QueryParameter.DEPRECATED, value));
		}
	}

	@Override
	public void visit(CourseType courseType) {
		Criteria criteria = Criteria.class.cast(getObject());
		if (query.getDeprecated() != null) {
			Boolean value = query.getDeprecated();
			criteria.add(Restrictions.eq(QueryParameter.DEPRECATED, value));
		}
	}

	@Override
	public void visit(Module module) {
		
	}

	@Override
	public void visit(Note note) {
		
	}

	@Override
	public void visit(Status status) {
		Criteria criteria = Criteria.class.cast(getObject());
		if (query.getDeprecated() != null) {
			Boolean value = query.getDeprecated();
			criteria.add(Restrictions.eq(QueryParameter.DEPRECATED, value));
		}
	}

	@Override
	public void visit(StudyProgram studyProgram) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(University university) {
		Criteria criteria = Criteria.class.cast(getObject());
		if (query.getCountryID() != null) {
			Short value = query.getCountryID();
			criteria.createCriteria(QueryParameter.COUNTRY).add(Restrictions.eq("id", value));
		}
		if (query.getRegionID() != null) {
			Short value = query.getRegionID();
			Criteria subCriteria = criteria.createCriteria(QueryParameter.COUNTRY);
			subCriteria.createCriteria(QueryParameter.REGION).add(Restrictions.eq("id", value));
		}
	}

	@Override
	public void visit(Visibility visibility) {
		Criteria criteria = Criteria.class.cast(getObject());
		if (query.getDeprecated() != null) {
			Boolean value = query.getDeprecated();
			criteria.add(Restrictions.eq(QueryParameter.DEPRECATED, value));
		}
		if (query.getMinValue() != null) {
			Integer value = query.getMinValue();
			criteria.add(Restrictions.ge(QueryParameter.LEVEL, value));
		}
	}

	@Override
	public void visit(Country country) {
		Criteria criteria = Criteria.class.cast(getObject());
		if (query.getDeprecated() != null) {
			Boolean value = query.getDeprecated();
			criteria.add(Restrictions.eq(QueryParameter.DEPRECATED, value));
		}
		
		if (query.getRegionID() != null) {
			Short value = query.getRegionID();
			criteria.createCriteria(QueryParameter.REGION).add(Restrictions.eq("id", value));
		}
	}

	@Override
	public void visit(Region region) {
		Criteria criteria = Criteria.class.cast(getObject());
		if (query.getDeprecated() != null) {
			Boolean value = query.getDeprecated();
			criteria.add(Restrictions.eq(QueryParameter.DEPRECATED, value));
		}
	}

	@Override
	public void visit(ModuleClassification moduleClassification) {
		// TODO Auto-generated method stub
		
	}
}