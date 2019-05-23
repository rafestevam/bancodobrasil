package com.idsscheer.webapps.arcm.dl.framework.viewhandler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.component.common.support.ArcmHelperBB;
import com.idsscheer.webapps.arcm.bl.component.common.support.UsergroupUtilityBB;
import com.idsscheer.webapps.arcm.bl.exception.RightException;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IUsergroupAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IEnumAttribute;
import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IUsergroupAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IUsergroupAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.config.metadata.rights.roles.RoleLevel;
import com.idsscheer.webapps.arcm.dl.framework.BusViewException;
import com.idsscheer.webapps.arcm.dl.framework.DataLayerComparator;
import com.idsscheer.webapps.arcm.dl.framework.IDataLayerObject;
import com.idsscheer.webapps.arcm.dl.framework.IFilterCriteria;
import com.idsscheer.webapps.arcm.dl.framework.IRightsFilterCriteria;
import com.idsscheer.webapps.arcm.dl.framework.dllogic.QueryDefinition;
import com.idsscheer.webapps.arcm.dl.framework.dllogic.SimpleFilterCriteria;
import com.idsscheer.webapps.arcm.ndl.PersistenceAPI;
import com.idsscheer.webapps.arcm.ui.framework.common.UIEnvironmentManager;


public abstract class AbstractObjectRoleViewHandlerBB implements IViewHandler{
	protected IUserContext userContext;

	@Override
	public void handleView(QueryDefinition query, List<IRightsFilterCriteria> rightsFilters, List<IFilterCriteria> filters, IDataLayerObject currentObject, QueryDefinition parentQuery) throws BusViewException {
		userContext = UIEnvironmentManager.get().getUserContext();
		handleObjectSpecificRole(rightsFilters, filters);		
	}

	protected void handleObjectSpecificRole(List<IRightsFilterCriteria> rightsFilters, List<IFilterCriteria> filters) {
		boolean clientRole = false;
		List<IEnumerationItem> roles = getObjectSpecificRolesToCheckClientLevel();
		for (IEnumerationItem role : roles) {
			for (IRightsFilterCriteria filterCriteria : rightsFilters) {
				if (role.getValue().equalsIgnoreCase(filterCriteria.getRole())) {
					if (Enumerations.USERROLE_LEVEL.OBJECT.equals(filterCriteria.getRoleLevel())) {
						
					}
					else {
						clientRole = true;
						break;
					}
				}
			}
			if (clientRole) {
				break;
			}
		}	
			
		if (!clientRole && userHasObjectSpecificRolesForRestrictions()) {
			Set<Long> orgunitIds = getOrgunitIds();
            addFilterRestrictions(orgunitIds, filters);
		}
	}
	
	protected void addFilterForIssueGroup(List<IFilterCriteria> filters, final Set<Long> orgunitIds, final IEnumerationItem groupRole, final String groupViewId) {
		Set<Long> usergroupIds = new HashSet<Long>();
		for (Long orgunitId : orgunitIds) {
			try {
				usergroupIds = UsergroupUtilityBB.getUsergroupIdsForOrgunit(orgunitId, groupRole, Enumerations.USERROLE_LEVEL.OBJECT);
			} catch (RightException e) {
				e.printStackTrace();
			}
		}
		if (!usergroupIds.isEmpty()) {
			List<Long> usergroupIdsList = new ArrayList<Long>(usergroupIds);
			SimpleFilterCriteria groupFilter = new SimpleFilterCriteria(groupViewId, DataLayerComparator.IN, usergroupIdsList);
            filters.add(groupFilter);
		}	

	}
	
	protected void addFilterForSurveyGroup(List<IFilterCriteria> filters, final Set<Long> orgunitIds, final IEnumerationItem groupRole, final String groupViewId) {
		Set<Long> usergroupIds = new HashSet<Long>();
		for (Long orgunitId : orgunitIds) {
			try {
				usergroupIds.addAll(UsergroupUtilityBB.getUsergroupIdsForOrgunit(orgunitId, groupRole, Enumerations.USERROLE_LEVEL.OBJECT));
			} catch (RightException e) {
				e.printStackTrace();
			}
		}
		if (!usergroupIds.isEmpty()) {
			List<Long> usergroupIdsList = new ArrayList<Long>(usergroupIds);
			IFilterCriteria groupFilter = PersistenceAPI.getFilterFactory().getSimpleFilterCriteria(groupViewId, DataLayerComparator.IN, usergroupIdsList);
            filters.add(groupFilter);
		}	

	}

	protected void addFilterForSignoffGroup(List<IFilterCriteria> filters, final Set<Long> orgunitIds, final IEnumerationItem groupRole, final String groupViewId) {
		Set<Long> usergroupIds = new HashSet<Long>();
		for (Long orgunitId : orgunitIds) {
			try {
				usergroupIds = UsergroupUtilityBB.getUsergroupIdsForOrgunit(orgunitId, groupRole, Enumerations.USERROLE_LEVEL.OBJECT);
			} catch (RightException e) {
				e.printStackTrace();
			}
		}
		if (!usergroupIds.isEmpty()) {
			
			List<IUsergroupAppObj> usergroups = userContext.getUserRelations().getGroups();
			
			for (IUsergroupAppObj usergroup : usergroups) {
				
				IEnumAttribute roleAttr = usergroup.getAttribute(IUsergroupAttributeType.ATTR_ROLE);
				IEnumerationItem role = ARCMCollections.extractSingleEntry(roleAttr.getRawValue(), true);
				
				if (groupRole.equals(role)) {
					long orgunitId = ArcmHelperBB.getListAttributeId(usergroup, IUsergroupAttributeTypeBB.LIST_BB_ORGUNIT);
					if (orgunitId != -1) {
						List<Long> usergroupIdsList = new ArrayList<Long>(usergroupIds);
						SimpleFilterCriteria groupFilter = new SimpleFilterCriteria(groupViewId, DataLayerComparator.IN, usergroupIdsList);
			            filters.add(groupFilter);
					}
					break;
				}
			}
		}	
	}
	
	protected Set<Long> getOrgunitIds() {
		Set<Long> result = new HashSet<Long>();
		
		List<IUsergroupAppObj> usergroups = userContext.getUserRelations().getGroups();
		for (IUsergroupAppObj usergroup : usergroups) {
			List<IEnumerationItem> rolesToRestrict = getObjectSpecificRolesForRestrictions();
			IEnumAttribute roleAttr = usergroup.getAttribute(IUsergroupAttributeType.ATTR_ROLE);
			IEnumerationItem role = ARCMCollections.extractSingleEntry(roleAttr.getRawValue(), true);	
			if (rolesToRestrict.contains(role)) {
				long orgunitId = ArcmHelperBB.getListAttributeId(usergroup, IUsergroupAttributeTypeBB.LIST_BB_ORGUNIT);
				if (orgunitId != -1) {
					result.add(orgunitId);
				}	
			}
		}
		return result;
	}

	protected boolean userHasObjectSpecificRolesForRestrictions() {
		List<IEnumerationItem> rolesToRestrict = getObjectSpecificRolesForRestrictions();
		for (IEnumerationItem roleToRestrict : rolesToRestrict) {
			if (!userContext.getUserRelations().getUserRoles(roleToRestrict, RoleLevel.O).isEmpty()) {
				return true;
			}
			
		}
		return false;
	}
	
	abstract protected List<IEnumerationItem> getObjectSpecificRolesToCheckClientLevel();
	abstract protected List<IEnumerationItem> getObjectSpecificRolesForRestrictions();
	abstract protected void addFilterRestrictions(Set<Long> orgunitIds, List<IFilterCriteria> groupFilters);

}
