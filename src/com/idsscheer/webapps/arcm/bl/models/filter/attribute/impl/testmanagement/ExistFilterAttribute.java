package com.idsscheer.webapps.arcm.bl.models.filter.attribute.impl.testmanagement;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.models.filter.attribute.impl.StringFilterAttribute;
import com.idsscheer.webapps.arcm.bl.models.filter.config.FilterAttributeConfig;
import com.idsscheer.webapps.arcm.config.metadata.filter.IFilterAttributeType;
import com.idsscheer.webapps.arcm.dl.framework.DataLayerComparator;
import com.idsscheer.webapps.arcm.dl.framework.IFilterCriteria;

public class ExistFilterAttribute extends StringFilterAttribute
{
	
	String value = null;
	
	public ExistFilterAttribute(IUserContext userContext, IFilterAttributeType attributeConfig)
	{
		super(userContext, attributeConfig);
	}

	public void setFilterValue(Locale locale, String viewColumnName, String[] values) {
		value = values[0];
		
		super.setFilterValue(locale, viewColumnName, values);
	}

	public List<IFilterCriteria> getFilterCriteria() {
		
		IFilterCriteria filterCriteria = null;
		if(value.equals("0"))
			filterCriteria = filterFactory.getSimpleFilterCriteria(null, getViewColumnAttributeAlias(), DataLayerComparator.EQUAL, value);
		else if(value.equals("1"))
			filterCriteria = filterFactory.getSimpleFilterCriteria(null, getViewColumnAttributeAlias(), DataLayerComparator.GREATEROREQUAL, value);
		else
			filterCriteria = filterFactory.getSimpleFilterCriteria(null, getViewColumnAttributeAlias(), DataLayerComparator.GREATEROREQUAL, value);
		
        return Collections.singletonList(filterCriteria);
        
	}
}