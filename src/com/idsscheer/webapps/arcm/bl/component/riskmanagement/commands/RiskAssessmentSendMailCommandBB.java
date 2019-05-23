/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.component.riskmanagement.commands;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.idsscheer.webapps.arcm.bl.component.common.command.SendMailCommandBB;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.framework.message.IMessage;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IListAttribute;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskassessmentAttributeType;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;

/**
 * @author Administrator
 *
 */
public class RiskAssessmentSendMailCommandBB extends SendMailCommandBB {
	
	@Override
	protected void addAdditionalParameters(CommandContext cc, IMessage message) {
		super.addAdditionalParameters(cc, message);
		
		IAppObj riskAssessment = cc.getChainContext().getTriggeringAppObj();
		IListAttribute riskAttr = riskAssessment.getAttribute(IRiskassessmentAttributeType.LIST_RISK);
		IAppObj risk = ARCMCollections.extractSingleEntry(riskAttr.getElements(fullGrant), true);
		
		String riskDisplayName = risk.getObjectId()+" - "+risk.getAttribute(IRiskAttributeTypeBB.ATTR_NAME).getPersistentRawValue();
		message.addToContext("riskDisplayName", riskDisplayName);
	}
	
	@Override
	protected List<IAppObj> getCCUserGroup(CommandContext cc, IAppObj triggeringAppObj) {
		
		String ccUserGroupParameter = cc.getCommandXMLParameter(PARAMETER_CC);
		List<IAppObj> ccUserGroupList = super.getCCUserGroup(cc, triggeringAppObj);
		
		if (ccUserGroupParameter != null) {
			
			String[] userGroupParameterArray = StringUtils.split(ccUserGroupParameter, ",");
			
			if (ArrayUtils.contains(userGroupParameterArray, IRiskAttributeTypeBB.LIST_MANAGER_GROUP.getId())) {
				IListAttribute riskAttr = triggeringAppObj.getAttribute(IRiskassessmentAttributeType.LIST_RISK);
				IAppObj risk = ARCMCollections.extractSingleEntry(riskAttr.getElements(fullGrant), true);
				
				IListAttribute managerGroupAttr = risk.getAttribute(IRiskAttributeTypeBB.LIST_MANAGER_GROUP);
				if (!managerGroupAttr.isEmpty()) {
					ccUserGroupList.addAll(managerGroupAttr.getElements(fullGrant));
				} 
			}
		}

		return ccUserGroupList;
	}

}
