package com.idsscheer.webapps.arcm.bl.component.signoffmanagement.commands.job.generator;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.component.common.command.AssignedUsergroupsSendBB;
import com.idsscheer.webapps.arcm.bl.exception.BLException;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandResult;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IHierarchyAppObj;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IHierarchyAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRecurringAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.ISotaskAttributeType;

public class SOTaskAssignedUsergroupsSendBB extends AssignedUsergroupsSendBB{

	public CommandResult execute(final CommandContext cc) throws BLException {
        IUserContext userContext = cc.getChainContext().getUserContext();
        final IAppObj appObj = cc.getChainContext().getTriggeringAppObj();
        
        IHierarchyAppObj hierarchyElement = (IHierarchyAppObj) appObj.getAttribute(ISotaskAttributeType.LIST_ELEMENT).getElements(userContext).get(0);
        
    	if (emptyUserGroup(hierarchyElement, userContext, IHierarchyAttributeType.LIST_SO_OWNER)) {
    		sendEmail(cc, hierarchyElement, userContext, IHierarchyAttributeType.LIST_SO_OWNER);
    	}	
    	if (emptyUserGroup(appObj, userContext, IRecurringAttributeType.LIST_REVIEWER_GROUP)) {
    		sendEmail(cc, appObj, userContext, IRecurringAttributeType.LIST_REVIEWER_GROUP);
    	}
        return new CommandResult(CommandResult.STATUS.OK);
	}
	
}
