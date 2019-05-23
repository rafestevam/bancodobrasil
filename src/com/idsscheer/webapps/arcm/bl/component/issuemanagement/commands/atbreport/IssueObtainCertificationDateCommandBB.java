package com.idsscheer.webapps.arcm.bl.component.issuemanagement.commands.atbreport;

import java.util.Date;

import com.idsscheer.webapps.arcm.bl.exception.BLException;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandResult;
import com.idsscheer.webapps.arcm.bl.framework.command.ICommand;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IDateAttribute;
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IObjectType;

public class IssueObtainCertificationDateCommandBB implements ICommand {

@Override
public CommandResult execute(CommandContext cc) throws BLException {
		IAppObj issue = cc.getChainContext().getTriggeringAppObj();
		
    	Boolean isInjuredIssue = issue.getRawValue(IIssueAttributeTypeBB.ATTR_INJUREDISSUE);
    	if (isInjuredIssue != null && !isInjuredIssue)
    	{
			IObjectType issueObjectType = issue.getObjectType();
			
			/* For ATB Report proposals
			 * The attribute execMgmgtApprL1Date is currently being filled just when it reach out the implementation phase and it isn't Injured issue also
			 * Then it's being used here to fill the ATB Report column "DCERTAPT* as well
			 * It's valid for TECHINAL_RECOMMENDATION and also ACTION issue types
			 * Enhancement #113824
			 */			
			if (issueObjectType.equals(ObjectType.ISSUE))
			{
				IEnumerationItem issueObjtType = ARCMCollections.extractSingleEntry(issue.getAttribute(IIssueAttributeTypeBB.ATTR_ISSUETYPE).getRawValue(), true);

				if (issueObjtType.equals(EnumerationsBB.ISSUETYPE.TECH_RECOMMENDATION)||issueObjtType.equals(EnumerationsBB.ISSUETYPE.ACTION))
				{					
					IDateAttribute execMgmgtApprL1DateAttr = issue.getAttribute(IIssueAttributeTypeBB.ATTR_EXEC_MGMT_APPRL1_DATE);
					Date execMgmgtApprL1Date = execMgmgtApprL1DateAttr.getRawValue();
														
					issue.getAttribute(IIssueAttributeTypeBB.ATTR_CERTIFICATION_DATE).setRawValue(execMgmgtApprL1Date);					
				}
			}
    	}
    	return CommandResult.OK;
	}
}
