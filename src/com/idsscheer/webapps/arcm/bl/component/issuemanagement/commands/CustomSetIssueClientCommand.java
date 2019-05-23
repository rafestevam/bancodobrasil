package com.idsscheer.webapps.arcm.bl.component.issuemanagement.commands;

import com.idsscheer.webapps.arcm.bl.authentication.context.ServerContext;
import com.idsscheer.webapps.arcm.bl.component.common.command.CreateObjectDialogCommand;
import com.idsscheer.webapps.arcm.bl.exception.BLException;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandResult;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IClientSignAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IObjectType;

/**
 * overridden to show client selection dialog for issues
 */
public class CustomSetIssueClientCommand extends CreateObjectDialogCommand {

	
	@Override
	public CommandResult execute(CommandContext cc) throws BLException {
		IObjectType type = cc.getChainContext().getTriggeringAppObj().getObjectType();

		if (!type.isClientDependent() && !type.equals(ObjectType.ISSUE)) {
		  return new CommandResult(CommandResult.STATUS.OK);
		}
		
		return super.execute(cc);
	}
	
	@Override
	protected CommandResult handleDialogFinished(CommandContext cc) throws BLException {
		String clientSign = (String)cc.getChainContext().getContextAttribute("client_sign");
		IAppObj appObj = cc.getChainContext().getTriggeringAppObj();
		IClientSignAttributeType type = (IClientSignAttributeType)appObj.getAttributeType("client_sign");
		appObj.getAttribute(type).setRawValue(ServerContext.getInstance().getClient(clientSign));
		return new CommandResult(CommandResult.STATUS.OK);
	}

}
