/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.component.common.command;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.idsscheer.webapps.arcm.bl.component.common.support.UsergroupUtilityBB;
import com.idsscheer.webapps.arcm.bl.exception.BLException;
import com.idsscheer.webapps.arcm.bl.exception.ObjectRelationException;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandResult;
import com.idsscheer.webapps.arcm.bl.framework.message.IMessage;
import com.idsscheer.webapps.arcm.bl.framework.message.MessageFactory;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IListAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IStringAttribute;
import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IHierarchyAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IUsergroupAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.resources.IResourceBundle;
import com.idsscheer.webapps.arcm.common.resources.ResourceBundleFactory;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IListAttributeType;

/**
 * @author Administrator
 *
 */
public class SendMailCommandBB extends SendMailCommand {
	
	@Override
	public CommandResult execute(CommandContext cc) throws BLException {
		IAppObj triggeringAppObj = cc.getChainContext().getTriggeringAppObj();
		
		String templateID = cc.getCommandXMLParameter(PARAMETER_TEMPLATE);
		IAppObj toUserGroup = getUserGroupByParameter(cc, triggeringAppObj, PARAMETER_TO);
		List<IAppObj> ccUserGroups = getCCUserGroup(cc, triggeringAppObj);
		
		IEnumerationItem initiator = Enumerations.INITIATORS.ENUM.getItemById(templateID);
		if (null == initiator) {
			throw new IllegalArgumentException("message template with id '" + templateID + "' not found");
		}
		
		IMessage message = createMessage(cc, triggeringAppObj, initiator, toUserGroup, ccUserGroups);
		addAdditionalParameters(cc, message);
		addLinkedListInformation(cc, message);
		
		cc.getChainContext().send(message);
		 
		return CommandResult.OK;
	}
	
	protected IAppObj getUserGroupByParameter(CommandContext cc, IAppObj triggeringAppObj, String parameter){
		String userGroupParameter = cc.getCommandXMLParameter(parameter);
		if (userGroupParameter == null) {
			if (parameter.equals(PARAMETER_TO)) {
				throw new IllegalArgumentException("The parameter '" + parameter + "' cannot be empty");
			}
			return null;
		}
		IListAttributeType userGroupAttrType = triggeringAppObj.getAttributeType(userGroupParameter);
		
		IListAttribute userGroupAttr = triggeringAppObj.getAttribute(userGroupAttrType);
		return userGroupAttr.isEmpty() ? null : ARCMCollections.extractSingleEntry(userGroupAttr.getElements(fullGrant), true);
	}
	
	protected List<IAppObj> getCCUserGroup(CommandContext cc, IAppObj triggeringAppObj){
		String ccUserGroupParameter = cc.getCommandXMLParameter(PARAMETER_CC);
		List<IAppObj> ccUserGroupList = new ArrayList<IAppObj>();
		
		if (ccUserGroupParameter != null) {
			String[] ccUserGroupParameterArray = StringUtils.split(ccUserGroupParameter, ",");
			
			
			for (int i = 0; i < ccUserGroupParameterArray.length; i++) {
				IListAttributeType ccUserGroupAttrType = triggeringAppObj.getAttributeType(ccUserGroupParameterArray[i]);
				if (ccUserGroupAttrType != null) {
					IListAttribute ccUserGroupAttr = triggeringAppObj.getAttribute(ccUserGroupAttrType);
					ccUserGroupList.addAll(ccUserGroupAttr.getElements(fullGrant));
				}
			}
		}

		return ccUserGroupList;
	}
	
	protected IMessage createMessage(CommandContext cc, IAppObj triggeringAppObj, IEnumerationItem initiator, IAppObj toUserGroup, List<IAppObj> ccUserGroups) throws BLException {
		IAppObj fromUserGroup = getUserGroupByParameter(cc, triggeringAppObj, PARAMETER_FROM);
		
		IStringAttribute receiverMail = UsergroupUtilityBB.getUserGroupEmailByOrgUnitStructure(toUserGroup);
		IMessage message = null;
		
		if (receiverMail == null || receiverMail.isEmpty()) {
			throw new ObjectRelationException("error.messaging.receivermail.notfound.ERR", toUserGroup.getRawValue(IUsergroupAttributeTypeBB.ATTR_NAME));
		}
		
		if (fromUserGroup != null) {
			IStringAttribute senderMail = UsergroupUtilityBB.getUserGroupEmailByOrgUnitStructure(fromUserGroup);
			message = MessageFactory.createMessage(receiverMail.getPersistentRawValue(), initiator, senderMail.getPersistentRawValue(), triggeringAppObj);
		} else {
			message = MessageFactory.createMessage(receiverMail.getPersistentRawValue(), initiator, cc.getChainContext().getUserContext().getUser(), triggeringAppObj);
		}
		
		for (IAppObj ccUserGroup : ccUserGroups) {
			IStringAttribute ccOrgUnitEmailAttr = UsergroupUtilityBB.getUserGroupEmailByOrgUnitStructure(ccUserGroup);
			
			if (ccOrgUnitEmailAttr == null || ccOrgUnitEmailAttr.isEmpty()) {
				throw new ObjectRelationException("error.messaging.receivermail.notfound.ERR", ccUserGroup.getRawValue(IUsergroupAttributeTypeBB.ATTR_NAME));
			}
			message.addCCMailAddress(ccOrgUnitEmailAttr.getPersistentRawValue());
		}
		
		return message;
	}
	
	@Override
	protected void addAdditionalParameters(CommandContext cc, IMessage message) {
		IAppObj fromUserGroup = getUserGroupByParameter(cc, message.getAffectedAppObj(), PARAMETER_FROM);
		if (fromUserGroup != null) {
			IStringAttribute fromOrgUnitName = UsergroupUtilityBB.getRelatedOrgUnitAttrByUserGroup(fromUserGroup, IHierarchyAttributeTypeBB.ATTR_NAME);
			message.addToContext("senderSignature", fromOrgUnitName.getPersistentRawValue());
			message.addToContext("fromOrgUnitName", fromOrgUnitName.getPersistentRawValue());
			
		} else {
			IResourceBundle resourceBundle = ResourceBundleFactory.getBundle(message.getMessageLocale());
			message.addToContext("senderSignature", resourceBundle.getString("login.dialog.header.DBI"));
		}
		
		IAppObj toUserGroup = getUserGroupByParameter(cc, message.getAffectedAppObj(), PARAMETER_TO);
		IStringAttribute toOrgUnitNameAttr = UsergroupUtilityBB.getRelatedOrgUnitAttrByUserGroup(toUserGroup, IHierarchyAttributeTypeBB.ATTR_NAME);
		message.addToContext("toOrgUnitName", toOrgUnitNameAttr.getPersistentRawValue());
		
	}
	
	
	

}
