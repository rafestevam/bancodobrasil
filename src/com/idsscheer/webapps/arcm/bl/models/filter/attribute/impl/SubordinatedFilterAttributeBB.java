package com.idsscheer.webapps.arcm.bl.models.filter.attribute.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.exception.BlConfigurationException;
import com.idsscheer.webapps.arcm.bl.framework.tree.ClientTreeLogic;
import com.idsscheer.webapps.arcm.bl.framework.tree.ClientTreeNodeObjectIDFilter;
import com.idsscheer.webapps.arcm.bl.framework.tree.ClientTreeUtility;
import com.idsscheer.webapps.arcm.bl.framework.tree.IClientTree;
import com.idsscheer.webapps.arcm.bl.framework.tree.IClientTreeNode;
import com.idsscheer.webapps.arcm.bl.framework.tree.IClientTreeNodeFilter;
import com.idsscheer.webapps.arcm.bl.models.filter.attribute.IFilterPersistence;
import com.idsscheer.webapps.arcm.bl.models.filter.attribute.IPredefinedFilterValueFilterAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IClientSignAttribute;
import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IHierarchyAttributeType;
import com.idsscheer.webapps.arcm.common.support.ConfigParameter;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.common.util.ovid.OVIDFactory;
import com.idsscheer.webapps.arcm.common.util.ovid.OVIDUtility;
import com.idsscheer.webapps.arcm.config.metadata.filter.IFilterAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.rights.roles.ObjectRight;
import com.idsscheer.webapps.arcm.config.metadata.rights.roles.RoleLevel;
import com.idsscheer.webapps.arcm.dl.framework.IFilterCriteria;

public class SubordinatedFilterAttributeBB extends AbstractSubordinatedFilterAttribute implements IPredefinedFilterValueFilterAttribute, IFilterPersistence
{
	protected static final Log log = LogFactory.getLog(SubordinatedFilterAttribute.class);
	@ConfigParameter(value="isRiskAssessmentFilter", optional=true)
	private static final String PARAMETER_IS_RISK_ASSESSMENT_FILTER = "isRiskAssessmentFilter";

	public SubordinatedFilterAttributeBB(IUserContext userContext, IFilterAttributeType filterAttributeConfig)
	{
		super(userContext, filterAttributeConfig);
		this.attributeName = this.filterAttributeMetadata.getId();
	}

	public List<IFilterCriteria> getFilterCriteria()
	{
		if (!ObjectType.HIERARCHY.equals(this.parentAppObj.getObjectType())) {
			throw new BlConfigurationException("filters*.xml", "SubordinatedFilterAttribute can only be used in module hierarchy");
		}

		ArrayList<IFilterCriteria> list = new ArrayList(1);
		List<IOVID> hierarchyIds = new ArrayList();
		List<IOVID> usergroup = new ArrayList();

		IClientSignAttribute clientSignAttribute = this.parentAppObj.getAttribute(IHierarchyAttributeType.ATTR_RELATED_CLIENT);
		IClientTree clientTree = ClientTreeUtility.getHierarchyClientTree();

		IOVID currentHierarchyId = this.parentAppObj.getVersionData().getOVID();
		IClientTreeNodeFilter nodeIDFilter = new ClientTreeNodeObjectIDFilter(currentHierarchyId, ClientTreeNodeObjectIDFilter.Mode.ID_ONLY);

		IClientTreeNode relevantRoot = ClientTreeLogic.getInstance().findFirst(clientTree.getRoot(), nodeIDFilter);
		String sign = clientSignAttribute.getUiValue(Locale.ENGLISH);

		String isRiskAssessmentFilterParam = getParameter("isRiskAssessmentFilter");
		boolean isRiskAssessmentFilter = (isRiskAssessmentFilterParam != null) && (isRiskAssessmentFilterParam.equalsIgnoreCase("true"));

		boolean isClientSpecific = (this.userContext.getUserRights().hasRight(ObjectRight.READ, ObjectType.TESTCASE, sign)) || (this.userContext.getUserRights().hasRight(ObjectRight.READ, ObjectType.RISKASSESSMENT, sign));



		if ("1".equals(this.value))
		{
			if (isClientSpecific) {
				hierarchyIds = ClientTreeUtility.getHierarchyIds(relevantRoot);
				list.add(filterFactory.getSimpleFilterCriteria(getAliasIDList(), OVIDUtility.toSimpleIds(hierarchyIds)));
			}
			else
			{
				hierarchyIds = ClientTreeUtility.getAuditorAssignedHierarchyIds(relevantRoot, this.userContext);

				hierarchyIds.addAll(ClientTreeUtility.getSignOffOwnerAssignedHierarchyIds(relevantRoot, this.userContext));
				if (!hierarchyIds.isEmpty()) {
					list.add(filterFactory.getSimpleFilterCriteria(getAliasIDList(), OVIDUtility.toSimpleIds(hierarchyIds)));
				}

				if (hierarchyIds.isEmpty())
				{
					boolean usergroupAdded = false;
					if (isTesterHierarchy(this.parentAppObj)) {
						hierarchyIds = ClientTreeUtility.getTesterAssignedHierarchyIds(relevantRoot, this.userContext);
						usergroup = new ArrayList();
						usergroup.addAll(ClientTreeUtility.getAllTesterUsergroupIds(clientTree, relevantRoot, this.userContext));
						if (!hierarchyIds.isEmpty()) {
							list.add(filterFactory.getSimpleFilterCriteria(getAliasIDList(), OVIDUtility.toSimpleIds(hierarchyIds)));
						}
					} else {
						usergroup = new ArrayList();
						usergroup.addAll(this.userContext.getUserRelations().getGroupsIDs());
					}


					usergroup.addAll(this.userContext.getUserRelations().getGroupsIDs(Enumerations.USERROLE_TYPE.TESTREVIEWER, new RoleLevel[0]));
					if (!usergroup.isEmpty()) {
						list.add(filterFactory.getSimpleFilterCriteria("responsible_2", OVIDUtility.toSimpleIds(usergroup)));
						usergroupAdded = true;
					}

					hierarchyIds = ClientTreeUtility.getHierarchyIds(relevantRoot);
					usergroup = new ArrayList();
					usergroup.addAll(this.userContext.getUserRelations().getGroupsIDs(Enumerations.USERROLE_TYPE.RISKOWNER, new RoleLevel[0]));
					usergroup.addAll(this.userContext.getUserRelations().getGroupsIDs(Enumerations.USERROLE_TYPE.RISKREVIEWER, new RoleLevel[0]));
					List<IOVID> riskManagerIDs = Collections.emptyList();
					if (this.userContext.getUserRights().hasRole(Enumerations.USERROLE_TYPE.RISKMANAGER, Enumerations.USERROLE_LEVEL.OBJECT)) {
						riskManagerIDs = this.userContext.getUserRelations().getGroupsIDs(Enumerations.USERROLE_TYPE.RISKMANAGER, new RoleLevel[0]);
						if (!isRiskAssessmentFilter) {
							usergroup.addAll(riskManagerIDs);
							riskManagerIDs.clear();
						}
					}


					if (!usergroup.isEmpty()) {
						list.add(filterFactory.getSimpleFilterCriteria("responsible_riskgroups", OVIDUtility.toSimpleIds(usergroup)));
						usergroupAdded = true;
					}




					if ((this.userContext.getUserRights().hasRole(Enumerations.USERROLE_TYPE.CONTROLMANAGER, Enumerations.USERROLE_LEVEL.OBJECT)) || ((isRiskAssessmentFilter) && (!riskManagerIDs.isEmpty())))
					{
						usergroup = new ArrayList();
						usergroup.addAll(riskManagerIDs);
						usergroup.addAll(this.userContext.getUserRelations().getGroupsIDs(Enumerations.USERROLE_TYPE.CONTROLMANAGER, new RoleLevel[] { RoleLevel.O }));
						list.add(filterFactory.getSimpleFilterCriteria("responsible_controlgroups", OVIDUtility.toSimpleIds(usergroup)));
						usergroupAdded = true;
					}

					if (usergroupAdded) {
						list.add(filterFactory.getSimpleFilterCriteria(getAliasID(), OVIDUtility.toSimpleIds(hierarchyIds)));
					}

					if (list.isEmpty())
					{
						hierarchyIds.clear();
						hierarchyIds.add(OVIDFactory.getOVID(-1L));
						list.add(filterFactory.getSimpleFilterCriteria(getAliasID(), OVIDUtility.toSimpleIds(hierarchyIds)));
					}

				}
			}
		}
		else if ("0".equals(this.value))
		{

			boolean hierarchyNodeAdded = false;
			List<IOVID> auditorOrSOOwnerHierarchyIds = ClientTreeUtility.getAuditorAssignedHierarchyIds(relevantRoot, this.userContext);
			auditorOrSOOwnerHierarchyIds.addAll(ClientTreeUtility.getSignOffOwnerAssignedHierarchyIds(relevantRoot, this.userContext));
			if ((isClientSpecific) || (!auditorOrSOOwnerHierarchyIds.isEmpty()))
			{

				hierarchyIds.add(currentHierarchyId);
			}
			if (!hierarchyIds.isEmpty()) {
				list.add(filterFactory.getSimpleFilterCriteria(getAliasID(), OVIDUtility.toSimpleIds(hierarchyIds)));
				hierarchyNodeAdded = true;
			}



			if (!hierarchyNodeAdded) {
				hierarchyIds.clear();
				boolean usergroupAdded = false;


				usergroup = new ArrayList();
				usergroup.addAll(this.userContext.getUserRelations().getGroupsIDs(Enumerations.USERROLE_TYPE.TESTREVIEWER, new RoleLevel[0]));


				usergroup.addAll(ClientTreeUtility.getAllTesterUsergroupIds(clientTree, relevantRoot, this.userContext));


				if (ClientTreeUtility.getAllTesterUsergroupIds(clientTree, relevantRoot, this.userContext).size() == 0) {
					usergroup.addAll(this.userContext.getUserRelations().getGroupsIDs(Enumerations.USERROLE_TYPE.TESTER, new RoleLevel[0]));
				}


				if (!usergroup.isEmpty()) {
					list.add(filterFactory.getSimpleFilterCriteria("responsible_2", OVIDUtility.toSimpleIds(usergroup)));
					usergroupAdded = true;
				}


				usergroup = new ArrayList();
				usergroup.addAll(this.userContext.getUserRelations().getGroupsIDs(Enumerations.USERROLE_TYPE.RISKREVIEWER, new RoleLevel[0]));
				usergroup.addAll(this.userContext.getUserRelations().getGroupsIDs(Enumerations.USERROLE_TYPE.RISKOWNER, new RoleLevel[0]));
				List<IOVID> riskManagerIDs = Collections.emptyList();
				if (this.userContext.getUserRights().hasRole(Enumerations.USERROLE_TYPE.RISKMANAGER, Enumerations.USERROLE_LEVEL.OBJECT)) {
					riskManagerIDs = this.userContext.getUserRelations().getGroupsIDs(Enumerations.USERROLE_TYPE.RISKMANAGER, new RoleLevel[0]);
					if (!isRiskAssessmentFilter) {
						usergroup.addAll(riskManagerIDs);
						riskManagerIDs.clear();
					}
				}


				if (!usergroup.isEmpty()) {
					list.add(filterFactory.getSimpleFilterCriteria("responsible_riskgroups", OVIDUtility.toSimpleIds(usergroup)));
					usergroupAdded = true;
				}



				if ((this.userContext.getUserRights().hasRole(Enumerations.USERROLE_TYPE.CONTROLMANAGER, Enumerations.USERROLE_LEVEL.OBJECT)) || ((isRiskAssessmentFilter) && (!riskManagerIDs.isEmpty())))
				{
					usergroup = new ArrayList();
					usergroup.addAll(riskManagerIDs);
					usergroup.addAll(this.userContext.getUserRelations().getGroupsIDs(Enumerations.USERROLE_TYPE.CONTROLMANAGER, new RoleLevel[0]));
					if (!usergroup.isEmpty()) {
						list.add(filterFactory.getSimpleFilterCriteria("responsible_controlgroups", OVIDUtility.toSimpleIds(usergroup)));
						usergroupAdded = true;
					}
				}

				if (usergroupAdded)
				{
					hierarchyIds.add(currentHierarchyId);
				}
				else {
					hierarchyIds.add(OVIDFactory.getOVID(-1L));
				}
				list.add(filterFactory.getSimpleFilterCriteria(getAliasID(), OVIDUtility.toSimpleIds(hierarchyIds)));
			}
		}

		return list;
	}

	protected boolean isTesterHierarchy(IAppObj hierarchyObject) {
		List<com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem> typEnumItems = hierarchyObject.getAttribute(IHierarchyAttributeType.ATTR_TYPE).getRawValue();
		return (typEnumItems != null) && (typEnumItems.contains(Enumerations.HIERARCHY_TYPE.TESTER));
	}

	protected String getAliasID(){
		return "control_id";
	}
	
	protected String getAliasIDList(){
		return "control_idlist";
	}
}
