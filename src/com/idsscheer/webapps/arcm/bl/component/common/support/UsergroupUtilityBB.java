/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.component.common.support;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.idsscheer.webapps.arcm.bl.authentication.context.ContextFactory;
import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.common.support.UsergroupUtility;
import com.idsscheer.webapps.arcm.bl.dataaccess.query.IViewQuery;
import com.idsscheer.webapps.arcm.bl.dataaccess.query.QueryFactory;
import com.idsscheer.webapps.arcm.bl.exception.RightException;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IHierarchyAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IUserAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IUsergroupAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IViewObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IEnumAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IListAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IStringAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.FacadeFactory;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjIterator;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjQuery;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.QueryJoin;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.QueryRestriction;
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IHierarchyAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IUsergroupAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IAttributeType;

/**
 * @author Administrator
 *
 */
public class UsergroupUtilityBB extends UsergroupUtility {
	
	
	@SuppressWarnings("rawtypes")
	public static <T extends IAttribute, Y extends IAttributeType> T getRelatedOrgUnitAttrByUserGroup(IAppObj userGroup, Y attributeId){
		if (!(userGroup instanceof IUsergroupAppObj)) {
			throw new IllegalArgumentException("Only IUsergroupAppObj are supported for this method...");
		}
		
		IUserContext ctx = ContextFactory.getFullReadAccessUserContext(Locale.ENGLISH);
		
		IListAttribute relatedOrgUnitListAttr = userGroup.getAttribute(IUsergroupAttributeTypeBB.LIST_BB_ORGUNIT);
		List<IAppObj> relatedOrgUnits = relatedOrgUnitListAttr.getElements(ctx);
		
		IAppObj relatedOrgUnit = ARCMCollections.extractSingleEntry(relatedOrgUnits, true);
		
		Y attributeType = relatedOrgUnit.getAttributeType(attributeId.getId());
		T attribute = relatedOrgUnit.getAttribute(attributeType);
		return attribute;
	}
	
	/**
	 * This method get the email attribute from the provided user group.
	 * In the case the user group email attribute is empty, there will be a recursive procedure
	 * to find an email attribute in the parents of the user group provided as argument.
	 * 
	 * @param userGroup
	 * @return orgUnitEmail
	 */
	public static IStringAttribute getUserGroupEmailByOrgUnitStructure(IAppObj userGroup){
		
		if (!(userGroup instanceof IUsergroupAppObj)) {
			throw new IllegalArgumentException("Only IUsergroupAppObj are supported for this method...");
		}
		
		IStringAttribute emailAttr = userGroup.getAttribute(IUsergroupAttributeTypeBB.ATTR_BB_EMAIL);
		IUserContext ctx = ContextFactory.getFullReadAccessUserContext(Locale.ENGLISH);
		
		if (emailAttr.isEmpty()) {
			IListAttribute orgUnitAttr = userGroup.getAttribute(IUsergroupAttributeTypeBB.LIST_BB_ORGUNIT);
			List<IAppObj> orgUnits = orgUnitAttr.getElements(ctx);
			
			IAppObj orgUnit = ARCMCollections.extractSingleEntry(orgUnits, true);
			emailAttr = getOrganizationalUnitEmailAttribute(orgUnit, ctx);
		}
		
		return emailAttr;
	}
	
	private static IStringAttribute getOrganizationalUnitEmailAttribute(IAppObj organizationalUnit, IUserContext userContext){
		IStringAttribute orgUnitEmailAttr = organizationalUnit.getAttribute(IHierarchyAttributeTypeBB.ATTR_EMAIL_UOR);
		
		if (orgUnitEmailAttr.isEmpty()) {
			IAppObj parentOrgUnit = organizationalUnit.getParentAppObj(userContext, IHierarchyAttributeTypeBB.LIST_CHILDREN);
			orgUnitEmailAttr = getOrganizationalUnitEmailAttribute(parentOrgUnit, userContext);
		}
		
		return orgUnitEmailAttr;
		
	}
	
	public static final IAppObj getIssueCreatorUserGroup(IUserContext userContext){
		IUserAppObj user = userContext.getUser();
		IAppObj issueCreatorUserGroup = null;
		
		IUserContext fullGrantContext = ContextFactory.getFullReadAccessUserContext(userContext.getLanguage());
		List<IAppObj> userGroups = user.getUsergroups(fullGrantContext);
		
		for (IAppObj userGroup : userGroups) {
			IEnumAttribute roleAttr = userGroup.getAttribute(IUsergroupAttributeTypeBB.ATTR_ROLE);
			IEnumerationItem role = ARCMCollections.extractSingleEntry(roleAttr.getRawValue(), true);
			
			if (role.equals(EnumerationsBB.USERROLE_TYPE_BB.ISSUECREATOR)) {
				issueCreatorUserGroup = userGroup;
				break;
			}
		}
		
		return issueCreatorUserGroup; 
	}

	public static Set<Long> getUsergroupIdsForOrgunit(final long bbOrgunitId, final IEnumerationItem role, final IEnumerationItem level) throws RightException {
		Set<Long> result = new HashSet<Long>();
		
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("ou_obj_id",bbOrgunitId);
        filterMap.put("ug_role",role.getValue());
        filterMap.put("ug_rolelevel",level.getValue());
        IViewQuery viewQuery = null;
        try {
        	IUserContext fullReadContext = ContextFactory.getFullReadAccessUserContext(Locale.ENGLISH);
			viewQuery = QueryFactory.createQuery(fullReadContext, "usergroupForRoleAndOrgunit", null, filterMap);
			long size = viewQuery.getSize();
	        if(size>0) {
	            Iterator<IViewObj> objIterator = viewQuery.getResultIterator();
	            if (objIterator.hasNext()) {
	                IViewObj viewObj = objIterator.next();
	                long usergroupId = (long)viewObj.getRawValue("ug_obj_id");
	                result.add(usergroupId);
	            }
	        }
        }
	    finally {
	    	viewQuery.release();
	    }
        return result;
	}
	
	public static Set<IAppObj> getUserGroupsByOrganizationalUnit(final IAppObj organizationalUnit, final IEnumerationItem role, IEnumerationItem rolelevel){
		Set<IAppObj> result = new HashSet<IAppObj>();
		IAppObjQuery userGroupQuery = null;
		
		try {
			IUserContext fullReadContext = ContextFactory.getFullReadAccessUserContext(Locale.ENGLISH);
			IAppObjFacade userGroupFacade = FacadeFactory.getInstance().getAppObjFacade(fullReadContext, ObjectType.USERGROUP);
			
			userGroupQuery = userGroupFacade.createQuery();
			userGroupQuery.addRestriction(
					QueryJoin.left(IUsergroupAttributeTypeBB.LIST_BB_ORGUNIT,
							QueryRestriction.eq(IHierarchyAppObj.ATTR_OBJ_ID, organizationalUnit.getObjectId())
					));
			userGroupQuery.addRestriction(QueryRestriction.eq(IUsergroupAttributeTypeBB.ATTR_ROLE, role));
			userGroupQuery.addRestriction(QueryRestriction.eq(IUsergroupAttributeTypeBB.ATTR_ROLELEVEL, rolelevel));
			
			IAppObjIterator userGroupIterator = userGroupQuery.getResultIterator();
			CollectionUtils.addAll(result, userGroupIterator);
			
			
		} finally {
			userGroupQuery.release();
		}
		
        return result;
		
	}

}
