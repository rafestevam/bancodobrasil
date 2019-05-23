package com.idsscheer.webapps.arcm.dl.framework.viewhandler;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;


public class CreatedSurveyObjectRoleViewHandlerBB extends SurveyObjectRoleViewHandlerBB {

	@Override
	protected List<IEnumerationItem> getObjectSpecificRolesToCheckClientLevel() {
		return Collections.unmodifiableList(Arrays.asList(Enumerations.USERROLE_TYPE.SURVEYMANAGER, Enumerations.USERROLE_TYPE.SURVEYAUDITOR, Enumerations.USERROLE_TYPE.AUDITREVIEWER));
	}

}
