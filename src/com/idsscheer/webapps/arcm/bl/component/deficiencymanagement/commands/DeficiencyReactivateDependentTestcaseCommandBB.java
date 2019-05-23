/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.component.deficiencymanagement.commands;

import java.util.List;
import java.util.Locale;

import com.idsscheer.webapps.arcm.bl.authentication.context.ContextFactory;
import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.common.support.AppObjUtility;
import com.idsscheer.webapps.arcm.bl.common.support.UsergroupUtility;
import com.idsscheer.webapps.arcm.bl.exception.BLException;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandResult;
import com.idsscheer.webapps.arcm.bl.framework.command.IChainContext;
import com.idsscheer.webapps.arcm.bl.framework.command.ICommand;
import com.idsscheer.webapps.arcm.bl.framework.message.IMessage;
import com.idsscheer.webapps.arcm.bl.framework.message.MessageFactory;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.ITestcaseAppObj;
import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations;
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IDeficiencyAttributeType;
import com.idsscheer.webapps.arcm.common.notification.NotificationList;
import com.idsscheer.webapps.arcm.common.notification.NotificationTypeEnum;
import com.idsscheer.webapps.arcm.common.resources.ResourceBundleFactory;

/**
 * <p>
 * If the triggering deficiency has a linked testcase:
 * <ul>
 * 	<li>The measure attribute is set from "deactivated" to "deficiency"</li>
 *  <li>A message will be send to inform the test reviewers to be aware about the deficiency's reactivation</li>
 * <ul>
 * </p>
 * <br>
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
 * @since v20160314-MNT-UATEnhancements
 */
public class DeficiencyReactivateDependentTestcaseCommandBB implements ICommand {

	/* (non-Javadoc)
	 * @see com.idsscheer.webapps.arcm.bl.framework.command.ICommand#execute(com.idsscheer.webapps.arcm.bl.framework.command.CommandContext)
	 */
	@Override
	public CommandResult execute(CommandContext cc) throws BLException {
		
		IChainContext chainCtx = cc.getChainContext();
        IAppObj deficiency = chainCtx.getTriggeringAppObj();
        IUserContext fullGrantUserCtx = ContextFactory.createFullGrantUserContext(chainCtx.getUserContext());

        //look up the linked testcase
        List<IAppObj> testcases = deficiency.getAttribute(IDeficiencyAttributeType.LIST_TESTCASE).getElements(fullGrantUserCtx);
        if (testcases.isEmpty()) {
            return CommandResult.OK;
        }
        IAppObj testcase = testcases.get(0);
        
        testcase.getAttribute(ITestcaseAppObj.ATTR_MEASURE).addItem(Enumerations.MEASURE.DEFICIENCY);
        
        //change the measure attribute from "deactivated" to "deficiency"
        chainCtx.allocateWriteLock(testcase);
        chainCtx.save(testcase, true);
        chainCtx.releaseWriteLock(testcase);
        
        NotificationList notifications = new NotificationList();
        Locale locale = fullGrantUserCtx.getLanguage();
        
        sendMail(testcase, fullGrantUserCtx, cc);
        notifications.add(
                NotificationTypeEnum.INFO, "testcase.measure.changed.INF", 
                AppObjUtility.getLocalisedAppObjName(testcase, locale, false),
                ResourceBundleFactory.getBundle(locale).getString( Enumerations.MEASURE.DEFICIENCY.getPropertyKey() )
        );
        
		return new CommandResult(CommandResult.STATUS.OK, notifications, null);
	}
	
	protected void sendMail(IAppObj testcase, IUserContext userContext, CommandContext cc) {

        List<IAppObj> reviewers = testcase.getAttribute(ITestcaseAppObj.LIST_REVIEWER_GROUP).getElements(userContext);

        for (IAppObj toUser : UsergroupUtility.getGroupMembers(reviewers)) {
        	IMessage message = MessageFactory.createMessage(toUser, EnumerationsBB.INITIATORS_BB.TESTCASE_DEFICIENCY_REACTIVATED, userContext.getUser(), testcase);
            cc.getChainContext().send(message);
        }
    }

}
