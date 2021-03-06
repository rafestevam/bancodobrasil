/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.models.filter.attribute.impl.issuemanagement;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.config.metadata.filter.IFilterAttributeType;

/**
 * @author Administrator
 *
 */
public class ImplementationApproverL1StringFilterAttributeBB extends
		AbstractIssueApproverStringFilterAttributeBB {

	/**
	 * @param userContext
	 * @param attributeConfig
	 */
	public ImplementationApproverL1StringFilterAttributeBB(
			IUserContext userContext, IFilterAttributeType attributeConfig) {
		super(userContext, attributeConfig);
	}

	@Override
	public String getApproverInfo() {
		return IMPL_APPRL1;
	}

}
