/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.component.riskmanagement.commands.util;

import java.util.List;

import com.idsscheer.utils.resource.IResourceBundle;
import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.exception.ObjectRelationException;
import com.idsscheer.webapps.arcm.bl.exception.RightException;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IListAttribute;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IFrequencyAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IImpacttypeAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IQuotationAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRa_impacttypeAttributeType;
import com.idsscheer.webapps.arcm.common.resources.ResourceBundleFactory;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;

/**
 * @author Administrator
 *
 */
public final class RiskAssessmentUtility {
	
	
	/**
     * This method translates the RISK.impact >> RISKASSESSMENT.impacttypes.lossQual
     * @param riskImpact
     * @param rait
     * @param snapshot
     * @param userContext
     * @throws RightException
     * @throws ObjectRelationException
     */
    public static void translateRiskImpactToRiskAssessmentQuotation(IEnumerationItem riskImpact, IAppObj rait, IAppObj snapshot, IUserContext userContext) throws RightException, ObjectRelationException{
    	
    	List<IAppObj> snapshotQuotations = snapshot.getAttribute(IImpacttypeAttributeType.LIST_QUOTATIONS).getElements(userContext);
		IListAttribute raitQuotation = rait.getAttribute(IRa_impacttypeAttributeType.LIST_LOSSQUAL);
    	
		IResourceBundle bundle = ResourceBundleFactory.getBundle(userContext.getLanguage());
		String riskImpactName = bundle.getString(riskImpact.getPropertyKey()); 
				
		for (IAppObj quotation : snapshotQuotations) {
			String quotationName = quotation.getAttribute(IQuotationAttributeType.ATTR_NAME).getRawValue();
			
			if (riskImpactName.equalsIgnoreCase(quotationName)) {
				raitQuotation.addLastElement(quotation, userContext);
			}
		}
    }
    
    /**
     * This method translates the RISK.impact >> RISKASSESSMENT.impacttypes.lossQual
     * @param riskImpact
     * @param rait
     * @param userContext
     * @throws RightException
     * @throws ObjectRelationException
     */
    public static void translateRiskImpactToRiskAssessmentQuotation(IEnumerationItem riskImpact, IAppObj rait, IUserContext userContext) throws RightException, ObjectRelationException{
    	
    	IListAttribute raitQuotationAttr = rait.getAttribute(IRa_impacttypeAttributeType.LIST_LOSSQUAL); 
    	List<IAppObj> raitQuotations = raitQuotationAttr.getElements(userContext);
    	
    	IResourceBundle bundle = ResourceBundleFactory.getBundle(userContext.getLanguage());
		String riskImpactName = bundle.getString(riskImpact.getPropertyKey()); 
		
		for (IAppObj quotation : raitQuotations) {
			String quotationName = quotation.getAttribute(IQuotationAttributeType.ATTR_NAME).getRawValue();
			
			if (riskImpactName.equalsIgnoreCase(quotationName)) {
				raitQuotationAttr.addLastElement(quotation, userContext);
			}
		}
    	
    }
    
    /**
     * This method translates the RISK.probability >> RISKASSESSMENT.impacttypes.frequLossQual
     * @param riskProbability
     * @param rait
     * @param snapshot
     * @param userContext
     * @throws RightException
     * @throws ObjectRelationException
     */
    public static void translateRiskProbabilityToRiskAssessmentFrequency(IEnumerationItem riskProbability, IAppObj rait, IAppObj snapshot, IUserContext userContext) throws RightException, ObjectRelationException{
    	
    	List<IAppObj> snapshotFrequencies = snapshot.getAttribute(IImpacttypeAttributeType.LIST_FREQUENCIES).getElements(userContext);
		IListAttribute raitFrequency = rait.getAttribute(IRa_impacttypeAttributeType.LIST_FREQULOSSQUAL);
		IResourceBundle bundle = ResourceBundleFactory.getBundle(userContext.getLanguage());
		String riskProbabilityName = bundle.getString(riskProbability.getPropertyKey()); 
		
    	for (IAppObj frequency : snapshotFrequencies) {
			String frequencyName = frequency.getAttribute(IFrequencyAttributeType.ATTR_NAME).getRawValue();
			
			if (riskProbabilityName.equalsIgnoreCase(frequencyName)) {
				raitFrequency.addLastElement(frequency, userContext);
			}
		}
    }
    
    /**
     * This method translates the RISK.probability >> RISKASSESSMENT.impacttypes.frequLossQual
     * @param riskProbability
     * @param rait
     * @param userContext
     * @throws RightException
     * @throws ObjectRelationException
     */
    public static void translateRiskProbabilityToRiskAssessmentFrequency(IEnumerationItem riskProbability, IAppObj rait, IUserContext userContext) throws RightException, ObjectRelationException{
    	
    	IListAttribute raitFrequencyAttr = rait.getAttribute(IRa_impacttypeAttributeType.LIST_FREQULOSSQUAL); 
    	List<IAppObj> raitFrequencies = raitFrequencyAttr.getElements(userContext);
    	
    	IResourceBundle bundle = ResourceBundleFactory.getBundle(userContext.getLanguage());
		String riskProbabilityName = bundle.getString(riskProbability.getPropertyKey()); 
		
		for (IAppObj frequency : raitFrequencies) {
			String frequencyName = frequency.getAttribute(IFrequencyAttributeType.ATTR_NAME).getRawValue();
			
			if (riskProbabilityName.equalsIgnoreCase(frequencyName)) {
				raitFrequencyAttr.addLastElement(frequency, userContext);
			}
		}
    	
    }
}
