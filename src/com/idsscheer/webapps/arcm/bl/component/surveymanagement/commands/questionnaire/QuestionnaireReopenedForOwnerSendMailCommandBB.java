/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.component.surveymanagement.commands.questionnaire;

import java.util.List;

import com.idsscheer.webapps.arcm.bl.component.common.command.SendMailCommand;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.framework.message.IMessage;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IListAttribute;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IHierarchyAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IQuestionnaireAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IUsergroupAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.resources.IResourceBundle;
import com.idsscheer.webapps.arcm.common.resources.ResourceBundleFactory;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;

/**
 * @author Administrator
 *
 */
public class QuestionnaireReopenedForOwnerSendMailCommandBB extends SendMailCommand {
	
	@Override
	protected void addAdditionalParameters(CommandContext cc, IMessage message) {
		IAppObj questionnaire = cc.getChainContext().getTriggeringAppObj();
		
		IListAttribute reviewerGroupAttr = questionnaire.getAttribute(IQuestionnaireAttributeType.LIST_REVIEWER_GROUP);
		IAppObj reviewerGroup = ARCMCollections.extractSingleEntry(reviewerGroupAttr.getElements(fullGrant), true);
		
		
		List<IAppObj> relatedOrgUnits = reviewerGroup.getAttribute(IUsergroupAttributeTypeBB.LIST_BB_ORGUNIT).getElements(fullGrant);
		
		if (!relatedOrgUnits.isEmpty()) {
			IAppObj relatedOrgUnit = ARCMCollections.extractSingleEntry(relatedOrgUnits, true);
			message.addToContext("sender", relatedOrgUnit.getAttribute(
					IHierarchyAttributeTypeBB.ATTR_PREFIXO).getRawValue()+" - "+relatedOrgUnit.getAttribute(IHierarchyAttributeTypeBB.ATTR_NAME).getRawValue());
			
		} else {
			IResourceBundle resourceBundle = ResourceBundleFactory.getBundle(message.getMessageLocale());
			message.addToContext("sender", resourceBundle.getString("login.dialog.header.DBI"));
			
		}
		
	}

}
