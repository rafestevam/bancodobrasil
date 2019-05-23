package com.idsscheer.webapps.arcm.dl.framework.viewhandler;

import java.util.List;

import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations;
import com.idsscheer.webapps.arcm.dl.framework.BusViewException;
import com.idsscheer.webapps.arcm.dl.framework.IDataLayerObject;
import com.idsscheer.webapps.arcm.dl.framework.IFilterCriteria;
import com.idsscheer.webapps.arcm.dl.framework.IRightsFilterCriteria;
import com.idsscheer.webapps.arcm.dl.framework.dllogic.QueryDefinition;

public class CustomIgnoreRoleViewHandler implements IViewHandler{

	@Override
	public void handleView(QueryDefinition query, List<IRightsFilterCriteria> rightsFilters, List<IFilterCriteria> filters, IDataLayerObject currentObject, QueryDefinition parentQuery) throws BusViewException {
        for (int i = 0; i < rightsFilters.size(); i++) {
            if (rightsFilters.get(i).getRoleLevel() != null && rightsFilters.get(i).getRoleLevel().equals(Enumerations.USERROLE_LEVEL.OBJECT)) {
                rightsFilters.remove(i);
                i--;
            }
        }
	}
}