package com.idsscheer.webapps.arcm.bl.component.testmanagement.commands;

import com.idsscheer.webapps.arcm.bl.component.common.command.AssignedUsergroupsSendBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsBB;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.MessageTemplateEnumerationItem;

public class FollowUpAssignedUsergroupsSendBB extends AssignedUsergroupsSendBB {
	protected MessageTemplateEnumerationItem getMessageTemplateEnumerationItem() {
		return EnumerationsBB.INITIATORS_BB.FOLLOWUPJOB_EMPTY_GROUP;
	}
}