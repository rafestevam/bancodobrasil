/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.framework.jobs.util;

import java.util.Set;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.component.common.support.UsergroupUtilityBB;
import com.idsscheer.webapps.arcm.bl.framework.message.IMessage;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IStringAttribute;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IHierarchyAttributeTypeBB;

/**
 * @author Administrator
 *
 */
public class JobMessageContainerBB extends JobMessageContainer {
	
	private IAppObj sender;
	
	@Override
	public void addMessageInformation(MessageInformation information) {
		super.addMessageInformation(information);
		this.sender = ((MessageInformationBB) information).getSender();
	}
	
	@Override
	public Set<IMessage> getMessages(IUserContext userContext) {
		Set<IMessage> messages = super.getMessages(userContext);
		
		String senderSignature = getSenderInformation();
		for (IMessage message : messages) {
			message.addToContext("senderSignature", senderSignature);
		}
		return messages;
	}
	
	private String getSenderInformation(){
		String senderSignature = null;
		IStringAttribute relatedOrgUnitNameAttr = UsergroupUtilityBB.getRelatedOrgUnitAttrByUserGroup(sender, IHierarchyAttributeTypeBB.ATTR_NAME);
		IStringAttribute relatedOrgUnitPrefixAttr = UsergroupUtilityBB.getRelatedOrgUnitAttrByUserGroup(sender, IHierarchyAttributeTypeBB.ATTR_PREFIXO);
		
		senderSignature = relatedOrgUnitPrefixAttr.getRawValue()+" - "+relatedOrgUnitNameAttr.getRawValue();
		
		return senderSignature;
	}

}
