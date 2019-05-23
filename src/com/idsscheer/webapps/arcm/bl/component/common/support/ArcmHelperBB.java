package com.idsscheer.webapps.arcm.bl.component.common.support;

import java.util.List;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IListAttribute;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IListAttributeType;

public class ArcmHelperBB {
	
	public static List<IOVID> getListAttributeOvids(IAppObj appObj, IListAttributeType listAttr) {
		IListAttribute listAttribute = appObj.getAttribute(listAttr);
		return listAttribute.getElementIds();
	}
	
	public static IOVID getListAttributeOvid(IAppObj appObj, IListAttributeType listAttr) {
		List<IOVID> listAttributeOvids = getListAttributeOvids(appObj, listAttr);
		if (listAttributeOvids != null && listAttributeOvids.size() > 0) {
			return listAttributeOvids.get(0);
		}
		else {
			return null;
		}
	}

	public static IAppObj getListAttributeAppObj(IAppObj appObj, IListAttributeType listAttr, IUserContext userContext) {
		IListAttribute listAttribute = appObj.getAttribute(listAttr);
		List<IAppObj> appObjs = listAttribute.getElements(userContext);
		return ARCMCollections.extractSingleEntry(appObjs, false);
	}

	public static long getListAttributeId(IAppObj appObj, IListAttributeType listAttr) {
		IOVID ovid = getListAttributeOvid(appObj, listAttr);
		if (ovid == null) {
			return -1;
		}
		else {
			return ovid.getId();
		}
	}
}