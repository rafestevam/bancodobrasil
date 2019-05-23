/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.models.filter.attribute.impl.administration;

import java.util.ArrayList;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.models.filter.attribute.impl.DynamicStringFilterAttributeBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.HierarchyTypeEnumerationItem;
import com.idsscheer.webapps.arcm.config.metadata.filter.IFilterAttributeType;

/**
 * @author Administrator
 *
 */
public class ApplicationSystemHierarchyStringFilterAttributeBB extends
		DynamicStringFilterAttributeBB {

	/**
	 * @param userContext
	 * @param attributeConfig
	 */
	public ApplicationSystemHierarchyStringFilterAttributeBB(
			IUserContext userContext, IFilterAttributeType attributeConfig) {
		super(userContext, attributeConfig);
	}
	/* (non-Javadoc)
	 * @see com.idsscheer.webapps.arcm.bl.models.filter.attribute.impl.FilterAttribute#isVisible()
	 */
	@Override
	public boolean isVisible() {
		@SuppressWarnings("unchecked")
		HierarchyTypeEnumerationItem leadingAttrValue = ((ArrayList<HierarchyTypeEnumerationItem>) leadingFilterAttr.getRawValue(leadingAttrName)).get(0);
		if (!leadingAttrValue.getId().equals(Enumerations.HIERARCHY_TYPE.APPLICATIONSYSTEMTYPE.getId())) {
			return false;
		}
		return super.isVisible();
	}
}
