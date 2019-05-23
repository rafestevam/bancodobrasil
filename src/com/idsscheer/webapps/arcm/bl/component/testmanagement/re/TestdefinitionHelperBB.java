package com.idsscheer.webapps.arcm.bl.component.testmanagement.re;


import java.util.List;

import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IEnumAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.ILongAttribute;
import com.idsscheer.webapps.arcm.bl.re.ext.MessageInformation;
import com.idsscheer.webapps.arcm.bl.re.ext.RuleAppObj;
import com.idsscheer.webapps.arcm.bl.re.ext.StateInformation;
import com.idsscheer.webapps.arcm.bl.re.impl.REEnvironment;
import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.ITestdefinitionAttributeType;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.config.Metadata;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.FrequencyEnumerationItem;



public class TestdefinitionHelperBB extends TestdefinitionHelper
{
	
	public static boolean isTestingStepsDefaultValue(){
		REEnvironment env = REEnvironment.getInstance();
		RuleAppObj testDef = env.getRuleAppObj();

		String prueba= testDef.getRawValue(ITestdefinitionAttributeType.STR_TESTINGSTEPS);
		if(prueba.trim().equalsIgnoreCase("Executar procedimentos de walkthrough")){
			return true;
		}
		return false;
	}
	
	public static void checkOffsetByControlPeriod(){
		REEnvironment env = REEnvironment.getInstance();
		RuleAppObj testDefinition = env.getRuleAppObj();
		
		StateInformation stateInformation = env.getStateInformation(); 
		MessageInformation messageInformation = env.getMessageInformation();
		
		IEnumAttribute controlPeriodAttr = (IEnumAttribute) testDefinition.getAttribute(ITestdefinitionAttributeType.ATTR_CONTROL_PERIOD.getId());
		List<FrequencyEnumerationItem> controlPeriods = controlPeriodAttr.getRawValue();
		FrequencyEnumerationItem controlPeriod = ARCMCollections.extractSingleEntry(controlPeriods, true);
		
		ILongAttribute offsetAttr = (ILongAttribute) testDefinition.getAttribute(ITestdefinitionAttributeType.ATTR_CONTROL_PERIOD_OFFSET.getId());
		long offset = offsetAttr.getRawValue();
		
		String controlPeriodField = Metadata.getMetadata().getResourceBundle(
				getFullReadAccessUserContext().getLanguage()).getString(controlPeriodAttr.getAttributeType().getPropertyKey());
		
		if (controlPeriod.equals(Enumerations.CONTROLPERIOD.DAY) && offset > 1) {
			stateInformation.setValid(ITestdefinitionAttributeType.ATTR_CONTROL_PERIOD_OFFSET.getId(), false);
			
			messageInformation.addErrorMessage(ITestdefinitionAttributeType.ATTR_CONTROL_PERIOD_OFFSET.getId(),
					"error.testdefinition.offset.not.ok.ERR", controlPeriodField,1);
			
		} else if (controlPeriod.equals(Enumerations.CONTROLPERIOD.WEEK) && offset > 7) {
			stateInformation.setValid(ITestdefinitionAttributeType.ATTR_CONTROL_PERIOD_OFFSET.getId(), false);
			
			messageInformation.addErrorMessage(ITestdefinitionAttributeType.ATTR_CONTROL_PERIOD_OFFSET.getId(),
					"error.testdefinition.offset.not.ok.ERR", controlPeriodField,7);
			
		} else if (controlPeriod.equals(Enumerations.CONTROLPERIOD.MONTH) && offset > 30) {
			stateInformation.setValid(ITestdefinitionAttributeType.ATTR_CONTROL_PERIOD_OFFSET.getId(), false);
			
			messageInformation.addErrorMessage(ITestdefinitionAttributeType.ATTR_CONTROL_PERIOD_OFFSET.getId(),
					"error.testdefinition.offset.not.ok.ERR", controlPeriodField,30);
			
		} else if (controlPeriod.equals(Enumerations.CONTROLPERIOD.QUARTER) && offset > 90) {
			stateInformation.setValid(ITestdefinitionAttributeType.ATTR_CONTROL_PERIOD_OFFSET.getId(), false);
			
			messageInformation.addErrorMessage(ITestdefinitionAttributeType.ATTR_CONTROL_PERIOD_OFFSET.getId(),
					"error.testdefinition.offset.not.ok.ERR", controlPeriodField,90);
			
		} else if (controlPeriod.equals(Enumerations.CONTROLPERIOD.SEMIANNUAL) && offset > 180) {
			stateInformation.setValid(ITestdefinitionAttributeType.ATTR_CONTROL_PERIOD_OFFSET.getId(), false);
			
			messageInformation.addErrorMessage(ITestdefinitionAttributeType.ATTR_CONTROL_PERIOD_OFFSET.getId(),
					"error.testdefinition.offset.not.ok.ERR", controlPeriodField,180);
			
		} else if (controlPeriod.equals(Enumerations.CONTROLPERIOD.YEAR) && offset > 365) {
			stateInformation.setValid(ITestdefinitionAttributeType.ATTR_CONTROL_PERIOD_OFFSET.getId(), false);
			
			messageInformation.addErrorMessage(ITestdefinitionAttributeType.ATTR_CONTROL_PERIOD_OFFSET.getId(),
					"error.testdefinition.offset.not.ok.ERR", controlPeriodField,365);
			
		}
	}
}

