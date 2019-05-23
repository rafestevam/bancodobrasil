package com.idsscheer.webapps.arcm.bl.component.issuemanagement.commands;

import java.util.List;
import java.util.Locale;

import org.apache.commons.collections.CollectionUtils;

import com.idsscheer.webapps.arcm.bl.authentication.context.ContextFactory;
import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.common.support.UsergroupUtility;
import com.idsscheer.webapps.arcm.bl.exception.BLException;
import com.idsscheer.webapps.arcm.bl.exception.ObjectRelationException;
import com.idsscheer.webapps.arcm.bl.exception.RightException;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandResult;
import com.idsscheer.webapps.arcm.bl.framework.command.ICommand;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IListAttribute;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeTypeBB;
import com.idsscheer.webapps.arcm.ui.framework.actioncommands.ActionCommandException;

/**
 * 
 * <p>
 * This command sincronyze the "owners" and "reviewers" user groups with the same users as "bb_owners_group" and "bb_reviewers_group"
 * </p>
 * @author aesc
 *
 */
public class IssueRefreshOwnersAndReviewersCommandBB implements ICommand{
	
	private static final IUserContext fullGrant = ContextFactory.getFullReadAccessUserContext(Locale.ENGLISH);
	
	public CommandResult execute(CommandContext cc) throws BLException {
		IAppObj appObj = cc.getChainContext().getTriggeringAppObj();	

		//OWNERS
		//Obtain the owners group´s name of "bb_owners_group"
		IListAttribute ownersGroupAttr = appObj.getAttribute(IIssueAttributeTypeBB.LIST_OWNERS_GROUP);
		List<IAppObj> ownersGroupList = ownersGroupAttr.getElements(fullGrant);
		
		IListAttribute ownersAttr = appObj.getAttribute(IIssueAttributeTypeBB.LIST_OWNERS);
		
		mapUserGroupMembersToAttribute(ownersGroupList, ownersAttr);
		
		//REVIEWERS
		IListAttribute reviewersGroupAttr = appObj.getAttribute(IIssueAttributeTypeBB.LIST_REVIEWERS_GROUP);
		List<IAppObj> reviewersGroupList = reviewersGroupAttr.getElements(fullGrant);
		
		IListAttribute reviewersAttr = appObj.getAttribute(IIssueAttributeTypeBB.LIST_REVIEWERS);
		
		mapUserGroupMembersToAttribute(reviewersGroupList, reviewersAttr);
		
		return CommandResult.OK;
	}
	
	private void mapUserGroupMembersToAttribute(List<IAppObj> userGroupList, IListAttribute attribute){
		if (!CollectionUtils.isEmpty(userGroupList)) {
			try {
				attribute.removeAllElements(fullGrant);
				attribute.addElements(UsergroupUtility.getGroupMembers(userGroupList), fullGrant);
			} catch (RightException e) {
				throw new ActionCommandException(e);
			} catch (ObjectRelationException e) {
				throw new ActionCommandException(e);
			}
			
		}
	}
}



