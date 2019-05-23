package com.idsscheer.webapps.arcm.dl.framework.viewhandler;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations;
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsBB;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.dl.framework.IFilterCriteria;


public class IssueObjectRoleViewHandlerBB extends AbstractObjectRoleViewHandlerBB{

	@Override
	protected List<IEnumerationItem> getObjectSpecificRolesToCheckClientLevel() {
		return Collections.unmodifiableList(Arrays.asList(Enumerations.USERROLE_TYPE.ISSUEMANAGER, Enumerations.USERROLE_TYPE.ISSUEAUDITOR));
	}

	@Override
	protected List<IEnumerationItem> getObjectSpecificRolesForRestrictions() {
		return Collections.unmodifiableList(Arrays.asList(Enumerations.USERROLE_TYPE.ISSUEMANAGER, Enumerations.USERROLE_TYPE.ISSUEAUDITOR));
	}

	@Override
	protected void addFilterRestrictions(Set<Long> orgunitIds, List<IFilterCriteria> groupFilters) {
		addFilterForIssueGroup(groupFilters, orgunitIds, EnumerationsBB.USERROLE_TYPE_BB.ISSUEMGMTAPPROVER_L1, "mgmtApproverL1Group_ID");
		addFilterForIssueGroup(groupFilters, orgunitIds, EnumerationsBB.USERROLE_TYPE_BB.ISSUEMGMTAPPROVER_L2, "mgmtApproverL2Group_ID");
		addFilterForIssueGroup(groupFilters, orgunitIds, EnumerationsBB.USERROLE_TYPE_BB.ISSUEIMPLAPPROVER_L1, "implApproverL1Group_ID");
		addFilterForIssueGroup(groupFilters, orgunitIds, EnumerationsBB.USERROLE_TYPE_BB.ISSUEIMPLAPPROVER_L2, "implApproverL2Group_ID");
		addFilterForIssueGroup(groupFilters, orgunitIds, EnumerationsBB.USERROLE_TYPE_BB.ISSUETECHAPPROVER_L1, "bb_techApproverL1Group_ID");
		addFilterForIssueGroup(groupFilters, orgunitIds, EnumerationsBB.USERROLE_TYPE_BB.ISSUETECHAPPROVER_L2, "bb_techApproverL2Group_ID");
	}
}