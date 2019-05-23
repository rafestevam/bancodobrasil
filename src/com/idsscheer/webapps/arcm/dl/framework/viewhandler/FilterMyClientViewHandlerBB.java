package com.idsscheer.webapps.arcm.dl.framework.viewhandler;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IClientAppObj;
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IClientAttributeType;
import com.idsscheer.webapps.arcm.dl.framework.BusViewException;
import com.idsscheer.webapps.arcm.dl.framework.DataLayerComparator;
import com.idsscheer.webapps.arcm.dl.framework.IDataLayerObject;
import com.idsscheer.webapps.arcm.dl.framework.IFilterCriteria;
import com.idsscheer.webapps.arcm.dl.framework.IRightsFilterCriteria;
import com.idsscheer.webapps.arcm.dl.framework.dllogic.FilterFactory;
import com.idsscheer.webapps.arcm.dl.framework.dllogic.QueryDefinition;
import com.idsscheer.webapps.arcm.dl.framework.dllogic.SimpleFilterCriteria;
import com.idsscheer.webapps.arcm.ui.framework.common.UIEnvironmentManager;

public class FilterMyClientViewHandlerBB implements IViewHandler {
	
	protected static final Log log = LogFactory.getLog(CreatedIssuesViewHandler.class);


	public void handleView(QueryDefinition query,
			List<IRightsFilterCriteria> rightsFilters,
			List<IFilterCriteria> filters, IDataLayerObject currentObject,
			QueryDefinition parentQuery) throws BusViewException {

		IUserContext userContext = (UIEnvironmentManager.get()).getUserContext();
		
		if(!userContext.getUserRights().hasRole(EnumerationsBB.USERROLE_TYPE_BB.ISSUECREATOR))
			return;
		
		List<IFilterCriteria> lstFilters = new ArrayList<IFilterCriteria>();
		
		for (IClientAppObj client : userContext.getUserRelations().getClients()) {
			lstFilters.add(new SimpleFilterCriteria("client_sign", DataLayerComparator.EQUAL, client.getRawValue(IClientAttributeType.ATTR_SIGN)));
		}
		
		if(!lstFilters.isEmpty())
			filters.add(new FilterFactory().or(lstFilters));
		
	}

}
