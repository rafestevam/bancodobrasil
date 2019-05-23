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
 * If a issue is classified as TECH_RECOMMENDATION or ACTION or DEALING_DEADLINE and for such issue the attribute bb_isTechSupportNeeded was
 * checked as 'true' then the confirmation dialog "issue_techapprover_needed" is displayed informing that will be needed Tech Approver's feedback.
 * </p>
 * <p>
 * If the dialog answer is 'ok' then technical approval phase will be needed to such issue, otherwise nothing will be changed.
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
public class IssueTechApprovalNeededDialogCommandBB extends RequestDialogCommand {
	
	@ConfigParameter(value = "dialogID", type = ConfigParameterType.DIALOG)
	public static final String DIALOG_ID_TRANSACTIONAL_CLOSE = "transactional_close";
	
	@ConfigParameter(value = "dialogID", type = ConfigParameterType.DIALOG)
	public static final String DIALOG_ID_ISSUE_TECHAPPROVER_NEEDED = "issue_techapprover_needed";
	
	protected boolean isNeededTechApproval;
	
	@Override
    protected CommandResult handleDialogFinished(CommandContext cc) throws BLException {
    	final IAppObj issueObj = cc.getChainContext().getTriggeringAppObj();
    	
    	isNeededTechApproval = issueObj.getAttribute(IIssueAttributeTypeBB.ATTR_ISTECHSUPPORTNEEDED).getRawValue();
    	
    	
		final Boolean decisionOk = cc.getChainContext().getContextAttribute(getDialogID(cc));
        final Boolean closingConfirmed = cc.getChainContext().isDialogFinished(DIALOG_ID_TRANSACTIONAL_CLOSE);
        if (decisionOk && closingConfirmed) {
        	prepareIssueForFurtherWorkflowStates(issueObj);
		}
    	
		return super.handleDialogFinished(cc);
    }
     
    @Override
    protected String getDialogID(CommandContext cc) {
    	final String dialogID = super.getDialogID(cc);
		if(null == dialogID) {
		    return DIALOG_ID_ISSUE_TECHAPPROVER_NEEDED;
	    }
		return dialogID;
    }
    
    @Override
    protected boolean checkPreconditions(CommandContext p_cc) {
    	final IAppObj issueObj = p_cc.getChainContext().getTriggeringAppObj();
    	isNeededTechApproval = issueObj.getAttribute(IIssueAttributeTypeBB.ATTR_ISTECHSUPPORTNEEDED).getRawValue();
    	
    	prepareIssueForFurtherWorkflowStates(issueObj);
    	return isNeededTechApproval;
    }
    
    private void prepareIssueForFurtherWorkflowStates(IAppObj issueObj){
    	IEnumerationItem issueType = ARCMCollections.extractSingleEntry(issueObj.getAttribute(IIssueAttributeTypeBB.ATTR_ISSUETYPE).getRawValue(), true);
    	
    	if (issueType.equals(EnumerationsBB.ISSUETYPE.TECH_RECOMMENDATION) || issueType.equals(EnumerationsBB.ISSUETYPE.ACTION)) {
        	final IEnumAttribute techApproverL1Status = issueObj.getAttribute(IIssueAttributeTypeBB.ATTR_TECH_APPROVERL1_STATUS);
        	final IEnumAttribute implApproverL2Status = issueObj.getAttribute(IIssueAttributeTypeBB.ATTR_IMPL_APPROVERL2_STATUS);
        	
        	if (isNeededTechApproval) {
				techApproverL1Status.addItem(EnumerationsBB.ISSUE_APPROVERL1_STATUS.UNSPECIFIED);
			} else {
				implApproverL2Status.addItem(EnumerationsBB.ISSUE_APPROVERL2_STATUS.UNSPECIFIED);
			}
    	} else if (issueType.equals(EnumerationsBB.ISSUETYPE.DEALING_DEADLINE)) {
    		final IEnumAttribute techApproverL1Status = issueObj.getAttribute(IIssueAttributeTypeBB.ATTR_TECH_APPROVERL1_STATUS);
        	final IEnumAttribute mgmtApproverL1Status = issueObj.getAttribute(IIssueAttributeTypeBB.ATTR_MGMT_APPROVERL1_STATUS);
        	
        	if (isNeededTechApproval) {
        		techApproverL1Status.addItem(EnumerationsBB.ISSUE_APPROVERL1_STATUS.UNSPECIFIED);
			} else {
				mgmtApproverL1Status.addItem(EnumerationsBB.ISSUE_APPROVERL1_STATUS.UNSPECIFIED);
			}
		} else {
			throw new IllegalArgumentException("The type of issue: "+issueType.getId()+" is not supported yet.");
		}
    }
    
    
    
    
    
    
}
