/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.component.issuemanagement.commands;

import java.util.List;
import java.util.Locale;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.component.common.support.UsergroupUtilityBB;
import com.idsscheer.webapps.arcm.bl.exception.BLException;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandResult;
import com.idsscheer.webapps.arcm.bl.framework.command.ICommand;
import com.idsscheer.webapps.arcm.bl.framework.message.IMessage;
import com.idsscheer.webapps.arcm.bl.framework.message.MessageFactory;
import com.idsscheer.webapps.arcm.bl.framework.message.MessageUtility;
import com.idsscheer.webapps.arcm.bl.framework.workflow.WorkflowUtility;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IBooleanAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IEnumAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IListAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IStringAttribute;
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IHierarchyAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IUsergroupAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.resources.ResourceBundleFactory;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.config.Metadata;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.MessageTemplateEnumerationItem;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IListAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IObjectType;
import com.idsscheer.webapps.arcm.config.metadata.workflow.IStateMetadata;
import com.idsscheer.webapps.arcm.config.metadata.workflow.ITransitionConditionMetadata;
import com.idsscheer.webapps.arcm.config.metadata.workflow.ITransitionMetadata;

/**
 * <p>
 * Abstract message sending command with the following strategy:<br>
 * Based on a specific workflow state and its commandchain, there will be classes responsible for 
 * the dynamic delivery of notifications to specific receivers depending of which is the current workflow's stage.
 * </p>
 * <p> This is a customization for <b><code>Banco do Brasil Project</code></b> </p>
 * <p>
 * command XML parameters (<b>bold</b>=mandatory): none
 * </p>
 * <p>
 * input work objects (<b>bold</b>=mandatory): none
 * </p>
 * <p>
 * output work objects (<b>bold</b>=mandatory): none
 * </p>
 * @author brmob
 * @since v20150302-CUST-IssueMgmt
 */
public abstract class AbstractIssueSendMailCommandBB implements ICommand {
	private IAppObj issueObj;
	private IUserContext userContext;
	// begin - "new approach"
	protected MessageTemplateEnumerationItem generalIssueTemplate;
	// end - "new approach"
	
	@Override
	public CommandResult execute(CommandContext cc) throws BLException {
		issueObj = cc.getChainContext().getTriggeringAppObj();
		userContext = cc.getChainContext().getUserContext();
		generalIssueTemplate = EnumerationsBB.INITIATORS_BB.ISSUE_GENERAL;
		return executeCommand(cc);
	}
	
	protected IAppObj getTrigerringAppObj(){
		return issueObj;
	}
	
	protected IUserContext getUserContext() {
		return userContext;
	}

	/**
	 * Template method to be implemented for the implementation classes
	 * @param cc - {@link CommandContext}
	 * @return {@link CommandResult}
	 * @throws BLException
	 */
	public abstract CommandResult executeCommand(CommandContext cc) throws BLException;
	
	/**
	 * Based on a specific transition, this method is responsible for which commandchain
	 * is going to be performed.
	 * @return 
	 */
	protected String getCurrentCommandChain(){
		List<ITransitionMetadata> workflowTransitions = WorkflowUtility.getStateMetadata(getTrigerringAppObj()).getTransitions();
		String commandChainID = null;
		for (ITransitionMetadata workflowTransition : workflowTransitions) {
			if (containsWorkflowState(workflowTransition)) {
				commandChainID = workflowTransition.getCommandChainId();
				break;
			}
		}
		if (commandChainID == null) {
			throw new IllegalArgumentException("CommandChainID not found because there isn't any target state in such collection: "+ getAllWorkflowStateID());
		}
		return commandChainID;
	}
	
	/**
	 * This method defines the commandchain that will be performed based on the workflowTransition and
	 * its conditions.
	 * @param workflowTransition - {@link ITransitionMetadata}
	 * @return {@link Boolean}
	 */
	private boolean containsWorkflowState(ITransitionMetadata workflowTransition){
		List<IStateMetadata> targetStates = workflowTransition.getTargetStates();
		List<ITransitionConditionMetadata> conditions = workflowTransition.getConditions();
		boolean workflowStateFound = false;
		for (IStateMetadata targetState : targetStates) {
			if (getAllWorkflowStateID().contains(targetState.getStateId())) {
				
				for (ITransitionConditionMetadata condition : conditions) {
					IAttribute attribute = getTrigerringAppObj().getAttribute(condition.getAttributeType());
					if (attribute instanceof IEnumAttribute) {
						IEnumAttribute enumAttribute = (IEnumAttribute) attribute;
						IEnumerationItem enumerationItem = ARCMCollections.extractSingleEntry(enumAttribute.getRawValue(), true);
						IEnumerationItem conditionEnumItem = enumerationItem.getEnumeration().getItemById(condition.getToAttributeValue());
						
						if (conditionEnumItem.isVirtual() && 
								conditionEnumItem.getRelatedEnumerationItemIds().contains(enumerationItem.getId())) {
							if (conditions.indexOf(condition) < conditions.size()-1) {
								continue;
							} else
							workflowStateFound = true;
							break;
						} else if (enumerationItem.getId().equals(condition.getToAttributeValue())) {
							if (conditions.indexOf(condition) < conditions.size()-1) {
								continue;
							}
							workflowStateFound = true;
							break;
						}
					} else if (attribute instanceof IBooleanAttribute) {
						IBooleanAttribute booleanAttribute = (IBooleanAttribute) attribute;
						
						if (booleanAttribute.getRawValue().equals(Boolean.parseBoolean(condition.getToAttributeValue()))) {
							if (conditions.indexOf(condition) < conditions.size()-1) {
								continue;
							}
							workflowStateFound = true;
							break;
						}
					} else {
						throw new IllegalArgumentException("The type of: "+condition.getAttributeType().getType()+" is not supported yet.");
					}
				}
			}
		}
		return workflowStateFound;
	}
	
	/**
	 * Each implementation class will have defined its specific workflow states.
	 * @return {@link List} - All workflow states configured to such implementation class.
	 */
	protected abstract List<String> getAllWorkflowStateID();
	
	/**
	 * Method responsible for the notification feature.
	 * @param cc - {@link CommandContext}
	 * @param messageTemplate - {@link IEnumerationItem}
	 * @param receivers - {@link IListAttributeType}
	 * @deprecated - substituted by {@link #sendMessage(CommandContext, IEnumerationItem, IListAttributeType, IEnumerationItem, IListAttributeType...)}
	 */
	@Deprecated
	protected void sendMessage(CommandContext cc, IEnumerationItem messageTemplate, final IEnumerationItem status, IListAttributeType... receivers ){
		MessageUtility messageUtility = MessageUtility.getInstance();

		List<IAppObj> toUsers = ARCMCollections.createList();
		List<IAppObj> ccUsers = getCCReceivers(receivers);
		
		if (receivers.length > 0) {
			
			toUsers = messageUtility.getUsers(issueObj, receivers[0].getId());
			
			
			if (!toUsers.isEmpty()) {
				for (IAppObj toUser : toUsers) {
					IMessage message = MessageFactory.createMessage(toUser, messageTemplate, userContext.getUser(), issueObj);
					addAdditionalParameters(cc, message, status, null);
					
					if (!ccUsers.isEmpty()) {
						message.addCC(ccUsers);
					}
					cc.getChainContext().send(message);
				}
			}
			
		} else {
            throw new IllegalArgumentException("receivers must not be null");
        }
		
	}
	
	/**
	 * Method used to do notifications using user group organizational unit emails. <br>
	 * The attribute <b>receivers</b> will hold all receivers (to and cc).
	 * @param cc - {@link CommandContext}
	 * @param messageTemplate - {@link IEnumerationItem}
	 * @param sender - {@link IListAttributeType}
	 * @param status - {@link IEnumerationItem}
	 * @param receivers - {@link IListAttributeType}
	 */
	protected void sendMessage(CommandContext cc, IEnumerationItem messageTemplate, IListAttributeType sender, final IEnumerationItem status, IListAttributeType... receivers){
		IListAttribute senderAttr = issueObj.getAttribute(sender);
		IAppObj senderUserGroup = ARCMCollections.extractSingleEntry(senderAttr.getElements(getUserContext()), true);
		
		IStringAttribute senderMail = UsergroupUtilityBB.getUserGroupEmailByOrgUnitStructure(senderUserGroup);
		
		IListAttribute receiverAttr = issueObj.getAttribute(receivers[0]);
		IAppObj receiverUserGroup = ARCMCollections.extractSingleEntry(receiverAttr.getElements(getUserContext()), true);
		
		IStringAttribute receiverMail = UsergroupUtilityBB.getUserGroupEmailByOrgUnitStructure(receiverUserGroup);
		
		IMessage message = MessageFactory.createMessage(receiverMail.getPersistentRawValue(), messageTemplate, senderMail.getPersistentRawValue(), issueObj);
		
		for (int i = 1; i < receivers.length; i++) {
			IListAttribute ccAttr = issueObj.getAttribute(receivers[i]);
			
			IAppObj ccUserGroup = ARCMCollections.extractSingleEntry(ccAttr.getElements(getUserContext()), true);
			IStringAttribute ccMail = UsergroupUtilityBB.getUserGroupEmailByOrgUnitStructure(ccUserGroup);
			
			message.addCCMailAddress(ccMail.getPersistentRawValue());
		}
		
		addAdditionalParameters(cc, message, status, receiverUserGroup);
		addSenderSignature(message, senderUserGroup);
		cc.getChainContext().send(message);
	}
	
	protected List<IAppObj> getCCReceivers(IListAttributeType[] receivers){
		MessageUtility messageUtility = MessageUtility.getInstance();
		List<IAppObj> ccUsers = ARCMCollections.createList();

		if (receivers.length > 1) {
			for (int i = 1; i < receivers.length; i++) {
				ccUsers.addAll(messageUtility.getUsers(issueObj, receivers[i].getId()));
			}
		}
		
		return ccUsers;
	}
	
	protected void addAdditionalParameters(final CommandContext cc, final IMessage message, final IEnumerationItem status, final IAppObj receiverUserGroup) {
		addUserGroupOrgUnit(cc, message, IIssueAttributeTypeBB.LIST_MGMT_APPROVERL1_GROUP, IIssueAttributeTypeBB.STR_MGMT_APPROVERL1_GROUP);
		addUserGroupOrgUnit(cc, message, IIssueAttributeTypeBB.LIST_IMPL_APPROVERL1_GROUP, IIssueAttributeTypeBB.STR_IMPL_APPROVERL1_GROUP);
		addUserGroupOrgUnit(cc, message, IIssueAttributeTypeBB.LIST_TECH_APPROVERL1_GROUP, IIssueAttributeTypeBB.STR_TECH_APPROVERL1_GROUP);
		
		String statusKey = status.getPropertyKey();
		if (statusKey != null) {
			/*
			 * Foi alterado 'userContext.getLanguage()' para 'new Locale("pt")'
			 * Isso foi feito para forçar o portugues independente do contexto do usuário
			 * Se houver reclamação, favor voltar para o anterior
			 */
            String statusUIValue = ResourceBundleFactory.getBundle(new Locale("pt")).getString(statusKey);
            if(status.getEnumeration().getId().equals(IIssueAttributeTypeBB.STR_STANDARD_APPROVERL1_STATUS) || 
            		status.getEnumeration().getId().equals(IIssueAttributeTypeBB.STR_STANDARD_APPROVERL2_STATUS)){
            	statusUIValue += " ("+receiverUserGroup.toString().split(" ")[0]+")";			
            }
			message.addToContext("groupStatus", statusUIValue);
		}
		else {
			message.addToContext("groupStatus", "N/A");
		}
	}	

	protected void addUserGroupOrgUnit(final CommandContext cc, final IMessage message, final IListAttributeType listAttributeType, final String group) {
		IListAttribute listAttribute = issueObj.getAttribute(listAttributeType);
        List<IAppObj> list = listAttribute.getElements(userContext);
        IAppObj appObj = ARCMCollections.extractSingleEntry(list, false);
        if (appObj != null) {
        	addOrgunitName(appObj, message, group);
        }	
        else {
        	String attributeTypeStr = listAttributeType.getId().replace('1', '2');
        	IObjectType issueObjtype = Metadata.getMetadata().getObjectType("ISSUE");
        	IListAttributeType listAttributeType2 = issueObjtype.getAttribute(attributeTypeStr);
        	listAttribute = issueObj.getAttribute(listAttributeType2);
            list = listAttribute.getElements(userContext);
            appObj = ARCMCollections.extractSingleEntry(list, false);
            if (appObj != null) {
            	addOrgunitName(appObj, message, group);
            }
        	else {
        		message.addToContext(group + "OrgUnitName", "N/A");
        	}
        }
	}
	
	protected void addOrgunitName(IAppObj appObj, final IMessage message, final String group) {
    	IListAttribute bbOrgunitListAttribute = appObj.getAttribute(IUsergroupAttributeTypeBB.LIST_BB_ORGUNIT);
    	List<IAppObj> bbOrgunitList = bbOrgunitListAttribute.getElements(userContext);
    	IAppObj orgunitAppObj = ARCMCollections.extractSingleEntry(bbOrgunitList, false);
    	if (orgunitAppObj != null) {
    		message.addToContext(group + "OrgUnitName", orgunitAppObj.getRawValue(IHierarchyAttributeType.ATTR_NAME));
    	}
    	else {
    		message.addToContext(group + "OrgUnitName", "N/A");
    	}
	}
	
	protected void addSenderSignature(IMessage message, IAppObj senderUserGroup){
		IStringAttribute senderSignatureAttr = UsergroupUtilityBB.getRelatedOrgUnitAttrByUserGroup(senderUserGroup, IUsergroupAttributeTypeBB.ATTR_NAME);
		message.addToContext("senderSignature", senderSignatureAttr.getRawValue());
	}
}
