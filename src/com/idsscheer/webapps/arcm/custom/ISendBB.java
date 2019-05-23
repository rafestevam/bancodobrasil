package com.idsscheer.webapps.arcm.custom;

import com.idsscheer.webapps.arcm.bl.framework.message.IMessage;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;

public interface ISendBB {
	public void addAdditionalParameters (final IAppObj appObj, final IMessage message);
	public IAppObj getAppObjToAdd();
}