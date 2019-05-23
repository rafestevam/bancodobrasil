/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.component.testmanagement.re;

import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IBooleanAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IListAttribute;
import com.idsscheer.webapps.arcm.bl.re.ext.RuleAppObj;
import com.idsscheer.webapps.arcm.bl.re.ext.StateInformation;
import com.idsscheer.webapps.arcm.bl.re.impl.REEnvironment;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.ITestcaseAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.ITestcaseAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.ITestdefinitionAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;

/**
 * <p>
 * Used by the rule engine in 'testcase.drl' and 'testcase.dsl'.
 * </p>
 * 
 * <p> This is a customization for <b><code>Banco do Brasil Project</code></b> </p>
 *@author brmob
 *@since v20160314-MNT-UATEnhancements
 *@see TestcaseHelper
 */
public class TestcaseHelperBB extends TestcaseHelper {
	
	public static void checkValidationTestCase(){
		REEnvironment env = REEnvironment.getInstance();
		RuleAppObj testCase = env.getRuleAppObj();
		
		IListAttribute testDefinitionAttr = (IListAttribute) testCase.getAttribute(ITestcaseAttributeType.LIST_TESTDEFINITION.getId());
		IAppObj testDefinition = ARCMCollections.extractSingleEntry(testDefinitionAttr.getElements(getFullReadAccessUserContext()), true);
		
		IBooleanAttribute testeValidacaoAttr = testDefinition.getAttribute(ITestdefinitionAttributeTypeBB.ATTR_TESTEVALIDACAO);
		Boolean testeValidacao = testeValidacaoAttr.getPersistentRawValue();
		
		if (testeValidacao) {
			StateInformation s = env.getStateInformation();
			s.setVisible(ITestcaseAttributeTypeBB.ATTR_VALIDACAO_REFERENCIA.getId(), true);
			
			if (isInWorkflowState("openForExecution", testCase) && isUserInAttributeGroup(Which.PERSISTENT.getString(), ITestcaseAttributeTypeBB.LIST_OWNER_GROUP.getId())) {
				s.setEditable(ITestcaseAttributeTypeBB.ATTR_VALIDACAO_REFERENCIA.getId(), true);
				s.setMandatory(ITestcaseAttributeTypeBB.ATTR_VALIDACAO_REFERENCIA.getId(), true);
			}
			
		}
	}

}
