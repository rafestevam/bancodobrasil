package com.idsscheer.webapps.arcm.bl.framework.jobs.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.dataaccess.query.IViewQuery;
import com.idsscheer.webapps.arcm.bl.dataaccess.query.QueryFactory;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IViewObj;
import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.dl.framework.DataLayerComparator;
import com.idsscheer.webapps.arcm.dl.framework.IFilterCriteria;
import com.idsscheer.webapps.arcm.dl.framework.dllogic.SimpleFilterCriteria;

public class RightsHelperBB {
	
    public static boolean userHasUpperRole(final long executingUserId, final List<IEnumerationItem> roles, final IUserContext userContext) {
    	List<Long> usergroups = getUsergroupsForRoles(executingUserId, roles, 
    			Collections.unmodifiableList(Arrays.asList(Enumerations.USERROLE_LEVEL.CROSSCLIENT, Enumerations.USERROLE_LEVEL.CLIENT)),
    			userContext);
    	return (usergroups.size() > 0);
    }
	
    public static List<Long> getObjectSpecificGroups(final long executingUserId, final IEnumerationItem role, final IUserContext userContext) {
    	return getUsergroupsForRoles(executingUserId, Collections.unmodifiableList(Arrays.asList(role)), Collections.unmodifiableList(Arrays.asList(Enumerations.USERROLE_LEVEL.OBJECT)), userContext);
	}    
	
	protected static List<Long> getUsergroupsForRoles(final long executingUserId, final List<IEnumerationItem> roles, final List<IEnumerationItem> levels, final IUserContext userContext) {
		List<Long> result = new ArrayList<Long>();
		
		List<IFilterCriteria> filterCriteria = new ArrayList<IFilterCriteria>();
		filterCriteria.add(new SimpleFilterCriteria("u_obj_id", DataLayerComparator.EQUAL, executingUserId));
        if (roles != null) {
        	List<String> roleValues = new ArrayList<String>();
        	for (IEnumerationItem role : roles) {
        		roleValues.add(role.getValue());
			}
    		filterCriteria.add(new SimpleFilterCriteria("ug_role", DataLayerComparator.IN, roleValues));
        }
        if (levels != null) {
        	List<String> levelValues = new ArrayList<String>();
        	for (IEnumerationItem level : levels) {
        		levelValues.add(level.getValue());
			}
    		filterCriteria.add(new SimpleFilterCriteria("ug_rolelevel", DataLayerComparator.IN, levelValues));
        }	
        IViewQuery viewQuery = null;
        try {
			viewQuery = QueryFactory.createQuery(userContext, "usergroupForRole", filterCriteria, null, null);
			long size = viewQuery.getSize();
	        if(size>0) {
	            Iterator<IViewObj> objIterator = viewQuery.getResultIterator();
	            if (objIterator.hasNext()) {
	                IViewObj viewObj = objIterator.next();
	                Long usergroupId = (Long)viewObj.getRawValue("ug_obj_id");
	                result.add(usergroupId);
	            }
	        }
        }
	    finally {
	    	viewQuery.release();
	    }
        return result;
	}
}
