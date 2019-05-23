/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.models.filter.attribute.impl;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.models.filter.attribute.IFilterAttribute;
import com.idsscheer.webapps.arcm.config.metadata.filter.IFilterAttributeType;

/**
 * This is a customization to hide/show fields in filters dynamically.
 * <p> This is a customization for <b><code>Banco do Brasil Project</code></b> </p>
 * @since v20150302-CUST-PRDIntegration
 * @author brmob
 * @see DateFilterAttribute
 */
public class DynamicDateFilterAttributeBB extends DateFilterAttribute {

	protected String leadingAttrName;
	protected IFilterAttribute leadingFilterAttr;
	
	/**
	 * @param p_userContext
	 * @param attributeConfig
	 */
	public DynamicDateFilterAttributeBB(IUserContext p_userContext,
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
