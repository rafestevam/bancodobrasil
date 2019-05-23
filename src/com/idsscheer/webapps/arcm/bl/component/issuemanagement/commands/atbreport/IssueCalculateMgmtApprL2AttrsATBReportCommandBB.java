/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.component.issuemanagement.commands.atbreport;

import java.util.Date;
import java.util.List;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.common.support.AppObjUtility;
import com.idsscheer.webapps.arcm.bl.component.issuemanagement.re.IssueHelperBB;
import com.idsscheer.webapps.arcm.bl.exception.BLException;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandResult;
import com.idsscheer.webapps.arcm.bl.framework.command.IChainContext;
import com.idsscheer.webapps.arcm.bl.framework.command.ICommand;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IDateAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IEnumAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IListAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.QueryOrder;
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IObjectType;

/**
 * <p>
 * This command is for the ATB Report - bb_mgmtApprL2ATB_date & bb_mgmtApprL2ATB_status
 * Requirement 01:
 * The issue's bb_mgmtApproverL2_date attribute will be copied to issueRelevantObject's bb_mgmtApprL2ATB_date attribute.
 * It's going to happen when the DEALING_DEADLINE is accepted/rejected by the Approver L2 - Mgmt
 * 
 * Requirement 02:
 * The issue's bb_mgmtApproverL2_status attribute will be copied to issueRelevantObject's bb_mgmtApprL2ATB_status attribute.
 * It's going to happen when the DEALING_DEADLINE is accepted/rejected by the Approver L2 - Mgmt
 * 
 * Requirement 03:
 * The issue's attributes bb_mgmtApproverL2_date, bb_mgmtApproverL1_date and bb_implApproverL2_date attributes will be deleted from the ATB attributes if the issue is rejected and will consider the last
 * DEALING_DEADLINE approved attributes.
 * It's going to happen when the DEALING_DEADLINE is accepted/rejected by the Approver L2 - Mgmt
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
public class IssueCalculateMgmtApprL2AttrsATBReportCommandBB implements ICommand {

	/* (non-Javadoc)
	 * @see com.idsscheer.webapps.arcm.bl.framework.command.ICommand#execute(com.idsscheer.webapps.arcm.bl.framework.command.CommandContext)
	 */
	@Override
	public CommandResult execute(CommandContext cc) throws BLException {
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
								
				/* For ATB Report proposals
				 * The issue's bb_mgmtApproverL2_status attribute will be copied to issueRelevantObject's bb_mgmtApprL2ATB_status attribute.
				 * It's going to happen when the DEALING_DEADLINE is accepted by the Approver L2 - Mgmt */

				List<IEnumerationItem> mgmtApproverL2_statusAttr = issue.getAttribute(IIssueAttributeTypeBB.ATTR_MGMT_APPROVERL2_STATUS).getRawValue();
				IEnumerationItem mgmtApproverL2_status = ARCMCollections.extractSingleEntry(mgmtApproverL2_statusAttr, true);
				IEnumAttribute iROmgmtApproverL2StatusAttr = issueRelatedObject.getAttribute(IIssueAttributeTypeBB.ATTR_MGMT_APPRL2ATB_STATUS);
				//iROmgmtApproverL2StatusAttr.addItem(mgmtApproverL2_status);

				IDateAttribute mgmtApproverL2DateAttr = issue.getAttribute(IIssueAttributeTypeBB.ATTR_MGMT_APPROVERL2_DATE);
				Date mgmtApproverL2Date = mgmtApproverL2DateAttr.getRawValue();
				IDateAttribute iROmgmtApprL2ATBDateAttr = issueRelatedObject.getAttribute(IIssueAttributeTypeBB.ATTR_MGMT_APPRL2ATB_DATE);
				//iROmgmtApprL2ATBDateAttr.setRawValue(mgmtApproverL2Date);
			
				
				/*If the status of the ISSUE_APPROVERL2_STATUS (DEALING_DEADLINE) is rejected, should follow these steps below:
					* Delete the ATTR_MGMT_APPRL1ATB_DATE in the ATB Report
					* Delete the ATTR_IMPL_APPRL2ATB_DATE in the ATB Report
					* After that, should get the last DEALING_DEADLINE that was approved (classified by the ascending OB_ID) and copy the following dates for the current ATB Report
					* ATTR_MGMT_APPROVERL1_DATE and ATTR_IMPL_APPRL2ATB_DATE
				*/
				if (mgmtApproverL2_status.equals(EnumerationsBB.ISSUE_APPROVERL2_STATUS.REJECTED))
				{
					/* If the DEALING_DEADLINE issue type that was rejected (the first and only one)don't consider this to ATB Report 
					 * Clean up all the attributes related to involved profiles of the previous steps of the DEALING_DEADLINE (ATTR_IMPL_APPRL2ATB_DATE, ATTR_MGMT_APPRL1ATB_DATE, ATTR_MGMT_APPRL2ATB_DATE and ATTR_MGMT_APPRL2ATB_STATUS)
					 * But if it do have some previous DEALING_DEADLINE already approved, must consider this.
					 * */
					AppObjUtility.resetToDefaultValue(userContext, issueRelatedObject, IIssueAttributeTypeBB.ATTR_IMPL_APPRL2ATB_DATE, IIssueAttributeTypeBB.ATTR_MGMT_APPRL1ATB_DATE, IIssueAttributeTypeBB.ATTR_MGMT_APPRL2ATB_DATE, IIssueAttributeTypeBB.ATTR_MGMT_APPRL2ATB_STATUS);
					
					//To find the parent obj list and set the last date of the DEALING_DEADLINE after it's rejected by the MGMG_APPROVERL2
					List<IAppObj> iROParents = issueRelatedObject.getParentAppObjs(userContext,IIssueAttributeTypeBB.LIST_ISSUERELEVANTOBJECTS, QueryOrder.descending(IIssueAttributeTypeBB.BASE_ATTR_OBJ_ID));
					
					for (IAppObj iROParent : iROParents)
					{
						IObjectType iROParentObjectType = iROParent.getObjectType();
						if (iROParentObjectType.equals(ObjectType.ISSUE))
						{
							IEnumerationItem iroParentIssueType = ARCMCollections.extractSingleEntry(iROParent.getAttribute(IIssueAttributeTypeBB.ATTR_ISSUETYPE).getRawValue(), true);
							if (iroParentIssueType.equals(EnumerationsBB.ISSUETYPE.DEALING_DEADLINE))
							{
								List<IEnumerationItem> iROParentBb_mgmtApproverL2_statusAttr = iROParent.getAttribute(IIssueAttributeTypeBB.ATTR_MGMT_APPROVERL2_STATUS).getRawValue();
								IEnumerationItem iROParentBb_mgmtApproverL2_status = ARCMCollections.extractSingleEntry(iROParentBb_mgmtApproverL2_statusAttr, true);

/*								List<IEnumerationItem> iROParentBb_implApproverL2_statusAttr = iROParent.getAttribute(IIssueAttributeTypeBB.ATTR_IMPL_APPROVERL2_STATUS).getRawValue();
								IEnumerationItem iROParentBb_implApproverL2_status = ARCMCollections.extractSingleEntry(iROParentBb_implApproverL2_statusAttr, true);
*/								
								/* For ATB Report purposes
								 * * Prepare to get the related attributes of the last DEALING_DEADLINE that was approved
								 */
								if (iROParentBb_mgmtApproverL2_status.equals(EnumerationsBB.ISSUE_APPROVERL2_STATUS.ACCEPTED))
								{
									//IMPL_APPR_L2
									IDateAttribute iROPimplApproverL2DateAttr = iROParent.getAttribute(IIssueAttributeTypeBB.ATTR_IMPL_APPROVERL2_DATE);
									Date iROPimplApproverL2Date = iROPimplApproverL2DateAttr.getRawValue();
									IDateAttribute iROimplApprL2ATBDateAttr = issueRelatedObject.getAttribute(IIssueAttributeTypeBB.ATTR_IMPL_APPRL2ATB_DATE);
									iROimplApprL2ATBDateAttr.setRawValue(iROPimplApproverL2Date);
									
									//MGMT_APPR_L1
									IDateAttribute iROPmgmtApproverL1DateAttr = iROParent.getAttribute(IIssueAttributeTypeBB.ATTR_MGMT_APPROVERL1_DATE);
									Date iROPmgmtApproverL1Date = iROPmgmtApproverL1DateAttr.getRawValue();
									IDateAttribute iROmgmtApprL1ATBDateAttr = issueRelatedObject.getAttribute(IIssueAttributeTypeBB.ATTR_MGMT_APPRL1ATB_DATE);
									iROmgmtApprL1ATBDateAttr.setRawValue(iROPmgmtApproverL1Date);
									
									//MGMT_APPR_L2
									IDateAttribute iROPmgmtApproverL2DateAttr = iROParent.getAttribute(IIssueAttributeTypeBB.ATTR_MGMT_APPROVERL2_DATE);
									Date iROPmgmtApproverL2Date = iROPmgmtApproverL2DateAttr.getRawValue();
									iROmgmtApprL2ATBDateAttr.setRawValue(iROPmgmtApproverL2Date);
									
									//MGMT_APPR_L2_STATUS
									IEnumAttribute mgmtApproverL2StatusAttr = iROParent.getAttribute(IIssueAttributeTypeBB.ATTR_MGMT_APPROVERL2_STATUS);
									IEnumerationItem mgmtApproverL2Status = ARCMCollections.extractSingleEntry(mgmtApproverL2StatusAttr.getRawValue(), true);
									//IEnumAttribute iROmgmtApproverL2StatusAttr = issueRelatedObject.getAttribute(IIssueAttributeTypeBB.ATTR_MGMT_APPRL2ATB_STATUS);
									iROmgmtApproverL2StatusAttr.addItem(mgmtApproverL2Status);
									
									break;
								}
							}
						}
						//AppObjUtility.copyAttributeValue(iROParent, issue, IIssueAttributeTypeBB.ATTR_MGMT_APPRL2ATB_STATUS, IIssueAttributeTypeBB.ATTR_MGMT_APPRL2ATB_STATUS);
					}
				}
				else if (mgmtApproverL2_status.equals(EnumerationsBB.ISSUE_APPROVERL2_STATUS.ACCEPTED))
				{
					/* For ATB Report proposals
					 * The issue's bb_mgmtApproverL2_date attribute will be copied to issueRelevantObject's bb_mgmtApprL2ATB_date attribute.
					 * It's going to happen when the DEALING_DEADLINE is accepted/rejected by the Approver L2 - Mgmt */
					//AppObjUtility.copyAttributeValue(issue, issueRelatedObject, IIssueAttributeTypeBB.ATTR_MGMT_APPROVERL2_DATE, IIssueAttributeTypeBB.ATTR_MGMT_APPRL2ATB_DATE);
					iROmgmtApprL2ATBDateAttr.setRawValue(mgmtApproverL2Date);
					iROmgmtApproverL2StatusAttr.addItem(mgmtApproverL2_status);
					
					/* For ATB Report proposals (Bug #111133)
					 * Calculates the diff between the issue PlannedEndDateAttr and the StartDate of the IRO.*/
					IDateAttribute issuePlannedEndDateAttr = issue.getAttribute(IIssueAttributeTypeBB.ATTR_PLANNEDENDDATE);
					Date issuePlannedEndDate = issuePlannedEndDateAttr.getRawValue();
					
					IDateAttribute iROStartDateAttr = issueRelatedObject.getAttribute(IIssueAttributeTypeBB.ATTR_START_DATE);
					Date iROStartDate = iROStartDateAttr.getRawValue();
									
					long newPlannedDays = IssueHelperBB.calculateDifferenceInDaysBetweenDates(issuePlannedEndDate, iROStartDate) +1;
					
					issueRelatedObject.getAttribute(IIssueAttributeTypeBB.ATTR_PLANNEDENDDATE).setRawValue(issuePlannedEndDate);
					issueRelatedObject.getAttribute(IIssueAttributeTypeBB.ATTR_PLANNED_DAYS).setRawValue(newPlannedDays);
					/* For ATB Report proposals (Bug #111133)
					
					/* For ATB Report proposals
					 * The issue's bb_mgmtApproverL2_status attribute will be copied to issueRelevantObject's bb_mgmtApprL2ATB_status attribute.
					 * It's going to happen when the DEALING_DEADLINE is accepted/rejected by the Approver L2 - Mgmt */
					
/*					//MGMT_APPR_L2_STATUS
					IEnumAttribute iSSUEmgmtApproverL2StatusAttr = issue.getAttribute(IIssueAttributeTypeBB.ATTR_MGMT_APPROVERL2_STATUS);
					IEnumerationItem iSSUEmgmtApproverL2Status = ARCMCollections.extractSingleEntry(iSSUEmgmtApproverL2StatusAttr.getRawValue(), true);
					IEnumAttribute iROmgmtApprL2ATBStatusAttr = issueRelatedObject.getAttribute(IIssueAttributeTypeBB.ATTR_MGMT_APPRL2ATB_STATUS);
					iROmgmtApprL2ATBStatusAttr.addItem(iSSUEmgmtApproverL2Status);
*/										
				}				
			}
			chainContext.allocateWriteLock(issueRelatedObject, true);
			chainContext.save(issueRelatedObject, true);
			chainContext.releaseWriteLock(issueRelatedObject);			
		}
		return CommandResult.OK;
	}

}
