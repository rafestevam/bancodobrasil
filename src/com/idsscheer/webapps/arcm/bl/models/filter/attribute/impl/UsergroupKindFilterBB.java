package com.idsscheer.webapps.arcm.bl.models.filter.attribute.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.models.filter.attribute.IPredefinedFilterValue;
import com.idsscheer.webapps.arcm.common.support.ConfigParameter;
import com.idsscheer.webapps.arcm.common.support.ConfigParameterType;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumeration;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.config.metadata.filter.IFilterAttributeType;
import com.idsscheer.webapps.arcm.dl.framework.DataLayerComparator;
import com.idsscheer.webapps.arcm.dl.framework.IFilterCriteria;
import com.idsscheer.webapps.arcm.dl.framework.ISimpleFilterCriteria;

public class UsergroupKindFilterBB extends BasePredefinedFilterAttribute {
    protected static final Log log = LogFactory.getLog(UsergroupKindFilterBB.class);

    protected static final String SUBVIEW_NOT_DEACTIVATED_ID = "usergroup2userSubViewNotDeactivated";
    protected static final String SUBVIEW_NOT_EMPTY_ID = "usergroup2userSubViewNotEmpty";
    
    public static final String PARAM_USERGROUPS = "usergroups";

	@ConfigParameter(value = PARAM_USERGROUPS, type = ConfigParameterType.PROPERTY_KEY, optional = false)
    protected String userGroups;
    
    protected IEnumeration enumeration;

    public UsergroupKindFilterBB(IUserContext userContext, IFilterAttributeType attributeConfig) {
        super(userContext, attributeConfig);
        enumeration = attributeConfig.getEnumeration();
    }
    
    @Override
    public List<IPredefinedFilterValue> getPredefinedValuesInternal() {
        ArrayList<IPredefinedFilterValue> arrayList = new ArrayList<IPredefinedFilterValue>();

    	for (final IEnumerationItem enumerationItem : enumeration.getItems()) {
    		arrayList.add(new PredefinedFilterValue(enumerationItem.getValue(), enumerationItem.getPropertyKey(), false));
		}
        return arrayList;
    }

    @Override
    public List<IFilterCriteria> getFilterCriteria() {
    	List<IFilterCriteria> list = new ArrayList<IFilterCriteria>(1);

    	if (isEmpty()) {
    		list = Collections.emptyList();
    	}    
    	else {
    		List<IFilterCriteria> subFilters = new ArrayList<IFilterCriteria>();    		
    		
    		userGroups = getParameter(PARAM_USERGROUPS);
    		String[] userGroupsList = userGroups.split(",");
    		for (String userGroup : userGroupsList) {
				if (selectedValue.getId().equals("1")) {
					ISimpleFilterCriteria ug = filterFactory.getSimpleFilterCriteria(userGroup, DataLayerComparator.NOTIN, null);
					ug.setSubView(SUBVIEW_NOT_EMPTY_ID);
					subFilters.add(ug);
				}
				else {
					ISimpleFilterCriteria ug = filterFactory.getSimpleFilterCriteria(userGroup, DataLayerComparator.NOTIN, null);
					ug.setSubView(SUBVIEW_NOT_DEACTIVATED_ID);
					subFilters.add(ug);
				}
    		}	
    		if (userGroupsList.length > 1) {
	    		IFilterCriteria orCriteria = filterFactory.or(subFilters);
	    		list.add(orCriteria);
    		}
    		else {
    			list.add(subFilters.get(0));
    		}	
    	}
		return list;
    }
}