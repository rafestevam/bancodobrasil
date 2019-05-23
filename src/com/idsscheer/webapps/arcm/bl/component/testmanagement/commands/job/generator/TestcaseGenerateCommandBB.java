/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.component.testmanagement.commands.job.generator;

import java.util.ArrayList;
import java.util.List;

import com.idsscheer.webapps.arcm.bl.common.support.AppObjUtility;
import com.idsscheer.webapps.arcm.bl.component.common.command.job.IJobWorkobjectConstants;
import com.idsscheer.webapps.arcm.bl.exception.BLException;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.framework.jobs.util.ConventionSupport;
import com.idsscheer.webapps.arcm.bl.framework.jobs.util.ICalculator;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.ITestcaseAttributeType;
import com.idsscheer.webapps.arcm.common.notification.INotificationList;
import com.idsscheer.webapps.arcm.common.notification.NotificationTypeEnum;

/**
 * <p>
 * Fills the triggering testcase AppObj.
 * </p>
 * <br>
 * <p>
 * command XML parameters (<b>bold</b>=mandatory): none
 * </p>
 * <p>
 * input work objects (<b>bold</b>=mandatory):
 * <ul>
 *  <li><b>IJobWorkobjectConstants.WORKOBJECT_APPOBJTOCHECK</b> - IAppObj (triggering testdefinition)</li>
 *  <li><b>IJobWorkobjectConstants.WORKOBJECT_LINKEDRISKS</b> - List&lt;IAppObj&gt; of risks for the new testcase</li>
 *  <li><b>IJobWorkobjectConstants.WORKOBJECT_LINKEDCONTROL</b> - control IAppObj for the new testcase</li>
 *  <li><b>IJobWorkobjectConstants.WORKOBJECT_CALCULATOR</b> - the ICalculator to fill the new testcase</li>
 *  <li><b>IJobWorkobjectConstants.WORKOBJECT_LINKEDACTIVETESTERHIERARCHIES</b> - List&lt;IAppObj&gt; of tester hierarchies for the followup testcase</li>
 * </ul>
 * </p>
 * <p>
 * output work objects (<b>bold</b>=mandatory): see {@link com.idsscheer.webapps.arcm.bl.component.common.command.job.GenerateCommand}
 * </p>
 * @since v20160314-MNT-UATEnhancements
 * @author brmob
 * @see TestcaseGenerateCommand
 */
public class TestcaseGenerateCommandBB extends TestcaseGenerateCommand {
	
	@Override
	protected GenerateResult fillGeneratedAppObj(IAppObj generatedMainAppObj, CommandContext cc, INotificationList notificationList) throws BLException {
		IAppObj testdefinition = getAppObjToCheck(cc);
        IAppObj control = cc.getChainContext().getContextAttribute(IJobWorkobjectConstants.WORKOBJECT_LINKEDCONTROL);
        List<IAppObj> risks = cc.getChainContext().getContextAttribute(IJobWorkobjectConstants.WORKOBJECT_LINKEDRISKS);

        List<IAppObj> sourceAppObjs = new ArrayList<IAppObj>();
        if (risks != null) {sourceAppObjs.addAll(risks);}
        if (control != null) {sourceAppObjs.add(control);}
        if (testdefinition != null) {sourceAppObjs.add(testdefinition);}

        if (testdefinition == null) {
            notificationList.add(NotificationTypeEnum.INFO, KEY_TEST_CASE_NO_TESTDEFINITION );
            return new GenerateResult(false);
        }

        //copy values by convention
        if (!sourceAppObjs.isEmpty()) {
            ConventionSupport convention = new ConventionSupport(   cc.getChainContext().getUserContext(),
                                                                    generatedMainAppObj,
                                                                    sourceAppObjs.toArray(new IAppObj[sourceAppObjs.size()]) );
            ICalculator calculator = cc.getChainContext().getContextAttribute(IJobWorkobjectConstants.WORKOBJECT_CALCULATOR);
            convention.setCalculator(calculator);
            convention.addAttributeTypeToBeSkipped( ITestcaseAttributeType.LIST_DOCUMENTS );
            //copy the "orgunit" values from testdefinition only (ignore risk attribute "orgunit")
            convention.addAttributeTypeToBeSkipped( IRiskAttributeType.LIST_ORGUNIT );

            convention.fulfillConventions();
        }

        //copy the tester hierarchy manually since this cannot be handled by convention
        copyTesterHierarchy(generatedMainAppObj, cc);

        // BB wants to add the ID for those testdefinitions that are part of the JOB message.
        notificationList.add(NotificationTypeEnum.INFO, KEY_TEST_CASE_GENERATED,
        		String.valueOf(generatedMainAppObj.getObjectId()),	
        		testdefinition.getObjectId()+" - "+AppObjUtility.getLocalisedAppObjName(testdefinition, cc.getChainContext().getLocale()) );
        return new GenerateResult(true);
	}

}
