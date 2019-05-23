package com.idsscheer.webapps.arcm.dl.framework.viewhandler;

import java.util.List;

import com.idsscheer.webapps.arcm.dl.framework.BusViewException;
import com.idsscheer.webapps.arcm.dl.framework.IDataLayerObject;
import com.idsscheer.webapps.arcm.dl.framework.IFilterCriteria;
import com.idsscheer.webapps.arcm.dl.framework.IRightsFilterCriteria;
import com.idsscheer.webapps.arcm.dl.framework.dllogic.QueryDefinition;

public class ProcessFilterViewHandler implements IViewHandler {

	@Override
	public void handleView(QueryDefinition query, List<IRightsFilterCriteria> rightsFilters, List<IFilterCriteria> filters, IDataLayerObject currentObject, QueryDefinition parentQuery) throws BusViewException {
		for (int i = 0; i < filters.size(); i++) {
			IFilterCriteria filter = filters.get(i);
			if(filter.getAttributeAliasName().equals("processrelatedto")){
				String value = (String) filter.getValue();
				filters.remove(i);
				try {
					/*
					IQueryObjectDefinition defTC = query.getQueryObjectMap().get("VO_testcase_refs");
					IQueryObjectDefinition defHH = query.getQueryObjectMap().get("VO_hierarchy_refs");

					if(value.equals("0"))
						query.join(defTC, Collections.singletonList(LinkTypeHelper.getInstance().getLinkType(54)), defHH, DataLayerJoin.INNER, null);
					else if(value.equals("1"))
						query.join(defTC, Collections.singletonList(LinkTypeHelper.getInstance().getLinkType(10051)), defHH, DataLayerJoin.INNER, null);
*/
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				/*
				if(value.equals("0"))
					query.join(parent, listAttrMetadata.getAttributeName(), child, com.idsscheer.webapps.arcm.dl.framework.DataLayerJoin.INNER, false, attendVersionizing, null);

				 */
				break;
			}
		}
	}
}