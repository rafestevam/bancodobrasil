/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.models.filter.attribute.impl.administration;

import java.util.ArrayList;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.models.filter.attribute.impl.DynamicNumberRangeFilterAttributeBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsBB;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.HierarchyTypeEnumerationItem;
import com.idsscheer.webapps.arcm.config.metadata.filter.IFilterAttributeType;

/**
 * @author Administrator
 *
 */
public class ICRHierarchyNumberRangeFilterAttribute extends
		DynamicNumberRangeFilterAttributeBB {

	public ICRHierarchyNumberRangeFilterAttribute(IUserContext userContext,
			IFilterAttributeType attributeConfig) {
		super(userContext, attributeConfig);
	}
	
	/**
	 * @param userContext
	 * @param attributeConfig
	 */
	
	
	@Override
	public boolean isVisible() {
		@SuppressWarnings("unchecked")
		HierarchyTypeEnumerationItem leadingAttrValue = ((ArrayList<HierarchyTypeEnumerationItem>) leadingFilterAttr.getRawValue(leadingAttrName)).get(0);
		if (!leadingAttrValue.getId().equals(EnumerationsBB.HIERACHY_TYPE_BB.BB_ICR.getId())) {
			return false;
		}
		return super.isVisible();
	}
}
