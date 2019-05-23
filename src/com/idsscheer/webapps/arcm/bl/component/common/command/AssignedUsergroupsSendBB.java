package com.idsscheer.webapps.arcm.bl.component.common.command;

import java.util.List;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.component.common.support.UsergroupUtilityBB;
import com.idsscheer.webapps.arcm.bl.exception.BLException;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandResult;
import com.idsscheer.webapps.arcm.bl.framework.command.ICommand;
import com.idsscheer.webapps.arcm.bl.framework.message.IMessage;
import com.idsscheer.webapps.arcm.bl.framework.message.MessageFactory;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IListAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IStringAttribute;
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRecurringAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IUsergroupAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.MessageTemplateEnumerationItem;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IListAttributeType;

public class AssignedUsergroupsSendBB implements ICommand {

	public CommandResult execute(final CommandContext cc) throws BLException {
        IUserContext userContext = cc.getChainContext().getUserContext();
        final IAppObj appObj = cc.getChainContext().getTriggeringAppObj();

    	if (emptyUserGroup(appObj, userContext, IRecurringAttributeType.LIST_OWNER_GROUP)) {
    		sendEmail(cc, appObj, userContext, IRecurringAttributeType.LIST_OWNER_GROUP);
    	}	
    	if (emptyUserGroup(appObj, userContext, IRecurringAttributeType.LIST_REVIEWER_GROUP)) {
    		sendEmail(cc, appObj, userContext, IRecurringAttributeType.LIST_REVIEWER_GROUP);
    	}
        return new CommandResult(CommandResult.STATUS.OK);
	}
	
	protected boolean emptyUserGroup(final IAppObj p_appObj, final IUserContext p_userContext, final IListAttributeType p_attrType) {
		final IListAttribute attribute = p_appObj.getAttribute(p_attrType);
		final List<IAppObj> assignedGroups = attribute.getElements(p_userContext);

		if(assignedGroups.isEmpty()) {
        	//it cannot happen because of previous chain steps        
			return false;
        } else {
            final IAppObj userGroup = assignedGroups.get(0);
	        final IListAttribute userGroupAttribute = userGroup.getAttribute(IUsergroupAttributeType.LIST_GROUPMEMBERS);
        	final List<IAppObj> members = userGroupAttribute.getElements(p_userContext);
	        boolean atLeastOneUserActive = false;
            for (final IAppObj user : members) {
                if(!user.getVersionData().isDeleted()) {
                    atLeastOneUserActive = true;
                    break;
                }
            }
            if (!atLeastOneUserActive) {
            	return true;
            }
        }
		return false;
	}	
	
	protected void sendEmail(final CommandContext cc, final IAppObj p_appObj, final IUserContext p_userContext, final IListAttributeType p_attrType) {
		final IListAttribute attribute = p_appObj.getAttribute(p_attrType);
		final List<IAppObj> assignedGroups = attribute.getElements(p_userContext);
		final IAppObj userGroup = assignedGroups.get(0);
        
		final IStringAttribute userGroupEmail = UsergroupUtilityBB.getUserGroupEmailByOrgUnitStructure(userGroup);
        
		IMessage message = MessageFactory.createMessage(userGroupEmail.getPersistentRawValue(), getMessageTemplateEnumerationItem(), p_userContext.getUser(), p_appObj);
	    cc.getChainContext().send(message);
	}	
	
	protected MessageTemplateEnumerationItem getMessageTemplateEnumerationItem() {
		return EnumerationsBB.INITIATORS_BB.GENERATORJOB_EMPTY_GROUP;
	}
}