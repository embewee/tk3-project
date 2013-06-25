package de.tu.darmstadt.informatik.ausland.database.hibernate.dao.visitor;

import de.tu.darmstadt.informatik.ausland.model.CourseBean;
import de.tu.darmstadt.informatik.ausland.model.CourseClassificationBean;
import de.tu.darmstadt.informatik.ausland.model.CourseTypeBean;
import de.tu.darmstadt.informatik.ausland.model.FieldOfChoiceBean;
import de.tu.darmstadt.informatik.ausland.model.ModuleBean;
import de.tu.darmstadt.informatik.ausland.model.ModuleClassificationBean;
import de.tu.darmstadt.informatik.ausland.model.NoteBean;
import de.tu.darmstadt.informatik.ausland.model.StudyProgramBean;
import de.tu.darmstadt.informatik.ausland.model.UniversityBean;
import de.tu.darmstadt.informatik.ausland.model.VisibilityBean;
import de.tu.darmstadt.informatik.ausland.model.database.Course;
import de.tu.darmstadt.informatik.ausland.model.database.CourseClassification;
import de.tu.darmstadt.informatik.ausland.model.database.StudyProgram;
import de.tu.darmstadt.informatik.ausland.model.database.University;

public class PartialDatabaseToModelVisitor extends PlainDatabaseToModelVisitor {

	public PartialDatabaseToModelVisitor(Object object) {
		super(object);
	}

	@Override
	public void visit(University databaseObject) {
		UniversityBean university = UniversityBean.class.cast(getObject());
		super.visit(databaseObject);
		university.setNoteList(convert(NoteBean.class, databaseObject.getNoteList()));
	}

	@Override
	public void visit(CourseClassification databaseObject) {
		CourseClassificationBean classification = CourseClassificationBean.class.cast(getObject());
		super.visit(databaseObject);
			classification.setExcludeList(convertPlain(ModuleBean.class, databaseObject.getExcludeList()));
			classification.setReplaceList(convertPlain(ModuleBean.class, databaseObject.getReplaceList()));
			classification.setNoteList(convert(NoteBean.class, databaseObject.getNoteList()));
	}

	@Override
	public void visit(Course databaseObject) {
		CourseBean course = CourseBean.class.cast(getObject());
		super.visit(databaseObject);
		course.setDiplomaSupplement(databaseObject.getDiplomaSupplement());
		course.setVisibility(convert(VisibilityBean.class, databaseObject.getVisibility()));
		course.setNoteList(convert(NoteBean.class, databaseObject.getNoteList()));
		course.setCourseClassificationList(convertPlain(CourseClassificationBean.class, databaseObject.getCourseClassificationList()));
	}

	@Override
	public void visit(StudyProgram databaseObject) {
		StudyProgramBean studyProgram = StudyProgramBean.class.cast(getObject());
		super.visit(databaseObject);
		studyProgram.setCourseTypeList(convert(CourseTypeBean.class, databaseObject.getCourseTypeList()));
		studyProgram.setFieldOfChoiceList(convert(FieldOfChoiceBean.class, databaseObject.getFieldOfChoiceList()));
		studyProgram.setModuleList(convertPlain(ModuleClassificationBean.class, databaseObject.getModuleList()));
	}
}