/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.component.issuemanagement.commands.atbreport;

import java.util.Date;

import com.idsscheer.webapps.arcm.bl.component.issuemanagement.re.IssueHelperBB;
import com.idsscheer.webapps.arcm.bl.exception.BLException;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandResult;
import com.idsscheer.webapps.arcm.bl.framework.command.ICommand;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IDateAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.ILongAttribute;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeTypeBB;

/**
 * <p>
 * This command is for the ATB Report - bb_execWorkedDays
 * Calculation formula: diffInDays (bb_execImplApprL2_date & bb_creator_date)
 * </p>
 * <p> This is a customization for <b><code>Banco do Brasil Project</code></b> </p>
 * <p>
 * command XML parameters (<b>bold</b>=mandatory): none
 * </p>
 * <p>
 * input work objects (<b>bold</b>=mandatory): none
 * </p>
 * <p>
 * output work objects (<b>bold</b>=mandatory): none
 * </p>
 * @author brmob
 * @since v20150601-CUST-ATBIntegration
 */
public class IssueCalculateWorkedDaysATBReportCommandBB implements ICommand {

	/* (non-Javadoc)
	 * @see com.idsscheer.webapps.arcm.bl.framework.command.ICommand#execute(com.idsscheer.webapps.arcm.bl.framework.command.CommandContext)
	 */
	@Override
	public CommandResult execute(CommandContext cc) throws BLException {
		IAppObj issue = cc.getChainContext().getTriggeringAppObj();
    	final IDateAttribute creatorDateAttr = issue.getAttribute(IIssueAttributeTypeBB.ATTR_CREATOR_DATE);
    	final Date creatorDate = creatorDateAttr.getPersistentRawValue();
    	final IDateAttribute execImplApprL1DateAttr = issue.getAttribute(IIssueAttributeTypeBB.ATTR_EXEC_IMPLAPPRL1_DATE);
    	final Date execImplApprL1Date = execImplApprL1DateAttr.getRawValue();
    	
    	final ILongAttribute workedDaysAttr = issue.getAttribute(IIssueAttributeTypeBB.ATTR_WORKED_DAYS);
    	workedDaysAttr.setRawValue(IssueHelperBB.calculateDifferenceInDaysBetweenDates(execImplApprL1Date, creatorDate) + 1);
    	
		return CommandResult.OK;
	}

}
