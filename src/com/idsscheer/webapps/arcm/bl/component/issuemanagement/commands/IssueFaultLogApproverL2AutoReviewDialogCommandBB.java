/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.component.issuemanagement.commands;

import com.idsscheer.webapps.arcm.bl.component.common.command.RequestDialogCommand;
import com.idsscheer.webapps.arcm.bl.exception.BLException;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandResult;
import com.idsscheer.webapps.arcm.bl.models.dialog.DialogRequest;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IEnumAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.ITextAttribute;
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.notification.NotificationTypeEnum;
import com.idsscheer.webapps.arcm.common.support.ConfigParameter;
import com.idsscheer.webapps.arcm.common.support.ConfigParameterType;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.config.Metadata;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;

/**
 * <p>
 * If the Issue Manager - Approver L1 is reviewing a issue classified as Fault log and for such issue the attribute bb_issueGrade was
 * not equal to 'four' then the confirmation dialog "issue_mgmtapprl2_autoreview" for the issue auto review is displayed.
 * </p>
 * <p>
 * If the dialog answer is 'ok' then the bb_mgmtApprL2_status is set to 'accepted', otherwise nothing will be changed.
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
public class IssueFaultLogApproverL2AutoReviewDialogCommandBB extends RequestDialogCommand {
	
	@ConfigParameter(value = "dialogID", type = ConfigParameterType.DIALOG)
	public static final String DIALOG_ID_TRANSACTIONAL_CLOSE = "transactional_close";
	
	@ConfigParameter(value = "dialogID", type = ConfigParameterType.DIALOG)
	public static final String DIALOG_ID_ISSUE_MGMTAPPRL2_AUTOREVIEW = "issue_mgmtapprl2_autoreview";
	
	@ConfigParameter(value = "mgmtApprL2_remark")
	public static final String ISSUE_MGMTAPPRL2_REMARK = "mgmtApprL2_remark";

    protected boolean isAllowedMgmtApprL2AutoReview;
    
    @Override
    protected CommandResult handleDialogFinished(CommandContext cc) throws BLException {
    	final IAppObj issueObj = cc.getChainContext().getTriggeringAppObj();
		if (isAllowedMgmtApprL2AutoReview) {
			final Boolean decisionOk = cc.getChainContext().getContextAttribute(getDialogID(cc));
            final Boolean closingConfirmed = cc.getChainContext().isDialogFinished(DIALOG_ID_TRANSACTIONAL_CLOSE);
			if (decisionOk && closingConfirmed) {
				final IEnumAttribute mgmtApproverL1Status = issueObj.getAttribute(IIssueAttributeTypeBB.ATTR_MGMT_APPROVERL1_STATUS);
	            final IEnumAttribute mgmtApproverL2Status = issueObj.getAttribute(IIssueAttributeTypeBB.ATTR_MGMT_APPROVERL2_STATUS);
	            final ITextAttribute mgmtApproverL2Remark = issueObj.getAttribute(IIssueAttributeTypeBB.ATTR_MGMT_APPROVERL2_REMARK);
	            final IEnumerationItem issueGrade = ARCMCollections.extractSingleEntry(issueObj.getAttribute(IIssueAttributeTypeBB.ATTR_ISSUEGRADE).getRawValue(), true);
	            
	            mgmtApproverL1Status.addItem(EnumerationsBB.ISSUE_APPROVERL1_STATUS.UNSPECIFIED);
				mgmtApproverL2Status.addItem(EnumerationsBB.ISSUE_APPROVERL2_STATUS.PLEASE_SELECT);
	            String mgmtApproverRemakText = Metadata.getMetadata().getResourceBundle(cc.getChainContext().getLocale()).getString(cc.getCommandXMLParameter(ISSUE_MGMTAPPRL2_REMARK),issueGrade.getValue());
				mgmtApproverL2Remark.setRawValue(mgmtApproverRemakText);
			} 
		}
		return super.handleDialogFinished(cc);
    }
    
    @Override
    protected void prepareDialogRequest(CommandContext cc, DialogRequest p_request) {
    	final IAppObj issueObj = cc.getChainContext().getTriggeringAppObj();
    	final IEnumerationItem issueGrade = ARCMCollections.extractSingleEntry(issueObj.getAttribute(IIssueAttributeTypeBB.ATTR_ISSUEGRADE).getRawValue(), true);
    	p_request.getNotifications().add(NotificationTypeEnum.QUESTION,"info.issuemanagement.faultlog.issuegrade.not.four.INF",issueGrade.getValue());
    }
    
    @Override
    protected String getDialogID(CommandContext cc) {
    	final String dialogID = super.getDialogID(cc);
		if(null == dialogID) {
		    return DIALOG_ID_ISSUE_MGMTAPPRL2_AUTOREVIEW;
	    }
		return dialogID;
    }
    
    @Override
    protected boolean checkPreconditions(CommandContext p_cc) {
    	isAllowedMgmtApprL2AutoReview = false;
    	
    	final IAppObj issueObj = p_cc.getChainContext().getTriggeringAppObj();
    	IEnumerationItem issueType = ARCMCollections.extractSingleEntry(issueObj.getAttribute(IIssueAttributeTypeBB.ATTR_ISSUETYPE).getRawValue(), true); 

    	if (EnumerationsBB.ISSUETYPE.FAULTLOG.equals(issueType)) {
			
    		IEnumerationItem issueGrade = ARCMCollections.extractSingleEntry(issueObj.getAttribute(IIssueAttributeTypeBB.ATTR_ISSUEGRADE).getRawValue(), true);
    		if (!issueGrade.equals(EnumerationsBB.ISSUEGRADE.FOUR)) {
				isAllowedMgmtApprL2AutoReview = true;
			} 
		}
    	
    	return isAllowedMgmtApprL2AutoReview;
    }
}
