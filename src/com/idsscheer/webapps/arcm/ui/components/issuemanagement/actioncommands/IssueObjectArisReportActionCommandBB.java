/**
 * 
 */
package com.idsscheer.webapps.arcm.ui.components.issuemanagement.actioncommands;

import com.idsscheer.webapps.arcm.config.metadata.report.IArissubreport;

/**
 * @author Administrator
 *
 */
public class IssueObjectArisReportActionCommandBB extends IssueObjectArisReportActionCommand {

	/**
	 * 
	 */
	private static final long serialVersionUID = 727171484408190465L;
	
	@Override
	protected boolean getReportDefinition() {
		boolean result = super.getReportDefinition();
		IArissubreport[] arisSubReports = arisreportBean.getArissubreport();
		for (IArissubreport subreportBean : arisSubReports) {
			String dataSourceID = subreportBean.getDatasourceID();
			if (dataSourceID.startsWith("IRO")) {
				subreportBean.setSubstitutePattern("DefaultHeaderPattern=IssueRelevantObjectHeaderPattern");
			}
		}
		return result;
	}


}
