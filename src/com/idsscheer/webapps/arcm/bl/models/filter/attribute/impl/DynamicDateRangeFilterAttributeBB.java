package com.idsscheer.webapps.arcm.bl.models.filter.attribute.impl;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.models.filter.attribute.IFilterAttribute;
import com.idsscheer.webapps.arcm.config.metadata.filter.IFilterAttributeType;

/**
 * This is a customization to hide/show fields in filters dynamically.
 * <p> This is a customization for <b><code>Banco do Brasil Project</code></b> </p>
 * @see DateRangeFilterAttribute
 */
public class DynamicDateRangeFilterAttributeBB extends DateRangeFilterAttribute {

	protected String leadingAttrName;
	protected IFilterAttribute leadingFilterAttr;
	
	/**
	 * @param p_userContext
	 * @param attributeConfig
	 */
	public DynamicDateRangeFilterAttributeBB(IUserContext p_userContext,
			IFilterAttributeType attributeConfig) {
		super(p_userContext, attributeConfig);
	}
	
	@Override
	public void update(String leadingAttrName,
			IFilterAttribute leadingFilterAttr) {
		this.leadingAttrName = leadingAttrName;
		this.leadingFilterAttr = leadingFilterAttr;
	}
}