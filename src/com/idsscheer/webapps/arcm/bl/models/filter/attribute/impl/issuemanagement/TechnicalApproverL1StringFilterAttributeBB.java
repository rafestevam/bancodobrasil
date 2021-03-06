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
public class TechnicalApproverL1StringFilterAttributeBB extends
		AbstractIssueApproverStringFilterAttributeBB {

	/**
	 * @param userContext
	 * @param attributeConfig
	 */
	public TechnicalApproverL1StringFilterAttributeBB(IUserContext userContext,
			IFilterAttributeType attributeConfig) {
		super(userContext, attributeConfig);
	}

	@Override
	public String getApproverInfo() {
		return TECH_APPRL1;
	}

}
