/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.models.filter.attribute.impl.issuemanagement;

/**
 * @author Administrator
 *
 */
public interface IIssueApproverGenericFilterAttributeBB {
	
	public static final String MGMT_APPRL1 = "ManagementApproverL1";
	public static final String MGMT_APPRL2 = "ManagementApproverL2";
	public static final String IMPL_APPRL1 = "ImplementationApproverL1";
	public static final String IMPL_APPRL2 = "ImplementationApproverL2";
	public static final String TECH_APPRL1 = "TechnicalApproverL1";
	public static final String TECH_APPRL2 = "TechnicalApproverL2";
	
	public String getApproverInfo();
}
