package com.idsscheer.webapps.arcm.bl.models.filter.attribute.impl.administration;

import java.util.ArrayList;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.models.filter.attribute.impl.DynamicStringFilterAttributeBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsBB;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.EnumerationItem;
import com.idsscheer.webapps.arcm.config.metadata.filter.IFilterAttributeType;

/**
 * This is a customization to hide/show fields in filters dynamically.
 * <p>
 * This is a customization for <b><code>Banco do Brasil Project</code></b>
 * </p>
 * 
 * @since v20150302-CUST-PRDIntegration
 * @author josm
 * @see DynamicStringFilterAttributeBB
 */
public class ProductModalityHierarchyStringFilterAttributeBB extends
		DynamicStringFilterAttributeBB {

	public ProductModalityHierarchyStringFilterAttributeBB(
			IUserContext userContext, IFilterAttributeType attributeConfig) {
		super(userContext, attributeConfig);
	}

	@Override
	public boolean isVisible() {
		@SuppressWarnings("unchecked")
		ArrayList<EnumerationItem> leadingAttrArray = (ArrayList<EnumerationItem>) leadingFilterAttr.getRawValue(leadingAttrName);
		if (!leadingAttrArray.contains(EnumerationsBB.HIERACHY_TYPE_BB.BB_PRODUCT)) {
			return false;
		}
		return super.isVisible();
	}
}
