/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.component.testmanagement.commands.job.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.common.support.AppObjUtility;
import com.idsscheer.webapps.arcm.bl.component.common.command.IWorkobjectConstants;
import com.idsscheer.webapps.arcm.bl.component.common.command.job.IJobWorkobjectConstants;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IControlAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.ITestdefinitionAppObj;
import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskAttributeType;
import com.idsscheer.webapps.arcm.common.notification.INotificationList;
import com.idsscheer.webapps.arcm.common.notification.NotificationTypeEnum;
import com.idsscheer.webapps.arcm.common.resources.IResourceBundle;
import com.idsscheer.webapps.arcm.common.resources.ResourceBundleFactory;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;

/**
 * <p>
 * Checks if the testdefinition has one active control linked directly and at least one active risk linked indirectly.
 * Otherwise the checks are aborted with an according info notification.
 * </p>
 * <p>
 * The found objects are used for some subsequent checks and for the object generation itself.
 * The risks are also checked if they have set the attributes "assertions", "impact", "probability".
 * </p>
 * <br>
 * <p>
 * command XML parameters (<b>bold</b>=mandatory): none
 * </p>
 * <p>
 * input work objects (<b>bold</b>=mandatory): none
 * </p>
 * <p>
 * output work objects (<b>bold</b>=mandatory):
 * <ul>
 *  <li>IJobWorkobjectConstants.WORKOBJECT_LINKEDCONTROL - the linked control IAppObj</li>
 *  <li>IJobWorkobjectConstants.WORKOBJECT_LINKEDRISKS - the List of linked risk IAppObjs of the linked control IAppObj</li>
 * </ul>
 * </p>
 * @since v20160314-MNT-UATEnhancements
 * @author brmob
 * @see TestdefinitionFindLinkedControlAndRisksCommand
 */
public class TestdefinitionFindLinkedControlAndRisksCommandBB extends TestdefinitionFindLinkedControlAndRisksCommand {
	
	/**
     * Determines the control to the given testdefinition.
     *
     * @param testdefinition the testdefinition with the link to the control
     * @return the control AppObj or null if no parent control was found or if it is deactivated
     */
	@Override
	protected IControlAppObj getControlToTestdefinition(ITestdefinitionAppObj testdefinition, CommandContext cc, INotificationList notificationList) {
		Locale locale = cc.getChainContext().getLocale();
        List<IAppObj> appObjList = testdefinition.getParentAppObjs(cc.getChainContext().getUserContext(), IControlAppObj.LIST_TESTDEFINITIONS);

        if(!appObjList.isEmpty()) {
            if(appObjList.get(0).getVersionData().isDeleted()) {
                notificationList.add(   NotificationTypeEnum.INFO,
                                        KEY_WARN_NOT_ACTIVE,
                                        testdefinition.getObjectId()+" - "+AppObjUtility.getLocalisedAppObjName(testdefinition, locale),
                                        appObjList.get(0).toString());
                return null;
            }
            return (IControlAppObj) appObjList.get(0);
        }

        notificationList.add(   NotificationTypeEnum.INFO,
                                KEY_WARN_NOT_ASSIGNED,
                                testdefinition.getObjectId()+" - "+AppObjUtility.getLocalisedAppObjName(testdefinition, locale),
                                ObjectType.CONTROL.getId());
        return null;
	}
	
	/**
     * Determines the risks to the given control.
     *
     * @param control the control with the link to the risk/risks
     * @return list with exactly one IAppObj (since this generator is used in the risk based approach);
     *         empty lists if there is no parent risk or if it is deactivated
     */
	@Override
	protected List<IAppObj> getRisksToControl(IControlAppObj control, CommandContext cc, INotificationList notificationList) {
		Locale locale = cc.getChainContext().getLocale();
        List<IAppObj> appObjList = control.getParentAppObjs(cc.getChainContext().getUserContext(), IRiskAttributeType.LIST_CONTROLS);

        if (appObjList.isEmpty()) {
            //Fix ACR-1061: using testdefinition from checkGeneratorCondition() would be better but signature change not possible
            IAppObj testdefinition = cc.getChainContext().getContextAttribute(IJobWorkobjectConstants.WORKOBJECT_APPOBJTOCHECK);
            if (testdefinition == null) {
                testdefinition = cc.getChainContext().getContextAttribute(IWorkobjectConstants.PARENT_APPOBJ);
            }
            notificationList.add(   NotificationTypeEnum.INFO,
                                    KEY_WARN_NOT_ASSIGNED,
                                    testdefinition.getObjectId()+" - "+AppObjUtility.getLocalisedAppObjName(testdefinition, locale),
                                    ObjectType.RISK.getId() );
            return appObjList;
        }
        else {
            return getActiveRiskList(appObjList, cc, notificationList);
        }
	}
	
	/**
     * Filters the given risk list and removes all deactivated risks
     *
     * @param riskAppObjs the list to be filtered
     * @return the filtered list
     */
	@Override
	protected List<IAppObj> getActiveRiskList(List<IAppObj> riskAppObjs, CommandContext cc, INotificationList notificationList) {
		Locale locale = cc.getChainContext().getLocale();
        IAppObj triggeringAppObj = cc.getChainContext().getTriggeringAppObj();
        List<IAppObj> riskAppObjList = new ArrayList<IAppObj>(riskAppObjs.size());

        for (IAppObj appObj : riskAppObjs) {
            if(!appObj.getVersionData().isDeleted()) {
                riskAppObjList.add(appObj);
            }
        }

        if (riskAppObjs.isEmpty()) {
            notificationList.add(   NotificationTypeEnum.INFO,
                                    KEY_WARN_NO_RISK_ACTIVE,
                                    triggeringAppObj.getObjectId()+" - "+AppObjUtility.getLocalisedAppObjName(triggeringAppObj, locale) );
        }

        return riskAppObjList;
	}
	
	/**
     * Checks if the given risks are of risk type "financial_reporting" and their mandatory attributes ("assertions", "impact" and "probability")
     * are maintained.
     * @return true if all risks fulfill this condition, otherwise false
     */
	@Override
	protected boolean checkNecessaryRiskAttributes(List<IAppObj> risks, CommandContext cc, INotificationList notificationList) {
		IUserContext userContext = cc.getChainContext().getUserContext();
        Locale locale = userContext.getLanguage();
        IResourceBundle resourceBundle = ResourceBundleFactory.getBundle(locale);
        boolean result = true;

        for (IAppObj risk : risks) {

            //checks only if risk type is "financial_reporting"
            if ( !risk.getAttribute(IRiskAttributeType.ATTR_RISKTYPE).getRawValue().contains(Enumerations.RISKTYPE.FINANCIAL_REPORTING) ) {
                continue;
            }

            // check the 'assertions' attribute
            List<IEnumerationItem> assertionsList = risk.getAttribute(IRiskAttributeType.ATTR_ASSERTIONS).getRawValue();
            if( assertionsList.isEmpty() ) {
                notificationList.add(   NotificationTypeEnum.INFO,
                                        KEY_WARN_RISK_ATTR_NOT_MAINTAINED,
                                        risk.getObjectId()+" - "+AppObjUtility.getLocalisedAppObjName(risk, locale),
                                        resourceBundle.getString(IRiskAttributeType.ATTR_ASSERTIONS.getPropertyKey()) );
                result = false;
            }

            // check the 'impact' attribute
            List<IEnumerationItem> impactList = risk.getAttribute(IRiskAttributeType.ATTR_IMPACT).getRawValue();
            if( impactList.isEmpty() ) {
                notificationList.add(   NotificationTypeEnum.INFO,
                                        KEY_WARN_RISK_ATTR_NOT_MAINTAINED,
                                        risk.getObjectId()+" - "+AppObjUtility.getLocalisedAppObjName(risk, locale),
                                        resourceBundle.getString(IRiskAttributeType.ATTR_IMPACT.getPropertyKey()) );
                result = false;
            }

            // check the 'probability' attribute
            List<IEnumerationItem> probabilityList = risk.getAttribute(IRiskAttributeType.ATTR_PROBABILITY).getRawValue();
            if( probabilityList.isEmpty() ) {
                notificationList.add(   NotificationTypeEnum.INFO,
                                        KEY_WARN_RISK_ATTR_NOT_MAINTAINED,
                                        risk.getObjectId()+" - "+AppObjUtility.getLocalisedAppObjName(risk, locale),
                                        resourceBundle.getString(IRiskAttributeType.ATTR_PROBABILITY.getPropertyKey()) );
                result = false;
            }

        }
        
        return result;
	}
	
	

}
