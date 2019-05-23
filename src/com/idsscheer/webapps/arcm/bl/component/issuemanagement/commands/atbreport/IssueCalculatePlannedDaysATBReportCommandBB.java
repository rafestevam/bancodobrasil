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
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeTypeBB;

/**
 * <p>
 * This command is for the ATB Report - bb_execPlannedDays
 * Calculation formula: diffInDays (bb_firstPlannedEndDate & bb_creator_date)
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
public class IssueCalculatePlannedDaysATBReportCommandBB implements ICommand {

	/* (non-Javadoc)
	 * @see com.idsscheer.webapps.arcm.bl.framework.command.ICommand#execute(com.idsscheer.webapps.arcm.bl.framework.command.CommandContext)
	 */
	@Override
	public CommandResult execute(CommandContext cc) throws BLException {
    	IAppObj issue = cc.getChainContext().getTriggeringAppObj();
    	
    	final IDateAttribute startDateAttr = issue.getAttribute(IIssueAttributeTypeBB.ATTR_START_DATE);
    	issue.getAttribute(IIssueAttributeTypeBB.ATTR_CREATOR_DATE).setRawValue(startDateAttr.getRawValue());
    	final IDateAttribute creatorDateAttr = issue.getAttribute(IIssueAttributeTypeBB.ATTR_CREATOR_DATE);
    	final Date creatorDate = creatorDateAttr.getRawValue();
    	//final IDateAttribute firstPlannedEndDateAttr = issue.getAttribute(IIssueAttributeTypeBB.ATTR_FIRST_PLANNED_ENDDATE);
    	final IDateAttribute firstPlannedEndDateAttr = issue.getAttribute(IIssueAttributeType.ATTR_PLANNEDENDDATE);
    	Date firstPlannedEndDate = firstPlannedEndDateAttr.getRawValue();
    	
    	/*
    	Date firstPlannedEndDate = stateMetadata.getType().equals(IStateMetadata.Type.PREPARED) ? firstPlannedEndDateAttr.getRawValue() : firstPlannedEndDateAttr.getPersistentRawValue();
    	if (firstPlannedEndDate == null) {
    		firstPlannedEndDate = issue.getRawValue(IIssueAttributeType.ATTR_PLANNEDENDDATE);
    	}
    	*/
    	
    	final ILongAttribute plannedDaysAttr = issue.getAttribute(IIssueAttributeTypeBB.ATTR_PLANNED_DAYS);
    	plannedDaysAttr.setRawValue(IssueHelperBB.calculateDifferenceInDaysBetweenDates(firstPlannedEndDate, creatorDate) + 1);
    	
		return CommandResult.OK;
	}
}
