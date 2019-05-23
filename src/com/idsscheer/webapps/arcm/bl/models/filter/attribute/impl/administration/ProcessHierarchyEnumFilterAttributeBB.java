/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.models.filter.attribute.impl.administration;

import java.util.ArrayList;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.models.filter.attribute.impl.DynamicEnumFilterAttributeBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.HierarchyTypeEnumerationItem;
import com.idsscheer.webapps.arcm.config.metadata.filter.IFilterAttributeType;

/**
 * This is a customization to hide/show fields in filters dynamically.
 * <p> This is a customization for <b><code>Banco do Brasil Project</code></b> </p>
 * @since v20150126-CUST-Compliance
 * @author brmob
 * @see DynamicEnumFilterAttributeBB
 */
public class ProcessHierarchyEnumFilterAttributeBB extends DynamicEnumFilterAttributeBB {
	
	/**
	 * @param userContext
	 * @param attributeConfig
	 */
	public ProcessHierarchyEnumFilterAttributeBB(IUserContext userContext,
			IFilterAttributeType attributeConfig) {
		super(userContext, attributeConfig);
	}

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
