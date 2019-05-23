/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.framework.jobs.util;

import java.util.Set;

import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IObjectType;

/**
 * @author Administrator
 *
 */
public class MessageInformationBB extends MessageInformation {
	
	private IAppObj sender;

	public MessageInformationBB(IEnumerationItem template, Set<IOVID> receivers, String client, IObjectType objectType, LinkedListInformation linkedListInformation, IAppObj sender) {
		super(template, receivers, client, objectType, linkedListInformation);
		this.sender = sender;
	}

	public IAppObj getSender() {
		return sender;
	}

	public void setSender(IAppObj sender) {
		this.sender = sender;
	}

}
