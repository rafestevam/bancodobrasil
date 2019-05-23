/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.component.issuemanagement.commands;

import java.util.Arrays;
import java.util.List;

import com.idsscheer.webapps.arcm.bl.exception.BLException;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandResult;

/**
 * @author Administrator
 *
 */
public class IssueSADSendMailCommandBB extends AbstractIssueSendMailCommandBB {
	
	/* Workflow states */
	private static final String OPEN_FOR_CREATION_CREATOR = "openForCreation";
	private static final String OPEN_FOR_CREATION_MGMTAPPL2 = "openForCreationSADMgmtApprL2";
	private static final String CLOSED_CREATION_MGMTAPPL2 = "creationSADClosedByMgmtApprL2";
	
	/* Command chains */
	private static final String CREATOR_CREATION_RELEASED = "issueCreatorSADCreationReleased";
	private static final String MGMTAPPL2_REVIEW_REJECTED = "mgmtApprL2SADReviewNotApproved";
	private static final String MGMTAPPL2_REVIEW_ACCEPTED = "mgmtApprL2SADReviewApproved";
	
	@Override
	public CommandResult executeCommand(CommandContext cc) throws BLException {
		/**
		String currentComandChain = getCurrentCommandChain();
		
		switch (currentComandChain) {
		case CREATOR_CREATION_RELEASED:
			sendMessage(cc, generalIssueTemplate, EnumerationsBB.ISSUE_APPROVERL2_STATUS.UNSPECIFIED, IIssueAttributeTypeBB.LIST_MGMT_APPROVERL2_GROUP);
			break;
		case MGMTAPPL2_REVIEW_REJECTED:
			sendMessage(cc, generalIssueTemplate, Enumerations.ISSUE_CREATOR_STATUS.OPEN, IIssueAttributeTypeBB.LIST_CREATOR);
			break;
		case MGMTAPPL2_REVIEW_ACCEPTED:
			sendMessage(cc, generalIssueTemplate, EnumerationsBB.ISSUE_APPROVERL2_STATUS.ACCEPTED, IIssueAttributeTypeBB.LIST_CREATOR);
		default:
			break;
		}*/
		return new CommandResult(CommandResult.STATUS.OK);
	}

	@Override
	protected List<String> getAllWorkflowStateID() {
		return Arrays.asList(OPEN_FOR_CREATION_CREATOR,
				OPEN_FOR_CREATION_MGMTAPPL2,
				CLOSED_CREATION_MGMTAPPL2);
	}

}
