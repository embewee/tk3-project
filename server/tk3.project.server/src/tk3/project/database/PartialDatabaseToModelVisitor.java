package de.tu.darmstadt.informatik.ausland.database.hibernate.dao.visitor;


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