/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.component.issuemanagement.commands;

import java.util.Collections;
import java.util.List;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.exception.BLException;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandResult;
import com.idsscheer.webapps.arcm.bl.framework.command.IChainContext;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations;
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.notification.INotificationList;
import com.idsscheer.webapps.arcm.common.notification.NotificationList;
import com.idsscheer.webapps.arcm.common.notification.NotificationTypeEnum;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.common.util.ovid.OVIDUtility;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;


/**
 * <p>
 * Checks if the triggering issue AppObj can be deleted. This is the case if at least one of these conditions is fulfilled:
 * <ul>
 *  <li>the current user is issue manager and the issue is in state 'openForCreation' or 'openForExecution'</li>
 *  <li>the current user is creator, owner and reviewer, the issue is in state 'openForCreation' or 'onHold'</li>
 *  <li>the current user is creator and the issue is in state 'openForCreation'</li>
 * </ul>
 * </p>
 * <br>
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
 * @since v20150302-CUST-IssueMgmt
 * @see CheckIssueDeletionPossibleCommand
 */
public class CheckIssueDeletionPossibleCommandBB extends CheckIssueDeletionPossibleCommand {

	@Override
	public CommandResult execute(CommandContext cc) throws BLException {
		
		INotificationList m = new NotificationList();
        IChainContext chainCtx = cc.getChainContext();
        IAppObj issue = chainCtx.getTriggeringAppObj();
        IUserContext ctx = chainCtx.getUserContext();
        boolean deletionAllowed = false;

        //noinspection ConstantConditions
        if (!deletionAllowed) {
            //
            // (1) issue manager are allowed to delete issues as long as they are open or not yet released
            //
            if (isManagerForThisIssue(issue, ctx) &&
                    (isInWorkflowState(issue, "openForCreation") || isInWorkflowState(issue, "openForCreation"))) {
                deletionAllowed = true;
            }
        }

        if (!deletionAllowed) {
            //
            // (2) creator=owner=reviewer is allowed to delete this issue typed as ACTION or TECH_RECOMMENDATION as long as it is open
            //
            List<IOVID> thisUserID = Collections.singletonList(ctx.getUserID());
            IEnumerationItem issueType = ARCMCollections.extractSingleEntry(issue.getAttribute(IIssueAttributeTypeBB.ATTR_ISSUETYPE).getPersistentRawValue(),true);
            if (issueType.equals(EnumerationsBB.ISSUETYPE.ACTION) || issueType.equals(EnumerationsBB.ISSUETYPE.TECH_RECOMMENDATION)) {
            	if (OVIDUtility.hasIntersection(issue.getAttribute(IIssueAttributeType.LIST_CREATOR).getPersistentRawValue(), thisUserID, true) &&
                		OVIDUtility.hasIntersection(issue.getAttribute(IIssueAttributeType.LIST_OWNERS).getPersistentRawValue(), thisUserID, true) &&
                		OVIDUtility.hasIntersection(issue.getAttribute(IIssueAttributeType.LIST_REVIEWERS).getPersistentRawValue(), thisUserID, true) &&
                        (isInWorkflowState(issue, "openForExecution"))){
                    deletionAllowed = true;
                }
			}
        }

        if (!deletionAllowed) {
            //
            // (3) creator is allowed to delete this issue as long as it is not released
            //
            List<IOVID> thisUserID = Collections.singletonList(ctx.getUserID());
            if (OVIDUtility.hasIntersection(issue.getAttribute(IIssueAttributeType.LIST_CREATOR).
                    getPersistentRawValue(), thisUserID, true) && isInWorkflowState(issue, "openForCreation")) {
                deletionAllowed = true;
            }
        }
        
        boolean ownerStatusInCreation = issue.getAttribute(IIssueAttributeType.ATTR_CREATOR_STATUS).hasItem(Enumerations.ISSUE_CREATOR_STATUS.IN_CREATION);
          
        if (!deletionAllowed) {
        	if(!ownerStatusInCreation){
        		/** if creator status!=in_creation, don't delete the issue and display a custom error message  **/
        		m.add(NotificationTypeEnum.INFO, "error.issue.delete.not.allowed.other.ERR");
        	}
        }

        CommandResult result;
        if (m.isEmpty()) {
            result = new CommandResult(CommandResult.STATUS.OK);
        } else {
            result = new CommandResult(CommandResult.STATUS.FAILED, m, null);
        }

        return result;
	}
}
