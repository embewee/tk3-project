package de.tu.darmstadt.informatik.ausland.database.hibernate.dao.visitor;

import java.beans.Visibility;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.metamodel.relational.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PartialModelToDatabaseVisitor extends BeanHibernateVisitor {

	/**
	 * logger
	 */
	private static Logger logger = LoggerFactory.getLogger(PartialModelToDatabaseVisitor.class);

	public PartialModelToDatabaseVisitor(Session session, Object databaseObject) {
		super(session, databaseObject);
	}

	private <D> D get(Class<D> databaseObjectClass, Serializable identifier) {
		return databaseObjectClass.cast(getSession().get(databaseObjectClass, identifier));
	}

	private <D extends Identifier<? extends Serializable>, M extends IdentifierBean<? extends Serializable>> List<D> get(
			Class<D> databaseObjectClass, List<M> beanList, List<D> databaseObjectList) {
		try {
			List<D> toRemoveList = new ArrayList<>();
			loop: for (D databaseObject : databaseObjectList) {
				for (M bean : beanList) {
					if (databaseObject.getID() == bean.getID()) {
						logger.debug("object of type {} an with id {} is unchanged", databaseObjectClass,
								databaseObject.getID());
						this.setDatabaseObject(databaseObject);
						bean.accept(this);
						beanList.remove(bean);
						continue loop;
					}
				}
				logger.debug("mark object of type {} an with id {} to remove", databaseObjectClass,
						databaseObject.getID());
				toRemoveList.add(databaseObject);
			}
			logger.debug("remove all marked object");
			databaseObjectList.removeAll(toRemoveList);

			for (M bean : beanList) {
				logger.debug("add new object of type {} an id {}", databaseObjectClass, bean.getID());
				if (bean.getID() == null) {
					logger.debug("create new object of type {}", databaseObjectClass);
					D databaseObject = databaseObjectClass.newInstance();
					this.setDatabaseObject(databaseObject);
					bean.accept(this);
					databaseObjectList.add(databaseObject);
				} else {
					logger.debug("add new association");
					databaseObjectList.add(this.get(databaseObjectClass, bean.getID()));
				}
			}
			return databaseObjectList;
		} catch (InstantiationException | IllegalAccessException exception) {
			logger.error(exception.getMessage());
			return null;
		}
	}

	@Override
	public void visit(UniversityBean university) {
		logger.debug("convert {} with id {} to database", university.getClass().getSimpleName(), university.getID());
		University databaseObject = University.class.cast(getDatabaseObject());
		databaseObject.setCountry(get(Country.class, university.getCountry().getID()));
		databaseObject.setName(university.getName());
		databaseObject.setID(university.getID());
		databaseObject.setShortName(university.getShortName());
		databaseObject.setUrl(university.getUrl());
		databaseObject.setNoteList(get(Note.class, university.getNoteList(), databaseObject.getNoteList()));
	}

	@Override
	public void visit(NoteBean note) {
		logger.debug("convert {} with id {} to database", note.getClass().getSimpleName(), note.getID());
		Note databaseObject = Note.class.cast(getDatabaseObject());
		databaseObject.setID(note.getID());
		databaseObject.setText(note.getText());
		databaseObject.setVisibility(get(Visibility.class, note.getVisibility().getID()));
	}

	@Override
	public void visit(ModuleBean module) {
		logger.debug("convert {} with id {} to database", module.getClass().getSimpleName(), module.getID());
		Module databaseObject = Module.class.cast(getDatabaseObject());
		databaseObject.setID(module.getID());
		databaseObject.setName(module.getName());
		databaseObject.setNumber(module.getNumber());
	}

	@Override
	public void visit(StudyProgramBean studyProgram) {
		logger.debug("convert {} with id {} to database", studyProgram.getClass().getSimpleName(), studyProgram.getID());
		StudyProgram databaseObject = StudyProgram.class.cast(getDatabaseObject());
		databaseObject.setID(studyProgram.getID());
		databaseObject.setName(studyProgram.getName());
		databaseObject.setVersion(studyProgram.getVersion());
		databaseObject.setVisibility(get(Visibility.class, studyProgram.getVisibility().getID()));
		databaseObject.setCourseTypeList(get(CourseType.class, studyProgram.getCourseTypeList(),
				databaseObject.getCourseTypeList()));
		databaseObject.setModuleList(get(ModuleClassification.class, studyProgram.getModuleList(),
				databaseObject.getModuleList()));
		databaseObject.setFieldOfChoiceList(get(FieldOfChoice.class, studyProgram.getFieldOfChoiceList(),
				databaseObject.getFieldOfChoiceList()));

	}

	@Override
	public void visit(CourseBean course) {
		logger.debug("convert {} with id {} to database", course.getClass().getSimpleName(), course.getID());
		Course databaseObject = Course.class.cast(getDatabaseObject());
		databaseObject.setID(course.getID());
		databaseObject.setCredits(convertToFloat(course.getCredits()));
		databaseObject.setDate(course.getDate() == null ? new Date() : course.getDate());
		databaseObject.setDiplomaSupplement(course.getDiplomaSupplement());
		databaseObject.setName(course.getName());
		databaseObject.setUrl(course.getUrl());
		databaseObject.setShortName(course.getShortName());
		databaseObject.setVisibility(get(Visibility.class, course.getVisibility().getID()));
		databaseObject.setUniversity(get(University.class, course.getUniversity().getID()));
		databaseObject.setNoteList(get(Note.class, course.getNoteList(), databaseObject.getNoteList()));
		databaseObject.setCourseClassificationList(get(CourseClassification.class,
				course.getCourseClassificationList(), databaseObject.getCourseClassificationList()));
	}

	@Override
	public void visit(CourseTypeBean courseType) {
		logger.debug("convert {} with id {} to database", courseType.getClass().getSimpleName(), courseType.getID());
		CourseType databaseObject = CourseType.class.cast(getDatabaseObject());
		databaseObject.setID(courseType.getID());
		databaseObject.setName(courseType.getName());
	}

	@Override
	public void visit(StatusBean status) {
		logger.debug("convert {} with id {} to database", status.getClass().getSimpleName(), status.getID());
		Status databaseObject = Status.class.cast(getDatabaseObject());
		databaseObject.setName(status.getName());
		databaseObject.setID(status.getID());
	}

	@Override
	public void visit(VisibilityBean visibility) {
		logger.debug("convert {} with id {} to database", visibility.getClass().getSimpleName(), visibility.getID());
		Visibility databaseObject = Visibility.class.cast(getDatabaseObject());
		databaseObject.setName(visibility.getName());
		databaseObject.setID(visibility.getID());
		databaseObject.setLevel(visibility.getLevel().shortValue());
	}

	@Override
	public void visit(CourseClassificationBean classification) {
		logger.debug("convert {} with id {} to database", classification.getClass().getSimpleName(),
				classification.getID());
		CourseClassification databaseObject = CourseClassification.class.cast(getDatabaseObject());
		databaseObject.setCredits(convertToFloat(classification.getCredits()));
		databaseObject.setDate(classification.getDate());
		databaseObject.setID(classification.getID());
		databaseObject.setVisibility(get(Visibility.class, classification.getVisibility().getID()));
		databaseObject.setStatus(get(Status.class, classification.getStatus().getID()));
		databaseObject.setCourse(get(Course.class, classification.getCourse().getID()));
		databaseObject.setStudyProgram(get(StudyProgram.class, classification.getStudyProgram().getID()));
		databaseObject.setFieldOfChoiceList(get(FieldOfChoice.class, classification.getFieldOfChoiceList(),
				databaseObject.getFieldOfChoiceList()));
		databaseObject.setExcludeList(get(Module.class, classification.getExcludeList(),
				databaseObject.getExcludeList()));
		databaseObject.setReplaceList(get(Module.class, classification.getReplaceList(),
				databaseObject.getReplaceList()));
		databaseObject.setCourseTypeList(get(CourseType.class, classification.getCourseTypeList(),
				databaseObject.getCourseTypeList()));
		databaseObject.setNoteList(get(Note.class, classification.getNoteList(), databaseObject.getNoteList()));
	}

	@Override
	public void visit(FieldOfChoiceBean fieldOfChoice) {
		logger.debug("convert {} with id {} to database", fieldOfChoice.getClass().getSimpleName(),
				fieldOfChoice.getID());
		FieldOfChoice databaseObject = FieldOfChoice.class.cast(getDatabaseObject());
		databaseObject.setCoordinator(fieldOfChoice.getCoordinator());
		databaseObject.setID(fieldOfChoice.getID());
		databaseObject.setName(fieldOfChoice.getName());
		databaseObject.setVisibility(get(Visibility.class, fieldOfChoice.getVisibility().getID()));
	}

	@Override
	public void visit(ConversionBean conversion) {
		logger.debug("convert {} with id {} to database", conversion.getClass().getSimpleName(), conversion.getID());
		Conversion databaseObject = Conversion.class.cast(getDatabaseObject());
		databaseObject.setHome(conversion.getHome());
		databaseObject.setID(conversion.getID());
		databaseObject.setReceiving(conversion.getReceiving());
	}

	@Override
	public void visit(CountryBean country) {
		logger.debug("convert {} with id {} to database", country.getClass().getSimpleName(), country.getID());
		Country databaseObject = Country.class.cast(getDatabaseObject());
		databaseObject.setName(country.getName());
		databaseObject.setID(country.getID());
		databaseObject.setRegion(get(Region.class, country.getRegion().getID()));
	}

	@Override
	public void visit(RegionBean region) {
		logger.debug("convert {} with id {} to database", region.getClass().getSimpleName(), region.getID());
		Region databaseObject = Region.class.cast(getDatabaseObject());
		databaseObject.setName(region.getName());
	}

	@Override
	public void visit(ModuleClassificationBean classification) {
		logger.debug("convert {} with id {} to database", classification.getClass().getSimpleName(),
				classification.getID());
		ModuleClassification databaseObject = ModuleClassification.class.cast(getDatabaseObject());
		databaseObject.setID(classification.getID());
		databaseObject.setCredits(convertToFloat(classification.getCredits()));
		databaseObject.setCourseTypeList(get(CourseType.class, classification.getCourseTypeList(),
				databaseObject.getCourseTypeList()));
		databaseObject.setFieldOfChoiceList(get(FieldOfChoice.class, classification.getFieldOfChoiceList(),
				databaseObject.getFieldOfChoiceList()));
		databaseObject.setModule(get(Module.class, classification.getModule().getID()));
		databaseObject.setStudyProgram(get(StudyProgram.class, classification.getStudyProgram().getID()));
	}
	
	private Float convertToFloat(String input) {
		if (input == null) {
			return null;
		}
		return Float.parseFloat(input.replaceAll(",", "."));
	}
}