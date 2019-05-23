/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.component.riskmanagement.re;

import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IEnumAttribute;
import com.idsscheer.webapps.arcm.bl.re.impl.REEnvironment;
import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskassessmentAttributeType;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;

/**
 * @author Administrator
 *
 */
public class RiskassessmentHelperBB extends RiskassessmentHelper {

	/**
	 * 
	 */
	public RiskassessmentHelperBB() {
	}
	
	public static boolean isAssessmentSuddenlyFinished(){
		boolean assessmentSuddenlyFinished = Boolean.FALSE;
		
		REEnvironment env = REEnvironment.getInstance();
		IAppObj riskAssessment = env.getRuleAppObj().getAppObj();
		
		IEnumAttribute ownerStatusAttr = riskAssessment.getAttribute(IRiskassessmentAttributeType.ATTR_OWNER_STATUS);
		IEnumerationItem ownerStatusPersistent = ARCMCollections.extractSingleEntry(ownerStatusAttr.getPersistentRawValue(), true);
		IEnumerationItem ownerStatusRaw = ARCMCollections.extractSingleEntry(ownerStatusAttr.getRawValue(), true);
		
		if (ownerStatusPersistent.equals(Enumerations.ASSESSMENT_STATE.NEW) && ownerStatusRaw.equals(Enumerations.ASSESSMENT_STATE.ASSESSED)) {
			assessmentSuddenlyFinished = Boolean.TRUE;
		}
		
		
		return assessmentSuddenlyFinished;
	}

}
