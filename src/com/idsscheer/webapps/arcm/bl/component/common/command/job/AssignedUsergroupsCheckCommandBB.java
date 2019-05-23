package com.idsscheer.webapps.arcm.bl.component.common.command.job;

import java.util.List;
import java.util.Locale;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.common.support.AppObjUtility;
import com.idsscheer.webapps.arcm.bl.component.common.support.UsergroupUtilityBB;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IListAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IStringAttribute;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IUsergroupAttributeType;
import com.idsscheer.webapps.arcm.common.notification.INotification;
import com.idsscheer.webapps.arcm.common.notification.NotificationTypeEnum;
import com.idsscheer.webapps.arcm.common.notification.ResourceBundleNotification;
import com.idsscheer.webapps.arcm.common.resources.IResourceBundle;
import com.idsscheer.webapps.arcm.common.resources.ResourceBundleFactory;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IListAttributeType;

public class AssignedUsergroupsCheckCommandBB extends AssignedUsergroupsCheckCommand {
    protected static final String KEY_WARN_GROUP_EMAIL_EMPTY = "job.generator.job.group.email.empty.WRG";

	protected CheckResult checkUserGroup(final IAppObj p_appObj, final IUserContext p_userContext, final IListAttributeType p_attrType, Locale locale) {
		final IListAttribute attribute = p_appObj.getAttribute(p_attrType);
		final IResourceBundle resourceBundle = ResourceBundleFactory.getBundle(p_userContext.getLanguage());

		final List<IAppObj> assignedGroups = attribute.getElements(p_userContext);
        if(assignedGroups.isEmpty()) {
            final INotification notification =  new ResourceBundleNotification( NotificationTypeEnum.INFO,
                                                                                KEY_WARN_NOT_ASSIGNED,
                                                                                resourceBundle.getString(attribute.getAttributeType().getPropertyKey()),
                                                                                AppObjUtility.getLocalisedAppObjName(p_appObj, locale) );
            return new CheckResult(notification, true);

        } else if (assignedGroups.get(0).getVersionData().isDeleted()) {
            final INotification notification =  new ResourceBundleNotification( NotificationTypeEnum.INFO,
                                                                                KEY_WARN_GROUP_NOT_ACTIVE,
                                                                                AppObjUtility.getLocalisedAppObjName(assignedGroups.get(0), locale, false),
                                                                                resourceBundle.getString( attribute.getAttributeType().getPropertyKey() ),
                                                                                AppObjUtility.getLocalisedAppObjName(p_appObj, locale));
            return new CheckResult(notification, true);

        } else {
            final IAppObj userGroup = assignedGroups.get(0);
            final IStringAttribute emailAttribute = UsergroupUtilityBB.getUserGroupEmailByOrgUnitStructure(userGroup);
            final String usergroupName = AppObjUtility.getLocalisedAppObjName(assignedGroups.get(0), locale, false);
            final String attributeType = resourceBundle.getString( attribute.getAttributeType().getPropertyKey());
            final String objectName = AppObjUtility.getLocalisedAppObjName(p_appObj, locale); 
	        final IListAttribute userGroupAttribute = userGroup.getAttribute(IUsergroupAttributeType.LIST_GROUPMEMBERS);
	        
	        if(userGroupAttribute.isEmpty()) {
	            if (emailAttribute.isEmpty()) {
	            	final INotification notification =  new ResourceBundleNotification( NotificationTypeEnum.WARNING,
	            			KEY_WARN_GROUP_EMAIL_EMPTY,
	            			usergroupName,
	                        attributeType,
	                        objectName);       	
	                return new CheckResult(notification, true);
	            }    
	        }

	        final List<IAppObj> members = userGroupAttribute.getElements(p_userContext);
	        boolean atLeastOneUserActive = false;
            for (final IAppObj user : members) {
                if(!user.getVersionData().isDeleted()) {
                    atLeastOneUserActive = true;
                }
                if(atLeastOneUserActive) 
                	break;
            }
            if (!atLeastOneUserActive) {
	            if (emailAttribute.isEmpty()) {
	            	final INotification notification =  new ResourceBundleNotification( NotificationTypeEnum.WARNING,
	            			KEY_WARN_GROUP_EMAIL_EMPTY,
	            			usergroupName,
	                        attributeType,
	                        objectName);       	
	                return new CheckResult(notification, true);
	            }    
            }
        }
		return null;
	}
}	