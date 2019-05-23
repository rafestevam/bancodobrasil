package com.idsscheer.webapps.arcm.bl.models.filter.attribute.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.component.administration.tree.HierarchyTreeNodeModel;
import com.idsscheer.webapps.arcm.bl.framework.tree.ClientTreeLogic;
import com.idsscheer.webapps.arcm.bl.framework.tree.ClientTreeNodeObjectIDFilter;
import com.idsscheer.webapps.arcm.bl.framework.tree.ClientTreeUtility;
import com.idsscheer.webapps.arcm.bl.framework.tree.IClientTree;
import com.idsscheer.webapps.arcm.bl.framework.tree.IClientTreeNode;
import com.idsscheer.webapps.arcm.bl.framework.tree.IClientTreeNodeFilter;
import com.idsscheer.webapps.arcm.bl.models.filter.attribute.FilterAttributeInitParameter;
import com.idsscheer.webapps.arcm.bl.models.filter.attribute.IPredefinedFilterValue;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IClientSignAttribute;
import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations;
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IHierarchyAttributeType;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.common.util.ovid.OVIDFactory;
import com.idsscheer.webapps.arcm.common.util.ovid.OVIDUtility;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.config.metadata.filter.IFilterAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.rights.roles.ObjectRight;
import com.idsscheer.webapps.arcm.config.metadata.rights.roles.RoleLevel;
import com.idsscheer.webapps.arcm.dl.framework.IFilterCriteria;

public class MasterDataSubordinatedFilterAttributeBB extends FilterAttribute implements com.idsscheer.webapps.arcm.bl.models.filter.attribute.IPredefinedFilterValueFilterAttribute, com.idsscheer.webapps.arcm.bl.models.filter.attribute.IFilterPersistence
{
	protected static final Log log = org.apache.commons.logging.LogFactory.getLog(SubordinatedFilterAttribute.class);

	@com.idsscheer.webapps.arcm.common.support.ConfigParameter(value="isRiskFilter", optional=true)
	private static final String PARAMETER_IS_RISK_FILTER = "isRiskFilter";

	protected final String EXCLUDE = "0";
	protected final String INCLUDE = "1";
	protected IAppObj parentAppObj;
	protected String attributeName;
	protected String value;
	protected String persistentValue;
	protected ArrayList<IPredefinedFilterValue> predefinedValues;

	public MasterDataSubordinatedFilterAttributeBB(IUserContext userContext, IFilterAttributeType filterAttributeConfig)
	{
		super(userContext, filterAttributeConfig);
		this.attributeName = this.filterAttributeMetadata.getId();
	}

	public void init(Map<FilterAttributeInitParameter, Object> parameterMap) {
		super.init(parameterMap);
		this.parentAppObj = ((IAppObj)parameterMap.get(FilterAttributeInitParameter.ListParentAppObj));
		
	}

	protected void resetFilterAttribute() {
		this.value = this.persistentValue;
	}

	public boolean isValid(Locale locale) {
		return true;
	}

	public void setFilterValue(Locale locale, String viewColumnName, String... values) {
		if ((values == null) || (values.length == 0)) {
			this.value = "0";
		}
		else {
			this.value = values[0];
		}
	}

	public Object getRawValue(String viewColumnName) {
		return this.value;
	}

	public Object getUiValue(String viewColumnName, Locale locale) {
		return getUiValue(locale);
	}

	public String getUiValue(Locale locale) {
		if (this.predefinedValues == null) {
			getPredefinedValues();
		}
		if (this.value.equals("0")) {
			return ((IPredefinedFilterValue)this.predefinedValues.get(0)).getValue(locale);
		}
		return ((IPredefinedFilterValue)this.predefinedValues.get(1)).getValue(locale);
	}

	public List<IFilterCriteria> getFilterCriteria()
	{
		ArrayList<IFilterCriteria> list = new ArrayList<IFilterCriteria>(1);
		List<IOVID> hierarchyIds = new ArrayList<IOVID>();
		
		if (!ObjectType.HIERARCHY.equals(this.parentAppObj.getObjectType()))
		{
			log.error("SubordinatedFilterAttributeOverMasterData can only used in Hierarchy Explorer!", new Throwable());
			return java.util.Collections.emptyList();
		}

		IClientSignAttribute clientSignAttribute = this.parentAppObj.getAttribute(IHierarchyAttributeType.ATTR_RELATED_CLIENT);
		IClientTree clientTree = ClientTreeUtility.getHierarchyClientTree();

		IOVID currentHierarchyId = this.parentAppObj.getVersionData().getOVID();
		IClientTreeNodeFilter nodeIDFilter = new ClientTreeNodeObjectIDFilter(currentHierarchyId, ClientTreeNodeObjectIDFilter.Mode.ID_ONLY);

		IClientTreeNode relevantRoot = ClientTreeLogic.getInstance().findFirst(clientTree.getRoot(), nodeIDFilter);
		String sign = clientSignAttribute.getUiValue(Locale.ENGLISH);

		IEnumerationItem hierarchyType = (IEnumerationItem)relevantRoot.getModel().getValue(HierarchyTreeNodeModel.TYPE);

		String isRiskFilterParam = getParameter("isRiskFilter");
		boolean isRiskFilter = (isRiskFilterParam != null) && (isRiskFilterParam.equalsIgnoreCase("true"));

		boolean isClientSpecific = (this.userContext.getUserRights().hasRight(ObjectRight.READ, ObjectType.RISK, sign)) || (this.userContext.getUserRights().hasRight(ObjectRight.READ, ObjectType.CONTROL, sign)) || (this.userContext.getUserRights().hasRight(ObjectRight.READ, ObjectType.TESTDEFINITION, sign)) || ((this.userContext.getUserRights().hasRole(sign, Enumerations.USERROLE_TYPE.LOSSOWNER)) && (isRiskFilter)) || ((this.userContext.getUserRights().hasRole(sign, Enumerations.USERROLE_TYPE.LOSSREVIEWER)) && (isRiskFilter));






		if ("1".equals(this.value))
		{
			if (isClientSpecific) {
				hierarchyIds = ClientTreeUtility.getHierarchyIds(relevantRoot);
				for (String s : getIDListStrings(hierarchyType)) {
					list.add(filterFactory.getSimpleFilterCriteria(s, OVIDUtility.toSimpleIds(hierarchyIds)));
				}

			}
			else
			{
				hierarchyIds = ClientTreeUtility.getAuditorAssignedHierarchyIds(relevantRoot, this.userContext);

				hierarchyIds.addAll(ClientTreeUtility.getSignOffOwnerAssignedHierarchyIds(relevantRoot, this.userContext));
				if (!hierarchyIds.isEmpty()) {
					for (String s : getIDListStrings(hierarchyType)) {
						list.add(filterFactory.getSimpleFilterCriteria(s, OVIDUtility.toSimpleIds(hierarchyIds)));
					}
				}

				boolean usergroupadded = false;
				if (hierarchyIds.isEmpty())
				{
					if (isTesterHierarchy(this.parentAppObj)) {
						hierarchyIds = ClientTreeUtility.getTesterAssignedHierarchyIds(relevantRoot, this.userContext);
						List<IOVID> usergroup = new ArrayList<IOVID>();
						usergroup.addAll(ClientTreeUtility.getAllTesterUsergroupIds(clientTree, relevantRoot, this.userContext));
						for (String s : getIDListStrings(hierarchyType)) {
							list.add(filterFactory.getSimpleFilterCriteria(s, OVIDUtility.toSimpleIds(hierarchyIds)));
						}
						if (!usergroup.isEmpty()) {
							list.add(filterFactory.getSimpleFilterCriteria("responsible_2", OVIDUtility.toSimpleIds(usergroup)));
							usergroupadded = true;
						}
					} else {
						List<IOVID> usergroup = new ArrayList<IOVID>();
						usergroup.addAll(this.userContext.getUserRelations().getGroupsIDs(Enumerations.USERROLE_TYPE.TESTER, new RoleLevel[0]));
						if (!usergroup.isEmpty()) {
							list.add(filterFactory.getSimpleFilterCriteria("responsible_2", OVIDUtility.toSimpleIds(usergroup)));
							usergroupadded = true;
						}
					}
				}
				if (hierarchyIds.isEmpty()) {
					hierarchyIds = ClientTreeUtility.getHierarchyIds(relevantRoot);
					List<IOVID> usergroup = new ArrayList<IOVID>();
					usergroup.addAll(this.userContext.getUserRelations().getGroupsIDs(Enumerations.USERROLE_TYPE.TESTREVIEWER, new RoleLevel[0]));
					if (!usergroup.isEmpty()) {
						usergroupadded = true;
						list.add(filterFactory.getSimpleFilterCriteria("responsible_reviewer", OVIDUtility.toSimpleIds(usergroup)));
					}


					usergroup = new ArrayList<IOVID>();
					usergroup.addAll(this.userContext.getUserRelations().getGroupsIDs(Enumerations.USERROLE_TYPE.RISKREVIEWER, new RoleLevel[0]));
					usergroup.addAll(this.userContext.getUserRelations().getGroupsIDs(Enumerations.USERROLE_TYPE.RISKOWNER, new RoleLevel[0]));
					if (!usergroup.isEmpty()) {
						usergroupadded = true;
						list.add(filterFactory.getSimpleFilterCriteria("responsible_opriskgroups", OVIDUtility.toSimpleIds(usergroup)));
					}
					usergroup = new ArrayList<IOVID>();
					if (this.userContext.getUserRights().hasRole(Enumerations.USERROLE_TYPE.RISKMANAGER, Enumerations.USERROLE_LEVEL.OBJECT)) {
						usergroup.addAll(this.userContext.getUserRelations().getGroupsIDs(Enumerations.USERROLE_TYPE.RISKMANAGER, new RoleLevel[0]));
					}
					if (!usergroup.isEmpty()) {
						usergroupadded = true;
						list.add(filterFactory.getSimpleFilterCriteria("responsible_riskgroups", OVIDUtility.toSimpleIds(usergroup)));
					}


					if (this.userContext.getUserRights().hasRole(Enumerations.USERROLE_TYPE.CONTROLMANAGER, Enumerations.USERROLE_LEVEL.OBJECT)) {
						usergroup = new ArrayList<IOVID>();
						usergroup.addAll(this.userContext.getUserRelations().getGroupsIDs(Enumerations.USERROLE_TYPE.CONTROLMANAGER, new RoleLevel[0]));
						if (!usergroup.isEmpty()) {
							usergroupadded = true;
							list.add(filterFactory.getSimpleFilterCriteria("responsible_controlgroups", OVIDUtility.toSimpleIds(usergroup)));
						}
					}

					if (usergroupadded) {
						for (String s : getExcludingFilterIDStrings(hierarchyType)) {
							list.add(filterFactory.getSimpleFilterCriteria(s, OVIDUtility.toSimpleIds(hierarchyIds)));
						}
					}
				}
			}
		}
		else if ("0".equals(this.value))
		{
			if (isClientSpecific) {
				List<IOVID> tempIds = new ArrayList<IOVID>();
				tempIds.add(currentHierarchyId);
				for (String s : getIDListStrings(hierarchyType)) {
					list.add(filterFactory.getSimpleFilterCriteria(s, OVIDUtility.toSimpleIds(tempIds)));
				}
			}
			else {
				boolean usergroupAdded = false;

				List<IOVID> usergroup = new ArrayList<IOVID>();
				usergroup.addAll(this.userContext.getUserRelations().getGroupsIDs(Enumerations.USERROLE_TYPE.TESTREVIEWER, new RoleLevel[0]));
				if (!usergroup.isEmpty()) {
					usergroupAdded = true;
					list.add(filterFactory.getSimpleFilterCriteria("responsible_reviewer", OVIDUtility.toSimpleIds(usergroup)));
				}


				usergroup = new ArrayList<IOVID>();
				usergroup.addAll(ClientTreeUtility.getAllTesterUsergroupIds(clientTree, relevantRoot, this.userContext));
				if (!usergroup.isEmpty()) {
					usergroupAdded = true;
					list.add(filterFactory.getSimpleFilterCriteria("responsible_2", OVIDUtility.toSimpleIds(usergroup)));
				}
				else
				{
					usergroup = new ArrayList<IOVID>();
					usergroup.addAll(this.userContext.getUserRelations().getGroupsIDs(Enumerations.USERROLE_TYPE.TESTER, new RoleLevel[0]));
					if (!usergroup.isEmpty()) {
						usergroupAdded = true;
						list.add(filterFactory.getSimpleFilterCriteria("responsible_2", OVIDUtility.toSimpleIds(usergroup)));
					}
				}



				usergroup = new ArrayList<IOVID>();
				usergroup.addAll(this.userContext.getUserRelations().getGroupsIDs(Enumerations.USERROLE_TYPE.RISKREVIEWER, new RoleLevel[0]));
				usergroup.addAll(this.userContext.getUserRelations().getGroupsIDs(Enumerations.USERROLE_TYPE.RISKOWNER, new RoleLevel[0]));
				if (!usergroup.isEmpty()) {
					usergroupAdded = true;
					list.add(filterFactory.getSimpleFilterCriteria("responsible_opriskgroups", OVIDUtility.toSimpleIds(usergroup)));
				}

				usergroup = new ArrayList<IOVID>();
				if (this.userContext.getUserRights().hasRole(Enumerations.USERROLE_TYPE.RISKMANAGER, Enumerations.USERROLE_LEVEL.OBJECT)) {
					usergroup.addAll(this.userContext.getUserRelations().getGroupsIDs(Enumerations.USERROLE_TYPE.RISKMANAGER, new RoleLevel[0]));
				}
				if (!usergroup.isEmpty()) {
					usergroupAdded = true;
					list.add(filterFactory.getSimpleFilterCriteria("responsible_riskgroups", OVIDUtility.toSimpleIds(usergroup)));
				}


				if (this.userContext.getUserRights().hasRole(Enumerations.USERROLE_TYPE.CONTROLMANAGER, Enumerations.USERROLE_LEVEL.OBJECT)) {
					usergroup = new ArrayList<IOVID>();
					usergroup.addAll(this.userContext.getUserRelations().getGroupsIDs(Enumerations.USERROLE_TYPE.CONTROLMANAGER, new RoleLevel[0]));
					if (!usergroup.isEmpty()) {
						usergroupAdded = true;
						list.add(filterFactory.getSimpleFilterCriteria("responsible_controlgroups", OVIDUtility.toSimpleIds(usergroup)));
					}
				}

				if (usergroupAdded) {
					List<IOVID> tempIds = new ArrayList<IOVID>();
					tempIds.add(currentHierarchyId);
					for (String s : getExcludingFilterIDStrings(hierarchyType)) {
						list.add(filterFactory.getSimpleFilterCriteria(s, OVIDUtility.toSimpleIds(tempIds)));
					}
				}


				List<IOVID> auditorOrSOOwnerHierarchyIds = ClientTreeUtility.getAuditorAssignedHierarchyIds(relevantRoot, this.userContext);
				auditorOrSOOwnerHierarchyIds.addAll(ClientTreeUtility.getSignOffOwnerAssignedHierarchyIds(relevantRoot, this.userContext));
				if ((!isClientSpecific) && (auditorOrSOOwnerHierarchyIds.isEmpty())) {
					if (!usergroupAdded) {
						hierarchyIds.add(OVIDFactory.getOVID(-1L));
						for (String s : getIDStrings(hierarchyType)) {
							list.add(filterFactory.getSimpleFilterCriteria(s, OVIDUtility.toSimpleIds(hierarchyIds)));
						}
					}
				} else {
					List<IOVID> temp = new ArrayList<IOVID>(1);
					temp.add(currentHierarchyId);

					if (OVIDUtility.hasIntersection(auditorOrSOOwnerHierarchyIds, temp, true)) {
						hierarchyIds.add(currentHierarchyId);
						for (String s : getIDStrings(hierarchyType)) {
							list.add(filterFactory.getSimpleFilterCriteria(s, OVIDUtility.toSimpleIds(hierarchyIds)));
						}
					}
				}
			}
		}


		if (list.isEmpty()) {
			for (String s : getIDStrings(hierarchyType)) {
				list.add(filterFactory.getSimpleFilterCriteria(s, Long.valueOf(-1L)));
			}
		}

		return list;
	}


	protected String[] getIDListStrings(IEnumerationItem hierarchyType)
	{
		if (Enumerations.HIERARCHY_TYPE.ORGUNIT.equals(hierarchyType))
			return new String[] { "idlist_orgH" };
		if (Enumerations.HIERARCHY_TYPE.TESTER.equals(hierarchyType))
			return new String[] { "idlist_testerH" };
		if (Enumerations.HIERARCHY_TYPE.PROCESS.equals(hierarchyType))
			return new String[] { "idlist_controlHPr" };
		if(EnumerationsBB.HIERACHY_TYPE_BB.BB_FATOR_DE_RISCO.equals(hierarchyType))
			return new String[] { "idlist_riskHFR" };
		if (Enumerations.HIERARCHY_TYPE.FINANCIALACCOUNT.equals(hierarchyType)) {
			return new String[] { "idlist_riskHFA" };
		}if (EnumerationsBB.HIERACHY_TYPE_BB.BB_PRODUCT.equals(hierarchyType)) {
			return new String[] { "idlist_processHpro" };
		}
		return new String[] { "idlist_riskHOR" };
	}



	protected String[] getIDStrings(IEnumerationItem hierarchyType)
	{
		if (Enumerations.HIERARCHY_TYPE.ORGUNIT.equals(hierarchyType))
			return new String[] { "id_orgH" };
		if (Enumerations.HIERARCHY_TYPE.TESTER.equals(hierarchyType))
			return new String[] { "id_testerH" };
		if (Enumerations.HIERARCHY_TYPE.PROCESS.equals(hierarchyType))
			return new String[] { "id_controlHPr" };
		if(EnumerationsBB.HIERACHY_TYPE_BB.BB_FATOR_DE_RISCO.equals(hierarchyType))
			return new String[] { "id_riskHFR" };
		if (Enumerations.HIERARCHY_TYPE.FINANCIALACCOUNT.equals(hierarchyType)) {
			return new String[] { "id_riskHFA" };
		}if (EnumerationsBB.HIERACHY_TYPE_BB.BB_PRODUCT.equals(hierarchyType)) {
			return new String[] { "id_processHpro" };
		}
		return new String[] { "id_riskHOR" };
	}

	protected String[] getExcludingFilterIDStrings(IEnumerationItem hierarchyType)
	{
		if (Enumerations.HIERARCHY_TYPE.ORGUNIT.equals(hierarchyType))
			return new String[] { "idforexcludefilter_orgH" };
		if (Enumerations.HIERARCHY_TYPE.TESTER.equals(hierarchyType))
			return new String[] { "idforexcludefilter_testerH" };
		if (Enumerations.HIERARCHY_TYPE.PROCESS.equals(hierarchyType))
			return new String[] { "idforexcludefilter_controlHPr" };
		if(EnumerationsBB.HIERACHY_TYPE_BB.BB_FATOR_DE_RISCO.equals(hierarchyType))
			return new String[] { "idforexcludefilter_riskHFR" };
		if (Enumerations.HIERARCHY_TYPE.FINANCIALACCOUNT.equals(hierarchyType)) {
			return new String[] { "idforexcludefilter_riskHFA" };
		}if (EnumerationsBB.HIERACHY_TYPE_BB.BB_PRODUCT.equals(hierarchyType)) {
			return new String[] { "idforexcludefilter_processHpro" };
		}
		return new String[] { "idforexcludefilter_riskHOR" };
	}

	protected boolean isTesterHierarchy(IAppObj hierarchyObject)
	{
		List<IEnumerationItem> typEnumItems = hierarchyObject.getAttribute(IHierarchyAttributeType.ATTR_TYPE).getRawValue();
		return (typEnumItems != null) && (typEnumItems.contains(Enumerations.HIERARCHY_TYPE.TESTER));
	}

	public boolean isEmpty() {
		return false;
	}

	public boolean isDirty() {
		if (this.value == null) {
			return this.persistentValue == null;
		}
		return !this.value.equals(this.persistentValue);
	}

	public List<IPredefinedFilterValue> getPredefinedValues() {
		if (this.predefinedValues == null) {
			this.predefinedValues = new ArrayList<IPredefinedFilterValue>(2);

			this.predefinedValues.add(new PredefinedFilterValue("0", "filter.subordinate.exclude.DBI", false));
			this.predefinedValues.add(new PredefinedFilterValue("1", "filter.subordinate.include.DBI", false));
		}
		return this.predefinedValues;
	}

	public void initialiseFromMap(Map<String, String> attributeValueMap) {
		String mapValue = (String)attributeValueMap.get(this.attributeName);
		if (mapValue == null) {
			mapValue = "0";
		}
		this.value = mapValue;
		this.persistentValue = mapValue;
	}

	public Map<String, String> convertToMap() {
		Map<String, String> attributeValueMap = new java.util.HashMap(1);
		attributeValueMap.put(this.attributeName, this.value);
		return attributeValueMap;
	}
	
}
