package com.idsscheer.webapps.arcm.dl.framework.viewhandler;

import java.util.ArrayList;
import java.util.List;

import com.idsscheer.webapps.arcm.dl.framework.IRightsFilterCriteria;

public class ViewHandlerHelperBB {
	
	@SuppressWarnings("rawtypes")
	public static void removeClient(List<IRightsFilterCriteria> rightsFilters) {
		IRightsFilterCriteria foundCriteria = null;
		for (IRightsFilterCriteria filterCriteria : rightsFilters) {
			Object value = filterCriteria.getValue();
			if (value instanceof ArrayList && !((ArrayList) value).isEmpty()) {
				Object item = ((ArrayList) value).get(0);
				if (item instanceof String && item.equals("_none_client_")) {
					foundCriteria = filterCriteria;
				}
			}
		}
		if (foundCriteria != null) {
			rightsFilters.remove(foundCriteria);
		}
	}
}