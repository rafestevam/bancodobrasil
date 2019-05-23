package com.idsscheer.webapps.arcm.dl.framework.viewhandler;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations;
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsBB;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.dl.framework.IFilterCriteria;


public class SignoffObjectRoleViewHandlerBB extends AbstractObjectRoleViewHandlerBB{

	@Override
	protected List<IEnumerationItem> getObjectSpecificRolesToCheckClientLevel() {
		return Collections.unmodifiableList(Arrays.asList(Enumerations.USERROLE_TYPE.SIGNOFFMANAGER, Enumerations.USERROLE_TYPE.SIGNOFFOWNER, Enumerations.USERROLE_TYPE.SIGNOFFREVIEWER));
	}

	@Override
	protected List<IEnumerationItem> getObjectSpecificRolesForRestrictions() {
		return Collections.unmodifiableList(Arrays.asList(Enumerations.USERROLE_TYPE.SIGNOFFMANAGER, Enumerations.USERROLE_TYPE.SIGNOFFOWNER, Enumerations.USERROLE_TYPE.SIGNOFFREVIEWER));
	}

	@Override
	protected void addFilterRestrictions(Set<Long> orgunitIds, List<IFilterCriteria> groupFilters) {
		addFilterForSignoffGroup(groupFilters, orgunitIds, Enumerations.USERROLE_TYPE.SIGNOFFMANAGER, 	"signoffmanager_ID");
		addFilterForSignoffGroup(groupFilters, orgunitIds, Enumerations.USERROLE_TYPE.SIGNOFFOWNER, 	"signoffower_ID");
		addFilterForSignoffGroup(groupFilters, orgunitIds, Enumerations.USERROLE_TYPE.SIGNOFFREVIEWER, 	"signoffreviewer_ID");
		
		
	}
}