/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.component.testmanagement.re;

import java.util.Date;

import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IDateAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.ILongAttribute;
import com.idsscheer.webapps.arcm.bl.re.ext.MessageInformation;
import com.idsscheer.webapps.arcm.bl.re.ext.RuleAppObj;
import com.idsscheer.webapps.arcm.bl.re.ext.StateInformation;
import com.idsscheer.webapps.arcm.bl.re.impl.REEnvironment;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRecommendationAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.ITestcaseAttributeType;
import com.idsscheer.webapps.arcm.common.util.ovid.OVIDFactory;


/**
 * <p>
 * Used by the rule engine in 'recommendation.drl' and 'recommendation_base.dsl'.
 * </p>
 * 
 * <p> This is a customization for <b><code>Banco do Brasil Project</code></b> </p>
 *@author brmob
 *@since v20160314-MNT-UATEnhancements
 *@see RecommendationHelper
 */
public class RecommendationHelperBB extends RecommendationHelper {
	
	public static void checkRecommendationDate(){
		REEnvironment env = REEnvironment.getInstance();
        RuleAppObj recommendation = env.getRuleAppObj();
        
        
        RuleAppObj testCase = retrieveTestcase(recommendation);
        
        IDateAttribute recommendationDateAttr = (IDateAttribute) recommendation.getAttribute(IRecommendationAttributeType.ATTR_RECOMMENDATION_DATE.getId());
        if (!recommendationDateAttr.isEmpty()) {
        	Date recommendationDate = recommendationDateAttr.getRawValue();
            
            IDateAttribute testCasePlannedStartDateAttr = (IDateAttribute) testCase.getAttribute(ITestcaseAttributeType.ATTR_PLANNEDSTARTDATE.getId());
            Date testCasePlannedStartDate = testCasePlannedStartDateAttr.getRawValue();
            
            if (recommendationDate.before(testCasePlannedStartDate)) {
            	StateInformation stateInformation = env.getStateInformation(); 
        		MessageInformation messageInformation = env.getMessageInformation();
        		
        		stateInformation.setValid(IRecommendationAttributeType.ATTR_RECOMMENDATION_DATE.getId(), false);
        		
        		messageInformation.addErrorMessage(IRecommendationAttributeType.ATTR_RECOMMENDATION_DATE.getId(),
    					"error.recommendation.recommendation_date.ERR", 
    							testCasePlannedStartDateAttr.getUiValue(env.getUserContext().getLanguage()),
    							testCase.getObjectId());
        		
    		}
		}
        
	}
	
	/**
     * Returns the parent testcase.
     *
     * @param recommendation The recommendation at hand (<em>not</em> testcase)
     * @return parent testcase. Null if no parent test case exists.
     */
    private static RuleAppObj retrieveTestcase(RuleAppObj recommendation) {
        RuleAppObj testcase;
        
        ILongAttribute testCaseIDAttr = (ILongAttribute) recommendation.getAttribute(IRecommendationAttributeType.ATTR_RELATEDOBJECTID.getId());
        Long testCaseID = testCaseIDAttr.getRawValue();
        
        testcase = REEnvironment.getInstance().loadRuleAppObj(ObjectType.TESTCASE, OVIDFactory.getOVID(testCaseID));
        return testcase;
    }

}
