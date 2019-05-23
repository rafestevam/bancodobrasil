package com.idsscheer.webapps.arcm.ui.components.surveymanagement.actioncommands;

public class QuestionnaireTemplateSaveActionCommandBB extends QuestionnaireTemplateSaveActionCommand {
	protected boolean isDirty() {
		if (!isValid()) {
			return true;
		}
		else {
			return super.isDirty();
		}
	}

}