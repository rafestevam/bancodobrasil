package com.idsscheer.webapps.arcm.ui.components.surveymanagement.actioncommands;

import com.idsscheer.webapps.arcm.ui.framework.actioncommands.object.BaseSaveActionCommand;

public class SurveyTaskSaveActionCommandBB extends BaseSaveActionCommand {
	protected boolean isDirty() {
		if (!isValid()) {
			return true;
		}
		else {
			return super.isDirty();
		}
	}

}