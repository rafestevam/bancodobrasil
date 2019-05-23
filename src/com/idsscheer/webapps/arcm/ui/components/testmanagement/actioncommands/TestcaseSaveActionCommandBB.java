/**
 * 
 */
package com.idsscheer.webapps.arcm.ui.components.testmanagement.actioncommands;

import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IEnumAttribute;
import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations;
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.ITestcaseAttributeType;
import com.idsscheer.webapps.arcm.common.notification.NotificationTypeEnum;
import com.idsscheer.webapps.arcm.ui.framework.actioncommands.ActionCommandException;
import com.idsscheer.webapps.arcm.ui.framework.actioncommands.object.BaseSaveActionCommand;
import com.idsscheer.webapps.arcm.ui.framework.support.UIException;

/**
 *
 * <p> This is a customization for <b><code>Banco do Brasil Project</code></b> </p>
 *@author ayo
 *@since 
 *@see TestcaseSaveActionCommand
 */
public class TestcaseSaveActionCommandBB extends BaseSaveActionCommand  {
	
	private IEnumAttribute measure = null;
    private boolean issueWorkflow = false;
    private IEnumAttribute ownerStatus = null;
    private boolean ownerStatusNonEffective = false;
    private boolean ownerStatusNotTestable = false;
    private boolean isIssueCreator = false;
    private boolean isTestReviewer = false;
	
	@Override
	protected void execute() {
		
		try {
			if (hasFlow()) {
				assumeSuccessful = true;
				
				/** check test reviewer rights in case of issue work flow**/
				getParams();
				if(isTestReviewer){
		        	/** Test case with NON EFFECTIVE or NOT TESTABLE status **/
		        	if(ownerStatusNonEffective || ownerStatusNotTestable){
		        		/** Issue WORKFLOW **/
		        		if(issueWorkflow){
		        			if(!isIssueCreator){
		        				/** Do not save the test case **/
		        				notificationDialog.addMessageKey("issue.generated.not.possible.DBI");
		            			return;
		        			}
		        		}
		        	}
		        }
				save();
			} else {
				assumeData();
				assumeSuccessful = true;
				if (!isDirty()) {
                    formModel.addControlInfoMessage(NotificationTypeEnum.INFO, "message.not.dirty.DBI", getStringRepresentation(formModel.getAppObj()));
					return;
				}
				if (!isValid()) {
                    saveSuccessful = false;
					return;
				}
				if (!assumeSuccessful()) {
					saveSuccessful = false;
					return;
				}
				
				/** check test reviewer rights in case of issue work flow**/
				getParams();
				if(isTestReviewer){
		        	/** Test case with NON EFFECTIVE status **/
		        	if(ownerStatusNonEffective || ownerStatusNotTestable){
		        		/** Issue WORKFLOW **/
		        		if(issueWorkflow){
		        			if(!isIssueCreator){
		        				/** Do not save the test case **/
		        				notificationDialog.addMessageKey("issue.generated.not.possible.DBI");
		            			return;
		        			}
		        		}
		        	}
		        }
				
                save();

                if (saveSuccessful) {
                    getHtmlPage().clearHtmlAttrDiffs();
                }  
			}
		} catch (ActionCommandException e) {
			throw e;
		} catch (UIException e) {
			throw e;
		} catch (Exception e) {
			if (!assumeSuccessful) {
				assumeFailed();
			}
			throw new ActionCommandException(e);
		}
	}
	
	/**
	 * This method is used to get input values of attributes for checking reviewer WORKFLOW
	 * */
	private void getParams(){
		measure = formModel.getAppObj().getAttribute(ITestcaseAttributeType.ATTR_MEASURE);
		issueWorkflow = measure.hasItem(Enumerations.MEASURE.ISSUE);
		ownerStatus = formModel.getAppObj().getAttribute(ITestcaseAttributeType.ATTR_OWNER_STATUS);
		ownerStatusNonEffective = ownerStatus.hasItem(Enumerations.TESTCASE_OWNER_STATUS.NONEFFECTIVE);
		ownerStatusNotTestable = ownerStatus.hasItem(Enumerations.TESTCASE_OWNER_STATUS.NOTTESTABLE);
		isIssueCreator = environment.getUserContext().getUserRights().hasRole(EnumerationsBB.USERROLE_TYPE_BB.ISSUECREATOR);
		isTestReviewer = environment.getUserContext().getUserRights().hasRole(Enumerations.USERROLE_TYPE.TESTREVIEWER);
	}
}