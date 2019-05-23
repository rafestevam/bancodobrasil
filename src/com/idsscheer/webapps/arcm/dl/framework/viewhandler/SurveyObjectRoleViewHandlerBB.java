package com.idsscheer.webapps.arcm.dl.framework.viewhandler;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.dl.framework.BusViewException;
import com.idsscheer.webapps.arcm.dl.framework.IDataLayerObject;
import com.idsscheer.webapps.arcm.dl.framework.IFilterCriteria;
import com.idsscheer.webapps.arcm.dl.framework.IRightsFilterCriteria;
import com.idsscheer.webapps.arcm.dl.framework.dllogic.QueryDefinition;


public class SurveyObjectRoleViewHandlerBB extends AbstractObjectRoleViewHandlerBB{

	@Override
	public void handleView(QueryDefinition query, List<IRightsFilterCriteria> rightsFilters, List<IFilterCriteria> filters, IDataLayerObject currentObject, QueryDefinition parentQuery) throws BusViewException {
		super.handleView(query, rightsFilters, filters, currentObject, parentQuery);
		// ARCM add automatically condition client="_none_client_" for survey auditor role. This is nonsense! So, it must be fixed.
		ViewHandlerHelperBB.removeClient(rightsFilters);
	}

	@Override
	protected List<IEnumerationItem> getObjectSpecificRolesToCheckClientLevel() {
		return Collections.unmodifiableList(Arrays.asList(Enumerations.USERROLE_TYPE.SURVEYMANAGER, Enumerations.USERROLE_TYPE.SURVEYAUDITOR));
	}

	@Override
	protected List<IEnumerationItem> getObjectSpecificRolesForRestrictions() {
		// object specific survey manager is added automatically
		return Collections.unmodifiableList(Arrays.asList(Enumerations.USERROLE_TYPE.SURVEYMANAGER, Enumerations.USERROLE_TYPE.SURVEYAUDITOR));
	}

	@Override
	protected void addFilterRestrictions(Set<Long> orgunitIds, List<IFilterCriteria> groupFilters) {
		addFilterForSurveyGroup(groupFilters, orgunitIds, Enumerations.USERROLE_TYPE.SURVEYMANAGER, "manager_group_id");
	}
}