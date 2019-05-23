/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.component.common.command.job;

import java.util.List;
import java.util.Set;

import com.aspose.imaging.internal.Exceptions.Exception;
import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.exception.BLException;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandResult;
import com.idsscheer.webapps.arcm.bl.framework.jobs.util.JobMessageContainer;
import com.idsscheer.webapps.arcm.bl.framework.jobs.util.JobMessageContainerBB;
import com.idsscheer.webapps.arcm.bl.framework.jobs.util.LinkedListInformation;
import com.idsscheer.webapps.arcm.bl.framework.jobs.util.MessageInformation;
import com.idsscheer.webapps.arcm.bl.framework.jobs.util.MessageInformationBB;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IListAttribute;
import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IClientAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IObjectAttributeType;
import com.idsscheer.webapps.arcm.common.support.ConfigParameter;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IListAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IObjectType;

/**
 * <p>
 * Prepare job message information by instantiating a MessageInformation object and passing it to the MessageContainer
 * stored in the ChainContext. This command is used to collect messages for GeneratorJob, FollowupGeneratorJob and
 * UpdaterJob.
 * </p>
* <br>
 * <p>
 * command XML parameters (<b>bold</b>=mandatory):
 * <ul>
 *  <li><b>message_template</b>: the template ID to use - String</li>
 *  <li>list_title: the list title as property key - String</li>
 *  <li>list_name: the list ID - String</li>
 *  <li>filter_name: a filter attribute ID from the filter of the list specified by parameter 'list_name'</li>
 *  <li>filter_value: the value for this filter attribute</li>
 * </ul>
 * </p>
 * <p>
 * input work objects (<b>bold</b>=mandatory):
 * <ul>
 *  <li>IJobWorkobjectConstants.WORKOBJECT_MESSAGE_CONTAINER: an object of class MessageContainer where the new message is stored.<br>
 *                                                            If not present then this command does nothing</li>
 * </ul>
 * </p>
 * <p>
 * output work objects (<b>bold</b>=mandatory): none
 * </p>
 * @author brmob
 * @since v20160314-MNT-UATEnhancements
 * @see PrepareJobMessageCommand
 */
public class PrepareJobMessageCommandBB extends PrepareJobMessageCommand {
	
	@ConfigParameter(value = "sender", optional = false)
	public static String PARAMETER_SENDER = "sender";
	
	@Override
	public CommandResult execute(CommandContext cc) throws BLException {
		IUserContext userContext = cc.getChainContext().getUserContext();
        IAppObj obj = cc.getChainContext().getTriggeringAppObj();
        IObjectType objectType = obj.getObjectType();

        String templateID = cc.getCommandXMLParameter(PARAMETER_MESSAGETEMPLATE);
        IEnumerationItem initiator = Enumerations.INITIATORS.ENUM.getItemById(templateID);
        
        // Read sender
        IAppObj sender = getSender(cc, obj, userContext);
        
        // Read receivers
        Set<IOVID> receiverOVIDsSet = readReceivers(obj, userContext);

        // Read client
        String clientSign = obj.getAttribute(IObjectAttributeType.BASE_ATTR_CLIENT_SIGN).getRawValue().getAttribute(IClientAttributeType.ATTR_SIGN).getRawValue();


        // Set linked list information
        LinkedListInformation linkedListInformation = getLinkedListInformation(cc);

        // Set message information object
        MessageInformation messageInformation = new MessageInformationBB(initiator,
                                                                       receiverOVIDsSet,
                                                                       clientSign,
                                                                       objectType,
                                                                       linkedListInformation,
                                                                       sender);

        JobMessageContainer messageContainer = (JobMessageContainer) cc.getChainContext().get( IJobWorkobjectConstants.WORKOBJECT_MESSAGE_CONTAINER );
        
        
        

        if (messageContainer != null)
            messageContainer.addMessageInformation(messageInformation);
		
		
		return CommandResult.OK;
		
	}
	
	protected IAppObj getSender(CommandContext cc, IAppObj triggeringAppObj, IUserContext userContext){
		
		String senderParameter = cc.getCommandXMLParameter(PARAMETER_SENDER);
        IAttributeType senderAttrType = triggeringAppObj.getAttributeType(senderParameter);
        IAppObj sender = null;
        
        if (senderAttrType instanceof IListAttributeType) {
			IListAttributeType senderListAttrType = (IListAttributeType) senderAttrType;
			IListAttribute senderAttr = triggeringAppObj.getAttribute(senderListAttrType);
			
			List<IAppObj> senders = senderAttr.getElements(userContext);
			if (senders.size() > 1) {
				throw new Exception("sender attribute does not support more than a sender...");
			}
			
			sender = ARCMCollections.extractSingleEntry(senders, true);
			
		}
        return sender;

	}
	

}
