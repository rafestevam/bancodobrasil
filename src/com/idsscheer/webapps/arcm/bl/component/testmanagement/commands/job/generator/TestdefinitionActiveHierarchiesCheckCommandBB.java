/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.component.testmanagement.commands.job.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.common.support.AppObjUtility;
import com.idsscheer.webapps.arcm.bl.component.common.command.job.IJobWorkobjectConstants;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandException;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IHierarchyAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IRiskAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.ITestcaseAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.ITestdefinitionAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IUsergroupAppObj;
import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskAttributeType;
import com.idsscheer.webapps.arcm.common.notification.INotification;
import com.idsscheer.webapps.arcm.common.notification.NotificationTypeEnum;
import com.idsscheer.webapps.arcm.common.notification.ResourceBundleNotification;
import com.idsscheer.webapps.arcm.common.resources.IResourceBundle;
import com.idsscheer.webapps.arcm.common.resources.ResourceBundleFactory;

/**
 * <p>
 * Checks if the necessary hierarchies are active for the triggering testdefinition AppObj and the given risk AppObjs
 * <ul>
 *     <li>Testdefinition: orgunit hierarchy</li>
 *     <li>Testdefinition: tester hierarchy (i.e. tester group muts be assigned to a tester hierarchy element)</li>
 *     <li>Risk: Financial account hierarchy (only for risk type "Financial reporting") - For BdB it's not mandatory anymore</li>
 *     <li>Risk: Process hierarchy</li>
 * </ul>
 * Otherwise the checks are aborted with an according info notification.<br>
 * In addition all active tester hierarchies are stored in the chain context for subsequent checks.
 * </p>
 * <br>
 * <p>
 * command XML parameters (<b>bold</b>=mandatory): none
 * </p>
 * <p>
 * input work objects (<b>bold</b>=mandatory):
 * <ul>
 *  <li><b>IJobWorkobjectConstants.WORKOBJECT_LINKEDRISKS</b> - List&lt;IAppObj&gt; of exactly one risk</li>
 * </ul>
 * </p>
 * <p>
 * output work objects (<b>bold</b>=mandatory):
 * <ul>
 *  <li>IJobWorkobjectConstants.WORKOBJECT_LINKEDACTIVETESTERHIERARCHIES - List&lt;IAppObj&gt; of all active tester hierarchies</li>
 * </ul>
 * </p>
 * @since v20160314-MNT-UATEnhancements
 * @author brmob
 * @see TestdefinitionActiveHierarchiesCheckCommand
 */
public class TestdefinitionActiveHierarchiesCheckCommandBB extends TestdefinitionActiveHierarchiesCheckCommand {
	
	@Override
	protected CheckResult checkGeneratorCondition(IAppObj appObj, CommandContext cc) throws CommandException {
		
		IUserContext userContext = cc.getChainContext().getUserContext();
        Locale locale = userContext.getLanguage();
        IResourceBundle resourceBundle = ResourceBundleFactory.getBundle(locale);
        IAppObj testdefinition = getTestdefinition(appObj, cc);

        //noinspection unchecked
		List<IRiskAppObj> riskList = (List<IRiskAppObj>) cc.getChainContext().getContextAttribute(IJobWorkobjectConstants.WORKOBJECT_LINKEDRISKS);
        IRiskAppObj risk = riskList.get(0);

        // check the organizational unit
        List<IAppObj> orgUnitList = testdefinition.getAttribute(ITestdefinitionAppObj.LIST_ORGUNIT)
                .getElements(userContext);
        if(orgUnitList.isEmpty()) {
            INotification notification = new ResourceBundleNotification(NotificationTypeEnum.INFO,
                                                                        KEY_INFO_NOT_ASSIGNED,
                                                                        testdefinition.getObjectId()+" - "+AppObjUtility.getLocalisedAppObjName(testdefinition, locale),
                                                                        resourceBundle.getString( ITestdefinitionAppObj.LIST_ORGUNIT.getPropertyKey() ) );
            return new CheckResult(notification, true);
        }

        IHierarchyAppObj orgUnit = (IHierarchyAppObj) orgUnitList.get(0);
        if(orgUnit.getVersionData().isDeleted()) {
            INotification notification = new ResourceBundleNotification(NotificationTypeEnum.INFO,
                                                                        KEY_INFO_NOT_ACTIVE,
                                                                        testdefinition.getObjectId()+" - "+AppObjUtility.getLocalisedAppObjName(testdefinition, locale),
                                                                        resourceBundle.getString( ITestdefinitionAppObj.LIST_ORGUNIT.getPropertyKey() ) );
            return new CheckResult(notification, true);
        }

        // check financial statements
        //only if risk type is "financial_reporting"
        if ( risk.getAttribute(IRiskAttributeType.ATTR_RISKTYPE).getRawValue().contains(Enumerations.RISKTYPE.FINANCIAL_REPORTING) ) {

        	// For BdB Project, a risks defined as risktype = finnacial_report don't need to have financial statements
            List<IAppObj> financialStatementList = risk.getAttribute(IRiskAppObj.LIST_FINACCOUNT).getElements(userContext);
            
        	for (IAppObj financialStatement : financialStatementList) {
        		
        		boolean isOneFinancialStatementActive = false;
        		
        		if(!financialStatement.getVersionData().isDeleted()) {
                    isOneFinancialStatementActive = true;
                    break;
                }
        		
        		if(!isOneFinancialStatementActive) {
        			INotification notification = new ResourceBundleNotification(NotificationTypeEnum.INFO,
                                                                            KEY_INFO_NOT_ACTIVE,
                                                                            testdefinition.getObjectId()+" - "+AppObjUtility.getLocalisedAppObjName(testdefinition, locale),
                                                                            resourceBundle.getString( IRiskAppObj.LIST_FINACCOUNT.getPropertyKey() ) );
        			return new CheckResult(notification, true);
        		}
			}
        }

        // check the function
        List<IAppObj> functionList = risk.getAttribute(IRiskAppObj.LIST_PROCESS)
                .getElements(userContext);
        if(functionList.isEmpty()) {
            INotification notification = new ResourceBundleNotification(NotificationTypeEnum.INFO,
                                                                        KEY_INFO_NOT_ASSIGNED,
                                                                        testdefinition.getObjectId()+" - "+AppObjUtility.getLocalisedAppObjName(testdefinition, locale),
                                                                        resourceBundle.getString( IRiskAppObj.LIST_PROCESS.getPropertyKey() ) );
            return new CheckResult(notification, true);
        }

        IHierarchyAppObj function = (IHierarchyAppObj) functionList.get(0);
        if(function.getVersionData().isDeleted()) {
            INotification notification = new ResourceBundleNotification(NotificationTypeEnum.INFO,
                                                                        KEY_INFO_NOT_ACTIVE,
                                                                        testdefinition.getObjectId()+" - "+AppObjUtility.getLocalisedAppObjName(testdefinition, locale),
                                                                        resourceBundle.getString( IRiskAppObj.LIST_PROCESS.getPropertyKey() ) );
            return new CheckResult(notification, true);
        }

        // check tester hierarchy
        List<IAppObj> testerGroupList = testdefinition.getAttribute(ITestdefinitionAppObj.LIST_OWNER_GROUP)
                .getElements(userContext);
        if(testerGroupList.isEmpty()) {
            INotification notification = new ResourceBundleNotification(NotificationTypeEnum.INFO,
                                                                        KEY_INFO_NOT_ASSIGNED,
                                                                        testdefinition.getObjectId()+" - "+AppObjUtility.getLocalisedAppObjName(testdefinition, locale),
                                                                        resourceBundle.getString( ITestdefinitionAppObj.LIST_OWNER_GROUP.getPropertyKey() ) );
            return new CheckResult(notification, true);
        }

        IUsergroupAppObj testOwnerGroup = (IUsergroupAppObj) testerGroupList.get(0);

        List<IAppObj> testerHierarchyList  = testOwnerGroup.getParentAppObjs(userContext, IHierarchyAppObj.LIST_TESTER, true, true);


		if(testerHierarchyList.isEmpty()) {
            INotification notification = new ResourceBundleNotification(NotificationTypeEnum.INFO,
                                                                        KEY_INFO_TESTERGROUP_NOT_ASSIGNED_TO_HIERARCHY,
                                                                        testdefinition.getObjectId()+" - "+AppObjUtility.getLocalisedAppObjName(testdefinition, locale),
                                                                        testOwnerGroup.getUiValue(locale, testOwnerGroup.getObjectType().getNameAttribute()) );
            return new CheckResult(notification, true);
        }
        if(testerHierarchyList.size() > 1) {
            INotification notification = new ResourceBundleNotification(NotificationTypeEnum.INFO,
                                                                        KEY_INFO_TESTERGROUP_ASSIGNED_TO_MULTIPLE_HIERARCHY,
                                                                        testdefinition.getObjectId()+" - "+AppObjUtility.getLocalisedAppObjName(testdefinition, locale),
                                                                        testOwnerGroup.getUiValue(locale, testOwnerGroup.getObjectType().getNameAttribute()) );
            return new CheckResult(notification, true);
        }

        List<IAppObj> activeTesterHierarchyList = new ArrayList<IAppObj>();
        for (IAppObj testerHierarchy : testerHierarchyList) {
            if(!testerHierarchy.getVersionData().isDeleted()) {
                activeTesterHierarchyList.add(testerHierarchy);
            }
        }

        if (activeTesterHierarchyList.isEmpty()) {
            INotification notification = new ResourceBundleNotification(NotificationTypeEnum.INFO,
                                                                        KEY_INFO_NOT_ACTIVE,
                                                                        testdefinition.getObjectId()+" - "+AppObjUtility.getLocalisedAppObjName(testdefinition, locale),
                                                                        resourceBundle.getString( ITestcaseAppObj.LIST_TESTERHIERARCHY.getPropertyKey() ) );
            return new CheckResult(notification, true);
        }
        cc.getChainContext().setWorkObject( IJobWorkobjectConstants.WORKOBJECT_LINKEDACTIVETESTERHIERARCHIES, activeTesterHierarchyList );

        return null;
	}

}
