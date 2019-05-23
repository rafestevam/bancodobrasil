/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.component.riskmanagement.commands;

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
import com.idsscheer.webapps.arcm.bl.framework.command.CommandResult;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandResult.STATUS;
import com.idsscheer.webapps.arcm.bl.framework.command.IChainContext;
import com.idsscheer.webapps.arcm.bl.framework.command.ICommand;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IClientAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IImpacttypeAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IListAttribute;
import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IClientAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRa_impacttypeAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskassessmentAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.ISiteAttributeType;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;

/**
 * @author Administrator
 *
 */
public class RiskAssessmentImpactTypeResetCommandBB implements ICommand {

	/* (non-Javadoc)
	 * @see com.idsscheer.webapps.arcm.bl.framework.command.ICommand#execute(com.idsscheer.webapps.arcm.bl.framework.command.CommandContext)
	 */
	@Override
	public CommandResult execute(CommandContext cc) throws BLException {
		
		IChainContext chainContext = cc.getChainContext();
		IAppObj riskAssessment = chainContext.getTriggeringAppObj();
		IUserContext fullGrantUserContext = ContextFactory.createFullGrantUserContext(cc.getChainContext().getUserContext());
		
		IEnumerationItem riskOwnerStatus = ARCMCollections.extractSingleEntry(riskAssessment.getAttribute(IRiskassessmentAttributeType.ATTR_OWNER_STATUS).getRawValue(), true);
		if (riskOwnerStatus.equals(Enumerations.ASSESSMENT_STATE.ASSESSMENT_NOT_POSSIBLE)) {
			List<IAppObj> impactTypes = new ArrayList<>();
			List<IAppObj> snapshots = new ArrayList<>();
			
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

	        ImpacttypeCopyHelper helper = new ImpacttypeCopyHelper(chainContext, fullGrantUserContext);
	        for (final IAppObj originalImpactType : impactTypes) {
	            IAppObj currentSnapshot = helper.getCurrentSnapshot(originalImpactType,client.getRawValue(IClientAttributeType.ATTR_SIGN));
	            if(currentSnapshot == null) {
	                IAppObj newSnapshot = helper.createSnapshot((IImpacttypeAppObj) originalImpactType, client);
	                snapshots.add(newSnapshot);
	            } else {
	                snapshots.add(currentSnapshot);
	            }
	        }
	        
	        IAppObj risk = ARCMCollections.extractSingleEntry(riskAssessment.getAttribute(IRiskassessmentAttributeType.LIST_RISK).getElements(fullGrantUserContext), true);
	        IEnumerationItem riskImpact;
	        IEnumerationItem riskProbability;
	        try {
	        	riskImpact = ARCMCollections.extractSingleEntry(risk.getAttribute(IRiskAttributeTypeBB.ATTR_IMPACT).getRawValue(), true);
	        	riskProbability = ARCMCollections.extractSingleEntry(risk.getAttribute(IRiskAttributeTypeBB.ATTR_PROBABILITY).getRawValue(), true);
			} catch (NoSuchElementException e) {
				return new CommandResult(STATUS.FAILED);
			}
	        
	        List<IAppObj> riskAssessmentImpactTypes = riskAssessment.getAttribute(IRiskassessmentAttributeType.LIST_IMPACTTYPES).getElements(fullGrantUserContext);
	        
	        for(final IAppObj snapshot : snapshots) {
	            for (IAppObj riskAssessmentImpactType : riskAssessmentImpactTypes) {
	            	IListAttribute impactTypeAttr = riskAssessmentImpactType.getAttribute(IRa_impacttypeAttributeType.LIST_IMPACTTYPE);
	            	
	            	IAppObj impactType = ARCMCollections.extractSingleEntry(impactTypeAttr.getElements(fullGrantUserContext), true);
	            	
					if (impactType.getObjectId() == snapshot.getObjectId()) {
						RiskAssessmentUtility.translateRiskImpactToRiskAssessmentQuotation(riskImpact, riskAssessmentImpactType, snapshot, fullGrantUserContext);
			            RiskAssessmentUtility.translateRiskProbabilityToRiskAssessmentFrequency(riskProbability, riskAssessmentImpactType, snapshot, fullGrantUserContext);

			            chainContext.save(riskAssessmentImpactType, true);
			            chainContext.releaseWriteLock(riskAssessmentImpactType);
					}
				}
	        }
		}
        
		return CommandResult.OK;
	}
}
