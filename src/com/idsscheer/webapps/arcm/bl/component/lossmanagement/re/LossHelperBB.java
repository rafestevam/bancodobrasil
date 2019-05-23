/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.component.lossmanagement.re;

import java.util.List;
import java.util.Locale;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.component.common.objectmodel.attribute.vc.converter.VCUtility;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.ICxnObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IEnumAttribute;
import com.idsscheer.webapps.arcm.bl.re.REKey;
import com.idsscheer.webapps.arcm.bl.re.ext.MessageInformation;
import com.idsscheer.webapps.arcm.bl.re.ext.RuleAppObj;
import com.idsscheer.webapps.arcm.bl.re.ext.StateInformation;
import com.idsscheer.webapps.arcm.bl.re.impl.REEnvironment;
import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.ILossAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;

/**
 * @author Administrator
 *
 */
public class LossHelperBB extends LossHelper {

	public LossHelperBB() {
		super();
	}
	
	public static void validateCustomAmounts(){
		REEnvironment env = REEnvironment.getInstance();
		RuleAppObj lossObj = env.getRuleAppObj();
		StateInformation s = env.getStateInformation();
		MessageInformation m = env.getMessageInformation();
		
		REKey netTotalLossAmountKey = lossObj.createAtomicKey("netTotalLoss");
		Double netTotalLossAmount = (Double)lossObj.getRawValue(netTotalLossAmountKey.getString());
		
		REKey grossTotalLossAmountKey = lossObj.createAtomicKey("grossTotalLoss");
		Double grossTotalLossAmount = (Double)lossObj.getRawValue(grossTotalLossAmountKey.getString());
		
		IEnumAttribute lossTypeAttr = (IEnumAttribute) lossObj.getAttribute(ILossAttributeTypeBB.ATTR_TYPE.getId());
		IEnumerationItem lossType = ARCMCollections.extractSingleEntry(lossTypeAttr.getRawValue(), true);
		
		if (!lossType.equals(Enumerations.LOSSDB_LOSS_TYPE.GAIN)) {
			if ((netTotalLossAmount != null) && (grossTotalLossAmount != null) && (netTotalLossAmount.doubleValue() > grossTotalLossAmount.doubleValue())) {
			  s.setValid(netTotalLossAmountKey.getString(), false);
			  m.addErrorMessage(netTotalLossAmountKey.getString(), "value.must.not.be.greater.than.ERR", new Object[] { lossObj.getAttributeLabel(grossTotalLossAmountKey.getString()) });
			  
			  s.setValid(grossTotalLossAmountKey.getString(), false);
			  m.addErrorMessage(grossTotalLossAmountKey.getString(), "value.must.not.be.smaller.than.ERR", new Object[] { lossObj.getAttributeLabel(netTotalLossAmountKey.getString()) });
			}
		}
		
		
		if (!lossType.equals(Enumerations.LOSSDB_LOSS_TYPE.GAIN)) {
			validateNetGross("relRisks", "relRisks_netActual", "relRisks_grossActual");			
			validateNetGross("process", "process_netActual", "process_grossActual");			
			validateNetGross("orgunit", "orgunit_netActual", "orgunit_grossActual");			
			validateNetGross("appsystem", "appsystem_netActual", "appsystem_grossActual");			
			validateNetGross("finaccount", "finaccount_netActual", "finaccount_grossActual");
			
		} else {
			validateGainNetGross("relRisks", "relRisks_netActual", "relRisks_grossActual");			
			validateGainNetGross("process", "process_netActual", "process_grossActual");			
			validateGainNetGross("orgunit", "orgunit_netActual", "orgunit_grossActual");			
			validateGainNetGross("appsystem", "appsystem_netActual", "appsystem_grossActual");			
			validateGainNetGross("finaccount", "finaccount_netActual", "finaccount_grossActual");
			
		}
		
	}
	
	protected static void validateGainNetGross(String listAttributeName, String netAmountAttributeName, String grossAmountAttributeName){
		REEnvironment env = REEnvironment.getInstance();
		RuleAppObj lossObj = env.getRuleAppObj();
		StateInformation s = env.getStateInformation();
		MessageInformation m = env.getMessageInformation();
		
		IUserContext ctx = env.getUserContext();
		Locale locale = ctx.getLanguage();
		
		String objName = lossObj.getObjectType().getId();
		REKey listKey = lossObj.createAtomicKey(listAttributeName);


		List<ICxnObj> cxns = lossObj.getConnections(listKey.getString());
		Double netActualSum = Double.valueOf(0.0D);
		Double grossActualSum = Double.valueOf(0.0D);
		
		for (ICxnObj cxnObj : cxns) {
			IOVID targetOVID = cxnObj.getTargetAppObj().getVersionData().getOVID();
			
			REKey netActualAmountKey = lossObj.createCompleteKey(listAttributeName, netAmountAttributeName, targetOVID);
			Double netActualAmount = (Double)lossObj.getRawValue(netActualAmountKey.getString());
			
			REKey grossActualAmountKey = lossObj.createCompleteKey(listAttributeName, grossAmountAttributeName, targetOVID);
			Double grossActualAmount = (Double)lossObj.getRawValue(grossActualAmountKey.getString());
			
			if (netActualAmount != null) {
				netActualSum = Double.valueOf(netActualSum.doubleValue() + netActualAmount.doubleValue());
			}
			
			if (grossActualAmount != null) {
				grossActualSum = Double.valueOf(grossActualSum.doubleValue() + grossActualAmount.doubleValue());
			}
		}
		
		REKey netTotalLossAmountKey = lossObj.createAtomicKey("netTotalLoss");
		Double netTotalLossAmount = (Double)lossObj.getRawValue(netTotalLossAmountKey.getString());
		
		if ((netTotalLossAmount != null) && (netActualSum.doubleValue() > netTotalLossAmount.doubleValue())) {
			
			String arg0 = lossObj.getAttributeLabel(lossObj.createConnectionKey(listAttributeName, netAmountAttributeName).getString());
			String arg1 = VCUtility.convertToUI(objName, "relRisks_netActual", locale, netActualSum);
			String arg2 = lossObj.getAttributeLabel(netTotalLossAmountKey.getString());
			String arg3 = VCUtility.convertToUI(objName, "netTotalLoss", locale, netTotalLossAmount);
			s.setValid(listKey.getString(), false);
			m.addErrorMessage(listKey.getString(), "sum.must.not.be.greater.than.value.ERR", new Object[] { arg0, arg1, arg2, arg3 });
		}
		
		REKey grossTotalLossAmountKey = lossObj.createAtomicKey("grossTotalLoss");
		Double grossTotalLossAmount = (Double)lossObj.getRawValue(grossTotalLossAmountKey.getString());
		
		if ((grossTotalLossAmount != null) && (grossActualSum.doubleValue() > grossTotalLossAmount.doubleValue())) {
			
			String arg0 = lossObj.getAttributeLabel(lossObj.createConnectionKey(listAttributeName, grossAmountAttributeName).getString());
			String arg1 = VCUtility.convertToUI(objName, "relRisks_grossActual", locale, grossActualSum);
			String arg2 = lossObj.getAttributeLabel(grossTotalLossAmountKey.getString());
			String arg3 = VCUtility.convertToUI(objName, "grossTotalLoss", locale, grossTotalLossAmount);
			s.setValid(listKey.getString(), false);
			m.addErrorMessage(listKey.getString(), "sum.must.not.be.greater.than.value.ERR", new Object[] { arg0, arg1, arg2, arg3 });
		}

	}
	
}
