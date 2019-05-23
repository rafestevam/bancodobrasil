package com.idsscheer.webapps.arcm.custom;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.framework.message.IMessage;
import com.idsscheer.webapps.arcm.bl.framework.message.MessageFactory;
import com.idsscheer.webapps.arcm.bl.framework.message.MessageHandler;
import com.idsscheer.webapps.arcm.bl.framework.transaction.ITransaction;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IListAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IStringAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.TransactionManager;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IUserAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IUsergroupAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IUsergroupAttributeTypeBB;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.MessageTemplateEnumerationItem;

public class SendHelperBB {
    private static Log log = LogFactory.getLog(SendHelperBB.class);

    public static Set<IAppObj> getToUsers(final IAppObj usergroupAppObj, final IUserContext userContext) {
    	Set<IAppObj> result = new HashSet<IAppObj>();
    	
    	IListAttribute usersListAttribute = usergroupAppObj.getAttribute(IUsergroupAttributeTypeBB.LIST_GROUPMEMBERS);
    	if (usersListAttribute != null && usersListAttribute.getSize() > 0) {
			List<IAppObj> usersAppObjList = usersListAttribute.getElements(userContext);
			for (IAppObj userAppObj : usersAppObjList) {
				if (!userAppObj.getRawValue(IUserAttributeType.BASE_ATTR_DEACTIVATED)) {
					result.add(userAppObj);
				}
			}
		}
    	return result;
    }

    /*
     * skipUsers - true: it is known that user group has no users
     */
    public static void send(final IAppObj usergroupAppObj, final MessageTemplateEnumerationItem template, final IUserContext userContext, final ISendBB callBack, final boolean skipUsers) throws Exception {
		Set<IAppObj> toUsersList = new HashSet<IAppObj>();
		if (!skipUsers) {
			toUsersList = getToUsers(usergroupAppObj, userContext);
		}	
		if (!toUsersList.isEmpty()) {
			ITransaction transaction = TransactionManager.getInstance().createTransaction();
	        try {
	        	for (IAppObj appObj : toUsersList) {
	        		IMessage message = MessageFactory.createMessage(appObj, template, userContext.getUser(), callBack.getAppObjToAdd());
	        		callBack.addAdditionalParameters (usergroupAppObj, message);
	        		MessageHandler.getInstance().addMessage(transaction, message);
				}
	            transaction.commit();
	        } catch (Exception e) {
	            transaction.rollback();   
	            throw new Exception(e.toString());
	        }
		}
		else {
			final IStringAttribute emailAttribute = usergroupAppObj.getAttribute(IUsergroupAttributeTypeBB.ATTR_BB_EMAIL);
	        if (!emailAttribute.isEmpty()) {
				ITransaction transaction = TransactionManager.getInstance().createTransaction();
		        try {
		        	IMessage message = MessageFactory.createMessage(emailAttribute.getRawValue(), template, userContext.getUser(), null);
	        		callBack.addAdditionalParameters (usergroupAppObj, message);
		            MessageHandler.getInstance().addMessage(transaction, message);
		            transaction.commit();
		        } catch (Exception e) {
		            transaction.rollback();
		            throw new Exception(e.toString());    
		        }
	        }
			else {
				log.warn("There is no value in email attribute of user group: '" + usergroupAppObj.getRawValue(IUsergroupAttributeType.ATTR_NAME) + "'");
			}
		}
	}

}