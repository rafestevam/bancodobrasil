/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.component.testmanagement.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.ListUtils;

import com.idsscheer.webapps.arcm.bl.authentication.context.ContextFactory;
import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.authorization.license.LicenseService;
import com.idsscheer.webapps.arcm.bl.common.support.AppObjUtility;
import com.idsscheer.webapps.arcm.bl.exception.BLException;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandResult;
import com.idsscheer.webapps.arcm.bl.framework.jobs.util.ConventionSupport;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IEnumAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IListAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.ITextAttribute;
import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations;
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IDeficiencyAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IDeficiencyAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IHierarchyAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.ITestcaseAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.ITestcaseAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.license.LicensedComponent;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.config.Metadata;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;

/**
 * <p>
 * Creates a deficiency to the triggering testcase AppObj in case the 'measure' attribute has value 'deficiency'.<br>
 * If there already exists one then nothing happens.<br>
 * If the testcase's client has no license for deficiency management then nothing happens.
 * </p>
 * <p> This is a customization for <b><code>Banco do Brasil Project</code></b> </p>
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
 *  <li>CreateDeficiencyCommand.WORKOBJECT_CREATED_DEFICIENCY: The generated deficiency IAppObj</li>
 * </ul>
 * </p>
 * @author brmob
 * @since v20150209-CUST-DeficiencyMgmt
 * @see CreateDeficiencyCommand
 *
 */
public class CreateDeficiencyCommandBB extends CreateDeficiencyCommand {

	
	@Override
	public CommandResult execute(CommandContext cc) throws BLException {
		IAppObj testcase = cc.getChainContext().getTriggeringAppObj();
        IUserContext userContext = cc.getChainContext().getUserContext();

        //do nothing if measure is not set to 'deficiency'
        if ( !testcase.getAttribute(ITestcaseAttributeType.ATTR_MEASURE).getRawValue().contains( Enumerations.MEASURE.DEFICIENCY ) ) {
            return new CommandResult(CommandResult.STATUS.OK);
        }

        //do nothing if deficiency already exists
        if ( !testcase.getParentAppObjs(cc.getChainContext().getUserContext(), IDeficiencyAttributeType.LIST_TESTCASE).isEmpty() ) {
            return new CommandResult(CommandResult.STATUS.OK);
        }

        IUserContext fullGrantUserContext = ContextFactory.createFullGrantUserContext(userContext);

        final IAppObj client = AppObjUtility.getRelatedClient(testcase);
        final String clientSign = AppObjUtility.getRelatedClientSign(testcase);

        //do nothing if client has no deficiency license
        if (!LicenseService.getInstance().isLicensed(LicensedComponent.DEFICIENCY_MANAGEMENT, clientSign)) {
            return new CommandResult(CommandResult.STATUS.OK);
        }

        //set the client sign inside chain context as dialog answer
        cc.getChainContext().put("client_sign", clientSign);
        IAppObj deficiency = cc.getChainContext().create(   Metadata.getMetadata().getObjectType("DEFICIENCY"), true );

        final List<IAppObj> risks = testcase.getAttribute(ITestcaseAttributeType.LIST_RISK).getElements(userContext);
		final IAppObj risk = risks.get(0);
        final List<IAppObj> controls = testcase.getAttribute(ITestcaseAttributeType.LIST_CONTROL).getElements(userContext);
		final IAppObj control = controls.get(0);

        //copy as much as possible by convention
        ConventionSupport conventionSupport = new ConventionSupport(fullGrantUserContext, deficiency, risk, control, testcase);
        conventionSupport.fulfillConventions();

        //currency
        setDeficiencyCurrency(client, deficiency);
        
        //control level
        //deficiency.getAttribute(IDeficiencyAttributeType.ATTR_CONTROL_LEVEL).addItem(Enumerations.DEF_LEVEL_OF_CONTROL.TRANSACTION);
        AppObjUtility.copyAttributeValue(control, deficiency, IControlAttributeTypeBB.ATTR_NIVEL_CONTROLE, IDeficiencyAttributeTypeBB.ATTR_CONTROL_LEVEL);
        
        //deficiency ID
        deficiency.getAttribute(IDeficiencyAttributeType.ATTR_DEFICIENCY_ID).setRawValue(clientSign + "_" + deficiency.getObjectId());

        //type - customized by BdB project
        final IEnumAttribute type = deficiency.getAttribute(IDeficiencyAttributeType.ATTR_TYPE);
        final IEnumAttribute effectiveness = testcase.getAttribute(ITestcaseAttributeType.ATTR_EFFECTIVENESS);
        
        translateTestcaseEffectivenessToDeficiencyType(effectiveness, type);
        
        
        //RBA/CBA specific attributes
	    setCustomDeficiencyAttributes(testcase, deficiency, fullGrantUserContext);

        
        cc.getChainContext().save(deficiency, true);
        cc.getChainContext().put(WORKOBJECT_CREATED_DEFICIENCY, deficiency);
        return new CommandResult(CommandResult.STATUS.OK);
	}
	
	@Override
	protected void setCustomDeficiencyAttributes(IAppObj testcase,
			IAppObj p_deficiency, IUserContext userContext) throws BLException {
		super.setCustomDeficiencyAttributes(testcase, p_deficiency, userContext);
		
		// Getting the financial accounts assigned to TESTCASE >> PROCESS.
		final IListAttribute controlProcessAttr = testcase.getAttribute(ITestcaseAttributeTypeBB.LIST_CONTROLPROCESS);
		final IAppObj controlProcess = ARCMCollections.extractSingleEntry(controlProcessAttr.getElements(userContext), true);
		
		final List<IAppObj> finAccounts = controlProcess.getAttribute(IHierarchyAttributeTypeBB.LIST_FINACCOUNT).getElements(userContext);
		List<IEnumerationItem> finAccountAssertions = calculateFinAccountAssetions(finAccounts);
		
		// The deficiency will show the union of all financial account's assertions attribute.
		p_deficiency.getAttribute(IDeficiencyAttributeTypeBB.ATTR_FINACCOUNT_ASSERTIONS).setRawValue(finAccountAssertions);
		
		// The test reviewer has defined a deficiency manager L1 during the test case evaluation
		AppObjUtility.copyAttributeValue(testcase, p_deficiency, ITestcaseAttributeTypeBB.LIST_DEFMANAGERL1_GROUP, IDeficiencyAttributeTypeBB.LIST_MANAGER_GROUP_L1);
		
		// The process test case has to be reflected to the deficiency during its creation.
		AppObjUtility.copyAttributeValue(testcase, p_deficiency, ITestcaseAttributeTypeBB.LIST_CONTROLPROCESS, IDeficiencyAttributeTypeBB.LIST_PROCESS);
		
		// Some control's attributes has to be reflected into deficiency during its creation.
		IListAttribute testcaseControlAttr = testcase.getAttribute(ITestcaseAttributeTypeBB.LIST_CONTROL);
		IAppObj testcaseControl = ARCMCollections.extractSingleEntry(testcaseControlAttr.getElements(userContext), true);
		
		ITextAttribute testcaseControlPlanilhaDeptAttr = testcaseControl.getAttribute(IControlAttributeTypeBB.ATTR_PLANILHA_DEPARTAMENTAL);
		if (!testcaseControlPlanilhaDeptAttr.isEmpty()) {
			p_deficiency.getAttribute(IDeficiencyAttributeTypeBB.ATTR_PLAN_DEPARTAMENTAL).setRawValue(true);
			p_deficiency.getAttribute(IDeficiencyAttributeTypeBB.ATTR_REL_APP).setRawValue(testcaseControlPlanilhaDeptAttr.getPersistentRawValue());
		} else {
			p_deficiency.getAttribute(IDeficiencyAttributeTypeBB.ATTR_PLAN_DEPARTAMENTAL).setRawValue(false);
		}
		
	}
	
	/**
	 * This method will perform the union of all financial account's assertions attribute
	 * based on the following object relations:
	 * DEFICIENCY >> TESTCASE >> PROCESS >> FINANCIAL ACCOUNTS >> bb_finAccountAssertions
	 * @param finAccounts 
	 * @return finAccountAssertions
	 */
	@SuppressWarnings("unchecked")
	private List<IEnumerationItem> calculateFinAccountAssetions(List<IAppObj> finAccounts){
		List<IEnumerationItem> finAccountAssertions = new ArrayList<IEnumerationItem>();
		for (IAppObj finAccountAppObj : finAccounts) {
			List<IEnumerationItem> tempAssertions = finAccountAppObj.getAttribute(IHierarchyAttributeTypeBB.ATTR_FINACCOUNTASSERTIONS).getPersistentRawValue();
			finAccountAssertions = ListUtils.union(finAccountAssertions, tempAssertions);
		}
		return finAccountAssertions;
	
	}
	
	/**
	 * This is a specific customization for the BdB Project:
	 * It was needed to change the mapping TESTCASE.effectiviness >> DEFICIENCY.type
	 * Now there are two different enumerations effectiveness and bb_defType to be mapped.
	 * @param effectiveness - from TESTCASE
	 * @param type - from DEFICIENCY
	 */
	private void translateTestcaseEffectivenessToDeficiencyType(IEnumAttribute effectiveness, IEnumAttribute type){
		List<IEnumerationItem> effectivenessEnumItem = effectiveness.getRawValue();
		for (IEnumerationItem iEnumerationItem : effectivenessEnumItem) {
			if (iEnumerationItem.equals(Enumerations.EFFECTIVENESS.DESIGN)) {
				type.addItem(EnumerationsBB.BB_DEFTYPE.DESIGN);
			} else {
				type.addItem(EnumerationsBB.BB_DEFTYPE.CONTROL);
			}
		}
	}
}
