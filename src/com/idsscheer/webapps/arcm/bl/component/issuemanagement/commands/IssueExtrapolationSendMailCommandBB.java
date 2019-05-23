/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.component.issuemanagement.commands;

import java.util.Arrays;
import java.util.List;

import com.idsscheer.webapps.arcm.bl.exception.BLException;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandResult;
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeTypeBB;

/**
* <p>
* Concrete message sending command for extrapolation issues:<br>
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
public class IssueExtrapolationSendMailCommandBB extends AbstractIssueSendMailCommandBB {
	/* Workflow states */
	private static final String OPEN_FOR_CREATION_CREATOR = "openForCreation";
	private static final String OPEN_FOR_CREATION_MGMTAPPL1 = "openForCreationExtrapolationMgmtApprL1";
	private static final String OPEN_FOR_CREATION_IMPLAPPL2 = "openForCreationExtrapolationImplApprL2";
	private static final String CLOSED_CREATION_IMPLAPPL2 = "creationExtrapolationClosedByImplApprL2";
	
	/* Command chains */
	private static final String MGMTAPPL1_REVIEW_ACCEPTED = "mgmtApprL1ExtrapolationReviewApproved";
	private static final String IMPLAPPL2_REVIEW_ACCEPTED = "implApprL2ExtrapolationReviewApproved";
	
	@Override
	public CommandResult executeCommand(CommandContext cc) throws BLException {
		String currentComandChain = getCurrentCommandChain();
		
		switch (currentComandChain) {
		case MGMTAPPL1_REVIEW_ACCEPTED:
			sendMessage(cc, generalIssueTemplate, 
					IIssueAttributeTypeBB.LIST_MGMT_APPROVERL1_GROUP, 
					EnumerationsBB.ISSUE_APPROVERL2_STATUS.UNSPECIFIED, 
					IIssueAttributeTypeBB.LIST_IMPL_APPROVERL2_GROUP,
					IIssueAttributeTypeBB.LIST_MGMT_APPROVERL1_GROUP);
			break;
		case IMPLAPPL2_REVIEW_ACCEPTED:
			sendMessage(cc, generalIssueTemplate,
					IIssueAttributeTypeBB.LIST_IMPL_APPROVERL2_GROUP,
					EnumerationsBB.ISSUE_APPROVERL2_STATUS.ACCEPTED, 
					IIssueAttributeTypeBB.LIST_MGMT_APPROVERL1_GROUP,
					IIssueAttributeTypeBB.LIST_IMPL_APPROVERL2_GROUP);
			break;
		default:
			break;
		}
		return new CommandResult(CommandResult.STATUS.OK);
	}

	@Override
	protected List<String> getAllWorkflowStateID() {
		return Arrays.asList(OPEN_FOR_CREATION_CREATOR,
				OPEN_FOR_CREATION_MGMTAPPL1,
				OPEN_FOR_CREATION_IMPLAPPL2,
				CLOSED_CREATION_IMPLAPPL2);
	}

}
