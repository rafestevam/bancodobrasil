package com.aris.arcm.transfer.parser.dynamic;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aris.arcm.transfer.IPostProcessor.ChangeType;
import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.dataaccess.query.IViewQuery;
import com.idsscheer.webapps.arcm.bl.dataaccess.query.QueryFactory;
import com.idsscheer.webapps.arcm.bl.exception.RightException;
import com.idsscheer.webapps.arcm.bl.framework.message.IMessage;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IViewObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IListAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.FacadeFactory;
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IUsergroupAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IUsergroupAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.common.util.ovid.OVIDFactory;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IStringAttributeType;
import com.idsscheer.webapps.arcm.custom.SendHelperBB;

public class UserGroupPostProcessorEngine extends AbstractPostProcessorEngine {
	protected final Log log = LogFactory.getLog(UserGroupPostProcessorEngine.class);
	
	protected IAppObj currentUsergroup = null;

	public UserGroupPostProcessorEngine(IUserContext userContext, String guid, ChangeType changeType) {
		super(userContext, guid, changeType);
	}

    protected void execute() throws Exception {
    	if (isToCheck()) {	    	
	    	currentUsergroup = getAppObj(getGuidRestrictions(guid));
	    	boolean currentDeactivated = currentUsergroup.getRawValue(IUsergroupAttributeType.BASE_ATTR_DEACTIVATED);
	    	if (currentDeactivated) {
	    		// nothing to do
	    	}
	    	else {
	    		int currentCount = countOfUsers(currentUsergroup); 
	    		IAppObj previousUsergroup = getPreviousAppObj(currentUsergroup); 
	    		boolean previousDeactivated = previousUsergroup.getRawValue(IUsergroupAttributeType.BASE_ATTR_DEACTIVATED);
		    	if (previousDeactivated) {
		    		// nothing to do
		    	}
		    	else {
		    		int previousCount = countOfUsers(previousUsergroup);
		    		handleUsergroup(currentCount, previousCount, currentUsergroup, previousUsergroup);
		    	}
	    	}
    	}	
    }
    
    protected boolean isToCheck() {
    	if (changeType.equals(ChangeType.created) || changeType.equals(ChangeType.deactivated) || changeType.equals(ChangeType.unchanged)) {
    		return false;
    	}
    	else {
    		return true;
    	}	
    }
    
	protected int countOfUsers(final IAppObj usergroup) {
		int result = 0;
		IListAttribute usersListAttribute = usergroup.getAttribute(IUsergroupAttributeTypeBB.LIST_GROUPMEMBERS);
		if (usersListAttribute != null && usersListAttribute.getSize() > 0) {
			List<IAppObj> usersAppObjList = usersListAttribute.getElements(userContext);
			/*
			for (IAppObj userAppObj : usersAppObjList) {
				if (!userAppObj.getRawValue(IUserAttributeType.BASE_ATTR_DEACTIVATED)) {
					result ++;
				}
			}
			*/
			result = usersAppObjList.size();
		}
		return result;
	}
	
	protected IAppObj getPreviousAppObj(final IAppObj appObj) throws RightException {
		IAppObj result = null; 
		
        IOVID currentOvid = OVIDFactory.getOVID(appObj.getObjectId());
        IOVID priorOvid = OVIDFactory.getOVID(currentOvid.getId(), appObj.getVersionData().getVersionCount()-1);
        result = facade.load(priorOvid, false);
		return result; 		
	}
	
	protected void handleUsergroup(final int currentCount, final int previousCount, final IAppObj currentUsergroup, final IAppObj previousUsergroup) throws Exception {
		String name = currentUsergroup.getRawValue(IUsergroupAttributeType.ATTR_NAME);
		log.debug("handleUsergroup: " + name + " current count: " + currentCount + " previous count: " + previousCount);
		if ((currentCount > 0 && previousCount > 0) || (currentCount == 0 && previousCount == 0)) {
			// nothing
		}
		else {
			if (isOpenWorkflow(currentUsergroup.getObjectId())) {
				if (previousCount == 0) {
					sendAddedToEmpty(currentUsergroup);
				}
				else {
					sendAllRemoved(currentUsergroup);
				}
			}	
		}
	}
	
	protected boolean isOpenWorkflow(final long userGroupId) {
		IViewQuery query = null;
        try {
        	Map<String, Object> filterMap = new HashMap<String, Object>();
        	filterMap.put("group_id", userGroupId);
        	query = QueryFactory.createQuery(userContext, "openTaskForUG", null, filterMap);
        	long size = query.getSize();
            if(size>0) {
            	Iterator<IViewObj> objIterator = query.getResultIterator();
                IViewObj viewObj = objIterator.next();
                long workflowCount =(Long)viewObj.getRawValue("obj_id");
            	return (workflowCount > 0);
            }
        }    
        finally {
            if(query != null){
                query.release();
            }                        
        }
        return false;
	}
	
	protected void sendAddedToEmpty(final IAppObj usergroupAppObj) throws Exception {
		SendHelperBB.send(usergroupAppObj, EnumerationsBB.INITIATORS_BB.USER_ADDED_EMPTY_GROUP, userContext, this, false);
	}
	
	protected void sendAllRemoved(final IAppObj usergroupAppObj) throws Exception {
		SendHelperBB.send(usergroupAppObj, EnumerationsBB.INITIATORS_BB.LAST_USER_REMOVED_GROUP, userContext, this, true);
	}
	
	@Override
	protected IStringAttributeType getGuidAttribute() {
		return IUsergroupAttributeType.BASE_ATTR_GUID;
	}

	@Override
	protected IAppObjFacade getFacade() {
		return FacadeFactory.getInstance().getAppObjFacade(userContext, ObjectType.USERGROUP);
	}

	@Override
	public void addAdditionalParameters (final IAppObj appObj, final IMessage message) {
		// TODO Auto-generated method stub	
    }

	@Override
	public IAppObj getAppObjToAdd() {
		return currentUsergroup;
	}
}