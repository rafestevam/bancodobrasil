package com.idsscheer.webapps.arcm.bl.component.issuemanagement.commands.atbreport;

import com.idsscheer.webapps.arcm.bl.exception.BLException;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandResult;
import com.idsscheer.webapps.arcm.bl.framework.command.ICommand;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IDateAttribute;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeTypeBB;

public class IssueResetConclusionDateCommandBB implements ICommand {

	@Override
	public CommandResult execute(CommandContext cc) throws BLException {
		IAppObj issue = cc.getChainContext().getTriggeringAppObj();
    	final IDateAttribute conclusionDateAttr = issue.getAttribute(IIssueAttributeTypeBB.ATTR_CONCLUSION_DATE);
    	conclusionDateAttr.setRawValue(null);
		return CommandResult.OK;
    	
	}
}
