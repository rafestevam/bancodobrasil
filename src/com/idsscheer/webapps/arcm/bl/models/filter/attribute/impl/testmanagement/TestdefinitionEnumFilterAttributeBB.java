/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.models.filter.attribute.impl.testmanagement;

import java.util.ArrayList;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.models.filter.attribute.impl.DynamicEnumFilterAttributeBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsBB;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.EnumerationItem;
import com.idsscheer.webapps.arcm.config.metadata.filter.IFilterAttributeType;

/**
 * This is a customization to hide/show fields in filters dynamically.
 * <p> This is a customization for <b><code>Banco do Brasil Project</code></b> </p>
 * @since v20150126-CUST-Compliance
 * @author brmob
 * @see DynamicEnumFilterAttributeBB
 */
public class TestdefinitionEnumFilterAttributeBB extends DynamicEnumFilterAttributeBB {
	
	/**
	 * @param userContext
	 * @param attributeConfig
	 */
	public TestdefinitionEnumFilterAttributeBB(IUserContext userContext,
			IFilterAttributeType attributeConfig) {
		super(userContext, attributeConfig);
	}
	
	@Override
	public boolean isVisible() {
		@SuppressWarnings("unchecked")
		ArrayList<EnumerationItem> leadingAttrArray = (ArrayList<EnumerationItem>) leadingFilterAttr.getRawValue(leadingAttrName);
		if (!leadingAttrArray.contains(EnumerationsBB.TESTTYPE_BB.BB_VALIDACAO)) {
			return false;
		}
		return super.isVisible();
	}

}
