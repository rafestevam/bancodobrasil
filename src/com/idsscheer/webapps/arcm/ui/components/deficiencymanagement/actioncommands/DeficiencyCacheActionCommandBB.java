/**
 * 
 */
package com.idsscheer.webapps.arcm.ui.components.deficiencymanagement.actioncommands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;

import com.idsscheer.webapps.arcm.bl.common.support.AppObjUtility;
import com.idsscheer.webapps.arcm.bl.exception.ObjectRelationException;
import com.idsscheer.webapps.arcm.bl.exception.RightException;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IHierarchyAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IRiskAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IBooleanAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IEnumAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IListAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.ITextAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IValueAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjIterator;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjQuery;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.QueryJoin;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.QueryRestriction;
import com.idsscheer.webapps.arcm.bl.re.impl.RECommons;
import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations;
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IDeficiencyAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IDeficiencyAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IHierarchyAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.ITestcaseAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.ITestcaseAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IEnumAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IListAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IValueAttributeType;
import com.idsscheer.webapps.arcm.ui.framework.actioncommands.ActionCommandException;


/**
 * <p>
 * This is an Action Command class that is invoked when the cache event is invoked by UI.
 * </p>
 * <p> This is a customization for <b><code>Banco do Brasil Project</code></b> </p>
 * 
 * @author brmob
 * @since v20150518-MNT-Compliance
 * @see DeficiencyCacheActionCommand
 */
public class DeficiencyCacheActionCommandBB extends DeficiencyCacheActionCommand {
	protected boolean doClearOptions = false;
	
	@Override
	protected void execute() {
		super.execute();
		
		IAppObj deficiency = formModel.getAppObj();
		
		IListAttribute defTestCaseAttr = deficiency.getAttribute(IDeficiencyAttributeTypeBB.LIST_TESTCASE);
		if (defTestCaseAttr.isEmpty()) {
			
			IListAttribute bbTestCaseAttr = deficiency.getAttribute(IDeficiencyAttributeTypeBB.LIST_BB_TESTCASE);
			
			IEnumAttribute defTypeAttr = deficiency.getAttribute(IDeficiencyAttributeTypeBB.ATTR_TYPE);
			
			if (bbTestCaseAttr.isEmpty()) {
				defTypeAttr.removeAllItems();
				defTypeAttr.addItem(EnumerationsBB.BB_DEFTYPE.GAP_CONTROL);
			} else {
				
				List<IAppObj> bbTestCaseList = bbTestCaseAttr.getElements(getFullGrantUserContext());
				IAppObj bbTestCase = ARCMCollections.extractSingleEntry(bbTestCaseList, true);
				
				IEnumAttribute type = deficiency.getAttribute(IDeficiencyAttributeType.ATTR_TYPE);
				IEnumAttribute effectiveness = bbTestCase.getAttribute(ITestcaseAttributeType.ATTR_EFFECTIVENESS);
				
				translateTestcaseEffectivenessToDeficiencyType(effectiveness, type);
				
				AppObjUtility.copyAttributeValue(bbTestCase, deficiency, ITestcaseAttributeTypeBB.LIST_CONTROLPROCESS, IDeficiencyAttributeTypeBB.LIST_PROCESS);
				AppObjUtility.copyAttributeValue(bbTestCase, deficiency, ITestcaseAttributeTypeBB.ATTR_INICIO_CICLO_AVALIATORIO, IDeficiencyAttributeTypeBB.ATTR_INICIO_CICLO_AVALIATORIO);
				AppObjUtility.copyAttributeValue(bbTestCase, deficiency, ITestcaseAttributeTypeBB.ATTR_FIM_CICLO_AVALIATORIO, IDeficiencyAttributeTypeBB.ATTR_FIM_CICLO_AVALIATORIO);
				AppObjUtility.copyAttributeValue(bbTestCase, deficiency, ITestcaseAttributeTypeBB.ATTR_CONTROLSTARTDATE, IDeficiencyAttributeTypeBB.ATTR_CONTROLSTARTDATE);
				AppObjUtility.copyAttributeValue(bbTestCase, deficiency, ITestcaseAttributeTypeBB.ATTR_CONTROLENDDATE, IDeficiencyAttributeTypeBB.ATTR_CONTROLENDDATE);
				
				/*
				 * Deficiency attributes based on the CONTROL object information
				 */
				IListAttribute testCaseControlAttr = bbTestCase.getAttribute(ITestcaseAttributeTypeBB.LIST_CONTROL);
				List<IAppObj> testCaseControlList = testCaseControlAttr.getElements(getFullGrantUserContext());
				IAppObj testCaseControl = ARCMCollections.extractSingleEntry(testCaseControlList, true);
				AppObjUtility.copyAttributeValue(testCaseControl, deficiency, IControlAttributeTypeBB.ATTR_SCOPE, IDeficiencyAttributeTypeBB.ATTR_SCOPE);
				AppObjUtility.copyAttributeValue(testCaseControl, deficiency, IControlAttributeTypeBB.LIST_APPSYSTEMS, IDeficiencyAttributeTypeBB.LIST_APPSYSTEMS);
				AppObjUtility.copyAttributeValue(testCaseControl, deficiency, IControlAttributeTypeBB.ATTR_ASSERTIONS, IDeficiencyAttributeTypeBB.ATTR_CONTROL_ASSERTIONS);
				/*Bug #121105 - Campo nível do controle preenchido automaticamente se há caso de teste vinculado*/
				AppObjUtility.copyAttributeValue(testCaseControl, deficiency, IControlAttributeTypeBB.ATTR_NIVEL_CONTROLE, IDeficiencyAttributeTypeBB.ATTR_CONTROL_LEVEL);
				
				ITextAttribute testCaseControlPlanilhaDeptAttr = testCaseControl.getAttribute(IControlAttributeTypeBB.ATTR_PLANILHA_DEPARTAMENTAL);
				if (!testCaseControlPlanilhaDeptAttr.isEmpty()) {
					deficiency.getAttribute(IDeficiencyAttributeTypeBB.ATTR_PLAN_DEPARTAMENTAL).setRawValue(true);
					deficiency.getAttribute(IDeficiencyAttributeTypeBB.ATTR_REL_APP).setRawValue(testCaseControlPlanilhaDeptAttr.getPersistentRawValue());
				} else {
					deficiency.getAttribute(IDeficiencyAttributeTypeBB.ATTR_PLAN_DEPARTAMENTAL).setRawValue(false);
				}
			
			}
			
			completeDeficiencyData(deficiency, bbTestCaseAttr);
			
		}
	}
	
	/**
	 * This method will be in charge to complete the remaining deficiency information for: <br>
	 * Organizational unit, Financial accounts, Risks and minor deficiency's attributes.
	 * 
	 * @param deficiency
	 * @param bbTestCaseAttr
	 */
	private void completeDeficiencyData(IAppObj deficiency, IListAttribute bbTestCaseAttr){
		
		List<IAppObj> processes = deficiency.getAttribute(IDeficiencyAttributeTypeBB.LIST_PROCESS).getElements(getFullGrantUserContext());
		
		try {
			
			
			IListAttribute riskAttr = deficiency.getAttribute(IDeficiencyAttributeTypeBB.LIST_RISK);
			IListAttribute finAccountAttr = deficiency.getAttribute(IDeficiencyAttributeTypeBB.LIST_FINACCOUNT);
			if(processes.isEmpty()) {
				riskAttr.removeAllElements(getFullGrantUserContext());
				finAccountAttr.removeAllElements(getFullGrantUserContext());
			}
			
			for (IAppObj process : processes) {
				
				IListAttribute orgUnitAttr = deficiency.getAttribute(IDeficiencyAttributeTypeBB.LIST_ORGUNIT);
				orgUnitAttr.removeAllElements(getFullGrantUserContext());
				
				IEnumAttribute finAccountAssertionsAttr = deficiency.getAttribute(IDeficiencyAttributeTypeBB.ATTR_FINACCOUNT_ASSERTIONS);
				finAccountAssertionsAttr.removeAllItems();
				
				AppObjUtility.copyAttributeValue(process, deficiency, IHierarchyAttributeTypeBB.LIST_DEPTMANAGER, IDeficiencyAttributeTypeBB.LIST_ORGUNIT);
				
				AppObjUtility.copyAttributeValue(process, deficiency, IHierarchyAttributeTypeBB.LIST_FINACCOUNT, IDeficiencyAttributeTypeBB.LIST_FINACCOUNT);
				
				List<IAppObj> finAccounts = finAccountAttr.getElements(getFullGrantUserContext());
				List<IEnumerationItem> finAccountAssertions = calculateFinAccountAssetions(finAccounts);
				deficiency.getAttribute(IDeficiencyAttributeTypeBB.ATTR_FINACCOUNT_ASSERTIONS).setRawValue(finAccountAssertions);
				
				List<IAppObj> risks = bbTestCaseAttr.isEmpty() ? 
						getRisksByProcess(process) : 
							getRisksByTestcase(ARCMCollections.extractSingleEntry(bbTestCaseAttr.getElements(getFullGrantUserContext()), true));
				
				Map<Long, IAppObj> mapRisk = new HashMap<Long, IAppObj>();
				
				if (!risks.isEmpty()) {
					for (IAppObj risk : risks) {
						mapRisk.put(risk.getObjectId(), risk);
					}
				} else {
					riskAttr.removeAllElements(getFullGrantUserContext());
				}
				
				Boolean isNeedReattachRisks = true;
				for (IOVID ovidRisk : riskAttr.getElementIds()) {
					if(mapRisk.containsKey(ovidRisk.getId())) {
						isNeedReattachRisks = false;
					}
				}
				
				if(isNeedReattachRisks){
					riskAttr.removeAllElements(getFullGrantUserContext());
					for (IAppObj oRisk : mapRisk.values()) {
						riskAttr.addFirstElement(oRisk, getFullGrantUserContext());
					}
				}
				
				if(riskAttr.getSize() == 1){
					
					IAppObj risk = riskAttr.getElements(getFullGrantUserContext()).get(0);
					AppObjUtility.copyAttributeValue(risk, deficiency, IRiskAttributeTypeBB.ATTR_ASSERTIONS, IDeficiencyAttributeTypeBB.ATTR_ASSERTIONS);
				}	
				
			}	
		} catch (RightException e) {
			e.printStackTrace();
			throw new ActionCommandException(e);
		} catch (ObjectRelationException e) {
			e.printStackTrace();
			throw new ActionCommandException(e);
		} 
	}
	
	private List<IAppObj> getRisksByProcess(IAppObj process){
		return getRisksFromProcessHierarchy(process);
	}
	
	private List<IAppObj> getRisksByTestcase(IAppObj bbTestCase){
		return bbTestCase.getAttribute(ITestcaseAttributeTypeBB.LIST_RISK).getElements(getFullGrantUserContext());
	}
	
	/**
	 * This method will perform the union of all financial account's assertions attribute
	 * based on the following object relations:
	 * DEFICIENCY >> PROCESS >> FINANCIAL ACCOUNTS >> bb_finAccountAssertions
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
	
	private List<IAppObj> getRisksFromProcessHierarchy(IAppObj process){
		
		IAppObjQuery query = null;
		List<IAppObj> risks = new ArrayList<IAppObj>();
		
		try {
			//manage risks
			IAppObjFacade riskFacade = environment.getAppObjFacade(ObjectType.RISK);
			query = riskFacade.createQuery();
			
			/*
			 * It was commented because users can be ARCM's administrator.
			 * Due to this fact all clients can by retrieved by the method #getUserContext().getUserRelations().getClients()
			 *
			List<IClientAppObj> userClients = getUserContext().getUserRelations().getClients();
			IClientAppObj userClient = ARCMCollections.extractSingleEntry(userClients, false);
			query.addClientRestriction(userClient.getAttribute(IClientAttributeType.ATTR_SIGN).getRawValue());
			*/
			
			Long processID = process.getAttribute(IHierarchyAttributeTypeBB.ATTR_OBJ_ID).getRawValue();
			query.addRestriction(
					QueryJoin.left(IRiskAppObj.LIST_PROCESS, 
							QueryRestriction.eq(IHierarchyAppObj.ATTR_OBJ_ID, processID)
					));
			
			IAppObjIterator risksIterator = query.getResultIterator();
			if (risksIterator.hasNext()) {
				CollectionUtils.addAll(risks, risksIterator);
			} else {
				String processLevel = process.getAttribute(IHierarchyAttributeTypeBB.ATTR_NIVEL).getRawValue();
				if (processLevel != null) {
					int processLevelNumber = Integer.valueOf(processLevel);
					if (processLevelNumber >= 5) {
						IAppObj parentProcess = process.getParentAppObj(getFullGrantUserContext(), IHierarchyAttributeTypeBB.LIST_CHILDREN);
						
						IBooleanAttribute isRootProcessAttr = parentProcess.getAttribute(IHierarchyAttributeTypeBB.ATTR_ISROOT);
						Boolean isRootProcess = isRootProcessAttr.getPersistentRawValue();
						
						if (!isRootProcess) {
							risks = getRisksFromProcessHierarchy(parentProcess);
						}
					}
				} else {
					IAppObj parentProcess = process.getParentAppObj(getFullGrantUserContext(), IHierarchyAttributeTypeBB.LIST_CHILDREN);
					risks = getRisksFromProcessHierarchy(parentProcess);
				}
			}
		} finally {
			query.release();
		}
		
		return risks;
	}
	
	/**
	 * This is a specific customization for the BdB Project:
	 * It was needed to change the mapping TESTCASE.effectiviness >> DEFICIENCY.type
	 * Now there are two different enumerations effectiveness and bb_defType to be mapped.
	 * @param effectiveness - from TESTCASE
	 * @param type - from DEFICIENCY
	 */
	private void translateTestcaseEffectivenessToDeficiencyType(IEnumAttribute effectiveness, IEnumAttribute type){
		type.removeAllItems();
		List<IEnumerationItem> effectivenessEnumItem = effectiveness.getRawValue();
		for (IEnumerationItem iEnumerationItem : effectivenessEnumItem) {
			if (iEnumerationItem.equals(Enumerations.EFFECTIVENESS.DESIGN)) {
				type.addItem(EnumerationsBB.BB_DEFTYPE.DESIGN);
			} else {
				type.addItem(EnumerationsBB.BB_DEFTYPE.CONTROL);
			}
		}
	}
	
	/**
	 * This is a specific customization for the BdB Project:
	 * Reset some Deficiency attributes while the attribute "defTestCaseAttr" is empty
	 */
	protected void resetAttributes() {
		IAppObj deficiencyAppObj = formModel.getAppObj();
		List<IAttribute> editableAttributes = deficiencyAppObj.getEditableAttributes(getUserContext());
		
		for (IAttribute attribute : editableAttributes) {
			if (attribute.getAttributeType().equals(IDeficiencyAttributeTypeBB.LIST_PROCESS)
					|| attribute.getAttributeType().equals(IDeficiencyAttributeTypeBB.LIST_RISK)
					|| attribute.getAttributeType().equals(IDeficiencyAttributeTypeBB.LIST_ORGUNIT)
					|| attribute.getAttributeType().equals(IDeficiencyAttributeTypeBB.ATTR_ASSERTIONS)
					|| attribute.getAttributeType().equals(IDeficiencyAttributeTypeBB.ATTR_CONTROLSTARTDATE)
					|| attribute.getAttributeType().equals(IDeficiencyAttributeTypeBB.ATTR_CONTROLENDDATE)
					|| attribute.getAttributeType().equals(IDeficiencyAttributeTypeBB.ATTR_SCOPE)
					|| attribute.getAttributeType().equals(IDeficiencyAttributeTypeBB.ATTR_FINACCOUNT_ASSERTIONS)
					|| attribute.getAttributeType().equals(IDeficiencyAttributeTypeBB.ATTR_CONTROL_ASSERTIONS)
					|| attribute.getAttributeType().equals(IDeficiencyAttributeTypeBB.ATTR_INICIO_CICLO_AVALIATORIO)
					|| attribute.getAttributeType().equals(IDeficiencyAttributeTypeBB.ATTR_FIM_CICLO_AVALIATORIO)) { 
				resetToBlankValue(attribute);
			}
		}
	}
	
	/**
	 * This method is used to reset an attribute value to the blank value
	 * 
	 * @param attribute
	 * */
	@SuppressWarnings("rawtypes")
	private void resetToBlankValue(IAttribute attribute) {
        IAttributeType attributeType = attribute.getAttributeType();
        if (attributeType instanceof IValueAttributeType) {
            IValueAttributeType type = (IValueAttributeType) attributeType;   
            ((IValueAttribute) attribute).setRawValue(type.getDefaultValue());
        } else if (attributeType instanceof IEnumAttributeType) {
            ((IEnumAttribute) attribute).setRawValue(null);
        } else if (attributeType instanceof IListAttributeType) {
            try {
                ((IListAttribute) attribute).removeAllElements(RECommons.getFullGrantContext());
            } catch (RightException e) {
                throw new RuntimeException(e);
            } catch (ObjectRelationException e) {
                throw new RuntimeException(e);
            }
        }
    }
	
	@Override
	protected void assumeData(final String... p_exculdeParameters) {
		super.assumeData(p_exculdeParameters);
		if (doClearOptions) {
			resetAttributes();
		}
	}
		
	@Override
	protected boolean beforeExecute() {
		if (!super.beforeExecute()) {
			return CANCEL_EXECUTION;
		}
		
        if (isBreadcrumbRequest() || !formModel.isEditable()) {
			return CONTINUE_EXECUTION;
		}
        
		if (bbTestCaseAttrIsDetached()) {
			doClearOptions = true;
			assumeData();
			return CONTINUE_EXECUTION;
		}
		return CONTINUE_EXECUTION;
	}
	
	/**
	 * This method is used to check detaching of bb_testcase attribute from Deficiency object
	 * 
	 * @return true | false
	 * */
	private boolean bbTestCaseAttrIsDetached() {
		final IListAttribute bbTestcaseListAttribute = formModel.getAppObj().getAttribute(IDeficiencyAttributeTypeBB.LIST_BB_TESTCASE);
		if(bbTestcaseListAttribute != null && bbTestcaseListAttribute.getSize() > 0){
			return true;
		
		}else{
			return false;
		}
	}	
}
