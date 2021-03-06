package com.idsscheer.webapps.arcm.bl.models.filter.attribute.impl;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.models.filter.attribute.IFilterAttribute;
import com.idsscheer.webapps.arcm.config.metadata.filter.IFilterAttributeType;

/**
 * This is a customization to hide/show fields in filters dynamically.
 * <p> This is a customization for <b><code>Banco do Brasil Project</code></b> </p>
 * @since v20150316-CUST-HierarchyMgmt
 * @author brmob
 * @see NumberRangeFilterAttribute
 */
public class DynamicNumberRangeFilterAttributeBB extends NumberRangeFilterAttribute {
	
	protected String leadingAttrName;
	protected IFilterAttribute leadingFilterAttr;
	
	/**
	 * @param userContext
	 * @param attributeConfig
	 */
	public DynamicNumberRangeFilterAttributeBB(IUserContext userContext,
			IFilterAttributeType attributeConfig) {
		super(userContext, attributeConfig);
	}
	
	@Override
	public void update(String leadingAttrName,
			IFilterAttribute leadingFilterAttr) {
		this.leadingAttrName = leadingAttrName;
		this.leadingFilterAttr = leadingFilterAttr;
	}
}
