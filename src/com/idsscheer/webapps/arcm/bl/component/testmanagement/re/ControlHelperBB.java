package com.idsscheer.webapps.arcm.bl.component.testmanagement.re;

import com.idsscheer.webapps.arcm.bl.models.objectmodel.IUsergroupAppObj;
import com.idsscheer.webapps.arcm.bl.re.impl.REEnvironment;
import java.util.Iterator;
import java.util.List;
public class ControlHelperBB extends ControlHelper
{
	//Checks if the user is in a specific role Group and this group has a specific role Level
	public static boolean hasRoleLevel(String groupLevel, String groupFunction){
		String stringLevel="";
		switch(groupLevel){
			case "crossclient": stringLevel="value=X";
			break;
			case "client": stringLevel="value=C";
			break;
			case "object": stringLevel="value=O";
			break;
			default: break;
		}
		REEnvironment env = REEnvironment.getInstance();
		List<IUsergroupAppObj> userGroups=env.getUserContext().getUserRelations().getGroups();
		Iterator<IUsergroupAppObj>grupo=userGroups.iterator();
		if(grupo.hasNext()){
			IUsergroupAppObj userGroup=(IUsergroupAppObj) grupo.next();
			String roleLevel=userGroup.getRawValue(userGroup.getAttributeType("rolelevel")).toString();
			String roleFunction=userGroup.getRawValue(userGroup.getAttributeType("role")).toString();
			if(roleLevel!=null && roleFunction!=null){
				if((roleLevel.toLowerCase().contains(stringLevel.toLowerCase()))&& (roleFunction.toLowerCase().contains(groupFunction.toLowerCase()))){
					return true;
				}		
			}
		}
		return false;			
	}
}
