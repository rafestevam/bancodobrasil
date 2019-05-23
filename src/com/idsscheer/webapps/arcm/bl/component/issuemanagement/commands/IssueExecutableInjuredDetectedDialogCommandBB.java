/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.component.issuemanagement.commands;

import com.idsscheer.webapps.arcm.bl.component.common.command.RequestDialogCommand;
import com.idsscheer.webapps.arcm.bl.exception.BLException;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandResult;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IEnumAttribute;
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.support.ConfigParameter;
import com.idsscheer.webapps.arcm.common.support.ConfigParameterType;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;

/**
 * <p>
 * If the Issue Implementation - Approver L1 is reviewing a issue detected as Injured and for such issue the attribute bb_injuredIssue was
 * checked as 'true' then the confirmation dialog "issue_injured_detected" is displayed informing that will be needed Implementation Approver L2 feedback.
 * </p>
 * <p>
 * If the dialog answer is 'ok' then implementation approval l2 phase will be needed to such issue, otherwise nothing will be changed.
 * </p>
 * <p> This is a customization for <b><code>Banco do Brasil Project</code></b> </p>
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
 * @see RequestDialogCommand
 */
public class IssueExecutableInjuredDetectedDialogCommandBB extends RequestDialogCommand {
	
	@ConfigParameter(value = "dialogID", type = ConfigParameterType.DIALOG)
	public static final String DIALOG_ID_TRANSACTIONAL_CLOSE = "transactional_close";
	
	@ConfigParameter(value = "dialogID", type = ConfigParameterType.DIALOG)
	public static final String DIALOG_ID_ISSUE_INJURED_DETECTED = "issue_injured_detected";
	
	protected boolean isInjuredIssue;
	
	@Override
    protected CommandResult handleDialogFinished(CommandContext cc) throws BLException {
		final IAppObj issueObj = cc.getChainContext().getTriggeringAppObj();
		if (isInjuredIssue) {
			final Boolean decisionOk = cc.getChainContext().getContextAttribute(getDialogID(cc));
            final Boolean closingConfirmed = cc.getChainContext().isDialogFinished(DIALOG_ID_TRANSACTIONAL_CLOSE);
			if (decisionOk && closingConfirmed) {
				final IEnumAttribute execImplApprL2Status = issueObj.getAttribute(IIssueAttributeTypeBB.ATTR_EXEC_IMPLAPPRL2_STATUS);
				final IEnumAttribute reviewerStatus = issueObj.getAttribute(IIssueAttributeTypeBB.ATTR_REVIEWER_STATUS);
	            
				reviewerStatus.addItem(EnumerationsBB.ISSUE_REVIEWER_STATUS_BB.UNDECIDED);
				execImplApprL2Status.addItem(EnumerationsBB.ISSUE_EXECAPPRL2_STATUS.UNSPECIFIED);
			} 
		}
		return super.handleDialogFinished(cc);
    }
    
    @Override
    protected String getDialogID(CommandContext cc) {
    	final String dialogID = super.getDialogID(cc);
		if(null == dialogID) {
		    return DIALOG_ID_ISSUE_INJURED_DETECTED;
	    }
		return dialogID;
    }
    
    @Override
    protected boolean checkPreconditions(CommandContext p_cc) {

    	final IAppObj issueObj = p_cc.getChainContext().getTriggeringAppObj();
    	IEnumerationItem issueType = ARCMCollections.extractSingleEntry(issueObj.getAttribute(IIssueAttributeTypeBB.ATTR_ISSUETYPE).getRawValue(), true); 

    	if (issueType.equals(EnumerationsBB.ISSUETYPE.TECH_RECOMMENDATION) || issueType.equals(EnumerationsBB.ISSUETYPE.ACTION)) {
    		isInjuredIssue = issueObj.getAttribute(IIssueAttributeTypeBB.ATTR_INJUREDISSUE).getRawValue();
		}
    	
    	return isInjuredIssue;
    }
}
