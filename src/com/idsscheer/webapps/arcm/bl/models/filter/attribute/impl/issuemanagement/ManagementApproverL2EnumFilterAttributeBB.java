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
public class ManagementApproverL2EnumFilterAttributeBB extends
		AbstractIssueApproverEnumFilterAttributeBB {

	/**
	 * @param userContext
	 * @param attributeConfig
	 */
	public ManagementApproverL2EnumFilterAttributeBB(IUserContext userContext,
			IFilterAttributeType attributeConfig) {
		super(userContext, attributeConfig);
	}

	/* (non-Javadoc)
	 * @see com.idsscheer.webapps.arcm.bl.models.filter.attribute.impl.issuemanagement.IIssueApproverGenericFilterAttributeBB#getApproverInfo()
	 */
	@Override
	public String getApproverInfo() {
		return MGMT_APPRL2;
	}

}
