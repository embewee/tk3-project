package de.tu.darmstadt.informatik.ausland.database.hibernate.dao.visitor;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tu.darmstadt.informatik.ausland.model.ConversionBean;
import de.tu.darmstadt.informatik.ausland.model.CountryBean;
import de.tu.darmstadt.informatik.ausland.model.CourseBean;
import de.tu.darmstadt.informatik.ausland.model.CourseClassificationBean;
import de.tu.darmstadt.informatik.ausland.model.CourseTypeBean;
import de.tu.darmstadt.informatik.ausland.model.FieldOfChoiceBean;
import de.tu.darmstadt.informatik.ausland.model.ModuleBean;
import de.tu.darmstadt.informatik.ausland.model.ModuleClassificationBean;
import de.tu.darmstadt.informatik.ausland.model.NoteBean;
import de.tu.darmstadt.informatik.ausland.model.RegionBean;
import de.tu.darmstadt.informatik.ausland.model.StatusBean;
import de.tu.darmstadt.informatik.ausland.model.StudyProgramBean;
import de.tu.darmstadt.informatik.ausland.model.UniversityBean;
import de.tu.darmstadt.informatik.ausland.model.VisibilityBean;
import de.tu.darmstadt.informatik.ausland.model.database.Conversion;
import de.tu.darmstadt.informatik.ausland.model.database.Country;
import de.tu.darmstadt.informatik.ausland.model.database.Course;
import de.tu.darmstadt.informatik.ausland.model.database.CourseClassification;
import de.tu.darmstadt.informatik.ausland.model.database.CourseType;
import de.tu.darmstadt.informatik.ausland.model.database.FieldOfChoice;
import de.tu.darmstadt.informatik.ausland.model.database.Module;
import de.tu.darmstadt.informatik.ausland.model.database.ModuleClassification;
import de.tu.darmstadt.informatik.ausland.model.database.Note;
import de.tu.darmstadt.informatik.ausland.model.database.ObjectVisitable;
import de.tu.darmstadt.informatik.ausland.model.database.Region;
import de.tu.darmstadt.informatik.ausland.model.database.Status;
import de.tu.darmstadt.informatik.ausland.model.database.StudyProgram;
import de.tu.darmstadt.informatik.ausland.model.database.University;
import de.tu.darmstadt.informatik.ausland.model.database.Visibility;

public class PlainDatabaseToModelVisitor extends ObjectHibernateVisitor {

	/**
	 * logger
	 */
	private static Logger logger = LoggerFactory.getLogger(PlainDatabaseToModelVisitor.class);
	
	public PlainDatabaseToModelVisitor(Object object) {
		super(object);
	}
	
	<M> M convert(Class<M> modelObjectClass, ObjectVisitable databaseObject) {
		try {
			M model = modelObjectClass.newInstance();
			setObject(model);
			databaseObject.accept(this);
			return model;
		} catch (InstantiationException | IllegalAccessException exception) {
			logger.error(exception.getMessage());
			return null;
		}
	}

	<M> List<M> convert(Class<M> modelObjectClass, List<? extends ObjectVisitable> databaseObjectList) {
		List<M> modelList = new ArrayList<>(databaseObjectList.size());
		try {
			for (ObjectVisitable databaseObject : databaseObjectList) {
				M model = modelObjectClass.newInstance();
				setObject(model);
				databaseObject.accept(this);
				modelList.add(model);
			}
		} catch (InstantiationException | IllegalAccessException exception) {
			logger.error(exception.getMessage());
		}
		return modelList;
	}
	
	<M> M convertPlain(Class<M> modelObjectClass, ObjectVisitable databaseObject) {
		try {
			M model = modelObjectClass.newInstance();
			PlainDatabaseToModelVisitor visitor = new PlainDatabaseToModelVisitor(model);
			databaseObject.accept(visitor);
			return model;
		} catch (InstantiationException | IllegalAccessException exception) {
			logger.error(exception.getMessage());
			return null;
		}
	}

	<M> List<M> convertPlain(Class<M> modelObjectClass, List<? extends ObjectVisitable> databaseObjectList) {
		PlainDatabaseToModelVisitor visitor = new PlainDatabaseToModelVisitor(null);
		List<M> modelList = new ArrayList<>(databaseObjectList.size());
		try {
			for (ObjectVisitable databaseObject : databaseObjectList) {
				M model = modelObjectClass.newInstance();
				visitor.setObject(model);
				databaseObject.accept(visitor);
				modelList.add(model);
			}
			return modelList;
		} catch (InstantiationException | IllegalAccessException exception) {
			logger.error(exception.getMessage());
			return null;
		}
	}

	@Override
	public void visit(Course databaseObject) {
		logger.debug("convert {} with id {} to model", databaseObject.getClass().getSimpleName(), databaseObject.getID());
		CourseBean course = CourseBean.class.cast(getObject());
		course.setID(databaseObject.getID());
		course.setName(databaseObject.getName());
		course.setShortName(databaseObject.getShortName());
		course.setUrl(databaseObject.getUrl());
		course.setUniversity(convertPlain(UniversityBean.class, databaseObject.getUniversity()));
		course.setCredits(convert(databaseObject.getCredits()));
		course.setDate(databaseObject.getDate());
	}

	@Override
	public void visit(University databaseObject) {
		logger.debug("convert {} with id {} to model", databaseObject.getClass().getSimpleName(), databaseObject.getID());
		UniversityBean university = UniversityBean.class.cast(getObject());
		university.setID(databaseObject.getID());
		university.setName(databaseObject.getName());
		university.setShortName(databaseObject.getShortName());
		university.setUrl(databaseObject.getUrl());
		university.setCountry(convert(CountryBean.class, databaseObject.getCountry()));
	}

	@Override
	public void visit(CourseType databaseObject) {
		logger.debug("convert {} with id {} to model", databaseObject.getClass().getSimpleName(), databaseObject.getID());
		CourseTypeBean courseType = CourseTypeBean.class.cast(getObject());
		courseType.setName(databaseObject.getName());
		courseType.setID(databaseObject.getID());
	}

	@Override
	public void visit(Note databaseObject) {
		logger.debug("convert {} with id {} to model", databaseObject.getClass().getSimpleName(), databaseObject.getID());
		NoteBean note = NoteBean.class.cast(getObject());
		note.setID(databaseObject.getID());
		note.setText(databaseObject.getText());
		note.setVisibility(convert(VisibilityBean.class, databaseObject.getVisibility()));
	}

	@Override
	public void visit(Status databaseObject) {
		logger.debug("convert {} with id {} to model", databaseObject.getClass().getSimpleName(), databaseObject.getID());
		StatusBean status = StatusBean.class.cast(getObject());
		status.setID(databaseObject.getID());
		status.setName(databaseObject.getName());
	}

	@Override
	public void visit(Visibility databaseObject) {
		logger.debug("convert {} with id {} to model", databaseObject.getClass().getSimpleName(), databaseObject.getID());
		VisibilityBean visibility = VisibilityBean.class.cast(getObject());
		visibility.setID(databaseObject.getID());
		visibility.setName(databaseObject.getName());
		visibility.setLevel(databaseObject.getLevel().intValue());
	}

	@Override
	public void visit(CourseClassification databaseObject) {
		logger.debug("convert {} with id {} to model", databaseObject.getClass().getSimpleName(), databaseObject.getID());
		CourseClassificationBean classification = CourseClassificationBean.class.cast(getObject());
		classification.setID(databaseObject.getID());
		classification.setDate(databaseObject.getDate());
		classification.setStatus(convert(StatusBean.class, databaseObject.getStatus()));
		classification.setCredits(convert(databaseObject.getCredits()));
		classification.setStudyProgram(convertPlain(StudyProgramBean.class, databaseObject.getStudyProgram()));
		classification.setCourse(convertPlain(CourseBean.class, databaseObject.getCourse()));
		classification.setVisibility(convert(VisibilityBean.class, databaseObject.getVisibility()));
		classification.setFieldOfChoiceList(convert(FieldOfChoiceBean.class, databaseObject.getFieldOfChoiceList()));
		classification.setCourseTypeList(convert(CourseTypeBean.class, databaseObject.getCourseTypeList()));
	}

	@Override
	public void visit(Module databaseObject) {
		logger.debug("convert {} with id {} to model", databaseObject.getClass().getSimpleName(), databaseObject.getID());
		ModuleBean module = ModuleBean.class.cast(getObject());
		module.setID(databaseObject.getID());
		module.setName(databaseObject.getName());
		module.setNumber(databaseObject.getNumber());
	}

	@Override
	public void visit(StudyProgram databaseObject) {
		logger.debug("convert {} with id {} to model", databaseObject.getClass().getSimpleName(), databaseObject.getID());
		StudyProgramBean studyProgram = StudyProgramBean.class.cast(getObject());
		studyProgram.setID(databaseObject.getID());
		studyProgram.setName(databaseObject.getName());
		studyProgram.setVersion(databaseObject.getVersion());
		studyProgram.setVisibility(convert(VisibilityBean.class, databaseObject.getVisibility()));
	}

	@Override
	public void visit(FieldOfChoice databaseObject) {
		logger.debug("convert {} with id {} to model", databaseObject.getClass().getSimpleName(), databaseObject.getID());
		FieldOfChoiceBean fieldOfChoice = FieldOfChoiceBean.class.cast(getObject());
		fieldOfChoice.setID(databaseObject.getID());
		fieldOfChoice.setCoordinator(databaseObject.getCoordinator());
		fieldOfChoice.setName(databaseObject.getName());
		fieldOfChoice.setVisibility(convert(VisibilityBean.class, databaseObject.getVisibility()));
	}

	@Override
	public void visit(Conversion databaseObject) {
		logger.debug("convert {} with id {} to model", databaseObject.getClass().getSimpleName(), databaseObject.getID());
		ConversionBean conversion = ConversionBean.class.cast(getObject());
		conversion.setID(databaseObject.getID());
		conversion.setHome(databaseObject.getHome());
		conversion.setReceiving(databaseObject.getReceiving());
	}

	@Override
	public void visit(Country databaseObject) {
		logger.debug("convert {} with id {} to model", databaseObject.getClass().getSimpleName(), databaseObject.getID());
		CountryBean country = CountryBean.class.cast(getObject());
		country.setID(databaseObject.getID());
		country.setName(databaseObject.getName());
		country.setRegion(convert(RegionBean.class, databaseObject.getRegion()));
	}

	@Override
	public void visit(Region databaseObject) {
		logger.debug("convert {} with id {} to model", databaseObject.getClass().getSimpleName(), databaseObject.getID());
		RegionBean region = RegionBean.class.cast(getObject());
		region.setID(databaseObject.getID());
		region.setName(databaseObject.getName());
	}

	@Override
	public void visit(ModuleClassification databaseObject) {
		logger.debug("convert {} with id {} to model", databaseObject.getClass().getSimpleName(), databaseObject.getID());
		ModuleClassificationBean classification = ModuleClassificationBean.class.cast(getObject());
		classification.setID(databaseObject.getID());
		classification.setCourseTypeList(convertPlain(CourseTypeBean.class, databaseObject.getCourseTypeList()));
		classification.setFieldOfChoiceList(convertPlain(FieldOfChoiceBean.class, databaseObject.getFieldOfChoiceList()));
		classification.setModule(convertPlain(ModuleBean.class, databaseObject.getModule()));
		classification.setStudyProgram(convertPlain(StudyProgramBean.class, databaseObject.getStudyProgram()));
		classification.setCredits(convert(databaseObject.getCredits()));
	}
	
	private String convert(Float input) {
		if (input == null) {
			return null;
		}
		return input.toString().replace(".", ",");
	}
}