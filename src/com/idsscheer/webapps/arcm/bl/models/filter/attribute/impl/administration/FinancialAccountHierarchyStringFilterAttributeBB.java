package com.idsscheer.webapps.arcm.bl.models.filter.attribute.impl.administration;

import java.util.ArrayList;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.models.filter.attribute.impl.DynamicStringFilterAttributeBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.HierarchyTypeEnumerationItem;
import com.idsscheer.webapps.arcm.config.metadata.filter.IFilterAttributeType;

public class FinancialAccountHierarchyStringFilterAttributeBB extends DynamicStringFilterAttributeBB  {

	public FinancialAccountHierarchyStringFilterAttributeBB(
			IUserContext userContext, IFilterAttributeType attributeConfig) {
		super(userContext, attributeConfig);
	}
	@Override
	public boolean isVisible() {
		@SuppressWarnings("unchecked")
		HierarchyTypeEnumerationItem leadingAttrValue = ((ArrayList<HierarchyTypeEnumerationItem>) leadingFilterAttr.getRawValue(leadingAttrName)).get(0);
		if (!leadingAttrValue.getId().equals(Enumerations.HIERARCHY_TYPE.FINANCIALACCOUNT.getId())) {
			return false;
		}
		return super.isVisible();
	}

}
