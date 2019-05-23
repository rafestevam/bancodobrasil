/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.component.riskmanagement.commands.job.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import com.idsscheer.webapps.arcm.bl.authentication.context.ContextFactory;
import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.authentication.context.ServerContext;
import com.idsscheer.webapps.arcm.bl.component.riskmanagement.commands.util.RiskAssessmentUtility;
import com.idsscheer.webapps.arcm.bl.component.riskmanagement.util.ImpacttypeCopyHelper;
import com.idsscheer.webapps.arcm.bl.exception.BLException;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IClientAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IImpacttypeAppObj;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IClientAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRa_impacttypeAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskassessmentAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.ISiteAttributeType;
import com.idsscheer.webapps.arcm.common.notification.INotificationList;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;

/**
 * <p>
 * First the GenerateCommand fills the attributes of the triggering AppObj by calling {@link #fillGeneratedAppObj}.
 * Then it may create depending new AppObjs by calling {@link #generateDependentAppObjs} (for example when generating surveys).
 * These new depending AppObs are stored inside the ChainContext. Later they are saved and committed by the executing
 * {@link com.idsscheer.webapps.arcm.bl.framework.jobs.generic.generator.GeneratorJob}
 * </p>
 * <p> This is a customization for <b><code>Banco do Brasil Project</code></b> </p>
 * <br>  
 * <p>
 * command XML parameters (<b>bold</b>=mandatory): none
 * </p>
 * <p>
 * input work objects (<b>bold</b>=mandatory): none
 * </p>
 * <br>
 * <p>
 * output work objects (<b>bold</b>=mandatory):
 * <ul>
 *  <li>IJobWorkobjectConstants.WORKOBJECT_GENERATEDAPPOBJS: All generated depending AppObjs as List&lt;IAppObj&gt; </li>
 * </ul>
 * </p>
 *	
 * @author brmob
 * @since v20150728-UPGRADE-ARCM98
 * @see RiskassessmentGenerateCommand 
 */
public class RiskassessmentGenerateCommandBB extends RiskassessmentGenerateCommand {
	
	/**
     * Each riskassessment has one dependent ra_impacttype for each impact type defined at site and the riskassessment client.
     * Furthermore, for BdB's project the attribute RISK.type will be reflected to RISKASSESSMENT.RA_IMPACTTYPE.type 
     * @param riskAssessment
     * @param cc the CommandContext
     * @param notificationList an empty list which can be filled by the GenerateCommand command.
     * @return
     * @throws BLException
     */
    @SuppressWarnings("unchecked")
	@Override
    protected GenerateResult generateDependentAppObjs(IAppObj riskAssessment, CommandContext cc, INotificationList notificationList) throws BLException {

        final IUserContext fullGrantUserContext = ContextFactory.createFullGrantUserContext(cc.getChainContext().getUserContext());
        final List<IAppObj> impactTypes = new ArrayList<>();
        final List<IAppObj> snapshots = new ArrayList<>();

        //start with the client impact types
        final IClientAppObj client = riskAssessment.getRawValue(IRiskassessmentAttributeType.ATTR_RELATED_CLIENT);
        List<IAppObj> clientImpactTypes = client.getAttribute(IClientAttributeType.LIST_IMPACTTYPES).getElements(fullGrantUserContext);
        impactTypes.addAll(clientImpactTypes);
        //include the site impact types if the flag is set
        Boolean include = client.getAttribute(IClientAttributeType.ATTR_INCLUDESITEIMPACTTYPES).getRawValue();
        if (include) {
            List<IAppObj> serverImpactTypes = ServerContext.getInstance().getSite().getAttribute(ISiteAttributeType.LIST_IMPACTTYPES).getElements(fullGrantUserContext);
            impactTypes.addAll(serverImpactTypes);
        }

        ImpacttypeCopyHelper helper = new ImpacttypeCopyHelper(cc.getChainContext(), fullGrantUserContext);
        for (final IAppObj originalImpactType : impactTypes) {
            IAppObj currentSnapshot = helper.getCurrentSnapshot(originalImpactType,client.getRawValue(IClientAttributeType.ATTR_SIGN));
            if(currentSnapshot == null) {
                IAppObj newSnapshot = helper.createSnapshot((IImpacttypeAppObj) originalImpactType, client);
                snapshots.add(newSnapshot);
            } else {
                snapshots.add(currentSnapshot);
            }
        }
        
        /* BdB's customizations begins here
         * The attribute RISK.type should to be reflected in the RISKASSESSMENT.RA_IMPACTTYPE.type
         * The attribute RISK.impact and RISK.probability should to be reflected in the RISKASSESSMENT
         */
        IAppObj risk = ARCMCollections.extractSingleEntry(riskAssessment.getAttribute(IRiskassessmentAttributeType.LIST_RISK).getElements(fullGrantUserContext), true);
        IEnumerationItem type;
        IEnumerationItem riskImpact;
        IEnumerationItem riskProbability;
        try {
        	type = ARCMCollections.extractSingleEntry(risk.getAttribute(IRiskAttributeTypeBB.ATTR_TYPE).getRawValue(), true);
        	riskImpact = ARCMCollections.extractSingleEntry(risk.getAttribute(IRiskAttributeTypeBB.ATTR_IMPACT).getRawValue(), true);
        	riskProbability = ARCMCollections.extractSingleEntry(risk.getAttribute(IRiskAttributeTypeBB.ATTR_PROBABILITY).getRawValue(), true);
		} catch (NoSuchElementException e) {
			return new GenerateResult(false);
		}
        
        for(final IAppObj snapshot : snapshots) {
            final IAppObj rait = cc.getChainContext().create(ObjectType.RA_IMPACTTYPE, true);
            rait.getAttribute(IRa_impacttypeAttributeType.LIST_IMPACTTYPE).addLastElement(snapshot, fullGrantUserContext);
            rait.getAttribute(IRa_impacttypeAttributeType.ATTR_CURRENCY).setRawValue((List<IEnumerationItem>) riskAssessment.getRawValue(IRiskassessmentAttributeType.ATTR_CURRENCY));
            rait.getAttribute(IRa_impacttypeAttributeType.ATTR_RELATED_CLIENT).setRawValue(client);
            
            RiskAssessmentUtility.translateRiskImpactToRiskAssessmentQuotation(riskImpact, rait, snapshot, fullGrantUserContext);
            RiskAssessmentUtility.translateRiskProbabilityToRiskAssessmentFrequency(riskProbability, rait, snapshot, fullGrantUserContext);

            // BdB's customization
            rait.getAttribute(IRa_impacttypeAttributeType.ATTR_TYPE).addItem(type);
            riskAssessment.getAttribute(IRiskassessmentAttributeType.LIST_IMPACTTYPES).addLastElement(rait, fullGrantUserContext);
            cc.getChainContext().save(rait, true);
            cc.getChainContext().releaseWriteLock(rait);
        }

        return new GenerateResult(true);
    }

}
