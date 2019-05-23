/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.component.issuemanagement.commands;

import java.util.Date;
import java.util.List;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.exception.BLException;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandResult;
import com.idsscheer.webapps.arcm.bl.framework.command.IChainContext;
import com.idsscheer.webapps.arcm.bl.framework.command.ICommand;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IDateAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IEnumAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IListAttribute;
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IObjectType;

/**
 * <p>
 * This action will be executed when a issue classified as 'DEALING_DEADLINE' be approved by
 * the Approver L2 - Management during its specific workflow execution.</p>
 * <p> This is a customization for <b><code>Banco do Brasil Project</code></b> </p>
 * <p>
 * command XML parameters (<b>bold</b>=mandatory): none
 * </p>
 * <p>
 * input work objects (<b>bold</b>=mandatory): none
 * </p>
 * <p>
 * output work objects (<b>bold</b>=mandatory): none
 * </p>
 * @author brmob
 * @since v20150302-CUST-IssueMgmt
 */
public class IssueDealingDeadlineAcceptedCommandBB implements ICommand {

	/* (non-Javadoc)
	 * @see com.idsscheer.webapps.arcm.bl.framework.command.ICommand#execute(com.idsscheer.webapps.arcm.bl.framework.command.CommandContext)
	 */
	@Override
	public CommandResult execute(CommandContext cc) throws BLException {
		IChainContext chainContext = cc.getChainContext();
		IAppObj issue = chainContext.getTriggeringAppObj();
		IUserContext userContext = chainContext.getUserContext();
		
		IListAttribute issueRelateObjectAttr = issue.getAttribute(IIssueAttributeTypeBB.LIST_ISSUERELEVANTOBJECTS);
		List<IAppObj> issueRelatedObjects = issueRelateObjectAttr.getElements(userContext);
		
		for (IAppObj issueRelatedObject : issueRelatedObjects) {
			IObjectType iroObjectType = issueRelatedObject.getObjectType();
			
			if (iroObjectType.equals(ObjectType.ISSUE)) {
				
				IDateAttribute issuePlannedEndDateAttr = (IDateAttribute) issue.getAttribute(IIssueAttributeTypeBB.ATTR_DEALINGDEADLINE_DATE); 
				
				Date issuePlannedEndDate = issuePlannedEndDateAttr.getRawValue();
				issue.getAttribute(IIssueAttributeTypeBB.ATTR_PLANNEDENDDATE).setRawValue(issuePlannedEndDate);
				
				IEnumAttribute iROStateTimeAttr = issueRelatedObject.getAttribute(IIssueAttributeTypeBB.ATTR_STATETIME);
				IEnumerationItem iROStateTime = ARCMCollections.extractSingleEntry(iROStateTimeAttr.getRawValue(), true);
				if (iROStateTime.equals(EnumerationsBB.ISSUESTATETIME_BB.OVERDUE)) {
					iROStateTimeAttr.addItem(EnumerationsBB.ISSUESTATETIME_BB.ON_TIME);
				}
				
				chainContext.allocateWriteLock(issueRelatedObject, true);
				chainContext.save(issueRelatedObject, true);
				chainContext.releaseWriteLock(issueRelatedObject);
				break;
			}
		}
		
		
		return CommandResult.OK;
	}

}
