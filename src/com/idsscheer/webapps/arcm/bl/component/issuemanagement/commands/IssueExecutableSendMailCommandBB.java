/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.component.issuemanagement.commands;

import java.util.Arrays;
import java.util.List;

import com.idsscheer.webapps.arcm.bl.exception.BLException;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandResult;
import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations;
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;

/**
* <p>
* Concrete message sending command for action and technical recommendation issues:<br>
* Based on a specific workflow state and its commandchain, there will be classes responsible for 
* the dynamic delivery of notifications to specific receivers depending of which is the current workflow's stage.
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
* @since v20150302-CUST-IssueMgmt
*/
public class IssueExecutableSendMailCommandBB extends AbstractIssueSendMailCommandBB {

	/* Workflow states */
	private static final String OPEN_FOR_CREATION_CREATOR = "openForCreation";
	private static final String OPEN_FOR_CREATION_TECH_RECOMMENDATION_MGMTAPPL1 = "openForCreationTechRecommendationMgmtApprL1";
	private static final String OPEN_FOR_CREATION_MGMTAPPL2 = "openForCreationTechRecommendationMgmtApprL2";
	private static final String OPEN_FOR_CREATION_TECH_RECOMMENDATION_IMPLAPPL1 = "openForCreationTechRecommendationImplApprL1";
	private static final String OPEN_FOR_CREATION_ACTION_IMPLAPPL1 = "openForCreationActionImplApprL1";
	private static final String OPEN_FOR_CREATION_IMPLAPPL2 = "openForCreationExecutableIssueImplApprL2";
	private static final String OPEN_FOR_CREATION_TECHAPPL1 = "openForCreationExecutableIssueTechApprL1";
	private static final String OPEN_FOR_CREATION_TECHAPPL2 = "openForCreationExecutableIssueTechApprL2";
	private static final String OPEN_FOR_CREATION_ACTION_MGMTAPPL1 = "openForCreationActionMgmtApprL1";
	private static final String OPEN_FOR_EXECUTION = "openForExecution";
	private static final String OPEN_FOR_EXECUTION_IMPLAPPL1 = "openForExecutionExecutableIssueImplApprL1";
	private static final String OPEN_FOR_EXECUTION_IMPLAPPL2 = "openForExecutionExecutableIssueImplApprL2";
	private static final String OPEN_FOR_REVIEW = "openForReview";
	private static final String OPEN_FOR_REVIEW_MGMTAPPL1 = "openForReviewExecutableIssueMgmtApprL1";
	private static final String OPEN_FOR_REVIEW_MGMTAPPL2 = "openForReviewExecutableIssueMgmtApprL2";
	private static final String CLOSED_REVIEW_MGMTAPPL1 = "closedByMgmtApprL1";
	private static final String CLOSED_REVIEW_MGMTAPPL2 = "closedByMgmtApprL2";
	
	
	/* Command chains */
	private static final String MGMTAPPL2_REVIEW_ACCEPTED = "mgmtApprL2TechRecommendationReviewApproved";
	private static final String IMPLAPPL1_REVIEW_TECH_RECOMMENDATION_REJECTED = "implApprL1TechRecommendationReviewNotApproved";
	private static final String IMPLAPPL1_REVIEW_ACCEPTED = "implApprL1ExecutableIssueReviewApproved";
	private static final String TECHAPPL1_REVIEW_REJECTED = "techApprL1ExecutableIssueReviewNotApproved";
	private static final String TECHAPPL2_REVIEW_REJECTED = "techApprL2ExecutableIssueReviewNotApproved";
	private static final String TECHAPPL2_REVIEW_ACCEPTED = "techApprL2ExecutableIssueReviewApproved";
	private static final String IMPLAPPL2_REVIEW_REJECTED = "implApprL2ExecutableIssueReviewNotApproved";
	private static final String IMPLAPPL2_REVIEW_TECH_RECOMMENDATION_ACCEPTED = "implApprL2ExecutableIssueReviewApproved";
	private static final String IMPLAPPL2_REVIEW_ACTION_ACCEPTED = "implApprL2ActionReviewApproved";
	private static final String MGMTAPPL1_REVIEW_ACTION_REJECTED = "mgmtApprL1ActionReviewNotApproved";
	private static final String MGMTAPPL1_REVIEW_ACTION_ACCEPTED = "mgmtApprL1ActionReviewApproved";
	private static final String IMPLAPPL1_EXECUTION_ACCEPTED = "implApprL1ExecutableIssueExecutionApproved";
	private static final String IMPLAPPL2_EXECUTION_ACCEPTED = "implApprL2ExecutableIssueExecutionApproved";
	private static final String MGMTAPPL1_EXECUTION_REJECTED = "mgmtApprL1ExecutableIssueCertificationRejected";
	private static final String MGMTAPPL2_EXECUTION_REJECTED = "mgmtApprL2ExecutableIssueCertificationRejected";
	private static final String MGMTAPPL1_CERTIFICATION_ACCEPTED = "mgmtApprL1ExecutableIssueCertificationApproved";
	private static final String MGMTAPPL1_EXECUTION_ACCEPTED = "mgmtApprL1ExecutableIssueCertificationAccepted";
	private static final String MGMTAPPL2_EXECUTION_ACCEPTED = "mgmtApprL2ExecutableIssueCertificationAccepted";
	
	@Override
	public CommandResult executeCommand(CommandContext cc) throws BLException {
		String currentComandChain = getCurrentCommandChain();
		Boolean isTechSupportNeeded = Boolean.FALSE;
		Boolean isInjuredIssue = Boolean.FALSE;
		
		switch (currentComandChain) {
		case MGMTAPPL2_REVIEW_ACCEPTED:
			sendMessage(cc, generalIssueTemplate, 
					IIssueAttributeTypeBB.LIST_MGMT_APPROVERL2_GROUP, 
					EnumerationsBB.ISSUE_APPROVERL1_STATUS.UNSPECIFIED, 
					IIssueAttributeTypeBB.LIST_IMPL_APPROVERL1_GROUP, IIssueAttributeTypeBB.LIST_MGMT_APPROVERL2_GROUP);
			break;
		case IMPLAPPL1_REVIEW_TECH_RECOMMENDATION_REJECTED:
			sendMessage(cc, generalIssueTemplate, 
					IIssueAttributeTypeBB.LIST_IMPL_APPROVERL1_GROUP, 
					Enumerations.ISSUE_CREATOR_STATUS.OPEN,
					IIssueAttributeTypeBB.LIST_MGMT_APPROVERL2_GROUP, IIssueAttributeTypeBB.LIST_IMPL_APPROVERL1_GROUP);
			break;
		case IMPLAPPL1_REVIEW_ACCEPTED:
			isTechSupportNeeded = getTrigerringAppObj().getAttribute(IIssueAttributeTypeBB.ATTR_ISTECHSUPPORTNEEDED).getRawValue();
			if (isTechSupportNeeded) {
				sendMessage(cc, generalIssueTemplate, 
						IIssueAttributeTypeBB.LIST_IMPL_APPROVERL1_GROUP, 
						EnumerationsBB.ISSUE_APPROVERL1_STATUS.UNSPECIFIED, 
						IIssueAttributeTypeBB.LIST_TECH_APPROVERL1_GROUP, IIssueAttributeTypeBB.LIST_MGMT_APPROVERL2_GROUP, IIssueAttributeTypeBB.LIST_IMPL_APPROVERL1_GROUP);
			} 
			break;
		case TECHAPPL1_REVIEW_REJECTED:
			if (isIssueTypeTechRecommendation()) {
				sendMessage(cc, generalIssueTemplate, 
						IIssueAttributeTypeBB.LIST_TECH_APPROVERL1_GROUP, 
						Enumerations.ISSUE_CREATOR_STATUS.OPEN, 
						IIssueAttributeTypeBB.LIST_MGMT_APPROVERL2_GROUP, IIssueAttributeTypeBB.LIST_IMPL_APPROVERL1_GROUP, IIssueAttributeTypeBB.LIST_TECH_APPROVERL1_GROUP);
			} else if (isIssueTypeAction()) {
				sendMessage(cc, generalIssueTemplate, 
						IIssueAttributeTypeBB.LIST_TECH_APPROVERL1_GROUP, 
						Enumerations.ISSUE_CREATOR_STATUS.OPEN, 
						IIssueAttributeTypeBB.LIST_IMPL_APPROVERL1_GROUP, IIssueAttributeTypeBB.LIST_TECH_APPROVERL1_GROUP);
			}
			break;
		case TECHAPPL2_REVIEW_REJECTED:
			if (isIssueTypeTechRecommendation()) {
				sendMessage(cc, generalIssueTemplate, 
						IIssueAttributeTypeBB.LIST_TECH_APPROVERL2_GROUP, 
						Enumerations.ISSUE_CREATOR_STATUS.OPEN, 
						IIssueAttributeTypeBB.LIST_MGMT_APPROVERL2_GROUP, IIssueAttributeTypeBB.LIST_IMPL_APPROVERL1_GROUP, IIssueAttributeTypeBB.LIST_TECH_APPROVERL2_GROUP);
			} else if (isIssueTypeAction()) {
				sendMessage(cc, generalIssueTemplate, 
						IIssueAttributeTypeBB.LIST_TECH_APPROVERL2_GROUP, 
						Enumerations.ISSUE_CREATOR_STATUS.OPEN, 
						IIssueAttributeTypeBB.LIST_IMPL_APPROVERL1_GROUP, IIssueAttributeTypeBB.LIST_TECH_APPROVERL2_GROUP);
			}
			break;
		case TECHAPPL2_REVIEW_ACCEPTED:
			if (isIssueTypeTechRecommendation()) {
				sendMessage(cc, generalIssueTemplate, 
						IIssueAttributeTypeBB.LIST_TECH_APPROVERL2_GROUP, 
						EnumerationsBB.ISSUE_APPROVERL2_STATUS.UNSPECIFIED, 
						IIssueAttributeTypeBB.LIST_IMPL_APPROVERL2_GROUP, IIssueAttributeTypeBB.LIST_MGMT_APPROVERL2_GROUP, IIssueAttributeTypeBB.LIST_TECH_APPROVERL2_GROUP);
				
			} else if (isIssueTypeAction()) {
				sendMessage(cc, generalIssueTemplate, 
						IIssueAttributeTypeBB.LIST_TECH_APPROVERL2_GROUP, 
						EnumerationsBB.ISSUE_APPROVERL2_STATUS.UNSPECIFIED, 
						IIssueAttributeTypeBB.LIST_IMPL_APPROVERL2_GROUP, IIssueAttributeTypeBB.LIST_TECH_APPROVERL2_GROUP);
			}
			break;
		case IMPLAPPL2_REVIEW_REJECTED:
			isTechSupportNeeded = getTrigerringAppObj().getAttribute(IIssueAttributeTypeBB.ATTR_ISTECHSUPPORTNEEDED).getRawValue();
			
			//there is no notification for actions
			if (isTechSupportNeeded) {
				if (isIssueTypeTechRecommendation()) {
					sendMessage(cc, generalIssueTemplate, 
							IIssueAttributeTypeBB.LIST_IMPL_APPROVERL2_GROUP, 
							Enumerations.ISSUE_CREATOR_STATUS.OPEN, 
							IIssueAttributeTypeBB.LIST_MGMT_APPROVERL2_GROUP, IIssueAttributeTypeBB.LIST_TECH_APPROVERL2_GROUP, IIssueAttributeTypeBB.LIST_IMPL_APPROVERL2_GROUP);
				} 
				
			} else {
				if (isIssueTypeTechRecommendation()) {
					sendMessage(cc, generalIssueTemplate, 
							IIssueAttributeTypeBB.LIST_IMPL_APPROVERL2_GROUP, 
							Enumerations.ISSUE_CREATOR_STATUS.OPEN, 
							IIssueAttributeTypeBB.LIST_MGMT_APPROVERL2_GROUP, IIssueAttributeTypeBB.LIST_IMPL_APPROVERL2_GROUP);
				} 
			}
			break;
		case IMPLAPPL2_REVIEW_TECH_RECOMMENDATION_ACCEPTED:
			isTechSupportNeeded = getTrigerringAppObj().getAttribute(IIssueAttributeTypeBB.ATTR_ISTECHSUPPORTNEEDED).getRawValue();
			if (isTechSupportNeeded) {
				sendMessage(cc, generalIssueTemplate, 
						IIssueAttributeTypeBB.LIST_IMPL_APPROVERL2_GROUP, 
						Enumerations.ISSUE_OWNER_STATUS.IN_PROGRESS, 
						IIssueAttributeTypeBB.LIST_IMPL_APPROVERL2_GROUP, IIssueAttributeTypeBB.LIST_MGMT_APPROVERL2_GROUP, IIssueAttributeTypeBB.LIST_TECH_APPROVERL2_GROUP);
				
			} else {
				sendMessage(cc, generalIssueTemplate, 
						IIssueAttributeTypeBB.LIST_IMPL_APPROVERL2_GROUP, 
						Enumerations.ISSUE_OWNER_STATUS.IN_PROGRESS, 
						IIssueAttributeTypeBB.LIST_IMPL_APPROVERL2_GROUP, IIssueAttributeTypeBB.LIST_MGMT_APPROVERL2_GROUP);
			}
			break;
		case IMPLAPPL2_REVIEW_ACTION_ACCEPTED:
			isTechSupportNeeded = getTrigerringAppObj().getAttribute(IIssueAttributeTypeBB.ATTR_ISTECHSUPPORTNEEDED).getRawValue();
			if (isTechSupportNeeded) {
				sendMessage(cc, generalIssueTemplate, 
						IIssueAttributeTypeBB.LIST_IMPL_APPROVERL2_GROUP, 
						EnumerationsBB.ISSUE_APPROVERL1_STATUS.UNSPECIFIED, 
						IIssueAttributeTypeBB.LIST_MGMT_APPROVERL1_GROUP, IIssueAttributeTypeBB.LIST_TECH_APPROVERL2_GROUP, IIssueAttributeTypeBB.LIST_IMPL_APPROVERL2_GROUP);
			} else {
				sendMessage(cc, generalIssueTemplate, 
						IIssueAttributeTypeBB.LIST_IMPL_APPROVERL2_GROUP, 
						EnumerationsBB.ISSUE_APPROVERL1_STATUS.UNSPECIFIED, 
						IIssueAttributeTypeBB.LIST_MGMT_APPROVERL1_GROUP, IIssueAttributeTypeBB.LIST_IMPL_APPROVERL2_GROUP);
			}
			break;
		case MGMTAPPL1_REVIEW_ACTION_REJECTED:
			isTechSupportNeeded = getTrigerringAppObj().getAttribute(IIssueAttributeTypeBB.ATTR_ISTECHSUPPORTNEEDED).getRawValue();
			if (isTechSupportNeeded) {
				sendMessage(cc, generalIssueTemplate, 
						IIssueAttributeTypeBB.LIST_MGMT_APPROVERL1_GROUP, 
						Enumerations.ISSUE_CREATOR_STATUS.OPEN, 
						IIssueAttributeTypeBB.LIST_IMPL_APPROVERL2_GROUP, IIssueAttributeTypeBB.LIST_TECH_APPROVERL2_GROUP, IIssueAttributeTypeBB.LIST_MGMT_APPROVERL1_GROUP);
			} else {
				sendMessage(cc, generalIssueTemplate, 
						IIssueAttributeTypeBB.LIST_MGMT_APPROVERL1_GROUP, 
						Enumerations.ISSUE_CREATOR_STATUS.OPEN, 
						IIssueAttributeTypeBB.LIST_IMPL_APPROVERL2_GROUP, IIssueAttributeTypeBB.LIST_MGMT_APPROVERL1_GROUP);
			}
			break;
		case MGMTAPPL1_REVIEW_ACTION_ACCEPTED:
			isTechSupportNeeded = getTrigerringAppObj().getAttribute(IIssueAttributeTypeBB.ATTR_ISTECHSUPPORTNEEDED).getRawValue();
			if (isTechSupportNeeded) {
				sendMessage(cc, generalIssueTemplate, 
						IIssueAttributeTypeBB.LIST_MGMT_APPROVERL1_GROUP, 
						Enumerations.ISSUE_OWNER_STATUS.OPEN_FOR_EXECUTION, 
						IIssueAttributeTypeBB.LIST_OWNERS_GROUP, IIssueAttributeTypeBB.LIST_TECH_APPROVERL2_GROUP, IIssueAttributeTypeBB.LIST_MGMT_APPROVERL1_GROUP);
			} else {
				sendMessage(cc, generalIssueTemplate, 
						IIssueAttributeTypeBB.LIST_MGMT_APPROVERL1_GROUP, 
						Enumerations.ISSUE_OWNER_STATUS.OPEN_FOR_EXECUTION, 
						IIssueAttributeTypeBB.LIST_OWNERS_GROUP, IIssueAttributeTypeBB.LIST_MGMT_APPROVERL1_GROUP);
			}
			break;
		case IMPLAPPL1_EXECUTION_ACCEPTED:
			isInjuredIssue = getTrigerringAppObj().getAttribute(IIssueAttributeTypeBB.ATTR_INJUREDISSUE).getRawValue();
			if (!isInjuredIssue) {					  
				isTechSupportNeeded = getTrigerringAppObj().getAttribute(IIssueAttributeTypeBB.ATTR_ISTECHSUPPORTNEEDED).getRawValue();
				if (isTechSupportNeeded) {
					sendMessage(cc, generalIssueTemplate, 
							IIssueAttributeTypeBB.LIST_IMPL_APPROVERL1_GROUP, 
							Enumerations.ISSUE_REVIEWER_STATUS.TO_BE_APPROVED, 
							IIssueAttributeTypeBB.LIST_REVIEWERS_GROUP, IIssueAttributeTypeBB.LIST_TECH_APPROVERL2_GROUP, IIssueAttributeTypeBB.LIST_IMPL_APPROVERL1_GROUP);
				}else{
					sendMessage(cc, generalIssueTemplate, 
						IIssueAttributeTypeBB.LIST_IMPL_APPROVERL1_GROUP, 
						Enumerations.ISSUE_REVIEWER_STATUS.TO_BE_APPROVED, 
						IIssueAttributeTypeBB.LIST_REVIEWERS_GROUP, IIssueAttributeTypeBB.LIST_IMPL_APPROVERL1_GROUP);
				}
			}
			break;
		case IMPLAPPL2_EXECUTION_ACCEPTED:
			isTechSupportNeeded = getTrigerringAppObj().getAttribute(IIssueAttributeTypeBB.ATTR_ISTECHSUPPORTNEEDED).getRawValue();
			if (isTechSupportNeeded) {
				sendMessage(cc, generalIssueTemplate, 
					IIssueAttributeTypeBB.LIST_IMPL_APPROVERL2_GROUP, 
					EnumerationsBB.ISSUE_EXECAPPRL1_STATUS.UNSPECIFIED, 
					IIssueAttributeTypeBB.LIST_MGMT_APPROVERL1_GROUP, IIssueAttributeTypeBB.LIST_TECH_APPROVERL2_GROUP, IIssueAttributeTypeBB.LIST_IMPL_APPROVERL2_GROUP);
			}else{ 
				sendMessage(cc, generalIssueTemplate, 
					IIssueAttributeTypeBB.LIST_IMPL_APPROVERL2_GROUP, 
					EnumerationsBB.ISSUE_EXECAPPRL1_STATUS.UNSPECIFIED, 
					IIssueAttributeTypeBB.LIST_MGMT_APPROVERL1_GROUP, IIssueAttributeTypeBB.LIST_IMPL_APPROVERL2_GROUP);
			}
			break;
		case MGMTAPPL1_EXECUTION_REJECTED:
			isTechSupportNeeded = getTrigerringAppObj().getAttribute(IIssueAttributeTypeBB.ATTR_ISTECHSUPPORTNEEDED).getRawValue();
			if (isTechSupportNeeded) {
				sendMessage(cc, generalIssueTemplate, 
						IIssueAttributeTypeBB.LIST_MGMT_APPROVERL1_GROUP, 
						Enumerations.ISSUE_OWNER_STATUS.OPEN_FOR_EXECUTION, 
						IIssueAttributeTypeBB.LIST_OWNERS_GROUP, IIssueAttributeTypeBB.LIST_TECH_APPROVERL2_GROUP, IIssueAttributeTypeBB.LIST_MGMT_APPROVERL1_GROUP);
			} else {
				sendMessage(cc, generalIssueTemplate, 
						IIssueAttributeTypeBB.LIST_MGMT_APPROVERL1_GROUP, 
						Enumerations.ISSUE_OWNER_STATUS.OPEN_FOR_EXECUTION, 
						IIssueAttributeTypeBB.LIST_OWNERS_GROUP, IIssueAttributeTypeBB.LIST_MGMT_APPROVERL1_GROUP);
			}
			break;
		case MGMTAPPL1_CERTIFICATION_ACCEPTED:
			isTechSupportNeeded = getTrigerringAppObj().getAttribute(IIssueAttributeTypeBB.ATTR_ISTECHSUPPORTNEEDED).getRawValue();
			if (isTechSupportNeeded) {
				sendMessage(cc, generalIssueTemplate, 
						IIssueAttributeTypeBB.LIST_MGMT_APPROVERL1_GROUP, 
						EnumerationsBB.ISSUE_EXECAPPRL1_STATUS.ACCEPTED, 
						IIssueAttributeTypeBB.LIST_IMPL_APPROVERL2_GROUP, IIssueAttributeTypeBB.LIST_TECH_APPROVERL2_GROUP, IIssueAttributeTypeBB.LIST_MGMT_APPROVERL1_GROUP);
			} else {
				sendMessage(cc, generalIssueTemplate, 
						IIssueAttributeTypeBB.LIST_MGMT_APPROVERL1_GROUP, 
						EnumerationsBB.ISSUE_EXECAPPRL1_STATUS.ACCEPTED, 
						IIssueAttributeTypeBB.LIST_IMPL_APPROVERL2_GROUP, IIssueAttributeTypeBB.LIST_MGMT_APPROVERL1_GROUP);
			}
			break;
		case MGMTAPPL2_EXECUTION_REJECTED:
			isTechSupportNeeded = getTrigerringAppObj().getAttribute(IIssueAttributeTypeBB.ATTR_ISTECHSUPPORTNEEDED).getRawValue();
			if (isTechSupportNeeded) {
				sendMessage(cc, generalIssueTemplate, 
						IIssueAttributeTypeBB.LIST_MGMT_APPROVERL2_GROUP, 
						Enumerations.ISSUE_OWNER_STATUS.OPEN_FOR_EXECUTION, 
						IIssueAttributeTypeBB.LIST_OWNERS_GROUP, IIssueAttributeTypeBB.LIST_TECH_APPROVERL2_GROUP, IIssueAttributeTypeBB.LIST_MGMT_APPROVERL2_GROUP);
			} else {
				sendMessage(cc, generalIssueTemplate, 
						IIssueAttributeTypeBB.LIST_MGMT_APPROVERL2_GROUP, 
						Enumerations.ISSUE_OWNER_STATUS.OPEN_FOR_EXECUTION, 
						IIssueAttributeTypeBB.LIST_OWNERS_GROUP, IIssueAttributeTypeBB.LIST_MGMT_APPROVERL2_GROUP);
			}
			break;
		case MGMTAPPL1_EXECUTION_ACCEPTED:
			isTechSupportNeeded = getTrigerringAppObj().getAttribute(IIssueAttributeTypeBB.ATTR_ISTECHSUPPORTNEEDED).getRawValue();
			if (isTechSupportNeeded) {
				sendMessage(cc, generalIssueTemplate, 
						IIssueAttributeTypeBB.LIST_MGMT_APPROVERL1_GROUP, 
						EnumerationsBB.ISSUE_EXECAPPRL1_STATUS.ACCEPTED, 
						IIssueAttributeTypeBB.LIST_IMPL_APPROVERL1_GROUP, IIssueAttributeTypeBB.LIST_TECH_APPROVERL2_GROUP, IIssueAttributeTypeBB.LIST_MGMT_APPROVERL1_GROUP);
			} else {
				sendMessage(cc, generalIssueTemplate, 
						IIssueAttributeTypeBB.LIST_MGMT_APPROVERL1_GROUP, 
						EnumerationsBB.ISSUE_EXECAPPRL1_STATUS.ACCEPTED, 
						IIssueAttributeTypeBB.LIST_IMPL_APPROVERL1_GROUP, IIssueAttributeTypeBB.LIST_MGMT_APPROVERL1_GROUP);
			}
			break;
		case MGMTAPPL2_EXECUTION_ACCEPTED:
			isTechSupportNeeded = getTrigerringAppObj().getAttribute(IIssueAttributeTypeBB.ATTR_ISTECHSUPPORTNEEDED).getRawValue();
			if (isTechSupportNeeded) {
				sendMessage(cc, generalIssueTemplate, 
						IIssueAttributeTypeBB.LIST_MGMT_APPROVERL2_GROUP, 
						EnumerationsBB.ISSUE_EXECAPPRL2_STATUS.ACCEPTED, 
						IIssueAttributeTypeBB.LIST_IMPL_APPROVERL2_GROUP, IIssueAttributeTypeBB.LIST_TECH_APPROVERL2_GROUP, IIssueAttributeTypeBB.LIST_MGMT_APPROVERL2_GROUP);
			} else {
				sendMessage(cc, generalIssueTemplate, 
						IIssueAttributeTypeBB.LIST_MGMT_APPROVERL2_GROUP, 
						EnumerationsBB.ISSUE_EXECAPPRL2_STATUS.ACCEPTED, 
						IIssueAttributeTypeBB.LIST_IMPL_APPROVERL2_GROUP, IIssueAttributeTypeBB.LIST_MGMT_APPROVERL2_GROUP);
			}
			break;
		default:
			break;
		}
		return new CommandResult(CommandResult.STATUS.OK);
	}

	@Override
	protected List<String> getAllWorkflowStateID() {
		return Arrays.asList(OPEN_FOR_CREATION_CREATOR,
				OPEN_FOR_CREATION_TECH_RECOMMENDATION_MGMTAPPL1,
				OPEN_FOR_CREATION_MGMTAPPL2,
				OPEN_FOR_CREATION_TECH_RECOMMENDATION_IMPLAPPL1,
				OPEN_FOR_CREATION_ACTION_IMPLAPPL1,
				OPEN_FOR_CREATION_IMPLAPPL2,
				OPEN_FOR_CREATION_TECHAPPL1,
				OPEN_FOR_CREATION_TECHAPPL2,
				OPEN_FOR_CREATION_ACTION_MGMTAPPL1,
				OPEN_FOR_EXECUTION,
				OPEN_FOR_EXECUTION_IMPLAPPL1,
				OPEN_FOR_EXECUTION_IMPLAPPL2,
				OPEN_FOR_REVIEW,
				OPEN_FOR_REVIEW_MGMTAPPL1,
				OPEN_FOR_REVIEW_MGMTAPPL2,
				CLOSED_REVIEW_MGMTAPPL1,
				CLOSED_REVIEW_MGMTAPPL2);
	}
	
	private boolean isIssueTypeTechRecommendation(){
		IEnumerationItem issueType = ARCMCollections.extractSingleEntry(getTrigerringAppObj().getAttribute(IIssueAttributeTypeBB.ATTR_ISSUETYPE).getRawValue(), true);
		return issueType.equals(EnumerationsBB.ISSUETYPE.TECH_RECOMMENDATION);
	}
	
	private boolean isIssueTypeAction(){
		IEnumerationItem issueType = ARCMCollections.extractSingleEntry(getTrigerringAppObj().getAttribute(IIssueAttributeTypeBB.ATTR_ISSUETYPE).getRawValue(), true);
		return issueType.equals(EnumerationsBB.ISSUETYPE.ACTION);
	}

}
