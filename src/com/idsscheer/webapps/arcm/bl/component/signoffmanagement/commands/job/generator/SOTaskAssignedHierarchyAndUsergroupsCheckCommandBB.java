package com.idsscheer.webapps.arcm.bl.component.signoffmanagement.commands.job.generator;

import java.util.List;
import java.util.Locale;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.common.support.AppObjUtility;
import com.idsscheer.webapps.arcm.bl.component.common.support.UsergroupUtilityBB;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandException;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IHierarchyAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IUsergroupAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IListAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IStringAttribute;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IHierarchyAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.ISotaskAttributeType;
import com.idsscheer.webapps.arcm.common.notification.INotification;
import com.idsscheer.webapps.arcm.common.notification.NotificationTypeEnum;
import com.idsscheer.webapps.arcm.common.notification.ResourceBundleNotification;
import com.idsscheer.webapps.arcm.common.resources.IResourceBundle;
import com.idsscheer.webapps.arcm.common.resources.ResourceBundleFactory;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;

public class SOTaskAssignedHierarchyAndUsergroupsCheckCommandBB extends SOTaskAssignedHierarchyAndUsergroupsCheckCommand {
    protected static final String KEY_WARN_GROUP_EMAIL_EMPTY = "job.generator.job.group.email.empty.WRG";

    @Override
    protected CheckResult checkGeneratorCondition(IAppObj appObj, CommandContext cc) throws CommandException {

        IUserContext userContext = cc.getChainContext().getUserContext();
        Locale locale = userContext.getLanguage();
        IResourceBundle resourceBundle = ResourceBundleFactory.getBundle(locale);
        IAppObj soTask = appObj;


        //----
        IListAttribute reviewerUserGroupAttr = soTask.getAttribute(ISotaskAttributeType.LIST_REVIEWER_GROUP);
        List<IAppObj> reviewerUserGroupElements = reviewerUserGroupAttr.getElements(userContext);
        //if no reviewer is attached
        if (reviewerUserGroupElements.isEmpty()) {
             INotification notification = new ResourceBundleNotification(NotificationTypeEnum.INFO,
                                                                        KEY_INFO_ASSIGNMENT,
                                                                        AppObjUtility.getLocalisedAppObjName(soTask, locale),
                                                                        resourceBundle.getString( ISotaskAttributeType.LIST_REVIEWER_GROUP.getPropertyKey() ) );
            return new CheckResult(notification, true);
        }
        
        IAppObj reviewerUserGroup = ARCMCollections.extractSingleEntry(reviewerUserGroupElements, false);
        
        //if usergroup of the reviewer is inactive
        if (reviewerUserGroup.getVersionData().isDeleted()) {
            INotification notification = new ResourceBundleNotification(NotificationTypeEnum.INFO,
                                                                        KEY_INFO_NOT_ACTIVE,
                                                                        AppObjUtility.getLocalisedAppObjName(soTask, locale),
                                                                        resourceBundle.getString( ISotaskAttributeType.LIST_REVIEWER_GROUP.getPropertyKey() ) );
            return new CheckResult(notification, true);
        }
        
        
        if (0 >= reviewerUserGroup.getAttribute(IUsergroupAppObj.LIST_GROUPMEMBERS).getSize()) {
        	
        	final IStringAttribute emailAttribute = UsergroupUtilityBB.getUserGroupEmailByOrgUnitStructure(reviewerUserGroup);
	        
        	if (emailAttribute.isEmpty()) {
	            final String reviewerUserGroupName = AppObjUtility.getLocalisedAppObjName((IUsergroupAppObj) reviewerUserGroupElements.get(0), locale, false);
	            final String reviwerUserGroupAttrType = resourceBundle.getString(reviewerUserGroupAttr.getAttributeType().getPropertyKey());
	            final String objectName = AppObjUtility.getLocalisedAppObjName(appObj, locale); 
            	final INotification notification =  new ResourceBundleNotification( NotificationTypeEnum.WARNING,
            			KEY_WARN_GROUP_EMAIL_EMPTY,
            			reviewerUserGroupName,
            			reviwerUserGroupAttrType,
                        objectName);       	
                return new CheckResult(notification, true);
            } 
        }


        //----
        //if the SOProcess is not assigned to a hierarchy element
        if (soTask.getAttribute(ISotaskAttributeType.LIST_ELEMENT).isEmpty()) {
            INotification notification = new ResourceBundleNotification(NotificationTypeEnum.INFO,
                                                                        KEY_INFO_ASSIGNMENT,
                                                                        AppObjUtility.getLocalisedAppObjName(soTask, locale),
                                                                        resourceBundle.getString( ISotaskAttributeType.LIST_ELEMENT.getPropertyKey() ) );
            return new CheckResult(notification, true);
        }

        IHierarchyAppObj hierarchyElement = (IHierarchyAppObj) soTask.getAttribute(ISotaskAttributeType.LIST_ELEMENT).getElements(userContext).get(0);
        if ( !hierarchyElement.getAttribute(IHierarchyAttributeType.ATTR_SIGNOFF).getRawValue() ) {
            INotification notification = new ResourceBundleNotification(NotificationTypeEnum.INFO,
                                                                        KEY_INFO_NO_SIGNOFF_RELEVANT_HIERARCHY,
                                                                        AppObjUtility.getLocalisedAppObjName(soTask, locale),
                                                                        resourceBundle.getString( ISotaskAttributeType.LIST_ELEMENT.getPropertyKey() ) );
            return new CheckResult(notification, true);
        }

        
        //----
        //if no SOOwner is assigned
        //TA NR: 196582
        if (hierarchyElement.getAttribute(IHierarchyAttributeType.LIST_SO_OWNER).isEmpty()) {
            INotification notification = new ResourceBundleNotification(NotificationTypeEnum.INFO,
                                                                        KEY_INFO_ASSIGNMENT,
                                                                        AppObjUtility.getLocalisedAppObjName(soTask, locale),
                                                                        resourceBundle.getString( IHierarchyAttributeType.LIST_SO_OWNER.getPropertyKey() ) );
            return new CheckResult(notification, true);
        }

        //if SOOwner is inactive
        if (hierarchyElement.getAttribute(IHierarchyAttributeType.LIST_SO_OWNER)
                .getElements(userContext).get(0).getVersionData().isDeleted() ) {
            INotification notification = new ResourceBundleNotification(NotificationTypeEnum.INFO,
                                                                        KEY_INFO_NOT_ACTIVE,
                                                                        AppObjUtility.getLocalisedAppObjName(soTask, locale),
                                                                        resourceBundle.getString( IHierarchyAttributeType.LIST_SO_OWNER.getPropertyKey() ) );
            return new CheckResult(notification, true);
        }
        IListAttribute ownerUserGroupAttr = hierarchyElement.getAttribute(IHierarchyAttributeType.LIST_SO_OWNER);
        List<IAppObj> ownerUserGroupElements = ownerUserGroupAttr.getElements(userContext);
        IAppObj ownerUserGroup = ARCMCollections.extractSingleEntry(ownerUserGroupElements, false);
        
        if (0 >= ownerUserGroup.getAttribute(IUsergroupAppObj.LIST_GROUPMEMBERS).getSize()) {
            
        	final IStringAttribute emailAttribute = UsergroupUtilityBB.getUserGroupEmailByOrgUnitStructure(ownerUserGroup);
	        
        	if (emailAttribute.isEmpty()) {
	            final String ownerUserGroupName = AppObjUtility.getLocalisedAppObjName((IUsergroupAppObj) ownerUserGroupElements.get(0), locale, false);
	            final String ownerUserGroupAttrType = resourceBundle.getString(ownerUserGroupAttr.getAttributeType().getPropertyKey());
	            final String objectName = AppObjUtility.getLocalisedAppObjName(appObj, locale); 
            	final INotification notification =  new ResourceBundleNotification( NotificationTypeEnum.WARNING,
            			KEY_WARN_GROUP_EMAIL_EMPTY,
            			ownerUserGroupName,
            			ownerUserGroupAttrType,
                        objectName);       	
                return new CheckResult(notification, true);
            }    
        }
        
        return null;
    }

}