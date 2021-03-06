package com.idsscheer.webapps.arcm.bl.models.filter.attribute.impl.administration;

import java.util.ArrayList;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.models.filter.attribute.impl.DynamicDateRangeFilterAttributeBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.HierarchyTypeEnumerationItem;
import com.idsscheer.webapps.arcm.config.metadata.filter.IFilterAttributeType;

/**
 * This is a customization to hide/show fields in filters dynamically.
 * <p> This is a customization for <b><code>Banco do Brasil Project</code></b> </p>
 * @see DynamicDateRangeFilterAttributeBB
 */

public class ProcessHierarchyDateRangeFilterAttributeBB extends DynamicDateRangeFilterAttributeBB {

	/**
	 * @param userContext
	 * @param attributeConfig
	 */
	public ProcessHierarchyDateRangeFilterAttributeBB(IUserContext userContext, IFilterAttributeType attributeConfig) {
		super(userContext, attributeConfig);
	}

	/* (non-Javadoc)
	 * @see com.idsscheer.webapps.arcm.bl.models.filter.attribute.impl.FilterAttribute#isVisible()
	 */
	@Override
	public boolean isVisible() {
		@SuppressWarnings("unchecked")
		HierarchyTypeEnumerationItem leadingAttrValue = ((ArrayList<HierarchyTypeEnumerationItem>) leadingFilterAttr.getRawValue(leadingAttrName)).get(0);
		if (!leadingAttrValue.getId().equals(Enumerations.HIERARCHY_TYPE.PROCESS.getId())) {
			return false;
		}
		return super.isVisible();
	}	
}