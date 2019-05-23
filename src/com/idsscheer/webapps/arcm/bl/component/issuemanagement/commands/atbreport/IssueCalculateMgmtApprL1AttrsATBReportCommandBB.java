/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.component.issuemanagement.commands.atbreport;

import java.util.Date;
import java.util.List;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.exception.BLException;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandResult;
import com.idsscheer.webapps.arcm.bl.framework.command.IChainContext;
import com.idsscheer.webapps.arcm.bl.framework.command.ICommand;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IDateAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IListAttribute;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeTypeBB;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IObjectType;

/**
 * <p>
 * This command is for the ATB Report - bb_mgmtApprL1ATB_date
 * Requirement 01:
 * The issue's bb_mgmtApproverL1_date attribute will be copied to issueRelevantObject's bb_mgmtApprL1ATB_date attribute.
 * It's going to happen when the DEALING_DEADLINE is accepted by the Approver L1 - Mgmt
 * 
 * Requirement 02:
 * The issue's bb_mgmtApproverL1_date attribute will be copied to issueRelevantObject's bb_mgmtApprL1ATB_date attribute.
 * It's going to happen when the DEALING_DEADLINE is accepted or rejected by the Approver L1 - Mgmt. This is only a date of the manager's L1 opinion.
 * 
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
public class IssueCalculateMgmtApprL1AttrsATBReportCommandBB implements ICommand {

	/* (non-Javadoc)
	 * @see com.idsscheer.webapps.arcm.bl.framework.command.ICommand#execute(com.idsscheer.webapps.arcm.bl.framework.command.CommandContext)
	 */
	@Override
	public CommandResult execute(CommandContext cc) throws BLException
	{
		IChainContext chainContext = cc.getChainContext();
		IAppObj issue = chainContext.getTriggeringAppObj();
		IUserContext userContext = chainContext.getUserContext();
		
		IListAttribute issueRelateObjectAttr = issue.getAttribute(IIssueAttributeTypeBB.LIST_ISSUERELEVANTOBJECTS);
		List<IAppObj> issueRelatedObjects = issueRelateObjectAttr.getElements(userContext);

		for (IAppObj issueRelatedObject : issueRelatedObjects)
		{
			IObjectType iroObjectType = issueRelatedObject.getObjectType();
			
			if (iroObjectType.equals(ObjectType.ISSUE))
			{
/*				//MGMT_APPR_L2_STATUS
				List<IEnumerationItem> mgmtApproverL2_statusAttr = issue.getAttribute(IIssueAttributeTypeBB.ATTR_MGMT_APPROVERL2_STATUS).getRawValue();
				IEnumerationItem mgmtApproverL2_status = ARCMCollections.extractSingleEntry(mgmtApproverL2_statusAttr, true);
				IEnumAttribute iROmgmtApprL2ATBStatusAttr = issueRelatedObject.getAttribute(IIssueAttributeTypeBB.ATTR_MGMT_APPRL2ATB_STATUS);
				iROmgmtApprL2ATBStatusAttr.addItem(mgmtApproverL2_status);
*/
				//Issue 111136 - The field bb_mgmtApprL1ATB_date will be filled only when the Mgmt Appr L1 has accepted.
				//IEnumerationItem mgmtApproverL1Status = ARCMCollections.extractSingleEntry(issue.getAttribute(IIssueAttributeTypeBB.ATTR_MGMT_APPROVERL1_STATUS).getRawValue(), true);
				
				IDateAttribute mgmtApproverL1DateAttr = issue.getAttribute(IIssueAttributeTypeBB.ATTR_MGMT_APPROVERL1_DATE);
				Date mgmtApproverL1Date = mgmtApproverL1DateAttr.getRawValue();

				/* For ATB Report proposals
				 * The issue's bb_mgmtApproverL1_date attribute will be copied to issueRelevantObject's bb_mgmtApprL1ATB_date attribute.
				 * Enhancement #113818 - This field must be filled anyway (rejected/accepted)*/
				IDateAttribute mgmtApprL1ATBDateAttr = issueRelatedObject.getAttribute(IIssueAttributeTypeBB.ATTR_MGMT_APPRL1ATB_DATE);
				mgmtApprL1ATBDateAttr.setRawValue(mgmtApproverL1Date);
				
				/* For ATB Report proposals
				 * If the issue DEALING_DEADLINE was reject by the mgmtApproverL1, the field date from mgmtApprL1ATBDateAttr must be filled on ATB Report with the last approval from implApproverL2
				 * PENDING FOR APPROVALS
				 */					
/*				else if (mgmtApproverL1Status.equals(EnumerationsBB.ISSUE_APPROVERL1_STATUS.REJECTED))
				{
					List<IAppObj> iROParents = issueRelatedObject.getParentAppObjs(userContext,IIssueAttributeTypeBB.LIST_ISSUERELEVANTOBJECTS, QueryOrder.descending(IIssueAttributeTypeBB.BASE_ATTR_OBJ_ID));
					
					for (IAppObj iROParent : iROParents)
					{
						IObjectType iROParentObjectType = iROParent.getObjectType();
						if (iROParentObjectType.equals(ObjectType.ISSUE))
						{
							IEnumerationItem iroParentIssueType = ARCMCollections.extractSingleEntry(iROParent.getAttribute(IIssueAttributeTypeBB.ATTR_ISSUETYPE).getRawValue(), true);
							
							if (iroParentIssueType.equals(EnumerationsBB.ISSUETYPE.DEALING_DEADLINE))
							{
								List<IEnumerationItem> iROParentBb_implApproverL2_statusAttr = iROParent.getAttribute(IIssueAttributeTypeBB.ATTR_IMPL_APPROVERL2_STATUS).getRawValue();
								IEnumerationItem iROParentBb_implApproverL2_status = ARCMCollections.extractSingleEntry(iROParentBb_implApproverL2_statusAttr, true);
		
								 For ATB Report purposes
								 * * Get the related attributes of the last DEALING_DEADLINE that was approved by the implApproverL2, even that the mgmtApprL2 hadn't approved it. 
								 
								if (iROParentBb_implApproverL2_status.equals(EnumerationsBB.ISSUE_APPROVERL2_STATUS.ACCEPTED))
								{
									//MGMT_APPR_L1
									IDateAttribute lastMgmtApproverL1DateAttr = iROParent.getAttribute(IIssueAttributeTypeBB.ATTR_MGMT_APPROVERL1_DATE);
									Date lastMgmtApproverL1Date = lastMgmtApproverL1DateAttr.getRawValue();
									IDateAttribute iRO_lastMgmtApprATBL1Date = issueRelatedObject.getAttribute(IIssueAttributeTypeBB.ATTR_MGMT_APPRL1ATB_DATE);
									iRO_lastMgmtApprATBL1Date.setRawValue(lastMgmtApproverL1Date);
									
									break;
								}
							}
						}
					}
					IEnumAttribute mgmtApproverL2StatusAttr = issue.getAttribute(IIssueAttributeTypeBB.ATTR_MGMT_APPROVERL2_STATUS);
					IEnumerationItem mgmtApproverL2Status = ARCMCollections.extractSingleEntry(mgmtApproverL2StatusAttr.getRawValue(), true);
					
					IEnumAttribute mgmtApprL2ATBStatusAttr = issueRelatedObject.getAttribute(IIssueAttributeTypeBB.ATTR_MGMT_APPRL2ATB_STATUS);
					mgmtApprL2ATBStatusAttr.addItem(mgmtApproverL2Status);
	
					break;
				}
*/			}
			chainContext.allocateWriteLock(issueRelatedObject, true);
			chainContext.save(issueRelatedObject, true);
			chainContext.releaseWriteLock(issueRelatedObject);
			
			break;
		}
		return CommandResult.OK;
	}
}
