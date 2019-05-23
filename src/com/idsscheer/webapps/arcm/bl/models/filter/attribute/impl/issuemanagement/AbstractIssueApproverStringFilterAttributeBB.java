/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.models.filter.attribute.impl.issuemanagement;

import java.util.ArrayList;
import java.util.List;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.component.issuemanagement.re.IssueHelperBB;
import com.idsscheer.webapps.arcm.bl.models.filter.attribute.impl.DynamicStringFilterAttributeBB;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.config.metadata.filter.IFilterAttributeType;

/**
 * @author Administrator
 *
 */
public abstract class AbstractIssueApproverStringFilterAttributeBB extends
		DynamicStringFilterAttributeBB implements IIssueApproverGenericFilterAttributeBB {

	/**
	 * @param userContext
	 * @param attributeConfig
	 */
	public AbstractIssueApproverStringFilterAttributeBB(
			IUserContext userContext, IFilterAttributeType attributeConfig) {
		super(userContext, attributeConfig);
	}
	
	@Override
	public boolean isVisible() {
		@SuppressWarnings("unchecked")
		IEnumerationItem leadingAttrValue = ((ArrayList<IEnumerationItem>) leadingFilterAttr.getRawValue(leadingAttrName)).get(0);
		if (!getSupportedIssueTypes().contains(leadingAttrValue)) {
			return false;
		}
		return super.isVisible();
	}
	
	public List<IEnumerationItem> getSupportedIssueTypes(){
		return IssueHelperBB.getSupportedIssueTypesByApproverInfo(getApproverInfo());
	}

}
