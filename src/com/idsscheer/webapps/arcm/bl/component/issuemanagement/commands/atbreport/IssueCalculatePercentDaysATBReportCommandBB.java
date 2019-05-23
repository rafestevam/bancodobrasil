/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.component.issuemanagement.commands.atbreport;

import com.idsscheer.webapps.arcm.bl.exception.BLException;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandResult;
import com.idsscheer.webapps.arcm.bl.framework.command.ICommand;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IDoubleAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.ILongAttribute;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeTypeBB;

/**
 * <p>
 * This command is for the ATB Report - bb_execPercentDays
 * Calculation formula: divisionInDays (bb_execWorkedDays & bb_execPlannedDays)
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
public class IssueCalculatePercentDaysATBReportCommandBB implements ICommand {

	/* (non-Javadoc)
	 * @see com.idsscheer.webapps.arcm.bl.framework.command.ICommand#execute(com.idsscheer.webapps.arcm.bl.framework.command.CommandContext)
	 */
	@Override
	public CommandResult execute(CommandContext cc) throws BLException {
		IAppObj issue = cc.getChainContext().getTriggeringAppObj();
		final ILongAttribute workedDaysAttr = issue.getAttribute(IIssueAttributeTypeBB.ATTR_WORKED_DAYS);
		final Long workedDays = workedDaysAttr.getRawValue();
		final ILongAttribute plannedDaysAttr = issue.getAttribute(IIssueAttributeTypeBB.ATTR_PLANNED_DAYS);
		final Long plannedDays = plannedDaysAttr.getRawValue();
		
		Double percentDays = (workedDays.doubleValue() / plannedDays.doubleValue()) * 100;
		final IDoubleAttribute percentDaysAttr = issue.getAttribute(IIssueAttributeTypeBB.ATTR_PERCENT_DAYS);
		percentDaysAttr.setRawValue(percentDays);
		
		return CommandResult.OK;
	}
}
