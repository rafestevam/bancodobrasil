/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.component.common.command.job;

import java.util.Locale;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.common.support.AppObjUtility;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandException;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IBooleanAttribute;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRecurringAttributeType;
import com.idsscheer.webapps.arcm.common.notification.INotification;
import com.idsscheer.webapps.arcm.common.notification.NotificationTypeEnum;
import com.idsscheer.webapps.arcm.common.notification.ResourceBundleNotification;
import com.idsscheer.webapps.arcm.common.util.guid.SystemGUID;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IObjectType;

/**
 * @author Administrator
 *
 */
public class IsEventDrivenCheckCommandBB extends IsEventDrivenCheckCommand {

	/**
	 * 
	 */
	public IsEventDrivenCheckCommandBB() {
	}
	
	@Override
	protected CheckResult checkGeneratorCondition(IAppObj appObj, CommandContext cc) throws CommandException {
		
		IUserContext userContext = cc.getChainContext().getUserContext();
		IObjectType objectType = cc.getChainContext().getTriggeringAppObj().getObjectType();
		Locale locale = userContext.getLanguage();
		
		IBooleanAttribute isEventDrivenAllowedAttr = appObj.getAttribute(IRecurringAttributeType.ATTR_EVENT_DRIVEN_ALLOWED);
		Boolean isEventDrivenAllowed = isEventDrivenAllowedAttr.getPersistentRawValue();
		
		if (!isEventDrivenAllowed && !isJobUser(userContext)) {
			String key = "job.generator.job." + objectType.getId().toLowerCase(Locale.ENGLISH) + ".eventdriven.not.allowed.INF";
			INotification notification = new ResourceBundleNotification(NotificationTypeEnum.INFO, key, new String[] { AppObjUtility.getLocalisedAppObjName(appObj, locale, false) });
			return new GeneratorConditionCheckCommand.CheckResult(notification, true);
		}
		
		return null;
		
	}
	
	private boolean isJobUser(IUserContext context){
		return context.getUserID().getId() == SystemGUID.JOB_USER.getObjID();
	}

}
