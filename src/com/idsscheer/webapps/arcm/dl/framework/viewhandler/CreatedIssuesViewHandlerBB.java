package com.idsscheer.webapps.arcm.dl.framework.viewhandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IUsergroupAppObj;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IUsergroupAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.dl.framework.BusViewException;
import com.idsscheer.webapps.arcm.dl.framework.DataLayerComparator;
import com.idsscheer.webapps.arcm.dl.framework.IDataLayerObject;
import com.idsscheer.webapps.arcm.dl.framework.IFilterCriteria;
import com.idsscheer.webapps.arcm.dl.framework.IRightsFilterCriteria;
import com.idsscheer.webapps.arcm.dl.framework.dllogic.FilterFactory;
import com.idsscheer.webapps.arcm.dl.framework.dllogic.QueryDefinition;
import com.idsscheer.webapps.arcm.dl.framework.dllogic.SimpleFilterCriteria;
import com.idsscheer.webapps.arcm.ui.framework.common.UIEnvironmentManager;

public class CreatedIssuesViewHandlerBB implements IViewHandler {
	
	protected static final Log log = LogFactory.getLog(CreatedIssuesViewHandler.class);

	public CreatedIssuesViewHandlerBB() {
	}

	public void handleView(QueryDefinition query, List<IRightsFilterCriteria> rightsFilters, List<IFilterCriteria> filters, IDataLayerObject currentObject, QueryDefinition parentQuery) throws BusViewException {

		IUserContext userContext = (UIEnvironmentManager.get()).getUserContext();

		List<IUsergroupAppObj> lstGroup = userContext.getUserRelations().getGroups();
		List<IFilterCriteria> groupsFilters = new ArrayList<IFilterCriteria>();
		String sFilterAttribute = "";
		Long lGroupID = 0L;

		Map<String, String> mapRoles = new HashMap<String, String>();
		mapRoles.put("issuemgmtapproverl1", "mgmtApproverL1Group_ID");
		mapRoles.put("issuemgmtapproverl2", "mgmtApproverL2Group_ID");
		mapRoles.put("issueimplapproverl1", "implApproverL1Group_ID");
		mapRoles.put("issueimplapproverl2", "implApproverL2Group_ID");
		mapRoles.put("issuetechapproverl1", "bb_techApproverL1Group_ID");
		mapRoles.put("issuetechapproverl2", "bb_techApproverL2Group_ID");
		mapRoles.put("issuecreator", "bb_creatorsGroup_ID");

		for (IUsergroupAppObj userGroup : lstGroup) {
			String sGroupRole = ((IEnumerationItem) userGroup.getAttribute(IUsergroupAttributeType.ATTR_ROLE).getRawValue().get(0)).getId();
			if (mapRoles.containsKey(sGroupRole)) {

				sFilterAttribute = mapRoles.get(sGroupRole);
				lGroupID = userGroup.getObjectId();
				groupsFilters.add(new SimpleFilterCriteria(sFilterAttribute, DataLayerComparator.EQUAL, lGroupID));
			}
		}

		filters.add(new FilterFactory().or(groupsFilters));

	}

}
