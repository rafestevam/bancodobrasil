/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.component.issuemanagement.commands.atbreport;

import java.util.Date;
import java.util.List;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.common.support.AppObjUtility;
import com.idsscheer.webapps.arcm.bl.exception.BLException;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandResult;
import com.idsscheer.webapps.arcm.bl.framework.command.IChainContext;
import com.idsscheer.webapps.arcm.bl.framework.command.ICommand;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IBooleanAttribute;
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
 * This command is for the ATB Report - bb_isOverdueExecution & bb_implApprL2ATB_date
 * Requirement 01:
 * This field will be calculated only for TECH_RECOMMENDATION and ACTION.
 * Furthermore, this field will be set when a issue DEALING_DEADLINE assigned for such TECH_RECOMMENDATION or ACTION based on as follows:
 * <ul>
 * <li>Such action or tech_recommendation is overdue: <b>bb_isOverdueExecution is true</b></li>
 * <li>Such action or tech_recommendation is on time: <b>bb_isOverdueExecution is false</b></li>
 * </ul>
 * 
 * Requirement 02:
 * The issue's bb_implApproverL2_date attribute will be copied to issueRelevantObject's bb_implApprL2ATB_date attribute.
 * It's going to happen when the DEALING_DEADLINE is accepted by the Approver L2 - Impl
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
public class IssueCalculateImplApprL2AttrsATBReportCommandBB implements ICommand {

	/* (non-Javadoc)
	 * @see com.idsscheer.webapps.arcm.bl.framework.command.ICommand#execute(com.idsscheer.webapps.arcm.bl.framework.command.CommandContext)
	 */
	@Override
	public CommandResult execute(CommandContext cc) throws BLException {
		IChainContext chainContext = cc.getChainContext();
		IAppObj issue = chainContext.getTriggeringAppObj();
		IUserContext userContext = chainContext.getUserContext();
		
		List<IEnumerationItem> issueBb_impltApproverL2_statusAttr = issue.getAttribute(IIssueAttributeTypeBB.ATTR_IMPL_APPROVERL2_STATUS).getRawValue();
		IEnumerationItem issueBb_impltApproverL2_status = ARCMCollections.extractSingleEntry(issueBb_impltApproverL2_statusAttr, true);
				

		IListAttribute issueRelateObjectAttr = issue.getAttribute(IIssueAttributeTypeBB.LIST_ISSUERELEVANTOBJECTS);
		List<IAppObj> issueRelatedObjects = issueRelateObjectAttr.getElements(userContext);

		for (IAppObj issueRelatedObject : issueRelatedObjects)
		{
			IObjectType iroObjectType = issueRelatedObject.getObjectType();
			
			if (iroObjectType.equals(ObjectType.ISSUE))
			{				
				/*If the status of the ISSUE_APPROVERL2_STATUS (DEALING_DEADLINE) is accepted, should follow these steps below:
					* Delete the ATTR_MGMT_APPRL1ATB_DATE in the ATB Report
					* Delete the ATTR_IMPL_APPRL2ATB_DATE in the ATB Report
					* ATTR_MGMT_APPROVERL1_DATE and ATTR_IMPL_APPRL2ATB_DATE
				*/
				if (issueBb_impltApproverL2_status.equals(EnumerationsBB.ISSUE_APPROVERL2_STATUS.ACCEPTED))
				{
					//Clean up all the attributes related to involved profiles of the next steps of the DEALING_DEADLINE (ATTR_MGMT_APPRL1ATB_DATE, ATTR_MGMT_APPRL2ATB_DATE)
					AppObjUtility.resetToDefaultValue(userContext, issueRelatedObject, IIssueAttributeTypeBB.ATTR_MGMT_APPRL1ATB_DATE, IIssueAttributeTypeBB.ATTR_MGMT_APPRL2ATB_DATE );

					//MGMT_APPR_L2_STATUS
					List<IEnumerationItem> mgmtApproverL2_statusAttr = issue.getAttribute(IIssueAttributeTypeBB.ATTR_MGMT_APPROVERL2_STATUS).getRawValue();
					IEnumerationItem mgmtApproverL2_status = ARCMCollections.extractSingleEntry(mgmtApproverL2_statusAttr, true);
					IEnumAttribute iROmgmtApprL2ATBStatusAttr = issueRelatedObject.getAttribute(IIssueAttributeTypeBB.ATTR_MGMT_APPRL2ATB_STATUS);
					iROmgmtApprL2ATBStatusAttr.addItem(mgmtApproverL2_status);					
					
					/* ACTION or TECH_RECOMMENDATION firstPlannedEndDate attribute */
					//IDateAttribute firstPlannedEndDateAttr = issueRelatedObject.getAttribute(IIssueAttributeTypeBB.ATTR_FIRST_PLANNED_ENDDATE);
					IDateAttribute firstPlannedEndDateAttr = issueRelatedObject.getAttribute(IIssueAttributeTypeBB.ATTR_PLANNEDENDDATE);
					Date firstPlannedEndDate = firstPlannedEndDateAttr.getPersistentRawValue();
					
					/* DEALING_DEADLINE implApproverL2 must to request it before the firstPlannedEndDate. Otherwise, 
					 * the DEALING_DEADLINE will be overdue */
					IDateAttribute implApproverL2DateAttr = issue.getAttribute(IIssueAttributeTypeBB.ATTR_IMPL_APPROVERL2_DATE);
					Date implApproverL2Date = implApproverL2DateAttr.getRawValue();
					issueRelatedObject.getAttribute(IIssueAttributeTypeBB.ATTR_IMPL_APPRL2ATB_DATE).setRawValue(implApproverL2Date);
					
					IBooleanAttribute isOverdueExecutionAttr = issueRelatedObject.getAttribute(IIssueAttributeTypeBB.ATTR_ISOVERDUEEXECUTION);
					if (implApproverL2Date.after(firstPlannedEndDate)) {
						isOverdueExecutionAttr.setRawValue(Boolean.TRUE);
					}
			
					/* For ATB Report proposals
					 * The issue's bb_implApproverL2_date attribute will be copied to issueRelevantObject's bb_implApprL2ATB_date attribute.
					 * It's going to happen when the DEALING_DEADLINE is accepted by the Approver L2 - Impl */
					//IDateAttribute implApprL2ATBDateAttr = issueRelatedObject.getAttribute(IIssueAttributeTypeBB.ATTR_IMPL_APPRL2ATB_DATE);
					//implApprL2DateAttr.setRawValue(implApproverL2Date);
					
				}
				/*For ATB Report purposes
				 * After the implApproverL2 reject it, consider the last DEALING_DEADLINE that was approved by the mgmtApproverL2 and print out the related attribute for this current level
				 */
				else if (issueBb_impltApproverL2_status.equals(EnumerationsBB.ISSUE_APPROVERL2_STATUS.REJECTED))
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
								List<IEnumerationItem> iROParentBb_mgmtApproverL2_statusAttr = iROParent.getAttribute(IIssueAttributeTypeBB.ATTR_MGMT_APPROVERL2_STATUS).getRawValue();
								IEnumerationItem iROParentBb_mgmtApproverL2_status = ARCMCollections.extractSingleEntry(iROParentBb_mgmtApproverL2_statusAttr, true);
		
								/* For ATB Report purposes
								 * * Get the related attributes of the last DEALING_DEADLINE that was approved by the implApproverL2, even that the mgmtApprL2 hadn't approved it. 
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
									IDateAttribute iROmgmtApprL2ATBDateAttr = issueRelatedObject.getAttribute(IIssueAttributeTypeBB.ATTR_MGMT_APPRL2ATB_DATE);
									iROmgmtApprL2ATBDateAttr.setRawValue(iROPmgmtApproverL2Date);
									
									//MGMT_APPR_L2_STATUS
									IEnumAttribute mgmtApproverL2StatusAttr = iROParent.getAttribute(IIssueAttributeTypeBB.ATTR_MGMT_APPROVERL2_STATUS);
									IEnumerationItem mgmtApproverL2Status = ARCMCollections.extractSingleEntry(mgmtApproverL2StatusAttr.getRawValue(), true);
									IEnumAttribute iROmgmtApproverL2StatusAttr = issueRelatedObject.getAttribute(IIssueAttributeTypeBB.ATTR_MGMT_APPRL2ATB_STATUS);
									iROmgmtApproverL2StatusAttr.addItem(mgmtApproverL2Status);
									
									break;
								}
							}
						}
					}
				}
			}
			chainContext.allocateWriteLock(issueRelatedObject, true);
			chainContext.save(issueRelatedObject, true);
			chainContext.releaseWriteLock(issueRelatedObject);
			
			break;
		}
		return CommandResult.OK;
	}
}