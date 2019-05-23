/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.component.surveymanagement.re;

import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IOfflineprocessingentryAppObj;
import com.idsscheer.webapps.arcm.bl.offlineprocessing.helpers.OfflineProcessingUtility;
import com.idsscheer.webapps.arcm.bl.offlineprocessing.offlinelock.OfflineLockService;
import com.idsscheer.webapps.arcm.bl.re.ext.MessageInformation;
import com.idsscheer.webapps.arcm.bl.re.ext.RuleAppObj;
import com.idsscheer.webapps.arcm.bl.re.impl.REEnvironment;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IOfflineprocessingentryAttributeType;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.config.Metadata;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;

/**
 * @author Administrator
 *
 */
public class QuestionnaireHelperBB extends QuestionnaireHelper {
	
	public static void isRelatedQuestionnaireOffline(){
		
		REEnvironment env = REEnvironment.getInstance();
		RuleAppObj ruleAppObj = env.getRuleAppObj();
		IAppObj questionnaire = ruleAppObj.getAppObj();
		
		MessageInformation messageInformation = env.getMessageInformation();
		
		IOVID lockUser = OfflineLockService.getInstance().getOfflineLockOwner(questionnaire.getVersionData().getOVID());
		IOfflineprocessingentryAppObj offlineEntry = OfflineProcessingUtility.getOfflineProcessingEntry(questionnaire);
		if (lockUser != null && offlineEntry != null) {
			
			IEnumerationItem offlineEntryState = ARCMCollections.extractSingleEntry(
					offlineEntry.getAttribute(IOfflineprocessingentryAttributeType.ATTR_OFFLINEPROCESS_STATE).getRawValue(), true);
			
			String state = Metadata.getMetadata().getResourceBundle(
					getFullReadAccessUserContext().getLanguage()).getString(offlineEntryState.getPropertyKey());
			
			messageInformation.addObjectInformationMessage("offlineprocessing.from.message.lockedby.you.DBI", state);
		}
	}
	

}
