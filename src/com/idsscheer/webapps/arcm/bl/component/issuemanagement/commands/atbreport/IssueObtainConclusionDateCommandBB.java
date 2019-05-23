package com.idsscheer.webapps.arcm.bl.component.issuemanagement.commands.atbreport;

import java.util.Date;

import com.idsscheer.webapps.arcm.bl.exception.BLException;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandResult;
import com.idsscheer.webapps.arcm.bl.framework.command.ICommand;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IDateAttribute;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeTypeBB;

public class IssueObtainConclusionDateCommandBB implements ICommand {

	@Override
	public CommandResult execute(CommandContext cc) throws BLException {
		IAppObj issue = cc.getChainContext().getTriggeringAppObj();
    	final IDateAttribute  execImplApprDateAttr;
    	final Date execImplApprDate;
    	final IDateAttribute conclusionDateAttr = issue.getAttribute(IIssueAttributeTypeBB.ATTR_CONCLUSION_DATE);
    	Boolean isInjuredIssue = issue.getRawValue(IIssueAttributeTypeBB.ATTR_INJUREDISSUE);
    	if (isInjuredIssue != null && !isInjuredIssue) {
    	execImplApprDateAttr = issue.getAttribute(IIssueAttributeTypeBB.ATTR_EXEC_IMPLAPPRL1_DATE);
    	execImplApprDate = execImplApprDateAttr.getRawValue();
    	
    	}
    	else {
    		execImplApprDateAttr = issue.getAttribute(IIssueAttributeTypeBB.ATTR_EXEC_IMPLAPPRL2_DATE);
        	execImplApprDate = execImplApprDateAttr.getRawValue();
    	}
    	conclusionDateAttr.setRawValue(execImplApprDate);
		return CommandResult.OK;
    	
	}
}
