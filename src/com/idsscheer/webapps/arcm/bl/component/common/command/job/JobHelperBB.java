package com.idsscheer.webapps.arcm.bl.component.common.command.job;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IUsergroupAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.EnumerationItem;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;

public class JobHelperBB {
    private static Log log = LogFactory.getLog(JobHelperBB.class);

    public static Set<EnumerationItem> getRoleLevels(final IEnumerationItem role, final IUserContext userContext) {
    	// it has to be done in this way due to fullUserContext - standard methods cannot be used
    	Set<EnumerationItem> result = new HashSet<EnumerationItem>();
    	List<IAppObj> usergroupList = userContext.getUser().getParentAppObjs(userContext, IUsergroupAttributeType.LIST_GROUPMEMBERS);
    	for (IAppObj usergroup : usergroupList) {
    		List<EnumerationItem> roleEnumItemList = usergroup.getAttribute(IUsergroupAttributeType.ATTR_ROLE).getRawValue();
    		if (roleEnumItemList != null && roleEnumItemList.size() > 0) {
    			EnumerationItem item = roleEnumItemList.get(0);
    			if (item.equals(role)) {
    				List<EnumerationItem> roleLevelEnumItemList = usergroup.getAttribute(IUsergroupAttributeType.ATTR_ROLELEVEL).getRawValue();
    				if (roleLevelEnumItemList != null && roleLevelEnumItemList.size() > 0) {
    	    			EnumerationItem itemRoleLevel = roleLevelEnumItemList.get(0);
    	    			result.add(itemRoleLevel);
    				}	
    			}
    		}
		}
    	
    	return result;
    }
    
    public static List<Long> getRoleUsergroupsIds(final IEnumerationItem role, final IEnumerationItem level, final IUserContext userContext) {
    	// it has to be done in this way due to fullUserContext - standard methods cannot be used
    	List<Long> result = new ArrayList<Long>();
    	
    	List<IAppObj> usergroupList = userContext.getUser().getParentAppObjs(userContext, IUsergroupAttributeType.LIST_GROUPMEMBERS);
    	for (IAppObj usergroup : usergroupList) {    		
    		List<EnumerationItem> roleEnumItemList = usergroup.getAttribute(IUsergroupAttributeType.ATTR_ROLE).getRawValue();
    		if (roleEnumItemList != null && roleEnumItemList.size() > 0) {
    			EnumerationItem item = roleEnumItemList.get(0);
    			if (item.equals(role)) {
    				if (usergroup.getAttribute(IUsergroupAttributeType.ATTR_ROLELEVEL).hasItem(level)) {
    					result.add(usergroup.getObjectId());
    				}
    			}
    		}
		}
    	
    	return result;
    }
}